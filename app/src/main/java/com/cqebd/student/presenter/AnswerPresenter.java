package com.cqebd.student.presenter;

import android.Manifest;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cqebd.student.R;
import com.cqebd.student.app.App;
import com.cqebd.student.app.BaseActivity;
import com.cqebd.student.constant.Constant;
import com.cqebd.student.db.dao.AttachmentDao;
import com.cqebd.student.db.dao.DaoSession;
import com.cqebd.student.db.dao.StudentAnswer;
import com.cqebd.student.db.dao.StudentAnswerDao;
import com.cqebd.student.dialog.Effectstype;
import com.cqebd.student.dialog.NiftyDialog;
import com.cqebd.student.http.NetApi;
import com.cqebd.student.http.NetCallBack;
import com.cqebd.student.http.NetClient;
import com.cqebd.student.service.SubmitTask;
import com.cqebd.student.tools.AlbumHelper;
import com.cqebd.student.tools.JSONS;
import com.cqebd.student.tools.KResKt;
import com.cqebd.student.tools.StringUtils;
import com.cqebd.student.tools.TimeConversion;
import com.cqebd.student.tools.Toast;
import com.cqebd.student.tools.UtilGson;
import com.cqebd.student.ui.AnswerFragment;
import com.cqebd.student.ui.WebActivity;
import com.cqebd.student.views.IAnswer;
import com.cqebd.student.vo.entity.AnswerInfo;
import com.cqebd.student.vo.entity.AnswerRequest;
import com.cqebd.student.vo.entity.Attachment;
import com.cqebd.student.vo.entity.BaseBean;
import com.cqebd.student.vo.entity.QuestionInfo;
import com.cqebd.student.vo.entity.WorkInfo;
import com.cqebd.student.vo.enums.AnswerMode;
import com.cqebd.student.vo.enums.TaskStatus;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.query.Query;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gorden.behavior.LoadingDialog;
import gorden.rxbus.RxBus;
import gorden.util.DensityUtil;
import gorden.util.PackageUtils;
import gorden.util.RxCounter;
import gorden.util.XLog;
import gorden.util.luban.Luban;
import gorden.util.luban.OnCompressListener;
import gorden.widget.selector.SelectorButton;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 学生答题页逻辑处理
 * Created by Gordn on 2017/3/17.
 */

public class AnswerPresenter implements AlbumHelper.AlbumCallBack {
    private IAnswer answer;
    private BaseActivity mContext;

    private WorkInfo taskInfo;
    private List<QuestionInfo> questionInfoList;

    private int userId;

    private AnswerMode answerMode;
    private Disposable tickDisposable;

    private int currentItem = 0;
    private SubjectItemAdapter itemAdapter;//题目

    private File picFile = null;

    private DaoSession mDaoSession;

    private SubmitTask submitTask;

    private boolean TimeIsEnd = false;
    private NiftyDialog niftyDialog;

    public AnswerPresenter(IAnswer answer) {
        this.answer = answer;
        mContext = (BaseActivity) answer;
        mDaoSession = App.Companion.getDaoSession();

        taskInfo = mContext.getIntent().getParcelableExtra(Constant.TASK_INFO);
        questionInfoList = mContext.getIntent().getParcelableArrayListExtra(Constant.QUESTION_INFO);

        //bind提交答案Service
        submitTask = new SubmitTask((int) taskInfo.getPapersId(), (int) taskInfo.getTaskId());

        mergeLocalAnswer();
        //noinspection ConstantConditions
        userId = (int) KResKt.getLoginId();
        initTimeModel();
    }

    /**
     * 合并本地数据库答案
     * 服务器  本地
     * null     data     本地数据
     * data     null      服务器
     * data     data      服务器
     */
    private void mergeLocalAnswer() {
        long start = System.currentTimeMillis();
        Query<StudentAnswer> query = mDaoSession.getStudentAnswerDao().queryBuilder().where(
                StudentAnswerDao.Properties.StudentQuestionsTaskId.eq(taskInfo.getTaskId()),
                StudentAnswerDao.Properties.QuestionId.eq("")).build();
        for (QuestionInfo info : questionInfoList) {
            query.setParameter(1, info.getId());
            List<StudentAnswer> local = query.list();
            if (local != null && local.size() > 0) {
                if (TextUtils.isEmpty(local.get(0).getStudentsAnswer()) || !TextUtils.isEmpty(info.getStudentsAnswer()))
                    continue;
                //服务器无答案 本地有答案
                XLog.e("答案校准   本地答案：" + local.get(0).getStudentsAnswer() + "    服务器：" + info.getStudentsAnswer());
                info.setStudentsAnswer(local.get(0).getStudentsAnswer());//将空数据替换为本地答案
                submitTask.submit(info);
            }

            //本地附件信息


        }
        XLog.e("数据库查询耗时:" + (System.currentTimeMillis() - start));
    }

