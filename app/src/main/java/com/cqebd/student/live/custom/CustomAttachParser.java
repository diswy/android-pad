package com.cqebd.student.live.custom;

import com.cqebd.student.live.entity.CustomMsg;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;
import com.orhanobut.logger.Logger;

/**
 * Created by xiaofu on 2018/5/30.
 */
public class CustomAttachParser implements MsgAttachmentParser {

    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            Gson gson = new Gson();
            CustomMsg msg = gson.fromJson(json, CustomMsg.class);
            switch (msg.getName()) {
                case "PPT":
                    attachment = new DocAttachment();
                    break;
//                default:
//                    attachment = new DefaultCustomAttachment();
//                    break;
            }

            if (attachment != null) {
                attachment.fromJson(msg.getContent());
            }

        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return attachment;
    }
}
