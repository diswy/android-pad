package com.cqebd.student.live.entity


data class LiveByRemote(
    val Id: Int,
    val Serial: Any,
    val PeriodId: Int,
    val CreateDateTime: String,
    val Status: Any,
    val Type: Any,
    val ChannelID: Any,
    val ChannelName: String,
    val ChannelPushUrl: String,
    val ChannelPullUrls: String,
    val ChannelInfo: Any,
    val ChatRoomId: String,
    val ChatRoomName: String,
    val ChatRoomInfo: Any,
    val VchatRoomName: String,
    val VchatRoomInfo: String,
    val IWBRoomName: String,
    val IWBRoomInfo: String,
    val Remarks: Any
)