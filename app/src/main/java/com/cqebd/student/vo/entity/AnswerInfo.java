package com.cqebd.student.vo.entity;

import java.util.List;

/**
 * document
 * Created by Gordn on 2017/3/21.
 */

public class AnswerInfo {
    private int Stu_Id;
    private int TaskId;
    private int Status;//-1 默认,0做题中,1交卷
    private int ExaminationPapersPushId;
    private int ExaminationPapersId;
    private String Version;
    private String Common;
    private String Source;
    private List<Answer> AnswerList;

    public int getStu_Id() {
        return Stu_Id;
    }

    public void setStu_Id(int stu_Id) {
        Stu_Id = stu_Id;
    }

    public int getTaskId() {
        return TaskId;
    }

    public void setTaskId(int taskId) {
        TaskId = taskId;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getExaminationPapersPushId() {
        return ExaminationPapersPushId;
    }

    public void setExaminationPapersPushId(int examinationPapersPushId) {
        ExaminationPapersPushId = examinationPapersPushId;
    }

    public int getExaminationPapersId() {
        return ExaminationPapersId;
    }

    public void setExaminationPapersId(int examinationPapersId) {
        ExaminationPapersId = examinationPapersId;
    }

    public List<Answer> getAnswerList() {
        return AnswerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.AnswerList = answerList;
    }

    public String getVersion() {
        return Version;
    }

    public void setVersion(String version) {
        Version = version;
    }

    public String getCommon() {
        return Common;
    }

    public void setCommon(String common) {
        Common = common;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public static class Answer {
        private int QuestionId;
        private int QuestionTypeId;
        private String Answer;
        private boolean IsError;
        private String ErrorMsg="";

        public int getQuestionId() {
            return QuestionId;
        }

        public void setQuestionId(int questionId) {
            QuestionId = questionId;
        }

        public int getQuestionTypeId() {
            return QuestionTypeId;
        }

        public void setQuestionTypeId(int questionTypeId) {
            QuestionTypeId = questionTypeId;
        }

        public String getAnswer() {
            return Answer;
        }

        public void setAnswer(String answer) {
            Answer = answer;
        }

        public boolean isError() {
            return IsError;
        }

        public void setError(boolean error) {
            IsError = error;
        }

        public String getErrorMsg() {
            return ErrorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            ErrorMsg = errorMsg;
        }
    }
}