    /**
     * 获得倒计时时间 答题模式
     */
    private void initTimeModel() {
        taskInfo.setDuration(taskInfo.getDuration() * 60);//转换为秒

        if (taskInfo.getDuration() == 0 && taskInfo.getIsTasks()) {
            answerMode = AnswerMode.TRAIN;//练习模式，没有规定答题时间  只有答题时间范围
            startTimeTick(TimeConversion.getDurationByEnd(taskInfo.getCanEndDateTime()));
        } else {
            answerMode = AnswerMode.EXAM;//答题模式 有限制时间
            startTimeTick(TimeConversion.getDurationByStart(taskInfo.getStartTime(), taskInfo.getDuration()));
        }

    }

    //绑定数据
    public void bindData() {
        if (taskInfo == null) return;
        answer.viewPager().addOnPageChangeListener(new AnswerPagerChangeListener());
        answer.viewPager().setPageTransformer(true, new AnswerPagerTransformer());
        answer.viewPager().setAdapter(new AnswerPagerAdapter(mContext.getSupportFragmentManager()));
        answer.setTaskName(taskInfo.getName());
        answer.setLoacation("1/" + taskInfo.getQuestionCount());
        int spanCount = DensityUtil.appWidth(mContext) / DensityUtil.dip2px(50, mContext);
        answer.recyclerView().setLayoutManager(new GridLayoutManager(mContext, spanCount));
        answer.recyclerView().setAdapter(itemAdapter = new SubjectItemAdapter());
        loadAnswerCard(questionInfoList.get(0));//创建第一小题的答题卡
        answerCardState();
    }

    /**
     * 答题倒计时
     * seconds 秒
     */
    private final String alertTitle = "本次测试剩余时间还有不到3分钟\n请抓紧时间!";

    private NiftyDialog niftyDialog() {
        if (niftyDialog == null) {
            niftyDialog = new NiftyDialog(mContext);
        }
        return niftyDialog;
    }

    private void startTimeTick(long seconds) {
        stopTimeTick();
        tickDisposable = RxCounter.tick(seconds).doOnNext(time -> {
            answer.setCountDown(TimeConversion.getHourMinSecondsData(time * 1000));
            if (time == 3 * 60) {
                niftyDialog().withMessage(alertTitle)
                        .withButton1Text("知道了").setButton1Click(view -> niftyDialog().dismiss()).show();
            }
        }).doOnComplete(() -> {
            answer.setCountDown("已结束");
            TimeIsEnd = true;
            answerCardState();//关闭答题卡
            niftyDialog().withMessage("答题时间已结束,将为你自动交卷")
                    .withButton1Text("知道了").show();
            niftyDialog().setOnDismissListener(dialog -> commitAnswer(true));

        }).subscribe();
    }

    private void stopTimeTick() {
        if (tickDisposable != null)
            tickDisposable.dispose();
    }

