package com.cqebd.student.db.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

/**
 * document
 * Created by Gordn on 2017/3/28.
 */
@Entity
public class StudentAnswer {
    @Id
    private String id;
    @NotNull
    private long studentQuestionsTaskId;
    private String Name;//试卷名
    private int SubjectTypeId;
    private String SubjectTypeName;//试卷科目
    @NotNull
    private long QuestionId;
    private String StudentsAnswer;
    @Generated(hash = 872532707)
    public StudentAnswer(String id, long studentQuestionsTaskId, String Name,
                         int SubjectTypeId, String SubjectTypeName, long QuestionId,
                         String StudentsAnswer) {
        this.id = id;
        this.studentQuestionsTaskId = studentQuestionsTaskId;
        this.Name = Name;
        this.SubjectTypeId = SubjectTypeId;
        this.SubjectTypeName = SubjectTypeName;
        this.QuestionId = QuestionId;
        this.StudentsAnswer = StudentsAnswer;
    }
    @Generated(hash = 504735348)
    public StudentAnswer() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public long getStudentQuestionsTaskId() {
        return this.studentQuestionsTaskId;
    }
    public void setStudentQuestionsTaskId(long studentQuestionsTaskId) {
        this.studentQuestionsTaskId = studentQuestionsTaskId;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public int getSubjectTypeId() {
        return this.SubjectTypeId;
    }
    public void setSubjectTypeId(int SubjectTypeId) {
        this.SubjectTypeId = SubjectTypeId;
    }
    public String getSubjectTypeName() {
        return this.SubjectTypeName;
    }
    public void setSubjectTypeName(String SubjectTypeName) {
        this.SubjectTypeName = SubjectTypeName;
    }
    public long getQuestionId() {
        return this.QuestionId;
    }
    public void setQuestionId(long QuestionId) {
        this.QuestionId = QuestionId;
    }
    public String getStudentsAnswer() {
        return this.StudentsAnswer;
    }
    public void setStudentsAnswer(String StudentsAnswer) {
        this.StudentsAnswer = StudentsAnswer;
    }
}
