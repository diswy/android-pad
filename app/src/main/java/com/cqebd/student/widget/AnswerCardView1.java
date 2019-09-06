package com.cqebd.student.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;


import com.cqebd.student.R;
import com.cqebd.student.adapter.CardFragmentAdapter;
import com.cqebd.student.app.App;
import com.cqebd.student.app.BaseActivity;
import com.cqebd.student.app.BaseFragment;
import com.cqebd.student.tools.AlbumHelper;
import com.cqebd.student.tools.CollectionUtils;
import com.cqebd.student.tools.KToastKt;
import com.cqebd.student.tools.UtilGson;
import com.cqebd.student.ui.card.DecideFragment;
import com.cqebd.student.ui.card.EditRichFragment;
import com.cqebd.student.ui.card.EditSimpleFragment;
import com.cqebd.student.ui.card.EnTaiAnswerFragment;
import com.cqebd.student.ui.card.MultiFragment;
import com.cqebd.student.ui.card.SingleFragment;
import com.cqebd.student.vo.entity.AlternativeContent;
import com.cqebd.student.vo.entity.AnswerType;
import com.cqebd.student.vo.entity.QuestionInfo;
import com.cqebd.student.vo.entity.StudentAnswer;
import com.cqebd.student.vo.enums.CardType;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gorden.util.DensityUtil;

/**
 * document
 * Created by Gordn on 2017/3/20.
 */

public class AnswerCardView1 extends LinearLayout {
    /**
     * consts
     */
    final int TYPE_SINGLE = 1;
    final int TYPE_MULTI = 2;
    final int TYPE_EDIT = 3;
    final int TYPE_RICH = 4;
    final int TYPE_DECIDE = 5;
    final int TYPE_NONE = 6;

    public static final int TYPE_EN_WORD = 32;
    public static final int TYPE_EN_SENTENCE = 33;
    public static final int TYPE_EN_PARAGRAPH = 34;
    public static final int TYPE_EN_FREE = 35;

    int SubjectTypeId = 0;

    ViewPager pager;
    PagerIndicator indicator;
    private BaseActivity mActivity;

    List<Fragment> cards = new ArrayList<>();
    private PagerAdapter mAdapter;

    private QuestionInfo mQuestionInfo;

    private AlbumHelper.AlbumCallBack albumCallBack;

    /**
     * data
     */
    List<AlternativeContent> alternativeContent = new ArrayList<>();
    List<StudentAnswer> studentAnswers = new ArrayList<>();
    String subjectContent = "";// 口语评测专用
    Map<Integer, Map<String, String>> imageMap = new HashMap<>();
    Map<String, StudentAnswer> answerMap = new HashMap<>();
    List<AnswerType> answerTypes = new ArrayList<>();

    public AnswerCardView1(Context context) {
        this(context, null);
    }