    /**
     * 答题卡状态，允许答题，禁止答题
     */
    public void answerCardState() {
        if (TimeIsEnd) {//答题时间结束
            answer.prohibitAnswer();
            answer.tipMessage("答题时间已结束");
            answer.audioInfo(null);
            return;
        }
        QuestionInfo questionInfo = questionInfoList.get(currentItem);

        List<Attachment> attachmentList = questionInfo.getQuestionSubjectAttachment();
        if (attachmentList != null && attachmentList.size() > 0) {

            if (attachmentList.size() == 1 && attachmentList.get(0).getMediaTypeName().toLowerCase().contains("mp3")) {
                Attachment attachment = attachmentList.get(0);
                com.cqebd.student.db.dao.Attachment resource = mDaoSession.getAttachmentDao()
                        .queryBuilder().where(AttachmentDao.Properties.Id
                                .eq(String.valueOf(taskInfo.getTaskId()) + attachment.getId())).build().unique();
                int answerType = attachment.getAnswerType();
                int count = 0;
                if (resource != null) {
                    answer.audioInfo(resource);
                    count = resource.getWatchCount();
                } else {
                    String id = String.valueOf(taskInfo.getTaskId()) + attachment.getId();
                    answer.audioInfo(new com.cqebd.student.db.dao.Attachment(id, (int) taskInfo.getTaskId(), attachment.getUrl(),
                            attachment.getName(), 0, attachment.getAnswerType(), attachment.getCanWatchTimes()));
                }
                if (answerType == 2 && count == 0) {
                    answer.tipMessage("当前不能答题,请先听完音频文件");
                    answer.prohibitAnswer();//不满足附件查看条件
                } else {
                    answer.tipMessage(null);
                    answer.allowAnswer();
                }
                return;
            } else {
                for (Attachment attachment : attachmentList) {
                    com.cqebd.student.db.dao.Attachment resource = mDaoSession.getAttachmentDao()
                            .queryBuilder().where(AttachmentDao.Properties.Id
                                    .eq(String.valueOf(taskInfo.getTaskId()) + attachment.getId())).build().unique();
                    int answerType = attachment.getAnswerType();
                    int count = 0;
                    if (resource != null) {
                        count = resource.getWatchCount();
                    }
                    if (answerType == 2 && count == 0) {
                        answer.audioInfo(null);
                        answer.tipMessage("当前不能答题,请先观看视频");
                        answer.prohibitAnswer();//不满足附件查看条件
                        return;
                    }
                }
            }
        }
        answer.audioInfo(null);
        answer.tipMessage(null);
        answer.allowAnswer();
    }

    /**
     * 加载答题卡
     */
    private void loadAnswerCard(QuestionInfo questionInfo) {
        answer.answerCardView().loadAnswerCard(questionInfo, taskInfo.getSubjectId(), this);
    }

