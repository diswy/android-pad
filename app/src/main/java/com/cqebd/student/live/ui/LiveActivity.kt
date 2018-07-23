package com.cqebd.student.live.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.live.custom.NormalAttachment
import com.cqebd.student.live.entity.EbdCustomNotification
import com.cqebd.student.live.entity.LiveByRemote
import com.cqebd.student.live.entity.PullAddress
import com.cqebd.student.live.helper.*
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.netease.NetEaseCache
import com.cqebd.student.test.BlankFragment
import com.cqebd.student.tools.RxCounter
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.UserAccount
import com.google.gson.Gson
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.avchat.AVChatCallback
import com.netease.nimlib.sdk.avchat.AVChatManager
import com.netease.nimlib.sdk.avchat.AVChatStateObserverLite
import com.netease.nimlib.sdk.avchat.constant.AVChatType
import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoCropRatio
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType
import com.netease.nimlib.sdk.avchat.model.*
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.orhanobut.logger.Logger
import com.wuhangjia.firstlib.view.FancyDialogFragment
import gorden.lib.video.ExVideoView
import kotlinx.android.synthetic.main.activity_live.*
import kotlinx.android.synthetic.main.dialog_live_confirm_up.view.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import org.jetbrains.anko.toast

class LiveActivity : BaseActivity() {
    private val mTitle = ArrayList<String>()// 标签名称
    private var id: Int = 0
    private var hasChat: Boolean = false // 暂时对我来说没有用
    private var hasIWB: Boolean = false
    private var hasVchat: Boolean = false // 是否是互动直播
    private var mChatRoomId: String? = null// 房间ID
    private var mCreator: String? = null // 教师ID
    private var mVChatId: String? = null// 互动直播房间ID
    private val mLiveAddress = ArrayList<String>()
    private var mHasPermission = false// 是否具备视频权限
    val mChat = LiveChatFragment()
    val mRts = LiveNeteaseRtsFragment()
    var mApply = false // false 可以申请 true 申请中
    var mVideoErrorCount = 0 //　视频重试次数


    override fun setContentView() {
        setContentView(R.layout.activity_live)
    }

    override fun bindEvents() {
        mToolbar.setNavigationOnClickListener {
            leaveRoom()
            exitRoom()
            this.finish()
        }

        mBtnApplyVideo.setOnClickListener {
            mCreator?.let {
                if (!mApply) {
                    mBtnApplyVideo.setImageResource(R.drawable.ic_cancel_hand_up)
                    mBtnApplyVideo.isEnabled = false
                    val mData = EbdCustomNotification("live", "1", VIDEO_IN, "STUDENT", loginId,
                            "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                    MsgManager.instance().sendP2PCustomNotification(it, mData)
                    btnTask()
                } else {
                    mBtnApplyVideo.setImageResource(R.drawable.ic_hand_up)
                    downMic(false)
                    val mData = EbdCustomNotification("live", "1", VIDEO_CANCEL, "STUDENT", loginId,
                            "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                    MsgManager.instance().sendP2PCustomNotification(it, mData)
                }
                mApply = !mApply
            }
        }

    }

    override fun onBackPressed() {
        leaveRoom()
        exitRoom()
        super.onBackPressed()
    }

    override fun initialize(savedInstanceState: Bundle?) {
        mToolbar.inflateMenu(R.menu.live_video_refresh)
        mToolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.mBtnRefresh && mLiveAddress.size > 0) {
                videoView.setVideoPath(mLiveAddress[0], "", R.drawable.ic_login_logo)
            }
            return@setOnMenuItemClickListener false
        }
        parseIntent()
        videoView.setLiveMode(true)// 该界面全部为直播界面
        videoView.setOnVideoErrorListener(object : ExVideoView.onVideoError {
            override fun onVideoError() {
                mVideoErrorCount++
                if (mLiveAddress.size > 0 && mVideoErrorCount < 4) {
                    videoView.setVideoPath(mLiveAddress[0], "", R.drawable.ic_login_logo)
                }
                if (mVideoErrorCount == 4) {
                    toast("请稍后尝试手动刷新视频")
                }
            }
        })
        initTag(hasVchat)
//        videoView.waitPlay(R.drawable.ic_no_video)
        getLiveInfo(id)
        registerObservers(true)
    }

    override fun onDestroy() {
        videoView.onStop()
        registerObservers(false)
        super.onDestroy()
    }

