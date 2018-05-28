package com.cqebd.student.live


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.glide.GlideApp
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver
import com.netease.nimlib.sdk.chatroom.model.*
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_chat_room2.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.support.v4.toast
import java.io.File


/**
 * 聊天室.
 *
 */
class ChatRoomFragment : BaseFragment() {
    private val mAdapter = ChatRoomMultipleAdapter(null)
    /**
     * 聊天室基本信息
     */
    private var roomId: String? = null
    private var roomInfo: ChatRoomInfo? = null

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_chat_room2, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        parseIntent()
        registerObservers(true)
        // 登录聊天室
        enterRoom()
        mChatRoomRv.adapter = mAdapter
    }

    override fun onDestroy() {
        registerObservers(false)
        super.onDestroy()
    }

    override fun bindEvents() {
        mSwitchInputType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.backgroundResource = R.color.color_schedule
            } else {
                buttonView.backgroundResource = R.color.color_price
            }
        }

        mBtnSend.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.backgroundResource = R.color.color_schedule
            } else {
                buttonView.backgroundResource = R.color.color_price
            }
        }
    }

    private fun parseIntent() {
//        roomId = arguments?.getString(EXTRA_ROOM_ID)
        roomId = "25154773"
    }


    private var enterRequest: AbortableFuture<EnterChatRoomResultData>? = null
    private fun enterRoom() {
        val data = EnterChatRoomData(roomId)
        enterRequest = NIMClient.getService(ChatRoomService::class.java).enterChatRoom(data)
        enterRequest?.setCallback(object : RequestCallback<EnterChatRoomResultData> {
            override fun onSuccess(result: EnterChatRoomResultData) {
                enterRequest = null
                roomInfo = result.roomInfo
                val member = result.member
                member.roomId = roomInfo?.roomId
                //ChatRoomMemberCache.getInstance().saveMyMember(member);
                //creator = roomInfo.getCreator()
            }

            override fun onFailed(code: Int) {
                enterRequest = null
                when (code) {
                    ResponseCode.RES_CHATROOM_BLACKLIST.toInt() -> toast("你已被拉入黑名单，不能再进入")
                    ResponseCode.RES_ENONEXIST.toInt() -> toast("该聊天室不存在")
                    else -> toast("enter chat room failed, code = $code")
                }
            }

            override fun onException(exception: Throwable) {
                enterRequest = null
                toast("enter chat room exception,$exception.message")
            }
        })
    }

    private fun registerObservers(register: Boolean) {
        NIMClient.getService(ChatRoomServiceObserver::class.java).observeOnlineStatus(onlineStatus, register)
        NIMClient.getService(ChatRoomServiceObserver::class.java).observeReceiveMessage(incomingChatRoomMsg, register)
        NIMClient.getService(MsgServiceObserve::class.java).observeMsgStatus(statusObserver, register)
//        NIMClient.getService(ChatRoomServiceObserver::class.java).observeKickOutEvent(kickOutObserver, register)
//        NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus(userStatusObserver, register)
    }

    // 状态
    private val onlineStatus: Observer<ChatRoomStatusChangeData> = Observer { chatRoomStatusChangeData ->
        if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
            // DialogMaker.updateLoadingMessage("连接中...");
        } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
            if (NIMClient.getService(ChatRoomService::class.java).getEnterErrorCode(roomId) == ResponseCode.RES_CHATROOM_STATUS_EXCEPTION.toInt()) {
                // 聊天室连接状态异常
                // Toast.makeText(ChatRoomActivity.this, R.string.chatroom_status_exception, Toast.LENGTH_SHORT).show();
                NIMClient.getService(ChatRoomService::class.java).exitChatRoom(roomId)
            } else {
                // Toast.makeText(ChatRoomActivity.this, R.string.nim_status_unlogin, Toast.LENGTH_SHORT).show();
            }
        } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
            // DialogMaker.updateLoadingMessage("登录中...");
        } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
        } else if (chatRoomStatusChangeData.status.wontAutoLogin()) {
        } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
        }
        Logger.w("Chat Room Online Status:" + chatRoomStatusChangeData.status.name)
    }

    private val incomingChatRoomMsg: Observer<List<ChatRoomMessage>> = Observer { messages ->
        val mMsgSingle = messages[messages.size - 1]
        if (mMsgSingle.msgType == MsgTypeEnum.text) {
            mAdapter.addData(ChatRoomEntity(ChatRoomEntity.TEXT, messages[messages.size - 1].content))
            mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
        }
    }

    private val statusObserver = Observer<IMMessage> { msg ->
        // 1、根据sessionId判断是否是自己的消息
        // 2、更改内存中消息的状态
        // 3、刷新界面
        toast("回调成功")
        if (msg.attachment is ImageAttachment) {
//            GlideApp.with(this@TestChatRoomActivity)
//                    .load((msg.attachment as ImageAttachment).path)
//                    .into(iv)
            Logger.d((msg.attachment as ImageAttachment).path)
        } else {
            Logger.d("这不属于图片")
//            var ss = tv.getText().toString()
//            ss = ss + "\n" + msg.content
//            tv.setText(ss)
        }
    }

    // 创建聊天室文本消息并发送
    private fun sendTextMsg(content: String) {
        val message = ChatRoomMessageBuilder.createChatRoomTextMessage(roomId, content)
        NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(param: Void) {
                        Logger.e("onTextSuccess")
                        mAdapter.addData(ChatRoomEntity(ChatRoomEntity.TEXT, content, true))
                        mChatRoomRv.scrollToPosition(mAdapter.data.size)
                    }

                    override fun onFailed(code: Int) {
                        Logger.e("onTextFailed")
                    }

                    override fun onException(exception: Throwable) {
                        Logger.e("onTextException:${exception.message}")
                    }
                })
    }

    /**
     * 创建图片消息并发送
     *
     * @file 压缩处理一下文件，避免图过大
     */
    private fun sendImgMsg(file: File) {
        val message = ChatRoomMessageBuilder.createChatRoomImageMessage(roomId, file, file.name)
        NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(param: Void) {
                        Logger.e("onSuccess Pic")
                    }

                    override fun onFailed(code: Int) {
                        Logger.e("onFailed Pic")
                    }

                    override fun onException(exception: Throwable) {
                        Logger.e("onException Pic:${exception.message}")
                    }
                })
    }

    // 下载之前判断一下是否已经下载。若重复下载，会报错误码414。（以SnapChatAttachment为例）
    private fun isOriginImageHasDownloaded(message: IMMessage) = message is ImageAttachment
            && message.attachStatus == AttachStatusEnum.transferred
            && !TextUtils.isEmpty((message.attachment as ImageAttachment).path)

}
