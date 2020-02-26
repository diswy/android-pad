package com.cqebd.student.live.ui


import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.glide.SimplePreviewActivity
import com.cqebd.student.live.adapter.ChatRoomMultipleAdapter
import com.cqebd.student.live.entity.ChatRoomEntity
import com.cqebd.student.utils.KeybordS
import com.cqebd.student.vo.entity.UserAccount
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData
import com.netease.nimlib.sdk.media.record.AudioRecorder
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback
import com.netease.nimlib.sdk.media.record.RecordType
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.orhanobut.logger.Logger
import gorden.lib.video.ExAudioPlayer
import gorden.library.Album
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_live_chat.*
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Live Chat
 *
 */
class LiveChatFragment : BaseFragment() {
    private lateinit var roomId: String
    private lateinit var mAnimation: AnimationDrawable
    private var tempAnimation:AnimationDrawable? = null
    private var mDisposable: Disposable? = null// 倒计时专用
    private fun timer() {
        mDisposable?.dispose()
        mDisposable = Flowable.intervalRange(0, 60, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val mySecond = (60 - it - 1).toInt()
                    if ( mySecond<= 5){
                        tv_record_hint.text = "最多还可以录制${mySecond}秒"
                    }
                }
                .doOnComplete {
                    timerCancel()
                    btnSpeak.text = "按住 说话"
                    stopAudio(false)
                    mAnimation.stop()
                    ic_mic.visibility = View.GONE
                    btnSpeak.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
                }
                .doOnError {
                }
                .subscribe()
    }

    private fun timerCancel(){
        mDisposable?.dispose()
        tv_record_hint.text = "手指上滑，取消发送"
    }

    private val mAdapter = ChatRoomMultipleAdapter(null)

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_live_chat, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        roomId = arguments!!.getString("roomID")
        mChatRoomRv.adapter = mAdapter
        registerObservers(true)

