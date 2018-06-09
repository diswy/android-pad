package com.cqebd.student.live.helper

import com.google.gson.Gson
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.netease.nimlib.sdk.msg.model.CustomNotificationConfig
import com.orhanobut.logger.Logger

/**
 * kotlin 单例模式
 * 网易云消息管理工具
 *
 * Created by xiaofu on 2018/5/30.
 */
class MsgManager private constructor() {

    companion object {
        fun instance() = Inner.singleInstance
    }

    private object Inner {
        val singleInstance = MsgManager()
    }

    /**
     * 开启自定义消息监听
     */
    fun init() {
        // PS：目前在APP全局初始化，就不需要取消监听，应该尝试在进入聊天室才初始化，离开时注销
        NIMClient.getService(MsgServiceObserve::class.java).observeCustomNotification(customNotification, true)
    }

    // 发送点对点不推送不支持离线的自定义系统通知
    fun sendP2PCustomNotification(account: String, mNotification: com.cqebd.student.live.entity.EbdCustomNotification) {
        val notification = CustomNotification()
        notification.sessionId = account// 指定接收者
        notification.sessionType = SessionTypeEnum.P2P
        val config = CustomNotificationConfig()
        config.enablePush = false// 不推送
        notification.config = config
        notification.isSendToOnlineUserOnly = true// 不支持离线

        notification.content = Gson().toJson(mNotification)
        Logger.wtf(notification.content)
        // 发送自定义通知
        NIMClient.getService(MsgService::class.java).sendCustomNotification(notification)
    }

    // 群发自定义消息
    fun sendCustomMsg(roomId: String) {
//        val message = ChatRoomMessageBuilder.createChatRoomCustomMessage(roomId,)
//        NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
    }


    //----------------监听----------------
    private var documentObserver: DocumentObserver? = null


    /**
     * 自定义通知消息
     */
    private val customNotification = Observer<CustomNotification> { customNotification ->
        val content = customNotification.content
        Logger.e(content)
    }

    interface DocumentObserver {
        fun onDocStart()
        fun onDocEnd()
        fun onDocNextPage()
        fun onDocPreviousPage()
    }

    fun registerDocumentObserver(o: DocumentObserver?, register: Boolean) {
        if (register) {
            this.documentObserver = o
        }
    }
}