    private fun registerObservers(register: Boolean) {
        NIMClient.getService(MsgServiceObserve::class.java).observeCustomNotification(customNotification, register)
        NIMClient.getService(ChatRoomServiceObserver::class.java).observeReceiveMessage(incomingChatRoomMsg, register)
        AVChatManager.getInstance().observeAVChatState(avChatListener, register)
//        NIMClient.getService(ChatRoomServiceObserver::class.java).observeKickOutEvent(kickOutObserver, register)
//        NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus(userStatusObserver, register)
    }

    private val avChatListener: AVChatStateObserverLite = object : AVChatStateObserverLite {
        override fun onUserLeave(account: String?, event: Int) {
            Logger.w("--->>>用户离开的ID = $account")
        }

        override fun onCallEstablished() {
        }

        override fun onLiveEvent(event: Int) {
        }

        override fun onAudioFrameFilter(frame: AVChatAudioFrame?): Boolean {
            return false
        }

        override fun onVideoFrameResolutionChanged(account: String?, width: Int, height: Int, rotate: Int) {
        }

        override fun onProtocolIncompatible(status: Int) {
        }

        override fun onNetworkQuality(account: String?, quality: Int, stats: AVChatNetworkStats?) {
        }

        override fun onVideoFrameFilter(frame: AVChatVideoFrame?, maybeDualInput: Boolean): Boolean {
            return false
        }

        override fun onJoinedChannel(code: Int, audioFile: String?, videoFile: String?, elapsed: Int) {
        }

        override fun onReportSpeaker(speakers: MutableMap<String, Int>?, mixedEnergy: Int) {
        }

        override fun onAudioDeviceChanged(device: Int) {
        }

        override fun onDisconnectServer(code: Int) {
        }

        override fun onSessionStats(sessionStats: AVChatSessionStats?) {
        }

        override fun onDeviceEvent(code: Int, desc: String?) {
        }

        override fun onConnectionTypeChanged(netType: Int) {
        }

        override fun onLeaveChannel() {
        }

        override fun onFirstVideoFrameAvailable(account: String?) {
        }

        override fun onVideoFpsReported(account: String?, fps: Int) {
        }

        override fun onFirstVideoFrameRendered(account: String?) {
        }

        override fun onUserJoined(account: String?) {
            Logger.w("--->>>用户加入进来的ID = $account")
        }

    }

    /**
     * 自定义通知监听
     */
    private val customNotification: Observer<CustomNotification> = Observer { message ->
        Logger.wtf(message.content)
        val notification = Gson().fromJson(message.content, EbdCustomNotification::class.java)
        when (notification.name) {
            VIDEO_IN_CONFIRM -> {
                mHasPermission = true
                mBtnApplyVideo.isEnabled = true
                upMic()// 上麦确认
            }
            VIDEO_IN_REFUSE -> {
                mHasPermission = false
                mBtnApplyVideo.isEnabled = true
                mBtnApplyVideo.setImageResource(R.drawable.ic_hand_up)
                toast("老师拒绝了你的上麦请求")
            }
            VIDEO_OUT -> {
                mHasPermission = false
                downMic()
            }
            VIDEO_IN -> {
                showUpMicDialog()
            }
            IWB_IN -> {
                toast("老师给予你白板权限")
                mRts.setRtsEnable(true)
            }
            IWB_OUT -> {
                toast("老师撤回了你白板权限")
                mRts.setBtnChecked(false)
                mRts.setRtsEnable(false)
            }
            IWB_IN_CONFIRM -> {
                mRts.setRtsEnable(true)
            }
            IWB_IN_REFUSE -> {
                toast("老师拒绝了你的请求")
                mRts.setBtnChecked(false)
                mRts.setRtsEnable(false)
            }
        }
    }

    private val incomingChatRoomMsg: Observer<List<ChatRoomMessage>> = Observer { messages ->
        val mMsgSingle = messages[messages.size - 1]
        when (mMsgSingle.msgType) {
            MsgTypeEnum.custom -> {
                if (mMsgSingle.attachment is NormalAttachment) {
                    val attachment = mMsgSingle.attachment as NormalAttachment
                    when (attachment.mCustomMsg?.name) {
                        "video" -> {
                            if (attachment.mCustomMsg?.parameter == "periodover") {
                                this.finish()
                                toast("课堂已结束")
                            } else if (attachment.mCustomMsg?.parameter == "init") {
                                if (mLiveAddress.size > 0) {
                                    videoView.setVideoPath(mLiveAddress[0], "", R.drawable.ic_login_logo)
                                }
                            } else {
                                addOtherPeople(attachment.mCustomMsg?.content!!)
                            }
                        }

                    }
                }
            }
            else -> {
            }
        }
    }

