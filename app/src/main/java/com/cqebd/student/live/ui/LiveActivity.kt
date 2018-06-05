package com.cqebd.student.live.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.view.KeyEvent
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.live.entity.CustomNoticifation
import com.cqebd.student.live.entity.LiveByRemote
import com.cqebd.student.live.entity.PullAddress
import com.cqebd.student.live.helper.MsgManager
import com.cqebd.student.live.helper.VIDEO_IN
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.netease.helper.MsgHelper
import com.cqebd.student.test.BlankFragment
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.UserAccount
import com.google.gson.Gson
import com.netease.nimlib.sdk.AbortableFuture
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.avchat.AVChatCallback
import com.netease.nimlib.sdk.avchat.AVChatManager
import com.netease.nimlib.sdk.avchat.constant.AVChatType
import com.netease.nimlib.sdk.avchat.constant.AVChatUserRole
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoCropRatio
import com.netease.nimlib.sdk.avchat.constant.AVChatVideoScalingType
import com.netease.nimlib.sdk.avchat.model.*
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_live.*
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

    override fun setContentView() {
        setContentView(R.layout.activity_live)
    }

    override fun bindEvents() {
        mToolbar.setNavigationOnClickListener {
            exitRoom()
            this.finish()
        }

        mBtnApplyVideo.setOnClickListener {

            applyVideo()

            mCreator?.let {
                val mData = CustomNoticifation(
                        "live",
                        "1",
                        VIDEO_IN,
                        "STUDENT",
                        loginId,
                        "TEACHER",
                        0,
                        UserAccount.load()?.Name ?: "")// P2P自定义通知
                MsgManager.instance().sendP2PCustomNotification(it, mData)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        exitRoom()
        return super.onKeyDown(keyCode, event)
    }

    override fun initialize(savedInstanceState: Bundle?) {
//        pageLoadView.show = true
//        pageLoadView.load()
        pageLoadView.hide()

        parseIntent()
        videoSetting()
        videoView.setLiveMode(true)// 该界面全部为直播界面
        initTag(true)
        getLiveInfo(id)
    }

    private fun parseIntent() {
        id = intent.getIntExtra("id", 0)
        hasChat = intent.getIntExtra("hasChat", 0) != 0 // 暂时对我来说没有用
        hasIWB = intent.getIntExtra("hasIWB", 0) != 0
        hasVchat = intent.getIntExtra("hasVchat", 0) != 0// 是否是互动直播
        Logger.d("id = $id hasChat = $hasChat hasIWB = $hasIWB hasVchat = $hasIWB")
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

    private fun initAdapter(roomId: String) {
        val mChat = LiveChatFragment()
        val bundle = Bundle()
        bundle.putString("roomID", roomId)
        mChat.arguments = bundle
        val mRts = LiveNeteaseRtsFragment()
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
//        mNonScrollVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
//            override fun onPageScrollStateChanged(state: Int) {
//            }
//
//            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//                when (position) {
////                    0 -> mChat.onCurrentInit()
//                }
//            }
//
//            override fun onPageSelected(position: Int) {
//
//            }
//        })
    }

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
                            videoView.setVideoPath(mPullAddress.httpPullUrl, "", R.drawable.ic_login_logo)
                            Logger.i(mPullAddress.httpPullUrl)

                            mVChatId = it.VchatRoomName
                            mVChatId = it.VchatRoomName
                            initAdapter(it.ChatRoomId)
                            mChatRoomId = it.ChatRoomId
                            enterRoom(it.ChatRoomId)// 进入聊天室
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
                toast("进入聊天室成功，聊天室ID：${result.roomInfo.roomId}教师ID：${result.roomInfo.creator}")
//                roomInfo = result.roomInfo
//                val member = result.member
//                member.roomId = roomInfo?.roomId
                //ChatRoomMemberCache.getInstance().saveMyMember(member);
                //creator = roomInfo.getCreator()
            }

            override fun onFailed(code: Int) {
                enterRequest = null
                when (code) {
                    ResponseCode.RES_CHATROOM_BLACKLIST.toInt() -> toast("你已被拉入黑名单，不能再进入聊天室")
                    ResponseCode.RES_ENONEXIST.toInt() -> toast("该聊天室不存在")
                    else -> toast("enter chat room failed, code = $code")
                }
            }

            override fun onException(exception: Throwable) {
                enterRequest = null
//                toast("enter chat room exception,$exception.message")
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


    private fun videoSetting(){
        // 开启音视频引擎
        AVChatManager.getInstance().enableRtc()
        // 打开视频模块
        AVChatManager.getInstance().enableVideo()

        // 设置视频采集模块
        if (videoCapturer == null) {
            videoCapturer = AVChatVideoCapturerFactory.createCameraCapturer()
            AVChatManager.getInstance().setupVideoCapturer(videoCapturer)
        }

//        val render = AVChatSurfaceViewRenderer(this)
//        AVChatManager.getInstance().setupLocalVideoRender(render, false, AVChatVideoScalingType.SCALE_ASPECT_BALANCED)
//        AVChatManager.getInstance().startVideoPreview()
        val parameters = AVChatParameters()
        parameters.setBoolean(AVChatParameters.KEY_SESSION_LIVE_MODE, true)
        parameters.setInteger(AVChatParameters.KEY_SESSION_MULTI_MODE_USER_ROLE, AVChatUserRole.NORMAL)
        parameters.setInteger(AVChatParameters.KEY_VIDEO_FIXED_CROP_RATIO, AVChatVideoCropRatio.CROP_RATIO_16_9)
        AVChatManager.getInstance().setParameters(parameters)
    }

    private fun applyVideo() {
        if (!hasVchat)
            return

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

            }
        })

    }

}
