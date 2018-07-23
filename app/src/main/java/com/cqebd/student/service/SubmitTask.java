package com.cqebd.student.service;

import android.os.Build;
import android.support.v4.util.SparseArrayCompat;


import com.cqebd.student.app.App;
import com.cqebd.student.http.NetApi;
import com.cqebd.student.http.NetCallBack;
import com.cqebd.student.http.NetClient;
import com.cqebd.student.tools.KResKt;
import com.cqebd.student.tools.StringUtils;
import com.cqebd.student.tools.TimeConversion;
import com.cqebd.student.vo.entity.BaseBean;
import com.cqebd.student.vo.entity.QuestionInfo;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import gorden.util.PackageUtils;
import gorden.util.XLog;

/**
 * 提交答案服务
 */

public class SubmitTask {
    private static final String TAG = "Submit_Task";

    private int userId;
    private String Version;
    private String Source;
    private int ExaminationPapersId;
    private int StudentQuestionsTasksId;

    //定时任务,每隔1分钟检测是否有未成功提交的答案
    private Timer submitTimer;
    private SubmitTimerTask submitTask;
    private boolean timerStart = false;
    //同步锁
    private Lock mLock = new ReentrantLock();
    //保存执行中Question(包含提交失败，和正在提交的)
    private SparseArrayCompat<QuestionInfo> submitCallList = new SparseArrayCompat<>();

    public SubmitTask(int examinationPapersId, int studentQuestionsTasksId) {
        ExaminationPapersId = examinationPapersId;
        StudentQuestionsTasksId = studentQuestionsTasksId;
        userId = (int) KResKt.getLoginId();
        Version = PackageUtils.getVersionName(App.mContext);
        Source = Build.MODEL;
    }


    /**
     * 提交答案
     *
     */
    public void submit(QuestionInfo info) {
        mLock.lock();
        XLog.e(TAG, "submitTask  " + info.getId());
        info.setUploadState(QuestionInfo.STATE_UPLOADING);
        info.setErrorMsg("正在提交答案");
        submitCallList.put(info.getId(), info);
        mLock.unlock();

        NetClient.createApi(NetApi.class)
                .submitAnswer(userId, info.getId(), ExaminationPapersId, StudentQuestionsTasksId, StringUtils.getUnicodeString(info.getStudentsAnswer()),
                        info.getQuestionTypeId(), Version, Source, TimeConversion.getFullDate(System.currentTimeMillis())).enqueue(new SubmitCallBack(info));
    }


    private void startTimer() {
        if (submitTimer == null) {
            submitTimer = new Timer();
        }
        if (submitTask == null) {
            submitTask = new SubmitTimerTask();
        }

        if (!timerStart) {
            timerStart = true;
            //每20秒查看是否有未提交的答案
            submitTimer.schedule(submitTask, 1000 * 10, 1000 * 20);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void stopTimer() {
        if (submitTask != null) {
            submitTask.cancel();
            submitTask = null;
            timerStart = false;
        }
    }

    private class SubmitCallBack extends NetCallBack<BaseBean> {
        QuestionInfo questionInfo;

        private SubmitCallBack(QuestionInfo questionInfo) {
            this.questionInfo = questionInfo;
        }

        @Override
        public void onSucceed(BaseBean response) {
            if (response.isSuccess()) {
                XLog.e(TAG, "成功提交作业 QuestionId = " + questionInfo.getId());
                questionInfo.setUploadState(QuestionInfo.STATE_UPSUCCEED);
                questionInfo.setErrorMsg("Success");
                mLock.lock();
                submitCallList.remove(questionInfo.getId());
                mLock.unlock();
            } else {
                questionInfo.setUploadState(QuestionInfo.STATE_UPFAILED);
                questionInfo.setErrorMsg(response.getMessage());
                startTimer();
                XLog.e(TAG, "提交作业失败 QuestionId = " + questionInfo.getId() + "   error: " + response.getMessage());
            }
        }

        @Override
        public void onFailure() {
        }

        @Override
        public void onFailure(int code, String msg) {
            questionInfo.setUploadState(QuestionInfo.STATE_UPFAILED);
            questionInfo.setErrorMsg(code + " : " + msg);
            startTimer();
            XLog.e(TAG, "提交作业失败 QuestionId = " + questionInfo.getId() + "   errorcode: " + code + " : " + msg);
        }
    }

    private class SubmitTimerTask extends TimerTask {
        @Override
        public void run() {
            XLog.e(TAG, "定时任务启动  未完成的数量" + submitCallList.size());

            if (submitCallList.size() == 0) {
                stopTimer();
                return;
            }

            mLock.lock();
            for (int i = 0; i < submitCallList.size(); i++) {
                QuestionInfo info = submitCallList.get(submitCallList.keyAt(i));
                if (info.uploadState() == QuestionInfo.STATE_UPFAILED) {
                    XLog.e(TAG, "执行任务,尝试重新提交答案: " + info.getId());
                    submit(info);
                }
            }
            mLock.unlock();
        }
    }
}
