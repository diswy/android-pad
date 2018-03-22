package com.cqebd.student.db.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 描述
 * Created by gorden on 2017/12/4.
 */
@Entity
public class Attachment {
    @Id
    private String id;//task+id
    private int taskId;
    private String url;
    private String name;
    private int watchCount;//当前观看次数
    private int AnswerType;//1播放中答、2是必须播放完后可答
    private int canWatchCount;//最多观看次数，0无限制 >0至少看1次，最多看count次
    @Generated(hash = 196945791)
    public Attachment(String id, int taskId, String url, String name,
            int watchCount, int AnswerType, int canWatchCount) {
        this.id = id;
        this.taskId = taskId;
        this.url = url;
        this.name = name;
        this.watchCount = watchCount;
        this.AnswerType = AnswerType;
        this.canWatchCount = canWatchCount;
    }
    @Generated(hash = 1924760169)
    public Attachment() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getTaskId() {
        return this.taskId;
    }
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getWatchCount() {
        return this.watchCount;
    }
    public void setWatchCount(int watchCount) {
        this.watchCount = watchCount;
    }
    public int getAnswerType() {
        return this.AnswerType;
    }
    public void setAnswerType(int AnswerType) {
        this.AnswerType = AnswerType;
    }
    public int getCanWatchCount() {
        return this.canWatchCount;
    }
    public void setCanWatchCount(int canWatchCount) {
        this.canWatchCount = canWatchCount;
    }

}
