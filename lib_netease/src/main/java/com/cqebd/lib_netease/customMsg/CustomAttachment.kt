package com.cqebd.lib_netease.customMsg

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment


abstract class CustomAttachment : MsgAttachment {

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息
//    protected var type: Int = 0

    // 解析附件内容。
    fun fromJson(data: String) {
        parseData(data)
    }

    override fun toJson(send: Boolean): String {
        return packData()
    }

    // 子类的解析和封装接口。
    protected abstract fun parseData(data: String)

    protected abstract fun packData():String

}