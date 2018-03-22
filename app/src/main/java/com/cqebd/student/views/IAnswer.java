package com.cqebd.student.views;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;

import com.cqebd.student.db.dao.Attachment;
import com.cqebd.student.widget.AnswerCardView1;


/**
 * document
 * Created by Gordn on 2017/3/17.
 */

public interface IAnswer {
    ViewPager viewPager();

    RecyclerView recyclerView();

    AnswerCardView1 answerCardView();

    void setTaskName(String name);

    void setLoacation(String location);

    void setCountDown(String time);

    void closeBehavior();

    /**
     * 答题时间结束,或不满足答题条件
     * 用户不能答题
     */
    void prohibitAnswer();

    /**
     * 允许用户答题
     */
    void allowAnswer();

    void tipMessage(String msg);

    void audioInfo(Attachment attachment);

    /**
     * 提交模式
     * 0 每个题提交 最后打包再提交
     * 1 最后打包提交
     * 返回的时候答案都要打包提交一次
     */
    int submitMode();
}
