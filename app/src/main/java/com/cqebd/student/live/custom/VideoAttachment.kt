package com.cqebd.student.live.custom

class VideoAttachment: CustomAttachment(CustomAttachmentType.Video) {

    var hasPermissionList: String = ""

    override fun parseData(data: String) {
        hasPermissionList = data
    }

    override fun packData(): String {
        return hasPermissionList
    }
}