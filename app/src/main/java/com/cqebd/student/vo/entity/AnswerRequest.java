package com.cqebd.student.vo.entity;

/**
 * document
 * Created by Gordn on 2017/3/28.
 */

public class AnswerRequest {
    private String name = "answer";
    private double version = 1.1;
    private AnswerInfo data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public AnswerInfo getData() {
        return data;
    }

    public void setData(AnswerInfo data) {
        this.data = data;
    }
}