    public AnswerCardView1(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    //初始化UI
    private void init(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        setPadding(0, dp2px(10), 0, dp2px(10));
        pager = new ViewPager(context);
        pager.setPadding(dp2px(10), 0, dp2px(10), 0);
        pager.setId(R.id.btn_login);
        addView(pager, new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(150)));

        indicator = new PagerIndicator(context);
        indicator.setmRadiu(dp2px(8));
        indicator.setSelectColor(ContextCompat.getColor(context, R.color.btn_green_press));
        indicator.setUnSelectColor(ContextCompat.getColor(context, R.color.btn_green));
        addView(indicator, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        mActivity = (BaseActivity) context;
        isInEditMode();
    }

    //加载答题卡
    public void loadAnswerCard(QuestionInfo questionInfo, int subjectTypeId, AlbumHelper.AlbumCallBack albumCallBack) {
        this.albumCallBack = albumCallBack;
        mQuestionInfo = questionInfo;
        this.SubjectTypeId = subjectTypeId;
        if (mQuestionInfo == null) {
            KToastKt.toast("答题卡加载失败");
            return;
        } else {
            alternativeContent.clear();
            studentAnswers.clear();
            cards.clear();
            answerMap.clear();
        }
        if (imageMap.get(mQuestionInfo.getId()) == null) {
            imageMap.put(mQuestionInfo.getId(), new HashMap<>());
        }
        if (!TextUtils.isEmpty(questionInfo.getAlternativeContent())) {
            Type alterType = new TypeToken<ArrayList<AlternativeContent>>() {
            }.getType();
            alternativeContent = (List<AlternativeContent>) UtilGson.getInstance().convertJsonStringToList(questionInfo.getAlternativeContent(), alterType);
        }
        if (!TextUtils.isEmpty(questionInfo.getStudentsAnswer())) {
            Type studentType = new TypeToken<ArrayList<StudentAnswer>>() {
            }.getType();
            studentAnswers = (List<StudentAnswer>) UtilGson.getInstance().convertJsonStringToList(questionInfo.getStudentsAnswer(), studentType);
        }
        Type type = new TypeToken<ArrayList<AnswerType>>() {
        }.getType();
        answerTypes = (List<AnswerType>) UtilGson.getInstance().convertJsonStringToList(questionInfo.getAnswerType(), type);
        subjectContent = questionInfo.getSubject();
        Logger.json(questionInfo.getAnswerType());
        Logger.d("count = " + getCount());
        initAnswerMap();
        buildCardFragments();

        mAdapter = new CardFragmentAdapter(mActivity.getSupportFragmentManager(), cards);
        pager.setAdapter(mAdapter);
        indicator.setupWithViewPager(pager);
    }

    private void initAnswerMap() {
        for (AnswerType answerType : answerTypes) {
            StudentAnswer answer = new StudentAnswer();
            answer.Id = answerType.getId();
            answer.TypeId = answerType.getTypeId();
            answerMap.put(answer.Id, answer);
        }
        for (StudentAnswer studentAnswer : studentAnswers) {
            answerMap.put(studentAnswer.Id, studentAnswer);
        }
    }

    private void buildCardFragments() {
        int height = 60;
        for (int i = 0; i < getCount(); i++) {
            cards.add(getViewFragment(i));
            if (getItemViewType(0) != TYPE_SINGLE) {
                height = 150;
            }
        }
        pager.getLayoutParams().height = dp2px(height);
    }

    public int getCount() {
        return answerTypes.size();
    }


    public int getItemViewType(int position) {
        if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.SINGLE_CHOOSE,
                CardType.LISTEN_SINGLE_CHOOSE
        )) {
            /**
             * 单选
             */
            return TYPE_SINGLE;
        } else if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.DECIDE)) {
            /**
             * 判断
             * */
            return TYPE_DECIDE;
        } else if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.MULTI_CHOOSE)) {
            /**
             * 多选
             */
            return TYPE_MULTI;
        } else if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.PACK,
                CardType.LISTEN_PACK)) {
            /**
             * 填空
             */
            /*除了英语填空，其他都需要拍照*/
            if (SubjectTypeId != 12 && mQuestionInfo.getWriteType() != 1) {
                return TYPE_RICH;
            }
            return TYPE_EDIT;
        } else if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.EXPLAIN)) {
            /**
             * 解答
             */
            return TYPE_RICH;
        } else if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.EN_WORD)) {
            return TYPE_EN_WORD;
        } else if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.EN_SENTENCE)) {
            // 口语评测 句子
            return TYPE_EN_SENTENCE;
        } else if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.EN_PARAGRAPH)) {
            // 口语评测 段落
            return TYPE_EN_PARAGRAPH;
        } else if (CollectionUtils.isIn(
                CardType.getType(answerTypes.get(position).getTypeId()),
                CardType.EN_FREE)) {
            // 口语评测 句子
            return TYPE_EN_FREE;
        }
        return TYPE_NONE;
    }

    public Fragment getViewFragment(int position) {
        switch (getItemViewType(position)) {
            case TYPE_SINGLE:
                return buildSingle(position);
            case TYPE_MULTI:
                return buildMulti(position);
            case TYPE_EDIT:
                return buildEditSimple(position);
            case TYPE_RICH:
                return buildEditRich(position);
            case TYPE_DECIDE:
                return buildDecide(position);
            case TYPE_EN_WORD:
                return buildEnTai(position, TYPE_EN_WORD);
            case TYPE_EN_SENTENCE:
                return buildEnTai(position, TYPE_EN_SENTENCE);
            case TYPE_EN_PARAGRAPH:// 段落
                return buildEnTai(position, TYPE_EN_PARAGRAPH);
            case TYPE_EN_FREE:// 自由
                return buildEnTai(position, TYPE_EN_FREE);
        }
        return null;
    }

    private Fragment buildEnTai(int position, int type) {
        StudentAnswer studentAnswer = answerMap.get(answerTypes.get(position).getId());
        EnTaiAnswerFragment enTaiAnswerFragment = new EnTaiAnswerFragment();
        enTaiAnswerFragment.setLifeListener(new BaseFragment.ViewLifeListener() {
            @Override
            public void onInitialized() {
                enTaiAnswerFragment.setDataChangeListener(answer -> answerMap.put(answer.Id, answer));
                enTaiAnswerFragment.build(answerTypes.get(position), type, studentAnswer, subjectContent);
            }
        });
        return enTaiAnswerFragment;
    }

    private Fragment buildSingle(int position) {
        StudentAnswer studentAnswer = answerMap.get(answerTypes.get(position).getId());
        SingleFragment singleFragment = new SingleFragment();
        singleFragment.setLifeListener(new BaseFragment.ViewLifeListener() {
            @Override
            public void onInitialized() {
                singleFragment.setDataChangeListener(studentAnswer -> answerMap.put(studentAnswer.Id, studentAnswer));
                singleFragment.build(answerTypes.get(position), alternativeContent, studentAnswer);

            }
        });
        return singleFragment;
    }

    private Fragment buildMulti(int position) {
        StudentAnswer studentAnswer = answerMap.get(answerTypes.get(position).getId());
        MultiFragment multiView = new MultiFragment();
        multiView.setLifeListener(new BaseFragment.ViewLifeListener() {
            @Override
            public void onInitialized() {
                multiView.setDataChangeListener(studentAnswer -> answerMap.put(studentAnswer.Id, studentAnswer));
                multiView.build(answerTypes.get(position), alternativeContent, studentAnswer);
            }
        });
        return multiView;
    }

    private Fragment buildEditSimple(int position) {
        StudentAnswer studentAnswer = answerMap.get(answerTypes.get(position).getId());
        EditSimpleFragment editSimpleView = new EditSimpleFragment();
        editSimpleView.setLifeListener(new BaseFragment.ViewLifeListener() {
            @Override
            public void onInitialized() {
                editSimpleView.setDataChangeListener(studentAnswer -> answerMap.put(studentAnswer.Id, studentAnswer));
                editSimpleView.build(answerTypes.get(position), studentAnswer);
            }
        });
        return editSimpleView;
    }

    private Fragment buildEditRich(int position) {
        StudentAnswer studentAnswer = answerMap.get(answerTypes.get(position).getId());
        EditRichFragment editRichView = new EditRichFragment();
        editRichView.setAlbumCallBack(albumCallBack);
        editRichView.setLifeListener(() -> {
            editRichView.setDataChangeListener(studentAnswer1 -> answerMap.put(studentAnswer1.Id, studentAnswer1));
            editRichView.build(answerTypes.get(position), studentAnswer, imageMap.get(mQuestionInfo.getId()));
        });
        return editRichView;
    }

    private Fragment buildDecide(int position) {
        StudentAnswer studentAnswer = answerMap.get(answerTypes.get(position).getId());
        DecideFragment decideView = new DecideFragment();
        decideView.setLifeListener(new BaseFragment.ViewLifeListener() {
            @Override
            public void onInitialized() {
                decideView.setDataChangeListener(studentAnswer -> answerMap.put(studentAnswer.Id, studentAnswer));
                decideView.build(answerTypes.get(position), studentAnswer);
            }
        });
        return decideView;
    }

    /**
     * 旧逻辑漏洞，单独修改图片不重新提交修复
     */
    private boolean isImgChange = false;

    public void setImgChanged() {
        this.isImgChange = true;
    }

    /**
     * 答案是否变换
     *
     * @return
     */
    public boolean isChanged() {
        if (isImgChange) {
            isImgChange = false;
            return true;
        }
        if (mQuestionInfo == null) return false;
        List<StudentAnswer> oldAnswers = new ArrayList<>();
        Map<String, StudentAnswer> oldAnswerMap = new HashMap<>();
        if (!TextUtils.isEmpty(mQuestionInfo.getStudentsAnswer())) {
            Type studentType = new TypeToken<ArrayList<StudentAnswer>>() {
            }.getType();
            oldAnswers = (List<StudentAnswer>) UtilGson.getInstance().convertJsonStringToList(mQuestionInfo.getStudentsAnswer(), studentType);
        }
        for (StudentAnswer studentAnswer : oldAnswers) {
            oldAnswerMap.put(studentAnswer.Id, studentAnswer);
        }
        for (AnswerType type : answerTypes) {
            //不为空的改变 返回true
            StudentAnswer studentAnswer = answerMap.get(type.getId());
            StudentAnswer oldStudentAnswer = oldAnswerMap.get(type.getId());
            if (studentAnswer != null) {
                if (TextUtils.isEmpty(studentAnswer.Answer.trim())) {
//                    if (oldStudentAnswer!=null&&!TextUtils.isEmpty(oldStudentAnswer.Answer.trim())){
//                        return true;
//                    }
                    continue;
                }
                if (oldStudentAnswer == null) {
                    return true;
                }
                if (!studentAnswer.Answer.trim().equals(oldStudentAnswer.Answer.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 答案内容
     *
     * @return
     */
    public String getAnswer() {
        Set<String> keys = answerMap.keySet();
        if (keys.size() == 0) return null;
        List<StudentAnswer> answerList = new ArrayList<>();
        for (String key : keys) {
            answerList.add(answerMap.get(key));
        }
        return UtilGson.getInstance().convertObjectToJsonString(answerList);
    }

    private DensityUtil densityUtil = new DensityUtil(App.mContext);

    private int dp2px(int dp) {
        return densityUtil.dip2px(dp);
    }

    public void hideSoftKeyBord() {
        for (Fragment card : cards) {
            if (card instanceof EditRichFragment) {
                ((EditRichFragment) card).hideSoftKeyBord();
            }
            if (card instanceof EditSimpleFragment) {
                ((EditSimpleFragment) card).hideSoftKeyBord();
            }
        }
    }
}
