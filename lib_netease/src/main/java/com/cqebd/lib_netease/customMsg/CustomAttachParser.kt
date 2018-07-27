package com.cqebd.lib_netease.customMsg

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser

class CustomAttachParser : MsgAttachmentParser {

    override fun parse(json: String): MsgAttachment {
        lateinit var attachment: CustomAttachment
//        var attachment:CustomAttachment? = null

        attachment.fromJson(json)

        return attachment
    }
}