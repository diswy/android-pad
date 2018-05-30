package com.cqebd.student.live.custom;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

/**
 * Created by xiaofu on 2018/5/30.
 */
public abstract class CustomAttachment implements MsgAttachment {

    protected int type;

    CustomAttachment(int type) {
        this.type = type;
    }

    public void fromJson(String data) {
        if (data != null) {
            parseData(data);
        }
    }

    @Override
    public String toJson(boolean send) {
        return packData();
    }

    public int getType() {
        return type;
    }

    protected abstract void parseData(String data);

    protected abstract String packData();
}
