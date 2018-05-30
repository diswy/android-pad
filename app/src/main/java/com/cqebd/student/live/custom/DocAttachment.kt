package com.cqebd.student.live.custom

class DocAttachment : CustomAttachment(CustomAttachmentType.Doc) {

    var mPPTAddress: String = ""

    override fun parseData(data: String) {
        mPPTAddress = data
    }

    override fun packData(): String {
        return mPPTAddress
    }
}