    @Override
    public void pathResult(String path, ImageView imageView) {
        new RxPermissions(mContext).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        followImage(path, imageView);
                    }
                });
    }

    private void followImage(String path, ImageView imageView) {
        picFile = new File(path);
        if (!picFile.exists()) return;

        new Luban(App.mContext).load(picFile).asGray(true).setCompressListener(new OnCompressListener() {
            @Override
            public void onStart() {
                LoadingDialog.lock = true;
                LoadingDialog.show(mContext, "正在上传图片");
            }

            @Override
            public void onSuccess(File file) {
                picFile = file;
                uploadFile(file, imageView);
            }

            @Override
            public void onError(Throwable e) {
                LoadingDialog.lock = false;
                LoadingDialog.stop();
                Toast.show("图片处理失败");
            }
        }).launch();
    }

    private void uploadFile(File file, ImageView imageView) {
        LoadingDialog.lock = false;
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        NetClient.createApi(NetApi.class).uploadFile(requestBody).enqueue(new NetCallBack<ResponseBody>() {
            @Override
            public void onSucceed(ResponseBody response) {
                try {
                    String url = new JSONObject(response.string()).getString("data");
                    imageView.setTag(R.id.image_file_path, picFile.getAbsolutePath());
                    imageView.setTag(R.id.image_url, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure() {
                niftyDialog().setOnDismissListener(null);
                niftyDialog().withMessage("图片上传失败,是否重新上传")
                        .withButton1Text("取消").withButton2Text("上传").setButton2Click(view -> {
                    LoadingDialog.show(mContext, "正在上传图片");
                    uploadFile(picFile, imageView);
                    niftyDialog().dismiss();
                }).show();
            }
        });
    }

    public void onBackPressed() {
        if (answerMode == AnswerMode.EXAM) {
            showCommitDialog();
        } else {
            stopTimeTick();
            submitTask.stopTimer();
            saveTheUploadEnd(0, false);
            mContext.finish();
        }
    }

    /**
     * 试题页适配
     */
    private class AnswerPagerAdapter extends FragmentStatePagerAdapter {

        AnswerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            AnswerFragment fragment = new AnswerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("taskId", (int) taskInfo.getTaskId());
            bundle.putParcelableArrayList("attachment", questionInfoList.get(position).getQuestionSubjectAttachment());
            String urlFormat = "HomeWork/Question?id=%s&PapersID=%s&studentid=%s";
            bundle.putString("url", Constant.BASE_WEB_URL + String.format(urlFormat, questionInfoList.get(position).getId(), taskInfo.getPapersId(), userId));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            if (taskInfo != null && questionInfoList != null) {
                return taskInfo.getQuestionCount();
            }
            return 0;
        }
    }


    private boolean isRight = false;

    /**
     * 翻页监听
     */
    private class AnswerPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //保存当前答案
            saveTheUpload();
            int temp = currentItem;
            currentItem = position;
            answer.setLoacation((position + 1) + "/" + taskInfo.getQuestionCount());
            loadAnswerCard(questionInfoList.get(position));
            itemAdapter.notifyItemChanged(temp + 1);
            itemAdapter.notifyItemChanged(currentItem + 1);
            answerCardState();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                answer.answerCardView().hideSoftKeyBord();
            }

            if (currentItem == questionInfoList.size() - 1) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (isRight) {
                            showCommitDialog();
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        isRight = true;
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        isRight = false;
                        break;
                }
            }
        }
    }

    /**
     * @param status 0保存,1交卷
     */
    private void saveTheUploadEnd(int status, boolean autoSubmit) {
        if (answer.submitMode() == 0) {
            QuestionInfo questionInfo = questionInfoList.get(currentItem);
            if (answer.answerCardView().isChanged() || questionInfo.uploadState() == QuestionInfo.STATE_UPFAILED) {
                questionInfo.setStudentsAnswer(answer.answerCardView().getAnswer());
                saveStudentAnswer(questionInfo);
                uploadAnswer(questionInfo, status == 1, autoSubmit);
            } else if (status == 1) {
                submitAnswers(1, autoSubmit);
            }
        } else {
            submitAnswers(status, autoSubmit);
        }
    }


    /**
     * 正常答题中保存上传答案
     */
    private void saveTheUpload() {
        QuestionInfo questionInfo = questionInfoList.get(currentItem);
        //如果答案改变 或者答案提交失败
        if (answer.answerCardView().isChanged()) {
            String cardAnswer = answer.answerCardView().getAnswer();
            if (TextUtils.isEmpty(cardAnswer)) return;
            questionInfo.setStudentsAnswer(cardAnswer);
            saveStudentAnswer(questionInfo);//保存本地数据库
            if (answer.submitMode() == 0) {//服务器提交规则
                submitTask.submit(questionInfo);
            }
        }
    }

    /**
     * 保存用户答案到本地数据库
     */
    private void saveStudentAnswer(QuestionInfo info) {
        int taskId = (int) taskInfo.getTaskId();
        int questionId = info.getId();

        //生成数据库唯一id
        String answerId = String.valueOf(taskId).concat(String.valueOf(questionId));

        StudentAnswer answer = new StudentAnswer();
        answer.setStudentQuestionsTaskId(taskId);
        answer.setSubjectTypeId(taskInfo.getSubjectId());
        answer.setSubjectTypeName(taskInfo.getSubjectName());
        answer.setName(taskInfo.getName());
        answer.setQuestionId(questionId);
        answer.setStudentsAnswer(info.getStudentsAnswer());
        answer.setId(answerId);
        mDaoSession.startAsyncSession().insertOrReplace(answer);
    }

    //答案提交到服务器
    private void uploadAnswer(QuestionInfo info, boolean end, boolean autoSubmit) {
        NetClient.createApi(NetApi.class).submitAnswer(userId, info.getId(), (int) taskInfo.getPapersId(), (int) taskInfo.getTaskId()
                , StringUtils.getUnicodeString(info.getStudentsAnswer()), info.getQuestionTypeId(), PackageUtils.getVersionName(mContext), Build.MODEL).enqueue(new NetCallBack<BaseBean>() {
            @Override
            public void onSucceed(BaseBean response) {
                if (response.isSuccess()) {
                    XLog.e("答案上传成功");
                    info.setUploadState(QuestionInfo.STATE_UPSUCCEED);
                    info.setErrorMsg("Success");
                    if (end) {
                        submitAnswers(1, autoSubmit);
                    }
                } else {
                    info.setUploadState(QuestionInfo.STATE_UPFAILED);
                    info.setErrorMsg(response.getMessage());
                    XLog.e("答案上传失败" + response.getMessage());
                    if (end) {
                        LoadingDialog.lock = false;
                        LoadingDialog.stop();
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                info.setUploadState(QuestionInfo.STATE_UPFAILED);
                info.setErrorMsg(code + " : " + msg);
                XLog.e("答案上传失败" + msg);
                if (end) {
                    LoadingDialog.lock = false;
                    LoadingDialog.stop();
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }


    //交卷
    private void commitAnswer(boolean autoSubmit) {
        LoadingDialog.show(mContext, "正在交卷,请稍后");
        LoadingDialog.lock = true;
        saveTheUploadEnd(1, autoSubmit);
    }


    /**
     * @param status 0做题中 1交卷
     */
    private void submitAnswers(int status, boolean autoSubmit) {
        List<AnswerInfo.Answer> answerList = new ArrayList<>();
        for (QuestionInfo info : questionInfoList) {
            if (TextUtils.isEmpty(info.getStudentsAnswer())) continue;
            AnswerInfo.Answer data = new AnswerInfo.Answer();
            data.setAnswer(info.getStudentsAnswer());
            data.setQuestionId(info.getId());
            data.setQuestionTypeId(info.getQuestionTypeId());
            data.setError(info.uploadState() != QuestionInfo.STATE_UPSUCCEED);
            data.setErrorMsg(info.getErrorMsg());
            answerList.add(data);
        }

        AnswerInfo answerInfo = new AnswerInfo();
        answerInfo.setStu_Id(userId);
        answerInfo.setTaskId((int) taskInfo.getTaskId());
        answerInfo.setStatus(status);
        answerInfo.setExaminationPapersId((int) taskInfo.getPapersId());
        answerInfo.setExaminationPapersPushId((int) taskInfo.getPushId());
        answerInfo.setAnswerList(answerList);
        answerInfo.setVersion(PackageUtils.getVersionName(mContext));
        answerInfo.setSource(Build.MODEL);
        answerInfo.setCommon(autoSubmit ? "自动提交" : "手动提交的答案");

        AnswerRequest requestBody = new AnswerRequest();
        requestBody.setData(answerInfo);
        NetClient.createApi(NetApi.class).SubmitAnswers((int) taskInfo.getTaskId(),
                StringUtils.getUnicodeString(UtilGson.getInstance().convertObjectToJsonString(requestBody)),
                0, answer.submitMode()).enqueue(new Callback<BaseBean>() {
            @Override
            public void onResponse(Call<BaseBean> call, Response<BaseBean> response) {
                LoadingDialog.lock = false;
                if (status == 1) {
                    endWork();
                } else {
                    LoadingDialog.stop();
                }
            }

            @Override
            public void onFailure(Call<BaseBean> call, Throwable t) {
                LoadingDialog.lock = false;
                LoadingDialog.stop();
            }
        });

    }

    public void showCommitDialog() {
        int undoAnswer = undoQuestions();
        niftyDialog().setOnDismissListener(null);
        String promptStrFormat = "还有%s题未做完\n你确定要现在交卷吗？";
        niftyDialog().withMessage(undoAnswer == 0 ? "你确定要现在交卷吗？" : String.format(promptStrFormat, undoAnswer))
                .withButton1Text("继续做题").withButton2Text("现在交卷").withGravity(Gravity.CENTER).withEffect(Effectstype.Shake)
                .setButton2Click(view -> {
                    niftyDialog().dismiss();
                    commitAnswer(false);
                }).show();
    }

    /**
     * @return 学生未做的题目数量
     */
    private int undoQuestions() {
        int count = 0;
        for (int i = 0; i < questionInfoList.size(); i++) {
            QuestionInfo info = questionInfoList.get(i);
            if (currentItem == i) {
                count += isNullAnswerCard() ? 1 : 0;
            } else if (isNullAnswer(info)) {
                count++;
            }
        }
        return count;
    }

    //判断题是否未答
    private boolean isNullAnswer(QuestionInfo info) {
        JsonArray array = JSONS.parseJsonArray(info.getStudentsAnswer());
        if (array == null || array.size() == 0)
            return true;
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            if (!TextUtils.isEmpty(object.get("Answer").getAsString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断当前答题卡是否为空
     */
    private boolean isNullAnswerCard() {
        JsonArray array = JSONS.parseJsonArray(answer.answerCardView().getAnswer());
        if (array == null || array.size() == 0)
            return true;
        for (int i = 0; i < array.size(); i++) {
            JsonObject object = array.get(i).getAsJsonObject();
            if (!TextUtils.isEmpty(object.get("Answer").getAsString())) {
                return false;
            }
        }
        return true;
    }

    //结束答题
    private void endWork() {
        NetClient.createApi(NetApi.class).endWork((int) taskInfo.getTaskId()).enqueue(new NetCallBack<BaseBean>() {
            @Override
            public void onSucceed(BaseBean response) {
                if (response.isSuccess()) {
                    RxBus.get().send(Constant.BUS_WORKTASK_CHANGE);
                    RxBus.get().send(Constant.BUS_FINISH_PREVIEW);
                    stopTimeTick();//停止计时
                    submitTask.stopTimer();

                    //交卷后删除本地数据
                    String deleteSql = "DELETE FROM " + new DaoConfig(mDaoSession.getDatabase(), StudentAnswerDao.class).tablename
                            + " WHERE STUDENT_QUESTIONS_TASK_ID = " + taskInfo.getTaskId();
                    String deleteAttach = "DELETE FROM " + new DaoConfig(mDaoSession.getDatabase(), AttachmentDao.class).tablename
                            + " WHERE TASK_ID = " + taskInfo.getTaskId();
                    try {
                        mDaoSession.getDatabase().execSQL(deleteSql);
                        mDaoSession.getDatabase().execSQL(deleteAttach);
                    } catch (SQLException e) {
                        XLog.exception(e);
                    }

                    niftyDialog().withMessage("完成交卷,点击\"查看记录\"可查看本次做题记录")
                            .withButton1Text("不想看").withButton2Text("查看记录").withGravity(Gravity.CENTER)
                            .setButton2Click(v -> {
                                niftyDialog().dismiss();
                                String checkFormat = "HomeWork/CheckPaper?StudentQuestionsTasksId=%s";
                                String url = Constant.BASE_WEB_URL + String.format(checkFormat, taskInfo.getTaskId());
                                Intent intent = new Intent(mContext, WebActivity.class);
                                taskInfo.setStatus(TaskStatus.COMPLETE.getStatus());
                                intent.putExtra("title", taskInfo.getName());
                                intent.putExtra("url", url);
                                mContext.startActivity(intent);
                            }).show();
                    niftyDialog().setOnDismissListener(dialog -> {
                        mContext.finish();
                        RxBus.get().send(Constant.BUS_MESSAGE_REFRESH);
                    });

                } else {
                    Toast.show(response.getMessage());
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    /**
     * 翻页效果
     */
    private class AnswerPagerTransformer implements ViewPager.PageTransformer {
        private float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(@NonNull View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1) {
                view.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) { // (0,1]
                view.setAlpha(1 - position);
                view.setTranslationX(pageWidth * -position);
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
                        * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                view.setAlpha(0);

            }
        }
    }

    /**
     * 题目项适配器
     */
    @SuppressWarnings("OctalInteger")
    private class SubjectItemAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0612) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tips, parent, false)) {
                };
            }
            return new SubjectItemHolder(LayoutInflater.from(mContext).inflate(R.layout.item_subject, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0) return;
            position -= 1;
            SubjectItemHolder itemHolder = (SubjectItemHolder) holder;
            QuestionInfo info = questionInfoList.get(position);
            if (position == currentItem) {
                itemHolder.item.setS_solid_color(ContextCompat.getColor(mContext, R.color.home_state_green));
                itemHolder.item.setS_stroke_width(0);
                itemHolder.item.setTextColor(Color.WHITE);
            } else if (!isNullAnswer(info)) {
                itemHolder.item.setS_solid_color(ContextCompat.getColor(mContext, R.color.state_red));
                itemHolder.item.setTextColor(Color.WHITE);
                itemHolder.item.setS_stroke_width(0);
            } else {
                itemHolder.item.setS_stroke_width(DensityUtil.dip2px(1, mContext));
                itemHolder.item.setS_solid_color(ContextCompat.getColor(mContext, R.color.white));
                itemHolder.item.setTextColor(ContextCompat.getColor(mContext, R.color.color_text_default));
            }

            itemHolder.item.setText(String.valueOf((position + 1)));
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return 0612;
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return taskInfo.getQuestionCount() + 1;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                int spanCount = gridManager.getSpanCount();
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int type = getItemViewType(position);
                        if (type == 0612) {
                            return spanCount;
                        }
                        return 1;
                    }
                });
            }
        }
    }

    /**
     * 题holder
     */
    private class SubjectItemHolder extends RecyclerView.ViewHolder {
        SelectorButton item;

        private SubjectItemHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);

            item.setOnClickListener(view -> {
                answer.viewPager().setCurrentItem(getLayoutPosition() - 1);
                answer.closeBehavior();
            });
        }
    }
}