    private fun parseIntent() {
        id = intent.getIntExtra("id", 0)
        hasChat = intent.getIntExtra("hasChat", 0) != 0 // 暂时对我来说没有用
        hasIWB = intent.getIntExtra("hasIWB", 0) != 0
        hasVchat = intent.getIntExtra("hasVchat", 0) != 0// 是否是互动直播
        toolbar_title.text = intent.getStringExtra("title")
        Logger.d("id = $id hasChat = $hasChat hasIWB = $hasIWB hasVchat = $hasIWB")

        if (hasVchat) {
            mBtnApplyVideo.visibility = View.VISIBLE
        } else {
            mBtnApplyVideo.visibility = View.GONE
        }
    }

    private fun initTag(hasRTS: Boolean = false) {
        mTitle.add("讨论")
        if (hasRTS) {
            mTitle.add("白板")
        }
        // 初始化页签
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = TitleNavigatorAdapter(this, mTitle, mNonScrollVp)
        mLiveIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(mLiveIndicator, mNonScrollVp)
    }

    private fun initAdapter(roomId: String, rtsName: String) {
        val bundle = Bundle()
        bundle.putString("roomID", roomId)
        mChat.arguments = bundle

        val rtsBundle = Bundle()
        rtsBundle.putString("rtsName", rtsName)
        mRts.arguments = rtsBundle

        mNonScrollVp.offscreenPageLimit = 2
        mNonScrollVp.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> mChat
                    1 -> mRts
                    else -> BlankFragment()
                }
            }

            override fun getCount(): Int {
                return mTitle.size
            }
        }
        mNonScrollVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
                    0 -> mChat.onCurrent()
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })
    }

    /**
     * 获取直播房间信息
     */
    private fun getLiveInfo(id: Int) {
        NetClient.videoService()
                .getLive(id)
                .enqueue(object : NetCallBack<BaseResponse<LiveByRemote>>() {
                    override fun onFailure() {

                    }

                    override fun onSucceed(response: BaseResponse<LiveByRemote>?) {
                        response?.data?.let {
                            Logger.json(Gson().toJson(it))
                            val address = it.ChannelPullUrls
                            val mPullAddress = Gson().fromJson(address, PullAddress::class.java)
                            mLiveAddress.add(mPullAddress.hlsPullUrl)
                            mLiveAddress.add(mPullAddress.httpPullUrl)
                            videoView.setVideoPath(mPullAddress.hlsPullUrl, "", R.drawable.ic_login_logo)
                            Logger.i(mPullAddress.httpPullUrl)

                            mVChatId = it.VchatRoomName
                            initAdapter(it.ChatRoomId, it.IWBRoomName)

                            mChatRoomId = it.ChatRoomId
                            enterRoom(it.ChatRoomId)// 进入聊天室
                            joinVideoRoom()
                        }

                    }
                })
    }

    private var enterRequest: AbortableFuture<EnterChatRoomResultData>? = null
    private fun enterRoom(roomId: String) {
        if (TextUtils.isEmpty(roomId)) {
            toast("聊天房间进入失败，请退出当前页面重新尝试")
            return
        }

        val data = EnterChatRoomData(roomId)
        enterRequest = NIMClient.getService(ChatRoomService::class.java).enterChatRoom(data)
        enterRequest?.setCallback(object : RequestCallback<EnterChatRoomResultData> {
            override fun onSuccess(result: EnterChatRoomResultData) {
                enterRequest = null
                mCreator = result.roomInfo.creator
                mRts.setCreator(result.roomInfo.creator)// 设置创建者
            }

            override fun onFailed(code: Int) {
                enterRequest = null
                when (code) {
                    ResponseCode.RES_CHATROOM_BLACKLIST.toInt() -> toast("你已被拉入黑名单，不能再进入聊天室")
                    ResponseCode.RES_ENONEXIST.toInt() -> toast("该聊天室不存在")
                    else -> toast("enter chat room failed, code = $code")
                }
                finish()
            }

            override fun onException(exception: Throwable) {
                enterRequest = null
            }
        })
    }

    private fun exitRoom() {
        mChatRoomId?.let {
            NIMClient.getService(ChatRoomService::class.java).exitChatRoom(it)
        }
    }

    //--------------------------音视频模块--------------------------
    private var videoCapturer: AVChatCameraCapturer? = null // 视频采集模块

    private fun initVideo() {
        // 开启音视频引擎
        AVChatManager.getInstance().enableRtc()
        // 打开视频模块
        AVChatManager.getInstance().enableVideo()
        // 设置视频采集模块
        if (videoCapturer == null) {
            videoCapturer = AVChatVideoCapturerFactory.createCameraCapturer()
            AVChatManager.getInstance().setupVideoCapturer(videoCapturer)
        }

//        AVChatManager.getInstance().setParameter(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.AUDIENCE)
//        val render = AVChatSurfaceViewRenderer(this)
//        AVChatManager.getInstance().setupLocalVideoRender(render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
//        AVChatManager.getInstance().startVideoPreview()

        val parameters = AVChatParameters()
        parameters.setBoolean(AVChatParameters.KEY_SESSION_LIVE_MODE, true)
        parameters.setInteger(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.NORMAL)
        parameters.setInteger(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, AVChatVideoCropRatio.CROP_RATIO_16_9)
        AVChatManager.getInstance().setParameters(parameters)
    }

    private fun joinVideoRoom() {
        if (!hasVchat)
            return

        initVideo()
        Logger.d("互动直播：$mVChatId")
        AVChatManager.getInstance().joinRoom2(mVChatId, AVChatType.VIDEO, object : AVChatCallback<AVChatData> {
            override fun onSuccess(avChatData: AVChatData) {
                Logger.d("join channel success, extra:" + avChatData.extra)
                // 设置音量信号监听, 通过AVChatStateObserver的onReportSpeaker回调音量大小
                val avChatParameters = AVChatParameters()
                avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_REPORT_SPEAKER, true)
                AVChatManager.getInstance().setParameters(avChatParameters)
            }

            override fun onFailed(i: Int) {
                Logger.d("join channel failed, code:$i")
            }

            override fun onException(throwable: Throwable) {
                Logger.e("互动直播异常：${throwable.message}")
            }
        })

    }

    /**
     * 离开互动直播房间
     */
    private fun leaveRoom() {
        if (TextUtils.isEmpty(mVChatId))
            return
        // 关闭视频预览
        AVChatManager.getInstance().stopVideoPreview()
        // 如果是视频通话，关闭视频模块
        AVChatManager.getInstance().disableVideo()
        // 关闭音视频引擎
        AVChatManager.getInstance().disableRtc()
        // 离开房间 不需要回调
        AVChatManager.getInstance().leaveRoom2(mVChatId, null)
    }

    // 添加到成员显示的画布
    private fun addIntoPreviewLayout(surfaceView: SurfaceView?, viewLayout: ViewGroup) {
        if (surfaceView == null) {
            return
        }
        if (surfaceView.parent != null)
            (surfaceView.parent as ViewGroup).removeView(surfaceView)
        viewLayout.addView(surfaceView)
        surfaceView.setZOrderMediaOverlay(true)
    }
    private fun addIntoPreviewLayout(surfaceView: TextureView?, viewLayout: ViewGroup) {
        if (surfaceView == null) {
            return
        }
        if (surfaceView.parent != null)
            (surfaceView.parent as ViewGroup).removeView(surfaceView)
        viewLayout.addView(surfaceView)
    }

    // 移除所有成员的画布
    private fun removePreviewLayout() {
        mMyself.removeAllViews()
        mCreatorView.removeAllViews()
        mStudent1.removeAllViews()
        mStudent2.removeAllViews()
        mStudent1.visibility = View.GONE
        mStudent2.visibility = View.GONE
    }

    /**
     * 拒绝远端上麦请求
     */
    private fun refuseMic() {
        mCreator?.let {
            val mData = EbdCustomNotification("live", "1", VIDEO_IN_REFUSE, "STUDENT", loginId,
                    "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
            MsgManager.instance().sendP2PCustomNotification(it, mData)
        }
    }

    private fun downMic(isKickOut: Boolean = true) {
        if (isKickOut) {
            toast("老师已将你踢下线")
        }

        switchRole(true)
        if (!mLiveAddress.isEmpty()) {
            videoView.setVideoPath(mLiveAddress[0], "", R.drawable.ic_login_logo)
        }
        AVChatManager.getInstance().enableAudienceRole(true)
        removePreviewLayout()
        mCreator?.let {
            val mData = EbdCustomNotification("live", "1", VIDEO_OUT_CONFIRM, "STUDENT", loginId,
                    "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
            MsgManager.instance().sendP2PCustomNotification(it, mData)
        }
    }

    /**
     * 上麦
     */
    private fun upMic(sendCallback: Boolean = false) {
        if (sendCallback) {
            mCreator?.let {
                val mData = EbdCustomNotification("live", "1", VIDEO_IN_CONFIRM, "STUDENT", loginId,
                        "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                MsgManager.instance().sendP2PCustomNotification(it, mData)
            }
        }
        switchRole(false)
        videoView.onStop()
        AVChatManager.getInstance().enableAudienceRole(false)
        val render = AVChatSurfaceViewRenderer(this)
        AVChatManager.getInstance().setupLocalVideoRender(render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
        AVChatManager.getInstance().startVideoPreview()
        addIntoPreviewLayout(render, mMyself)
        mCreator?.let {
            val remoteRender = AVChatSurfaceViewRenderer(this)
            AVChatManager.getInstance().setupRemoteVideoRender(mCreator, remoteRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
            addIntoPreviewLayout(remoteRender, mCreatorView)
        }
    }

    /**
     * 切换角色
     */
    private fun switchRole(isNormal: Boolean) {
        videoView.visibility = if (isNormal) View.VISIBLE else View.GONE
    }

    /**
     * 服务端30秒不响应，自动取消
     */
    private fun btnTask() {
        RxCounter.tick(30).doOnComplete {
            if (!mHasPermission) {
                mBtnApplyVideo.isEnabled = true
                mBtnApplyVideo.setImageResource(R.drawable.ic_hand_up)
                mCreator?.let {
                    val mData = EbdCustomNotification("live", "1", VIDEO_CANCEL, "STUDENT", loginId,
                            "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                    MsgManager.instance().sendP2PCustomNotification(it, mData)
                }
            }
        }.subscribe()
    }

    private fun showUpMicDialog() {
        val dialog = FancyDialogFragment.create()
        dialog.setCanCancelOutside(false)
                .setLayoutRes(R.layout.dialog_live_confirm_up)
                .setWidth(this, 300)
                .setViewListener {
                    it.apply {
                        mBtnConfirm.setOnClickListener {
                            mBtnApplyVideo.setImageResource(R.drawable.ic_cancel_hand_up)
                            upMic(true)
                            dialog.dismiss()
                        }
                        mBtnCancel.setOnClickListener {
                            refuseMic()
                            dialog.dismiss()
                        }
                    }
                }
                .show(fragmentManager, "")
    }

    private fun addOtherPeople(hasPermissionList: String) {
        Logger.d("群发消息：本人ID = $loginId , $hasPermissionList")
        val names = hasPermissionList.split(",")
        if (names.contains(NetEaseCache.getAccount())) {
            val mList = ArrayList<String>()
            for (item in names) {
                mList.add(item)
            }

            mStudent1.removeAllViews()
            mStudent2.removeAllViews()
            mList.remove(NetEaseCache.getAccount())
            when (mList.size) {
                0 -> {
                    mStudent1.visibility = View.GONE
                    mStudent2.visibility = View.GONE
                }
                1 -> {
                    Logger.d("群发消息：本人ID = $loginId ,添加的ID ${mList[0]}")
                    mStudent1.visibility = View.VISIBLE
                    mStudent2.visibility = View.GONE
                    mStudent1.removeAllViews()
//                    val remoteRender = AVChatSurfaceViewRenderer(this)
                    val remoteRender = AVChatTextureViewRenderer(this)
                    AVChatManager.getInstance().setupRemoteVideoRender(mList[0], remoteRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
                    addIntoPreviewLayout(remoteRender, mStudent1)
                }
                2 -> {
                    mStudent1.visibility = View.VISIBLE
                    mStudent2.visibility = View.VISIBLE
                    mStudent1.removeAllViews()
                    mStudent2.removeAllViews()
                    val remoteRender = AVChatTextureViewRenderer(this)
                    AVChatManager.getInstance().setupRemoteVideoRender(mList[0], remoteRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
                    addIntoPreviewLayout(remoteRender, mStudent1)
                    val remoteRender2 = AVChatTextureViewRenderer(this)
                    AVChatManager.getInstance().setupRemoteVideoRender(mList[1], remoteRender2, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
                    addIntoPreviewLayout(remoteRender2, mStudent2)
                }
            }
        }
    }

}
