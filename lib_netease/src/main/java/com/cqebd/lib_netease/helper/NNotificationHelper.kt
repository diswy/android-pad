package com.cqebd.lib_netease.helper

import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig

class NNotificationHelper private constructor(){
    companion object {
        fun instance() = Inner.singleInstance
    }

    private object Inner {
        val singleInstance = NNotificationHelper()
    }

    /**
     * 发送点对点不推送不支持离线的自定义系统通知
     */
    fun sendP2PCustomNotification(account: String){
        val notification = CustomNotification()
        notification.sessionId = account// 指定接收者
        notification.sessionType = SessionTypeEnum.P2P
        val config = CustomNotificationConfig()
        config.enablePush = false// 不推送
        notification.config = config
        notification.isSendToOnlineUserOnly = true// 不支持离线


        NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
    }

}