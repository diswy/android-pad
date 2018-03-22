package com.cqebd.student.vo.entity;

/**
 * document
 * Created by Gordn on 2017/3/8.
 */

public class BaseBean {

    protected int errorId;
    protected String message;
    protected boolean isSuccess;

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
}
