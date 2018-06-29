package com.cqebd.student.live.custom

import com.cqebd.student.live.entity.CustomMsg
import com.google.gson.Gson

class NormalAttachment : CustomAttachment(CustomAttachmentType.Normal) {

    var mCustomMsg: CustomMsg? = null

    override fun parseData(data: String) {
        try {
            mCustomMsg = Gson().fromJson(data, CustomMsg::class.java)
        } catch (e: Exception) {

        }
    }

    override fun packData(): String {
        return if (mCustomMsg != null)
            Gson().toJson(mCustomMsg)
        else
            "data parse error!"
    }
}