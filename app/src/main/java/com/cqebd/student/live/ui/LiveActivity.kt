package com.cqebd.student.live.ui

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.live.custom.NormalAttachment
import com.cqebd.student.live.entity.EbdCustomNotification
import com.cqebd.student.live.entity.LiveByRemote
import com.cqebd.student.live.entity.PullAddress
import com.cqebd.student.live.helper.*
import com.cqebd.student.live.mychat.IMyAVChatSateObserverLite
import com.cqebd.student.live.mychat.INetPlayerKit
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.netease.NetEaseCache
import com.cqebd.student.test.BlankFragment
import com.cqebd.student.tools.RxCounter
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.UserAccount
import com.google.gson.Gson
import com.netease.neliveplayer.playerkit.sdk.LivePlayer
import com.netease.neliveplayer.playerkit.sdk.PlayerManager
import com.netease.neliveplayer.playerkit.sdk.constant.CauseCode
import com.netease.neliveplayer.playerkit.sdk.model.SDKOptions
import com.netease.neliveplayer.playerkit.sdk.model.VideoBufferStrategy
import com.netease.neliveplayer.playerkit.sdk.model.VideoOptions
import com.netease.neliveplayer.playerkit.sdk.model.VideoScaleMode
import com.netease.neliveplayer.proxy.config.NEPlayerConfig
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.avchat.AVChatCallback
import com.netease.nimlib.sdk.avchat.AVChatManager
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
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_live.*
import kotlinx.android.synthetic.main.dialog_live_confirm_up.view.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import okhttp3.ResponseBody
import org.jetbrains.anko.dip
import org.jetbrains.anko.toast
import java.lang.Exception
import java.util.concurrent.TimeUnit

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
    var mVideoErrorCount = 0 //　视频重试次数
    val mChat = LiveChatFragment()
    val mRts = LiveNeteaseRtsFragment()
    private var mLivePullAddress: String = ""// 直播模式拉流地址
    //-----连麦者-----
    private var mRender1 = false // 容器是否被占用
    private var mRender2 = false
    private var mRender3 = false
    //-----连麦者-----
    private var mTaskDisposable: Disposable? = null
    private var mAddTask: Disposable? = null
    private var mOnline: Boolean = false

    override fun setContentView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_live)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        mToolbar.inflateMenu(R.menu.live_video_refresh)
        mToolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.mBtnRefresh) {
                if (hasVchat) {
                    if (mOnline){// 在线
                        toast("正在连麦中，如需刷新请先退出互动")
                    }else{
                        leaveRoom()
                        clearRenderView()
                        initPlayerKit(mLivePullAddress)
                    }
                } else if (mLiveAddress.size > 0) {
                    videoView.setVideoPath(mLiveAddress[0], "", R.drawable.ic_login_logo)
                }
            }
            return@setOnMenuItemClickListener false
        }
        parseIntent()
        initTag(hasVchat)
        getLiveInfo(id)
        registerObservers(true)
    }

    override fun bindEvents() {
        mToolbar.setNavigationOnClickListener {
            leaveRoom()
            exitRoom()
            this.finish()
        }

        mBtnApplyVideo.setOnClickListener {
            mCreator?.let {
                when (tv_mic_status.text.toString()) {
                    "申请连麦" -> {
                        setApplyBtnStyle(3)
                        val mData = EbdCustomNotification("live", "1", VIDEO_IN, "STUDENT", loginId,
                                "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                        MsgManager.instance().sendP2PCustomNotification(it, mData)
                        btnTask()
                    }
                    "连麦中..." -> {
                        downMic(false)
                        val mData = EbdCustomNotification("live", "1", VIDEO_CANCEL, "STUDENT", loginId,
                                "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                        MsgManager.instance().sendP2PCustomNotification(it, mData)
                    }
                    "申请中..." -> {
                        mTaskDisposable?.dispose()
                        setApplyBtnStyle(1)
                        val mData = EbdCustomNotification("live", "1", VIDEO_CANCEL, "STUDENT", loginId,
                                "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                        MsgManager.instance().sendP2PCustomNotification(it, mData)
                    }
                }
            }
        }

        switchScreen.setOnCheckedChangeListener { _, isChecked ->
            screenStatus(isChecked)
        }
    }

    override fun onBackPressed() {
        leaveRoom()
        exitRoom()
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        player?.onActivityResume(true)
    }

    override fun onStop() {
        super.onStop()
        player?.onActivityStop(true)
    }

    override fun onDestroy() {
        retryTask?.dispose()
        releasePlayer()
        videoView.onStop()
        registerObservers(false)
        super.onDestroy()
    }

    private fun registerObservers(register: Boolean) {
        NIMClient.getService(MsgServiceObserve::class.java).observeCustomNotification(customNotification, register)
        NIMClient.getService(ChatRoomServiceObserver::class.java).observeReceiveMessage(incomingChatRoomMsg, register)
        AVChatManager.getInstance().observeAVChatState(avChatListener, register)
    }

    private val avChatListener: IMyAVChatSateObserverLite = object : IMyAVChatSateObserverLite {}
    /**
     * 自定义通知监听
     */
    private val customNotification: Observer<CustomNotification> = Observer { message ->
        Logger.wtf(message.content)
        val notification = Gson().fromJson(message.content, EbdCustomNotification::class.java)
        when (notification.name) {
            VIDEO_IN_CONFIRM -> {
                mTaskDisposable?.dispose()
                switchAudienceRole(false)
            }
            VIDEO_IN_REFUSE -> {
                mTaskDisposable?.dispose()
                setApplyBtnStyle(1)
                toast("老师拒绝了你的上麦请求")
            }
            VIDEO_OUT -> {
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
                mRts.setRtsEnable(false)
            }
            IWB_IN_CONFIRM -> {
                mRts.setRtsEnable(true)
            }
            IWB_IN_REFUSE -> {
                toast("老师拒绝了你的请求")
                mRts.setRtsEnable(false)
            }
            IS_VIDEO_START -> {
                hintView.visibility = View.GONE
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
                            when (attachment.mCustomMsg?.parameter) {
                                "periodover" -> {
                                    this.finish()
                                    toast("课堂已结束")
                                }
                                "init" -> {
                                    if (hasVchat) {
                                        hintView.visibility = View.GONE
                                        leaveRoom()
                                        clearRenderView()
                                        initPlayerKit(mLivePullAddress)
                                    } else if (mLiveAddress.size > 0) {
                                        videoView.setVideoPath(mLiveAddress[0], "", R.drawable.ic_login_logo)
                                    }
                                }
                                "onlineuser" -> {
                                    addOtherPeople(attachment.mCustomMsg?.content!!)
                                }
                                "out" -> {
                                    player?.onActivityStop(true)
                                    leaveRoom()
                                    clearRenderView()
                                    hintView.visibility = View.VISIBLE
                                }
                            }
                        }
                        "iwb" -> {
                            when (attachment.mCustomMsg?.parameter){
                                "clear"-> mRts.clear()
                            }
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    // 初始化房间数据
    private fun parseIntent() {
        id = intent.getIntExtra("id", 0)
        hasChat = intent.getIntExtra("hasChat", 0) != 0 // 暂时对我来说没有用
        hasIWB = intent.getIntExtra("hasIWB", 0) != 0
        hasVchat = intent.getIntExtra("hasVchat", 0) != 0// 是否是互动直播
        toolbar_title.text = intent.getStringExtra("title")
        if (hasVchat) {
            hintView.visibility = View.VISIBLE
            videoView.visibility = View.GONE
            mBtnApplyVideo.visibility = View.VISIBLE
            live_texture.visibility = View.VISIBLE
            initPlayer()
        } else {
            hintView.visibility = View.GONE
            videoView.visibility = View.VISIBLE
            mBtnApplyVideo.visibility = View.GONE
            live_texture.visibility = View.GONE
            initVideoView()
        }
    }

    private fun initTag(hasRTS: Boolean = false) {
        mTitle.add("提问/解答")
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
                    override fun onFailure() {}

                    override fun onSucceed(response: BaseResponse<LiveByRemote>?) {
                        response?.data?.let {
                            startLive(it)
                        }
                    }
                })
    }

    /**
     * 根据后台参数开始直播
     */
    private fun startLive(data: LiveByRemote) {
        Logger.json(Gson().toJson(data))

        val address = data.ChannelPullUrls
        val mPullAddress = Gson().fromJson(address, PullAddress::class.java)

        if (!hasVchat) {// 非互动直播时播放器开始工作
            mLiveAddress.add(mPullAddress.hlsPullUrl)
            mLiveAddress.add(mPullAddress.httpPullUrl)
            videoView.setVideoPath(mPullAddress.hlsPullUrl, "", R.drawable.ic_login_logo)
        }
        mLivePullAddress = mPullAddress.rtmpPullUrl
        initPlayerKit(mLivePullAddress)

        mVChatId = data.VchatRoomName// 房间名
        mChatRoomId = data.ChatRoomId// 房间ID
        initAdapter(data.ChatRoomId, data.IWBRoomName)

        enterRoom(data.ChatRoomId)// 进入聊天室，任意模式都需要进入
    }

    private var enterRequest: AbortableFuture<EnterChatRoomResultData>? = null
    private fun enterRoom(roomId: String) {
        if (TextUtils.isEmpty(roomId)) {
            toast("聊天房间进入失败，请退出当前页面重新尝试")
            return
        }
        val data = EnterChatRoomData(roomId)
        data.nick = UserAccount.load()?.Name
        data.avatar = UserAccount.load()?.Avatar
        enterRequest = NIMClient.getService(ChatRoomService::class.java).enterChatRoomEx(data, 3)
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
                    else -> toast("无法使用聊天室功能，请联系管理员。错误码：$code")
                }
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
    private var videoCapture: AVChatCameraCapturer? = null // 视频采集模块

    private fun initVideo() {
        // 开启音视频引擎
        AVChatManager.getInstance().enableRtc()
        AVChatManager.getInstance().setSpeaker(true)
        // 打开视频模块
        AVChatManager.getInstance().enableVideo()
        // 设置视频采集模块
        if (videoCapture == null) {
            videoCapture = AVChatVideoCapturerFactory.createCameraCapturer()
            AVChatManager.getInstance().setupVideoCapturer(videoCapture)
        }
        val parameters = AVChatParameters()
        parameters.setBoolean(AVChatParameters.KEY_SESSION_LIVE_MODE, true)
        // 啸叫抑制 目前功能为实验性性质 这里开启，减少噪音
        parameters.setBoolean(AVChatParameters.KEY_AUDIO_HOWLING_SUPPRESS, true)
        // 默认都是观众
        parameters.setInteger(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.AUDIENCE)
        // 视频画面裁剪比例
        parameters.setInteger(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, AVChatVideoCropRatio.CROP_RATIO_1_1)
        AVChatManager.getInstance().setParameters(parameters)
    }

    private fun joinVideoRoom() {
        if (!hasVchat)
            return
        initVideo()
        AVChatManager.getInstance().joinRoom2(mVChatId, AVChatType.VIDEO, object : AVChatCallback<AVChatData> {
            override fun onSuccess(avChatData: AVChatData) {
                Logger.d("join channel success, extra:" + avChatData.extra)
                // 设置音量信号监听, 通过AVChatStateObserver的onReportSpeaker回调音量大小
//                val avChatParameters = AVChatParameters()
//                avChatParameters.setBoolean(AVChatParameters.KEY_AUDIO_REPORT_SPEAKER, true)
//                AVChatManager.getInstance().setParameters(avChatParameters)
                addCreatorView()
                // 普通用户模式
                AVChatManager.getInstance().enableAudienceRole(false)
                AVChatManager.getInstance().startVideoPreview()

                mCreator?.let {
                    val mData = EbdCustomNotification("live", "1", VIDEO_IN_CONFIRM, "STUDENT", loginId,
                            "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                    MsgManager.instance().sendP2PCustomNotification(it, mData)
                }
            }

            override fun onFailed(i: Int) {
                Logger.d("join channel failed, code:$i")
                if (i == 404) {
                    hintView.visibility = View.VISIBLE
                    tv_subtitle.text = "如需刷新，请返回后重新进入"
                }
            }

            override fun onException(throwable: Throwable) {
                Logger.e("互动直播异常：${throwable.message}")
            }
        })
    }

    /**
     * 添加主播界面
     */
    private fun addCreatorView() {
        mCreator?.let {
            Flowable.just(it).delay(300, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val remoteRender = AVChatTextureViewRenderer(this)
                        AVChatManager.getInstance().setupRemoteVideoRender(mCreator, remoteRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
                        addIntoPreviewLayout(remoteRender, mCreatorView)
                        hintView.visibility = View.GONE
                    }, {
                        hintView.visibility = View.VISIBLE
                    })
        }
    }

    /**
     * 切换角色
     * @param isAudience {@code true} 观众模式，{@code false} 普通用户模式
     */
    private fun switchAudienceRole(isAudience: Boolean) {
        mOnline = !isAudience
        live_texture.visibility = if (isAudience) View.VISIBLE else View.GONE
        mCreatorView.visibility = if (isAudience) View.GONE else View.VISIBLE
        if (isAudience) {
            leaveRoom()
            clearRenderView()
            initPlayerKit(mLivePullAddress)
            setApplyBtnStyle(1)
        } else {
            setApplyBtnStyle(2)
            player?.onActivityStop(true)
            joinVideoRoom()
        }
    }

    private fun clearRenderView() {
        mCreatorView.removeAllViews()
        mStudent1.removeAllViews()
        mStudent2.removeAllViews()
        mStudent3.removeAllViews()
    }

    /**
     * 离开互动直播房间
     */
    private fun leaveRoom() {
        if (TextUtils.isEmpty(mVChatId))
            return
        // 关闭视频预览
        AVChatManager.getInstance().stopVideoPreview()
        // 关闭音视频引擎
        AVChatManager.getInstance().disableRtc()
        // 如果是视频通话，关闭视频模块
        AVChatManager.getInstance().disableVideo()
        // 离开房间 不需要回调
        AVChatManager.getInstance().leaveRoom2(mVChatId, null)
        videoCapture = null
    }

    private fun addIntoPreviewLayout(surfaceView: TextureView?, viewLayout: ViewGroup) {
        if (surfaceView == null) {
            return
        }
        if (surfaceView.parent != null)
            (surfaceView.parent as ViewGroup).removeView(surfaceView)
        viewLayout.removeAllViews()
        viewLayout.addView(surfaceView)
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

    /**
     * 下麦
     */
    private fun downMic(isKickOut: Boolean = true) {
        if (isKickOut) {
            toast("老师已将你踢下线")
        }
        switchAudienceRole(true)
        mCreator?.let {
            val mData = EbdCustomNotification("live", "1", VIDEO_OUT_CONFIRM, "STUDENT", loginId,
                    "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
            MsgManager.instance().sendP2PCustomNotification(it, mData)
        }
    }

    /**
     * 服务端30秒不响应，自动取消
     */
    private fun btnTask() {
        mTaskDisposable = RxCounter.tick(30).doOnComplete {
            setApplyBtnStyle(1)
            mCreator?.let {
                val mData = EbdCustomNotification("live", "1", VIDEO_CANCEL, "STUDENT", loginId,
                        "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                MsgManager.instance().sendP2PCustomNotification(it, mData)
            }
        }.subscribe()
    }

    /**
     * 恢复成普通观众
     * @param type 1.普通观众，可申请 2.连麦中，可取消 3.申请中
     */
    private fun setApplyBtnStyle(type: Int) {
        when (type) {
            1 -> {
                tv_mic_status.text = "申请连麦"
                mBtnApplyVideo.setBackgroundResource(R.drawable.bg_arc_trans)
                val mDrawable = ContextCompat.getDrawable(this, R.drawable.ic_video_capture)
                mDrawable?.let { mSrc ->
                    mSrc.setBounds(0, 0, mDrawable.minimumWidth, mDrawable.minimumHeight)
                    tv_mic_status.setCompoundDrawables(mSrc, null, null, null)
                }
            }
            2 -> {
                tv_mic_status.text = "连麦中..."
                mBtnApplyVideo.setBackgroundResource(R.drawable.bg_arc_trans_accent)
                val mDrawable = ContextCompat.getDrawable(this, R.drawable.ic_video_capture_cancel)
                mDrawable?.let { mSrc ->
                    mSrc.setBounds(0, 0, mDrawable.minimumWidth, mDrawable.minimumHeight)
                    tv_mic_status.setCompoundDrawables(mSrc, null, null, null)
                }
            }
            3 -> {
                tv_mic_status.text = "申请中..."
                mBtnApplyVideo.setBackgroundResource(R.drawable.bg_arc_trans_accent)
                val mDrawable = ContextCompat.getDrawable(this, R.drawable.ic_video_capture_cancel)
                mDrawable?.let { mSrc ->
                    mSrc.setBounds(0, 0, mDrawable.minimumWidth, mDrawable.minimumHeight)
                    tv_mic_status.setCompoundDrawables(mSrc, null, null, null)
                }
            }
        }
    }

    /**
     * 老师邀请学生上麦，学生二次确认
     */
    private fun showUpMicDialog() {
        val dialog = FancyDialogFragment.create()
        dialog.setCanCancelOutside(false)
                .setLayoutRes(R.layout.dialog_live_confirm_up)
                .setWidth(this, 300)
                .setViewListener {
                    it.apply {
                        mBtnConfirm.setOnClickListener {
                            switchAudienceRole(false)
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
        val names = hasPermissionList.split(",")
        Logger.d("连麦的同学：$hasPermissionList")

        mRender1 = false
        mRender2 = false
        mRender3 = false
        mStudent1.removeAllViews()
        mStudent2.removeAllViews()
        mStudent3.removeAllViews()

        if (!names.contains(NetEaseCache.getAccount()))
            return

        mAddTask?.dispose()
        mAddTask = Flowable.fromIterable(names)
                .take(3)
                .delay(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val remoteRender = AVChatTextureViewRenderer(this)
                    if (it == NetEaseCache.getAccount()) {
                        AVChatManager.getInstance().setupLocalVideoRender(remoteRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
                    } else {
                        AVChatManager.getInstance().setupRemoteVideoRender(it, remoteRender, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
                    }
                    if (!mRender1) {
                        mRender1 = true
                        addIntoPreviewLayout(remoteRender, mStudent1)
                    } else if (!mRender2) {
                        mRender2 = true
                        addIntoPreviewLayout(remoteRender, mStudent2)
                    } else if (!mRender3) {
                        mRender3 = true
                        addIntoPreviewLayout(remoteRender, mStudent3)
                    }
                }, {
                    refreshLayoutStatus()
                }, {
                    refreshLayoutStatus()
                })
    }

    private fun refreshLayoutStatus() {
        mStudent1.visibility = if (mRender1) View.VISIBLE else View.GONE
        mStudent2.visibility = if (mRender2) View.VISIBLE else View.GONE
        mStudent3.visibility = if (mRender3) View.VISIBLE else View.GONE
    }

    //----------------------------VIDEO处理
    // 初始化播放器相关
    private fun initVideoView() {
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

        NetClient.videoService()
                .playRecord(id, 0)
                .enqueue(object : NetCallBack<ResponseBody>() {
                    override fun onSucceed(response: ResponseBody) {}
                    override fun onFailure() {}
                })
        videoView.setOnCompletionListener {
            NetClient.videoService()
                    .playRecord(id, videoView.duration)
                    .enqueue(object : NetCallBack<ResponseBody>() {
                        override fun onSucceed(response: ResponseBody) {}
                        override fun onFailure() {}
                    })
        }
    }
    //----------------------------VIDEO处理

    //-----------------网易播放组件
    private lateinit var config: SDKOptions
    private var player: LivePlayer? = null

    private fun initPlayer() {
        config = SDKOptions()
        config.privateConfig = NEPlayerConfig()
        PlayerManager.init(this, config)
    }

    private fun initPlayerKit(mVideoPath: String) {
        val options = VideoOptions()
        options.hardwareDecode = true
        options.bufferStrategy = VideoBufferStrategy.LOW_LATENCY
        player = PlayerManager.buildLivePlayer(this, mVideoPath, options)
        start()
        player?.setupRenderView(live_texture, VideoScaleMode.FIT)
    }

    private fun start() {
        player?.registerPlayerObserver(playerObserver, true)
        player?.start()
    }

    private val playerObserver = object : INetPlayerKit {
        override fun onPreparing() {
            super.onPreparing()
//            tvLoading.visibility = View.VISIBLE
        }

        override fun onFirstVideoRendered() {
            super.onFirstVideoRendered()
//            tvLoading.visibility = View.GONE
            hintView.visibility = View.GONE
        }

        override fun onError(code: Int, extra: Int) {
            if (code == CauseCode.CODE_VIDEO_PARSER_ERROR) {
                val build = AlertDialog.Builder(this@LiveActivity)
                build.setTitle("播放错误").setMessage("视频解析出错").setPositiveButton("确定", null).setCancelable(false)
                        .show()
            } else {
                retryTask?.dispose()
                retryTask = Flowable.just(mLivePullAddress)
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            leaveRoom()
                            clearRenderView()
                            initPlayerKit(it)
                        }, {

                        })
            }
        }
    }
    private var retryTask: Disposable? = null

    private fun releasePlayer() {
        player?.registerPlayerObserver(playerObserver, false)
        player?.setupRenderView(null, VideoScaleMode.NONE)
        live_texture.releaseSurface()
        player?.stop()
        player = null
    }

    //------------------全屏切换
    private fun updatePlayer(isFull: Boolean) {
        val lp: LinearLayout.LayoutParams = mVideoContainer.layoutParams as LinearLayout.LayoutParams
        if (isFull) {
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT
            lp.height = LinearLayout.LayoutParams.MATCH_PARENT
            hover.visibility = View.GONE
        } else {
            lp.width = 0
            lp.height = LinearLayout.LayoutParams.MATCH_PARENT
            lp.weight = 1f
            hover.visibility = View.VISIBLE
        }
        mVideoContainer.layoutParams = lp
    }

    private fun screenStatus(isFull: Boolean) {
        if (isFull){
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            updatePlayer(true)
            mToolbar.visibility = View.GONE
        }else{
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            updatePlayer(false)
            mToolbar.visibility = View.VISIBLE
        }
    }
}
