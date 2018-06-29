package com.cqebd.student.live.custom;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

/**
 * Created by xiaofu on 2018/5/30.
 */
public class CustomAttachParser implements MsgAttachmentParser {

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = new NormalAttachment();
        attachment.fromJson(json);
        return attachment;
    }
}