        mAnimation = iv_recording_animation.drawable as AnimationDrawable
    }

    override fun onDestroyView() {
        mExoAudioPlayer.setOnCompletionListener(null)
        mExoAudioPlayer.release()
        registerObservers(false)
        super.onDestroyView()
    }

    var mPosY = 0f
    var mEndPosY = 0f

    override fun bindEvents() {
        mSwitchInputType.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.backgroundResource = R.drawable.ic_btn_keyboard
                btnSpeak.visibility = View.VISIBLE
            } else {
                buttonView.backgroundResource = R.drawable.ic_btn_mic
                btnSpeak.visibility = View.GONE
            }
        }

        btnSpeak.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    mPosY = motionEvent.rawY
                    btnSpeak.text = "松开 结束"
                    ic_mic.visibility = View.VISIBLE
                    mAnimation.start()
                    btnSpeak.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.color_cd))

                    startAudio()
                }
                MotionEvent.ACTION_UP -> {
                    timerCancel()
                    btnSpeak.text = "按住 说话"
                    val gap = mPosY - mEndPosY
                    if (gap > 100) {// 认为上滑取消
                        stopAudio(true)
                    } else {// 发送
                        stopAudio(false)
                    }
                    mAnimation.stop()
                    ic_mic.visibility = View.GONE
                    btnSpeak.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
                }
                MotionEvent.ACTION_MOVE -> {
                    mEndPosY = motionEvent.rawY

                    val gap = mPosY - mEndPosY
                    if (gap > 100) {// 认为上滑取消 刷新UI
                        ic_mic.setBackgroundResource(R.drawable.bg_mic_cancel)
                        tv_record_hint.text = "松开手指，取消发送"
                        iv_mic_cancel.visibility = View.VISIBLE
                        iv_mic_recording.visibility = View.GONE
                        mAnimation.stop()
                    } else {// 发送
                        ic_mic.setBackgroundResource(R.drawable.bg_mic)
                        tv_record_hint.text = "手指上滑，取消发送"
                        iv_mic_cancel.visibility = View.GONE
                        iv_mic_recording.visibility = View.VISIBLE
                        mAnimation.start()
                    }
                }
            }
            return@setOnTouchListener true
        }

        mBtnSend.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val lp = mBtnSend.layoutParams
                lp.width = dip(60)
                mBtnSend.layoutParams = lp
                buttonView.backgroundResource = R.color.colorPrimary
            } else {
                val lp = mBtnSend.layoutParams
                lp.width = dip(38)
                mBtnSend.layoutParams = lp
                buttonView.backgroundResource = R.drawable.ic_btn_pic
            }
        }

        mBtnSend.setOnClickListener {
            if (mBtnSend.isChecked) {// 发送图片
                Album.create().single().start(this, Album.REQUEST_CODE)
            } else {// 发送文本
                sendTextMsg(mChatRoomEdit.text.toString().trim())
                mChatRoomEdit.setText("")
                KeybordS.closeKeybord(mChatRoomEdit, context)
            }

            mBtnSend.isChecked = mChatRoomEdit.text.toString().trim().isNotEmpty()
        }

        mChatRoomEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mBtnSend.isChecked = s.toString().trim().isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        mAdapter.setAudioCallback { url, animation ->
            tempAnimation?.stop()
            tempAnimation?.selectDrawable(0)
            tempAnimation = animation
            if (mExoAudioPlayer.isPlaying()) {
                mExoAudioPlayer.release()
                tempAnimation?.stop()
                tempAnimation?.selectDrawable(0)
            } else {
                mExoAudioPlayer.openAudio(url)
                tempAnimation?.start()
            }
        }

        mAdapter.setPicListener { v, path ->
            val options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(activity!!, v, "base_single_simple_img")
            val intent = Intent(activity, SimplePreviewActivity::class.java)
            intent.putExtra("url", path)
            ActivityCompat.startActivity(activity!!, intent, options.toBundle())
        }

        mExoAudioPlayer.setOnCompletionListener {
            tempAnimation?.stop()
            tempAnimation?.selectDrawable(0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Album.REQUEST_CODE -> {
                    val pathList = data?.getStringArrayListExtra(Album.KEY_IMAGES)
                    pathList?.let {
                        compressImage(it[0])
                        mAdapter.addData(ChatRoomEntity(ChatRoomEntity.IMG, UserAccount.load()?.Account
                                ?: "未知账号", it[0], UserAccount.load()?.Name
                                ?: "神秘同学", true))
                        mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
                    }
                }
            }
        }
    }

    fun onCurrent() {
        mChatRoomRv?.apply {
            adapter = mAdapter
            try {
                mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
        }
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
        Logger.d(mMsgSingle)
        when (mMsgSingle.msgType) {
            MsgTypeEnum.text -> {// 处理文本消息

//                var nickName = ""
//                if (mMsgSingle.remoteExtension != null && mMsgSingle.remoteExtension["nickName"] != null) {
//                    nickName = mMsgSingle.remoteExtension["nickName"] as String
//                }

                Logger.d("Account: ${mMsgSingle.fromAccount}, Nick: ${mMsgSingle.chatRoomMessageExtension.senderNick}" +
                        ",Avatar:${mMsgSingle.chatRoomMessageExtension.senderAvatar}")

                mAdapter.addData(ChatRoomEntity(ChatRoomEntity.TEXT,
                        mMsgSingle.fromAccount,
                        mMsgSingle.content,
                        mMsgSingle.chatRoomMessageExtension.senderAvatar,
                        mMsgSingle.chatRoomMessageExtension.senderNick))


//                if (mMsgSingle.remoteExtension != null
//                        && mMsgSingle.remoteExtension["avatar"] != null
//                        && mMsgSingle.remoteExtension["avatar"] is String) {
//                    val mAvatar = mMsgSingle.remoteExtension["avatar"] as String
//                    mAdapter.addData(ChatRoomEntity(ChatRoomEntity.TEXT, mMsgSingle.content, mAvatar, mMsgSingle.chatRoomMessageExtension.senderNick))
//                } else {
//                    mAdapter.addData(ChatRoomEntity(ChatRoomEntity.TEXT, mMsgSingle.content, mMsgSingle.chatRoomMessageExtension.senderNick))
//                }


            }
            MsgTypeEnum.image -> {// 处理图片
                val imgSrc = (mMsgSingle.attachment as ImageAttachment).url
                mAdapter.addData(ChatRoomEntity(ChatRoomEntity.IMG,
                        mMsgSingle.fromAccount,
                        imgSrc,
                        mMsgSingle.chatRoomMessageExtension.senderAvatar,
                        mMsgSingle.chatRoomMessageExtension.senderNick))

//                var nickName = ""
//                if (mMsgSingle.remoteExtension != null && mMsgSingle.remoteExtension["nickName"] != null) {
//                    nickName = mMsgSingle.remoteExtension["nickName"] as String
//                }
//
//                if (mMsgSingle.remoteExtension !== null
//                        && mMsgSingle.remoteExtension["avatar"] != null
//                        && mMsgSingle.remoteExtension["avatar"] is String) {
//                    val imgSrc = (mMsgSingle.attachment as ImageAttachment).thumbUrl
//                    if (mMsgSingle.remoteExtension["avatar"] != null
//                            && mMsgSingle.remoteExtension["avatar"] is String) {
//                        val mAvatar = mMsgSingle.remoteExtension["avatar"] as String
//                        mAdapter.addData(ChatRoomEntity(ChatRoomEntity.IMG, imgSrc, mAvatar, nickName))
//                    } else {
//                        mAdapter.addData(ChatRoomEntity(ChatRoomEntity.IMG, imgSrc, nickName))
//                    }
//                }
            }
            MsgTypeEnum.audio -> {
                try {
                    val mAudioAttachment = mMsgSingle.attachment as AudioAttachment
                    mAdapter.addData(ChatRoomEntity(ChatRoomEntity.AUDIO,
                            mMsgSingle.fromAccount,
                            mAudioAttachment.url,
                            mAudioAttachment.duration,
                            mMsgSingle.chatRoomMessageExtension.senderAvatar,
                            mMsgSingle.chatRoomMessageExtension.senderNick))
                } catch (e: Exception) {

                }
            }
            MsgTypeEnum.notification -> {

            }
            else -> {
            }
        }
        if (!mAdapter.data.isEmpty() && mChatRoomRv != null) {
            mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
        }
    }

    private val statusObserver = Observer<IMMessage> { msg ->
        // 1、根据sessionId判断是否是自己的消息
        // 2、更改内存中消息的状态
        // 3、刷新界面
        if (msg.attachment is ImageAttachment) {
//            GlideApp.with(this@TestChatRoomActivity)
//                    .load((msg.attachment as ImageAttachment).path)
//                    .into(iv)
            Logger.d((msg.attachment as ImageAttachment).path)
        } else {
            Logger.d("这不属于图片")
        }
    }

    // 创建聊天室文本消息并发送
    private fun sendTextMsg(content: String) {
        val message = ChatRoomMessageBuilder.createChatRoomTextMessage(roomId, content)
        val mExtensionMap = HashMap<String, Any>()
        mExtensionMap["avatar"] = UserAccount.load()?.Avatar ?: ""
        mExtensionMap["nickName"] = UserAccount.load()?.Name ?: "神秘同学"
        message.remoteExtension = mExtensionMap
        NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(param: Void?) {
                        Logger.e("onTextSuccess")
                        mAdapter.addData(ChatRoomEntity(ChatRoomEntity.TEXT, UserAccount.load()?.Account
                                ?: "未知账号", content, UserAccount.load()?.Name
                                ?: "神秘同学", true))
                        if (!mAdapter.data.isEmpty() && mChatRoomRv != null) {
                            mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
                        }
                    }

                    override fun onFailed(code: Int) {
                        Logger.e("onTextFailed")
                    }

                    override fun onException(exception: Throwable?) {
                        Logger.e("onTextException:${exception?.message}")
                    }
                })
    }


    private fun sendAudioMsg(file: File?, length: Long) {
        file ?: return
        val audioMsg = ChatRoomMessageBuilder.createChatRoomAudioMessage(roomId, file, length)
        NIMClient.getService(ChatRoomService::class.java).sendMessage(audioMsg, false)
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(param: Void?) {
                        mAdapter.addData(ChatRoomEntity(ChatRoomEntity.AUDIO,
                                UserAccount.load()?.Account ?: "未知账号",
                                file.absolutePath,
                                length,
                                UserAccount.load()?.Name ?: "神秘同学",
                                true))
//                        mAdapter.addData()
                        mChatRoomRv.scrollToPosition(mAdapter.data.size - 1)
                    }

                    override fun onFailed(code: Int) {
                    }

                    override fun onException(exception: Throwable?) {
                    }

                })
    }

    private fun startAudio() {
        activity ?: return
        mAudioRecorder.startRecord()
    }

    //如果cancel为true，回调onRecordCancel, 为false，回调onRecordSuccess
    private fun stopAudio(cancel: Boolean) {
        mAudioRecorder.completeRecord(cancel)
    }

    private val mAudioRecorder by lazy { AudioRecorder(activity, RecordType.AAC, 61, audioCallBack) }
    private val mExoAudioPlayer by lazy { ExAudioPlayer(activity!!) }

    // 音频录制回调
    private val audioCallBack = object : IAudioRecordCallback {
        override fun onRecordSuccess(audioFile: File?, audioLength: Long, recordType: RecordType?) {
            sendAudioMsg(audioFile, audioLength)
        }

        override fun onRecordReachedMaxTime(maxTime: Int) {
        }

        override fun onRecordReady() {
            timer()
        }

        override fun onRecordCancel() {
        }

        override fun onRecordStart(audioFile: File?, recordType: RecordType?) {
        }

        override fun onRecordFail() {
        }

    }

    /**
     * 创建图片消息并发送
     *
     * @file 压缩处理一下文件，避免图过大
     */
    private fun sendImgMsg(file: File) {
        val message = ChatRoomMessageBuilder.createChatRoomImageMessage(roomId, file, file.name)
        val mExtensionMap = HashMap<String, Any>()
        mExtensionMap["avatar"] = UserAccount.load()?.Avatar ?: ""
        mExtensionMap["nickName"] = UserAccount.load()?.Name ?: "神秘同学"
        message.remoteExtension = mExtensionMap
        NIMClient.getService(ChatRoomService::class.java).sendMessage(message, false)
                .setCallback(object : RequestCallback<Void> {
                    override fun onSuccess(param: Void?) {
                        Logger.e("onSuccess Pic")
                    }

                    override fun onFailed(code: Int) {
                        Logger.e("onFailed Pic")
                    }

                    override fun onException(exception: Throwable?) {
                        Logger.e("onException Pic:${exception?.message}")
                    }
                })
    }

    /**
     * 压缩图片
     */
    private fun compressImage(path: String) {
        Luban.with(context)
                .load(path)
                .ignoreBy(100)
                .setTargetDir(getPath())
                .filter {
                    return@filter !(TextUtils.isEmpty(it) || it.toLowerCase().endsWith(".gif"))
                }
                .setCompressListener(object : OnCompressListener {
                    override fun onSuccess(file: File?) {
                        file?.let {
                            sendImgMsg(it)
                        }
                        if (file == null) {
                            toast("图片发送失败，请重新尝试")
                        }
                    }

                    override fun onError(e: Throwable?) {
                        toast("图片发送失败，请重新尝试")
                        Logger.e("${e?.message}")
                    }

                    override fun onStart() {
                    }
                })
                .launch()
    }

    private fun getPath(): String {
        val path = "${Environment.getExternalStorageDirectory()}/cqebd/image/"
        val file = File(path)
        if (file.mkdirs()) {
            return path
        }
        return path
    }

    // 下载之前判断一下是否已经下载。若重复下载，会报错误码414。（以SnapChatAttachment为例）
    private fun isOriginImageHasDownloaded(message: IMMessage) = message is ImageAttachment
            && message.attachStatus == AttachStatusEnum.transferred
            && !TextUtils.isEmpty((message.attachment as ImageAttachment).path)
}
