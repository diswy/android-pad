package com.cqebd.student.live.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.live.entity.LiveByRemote
import com.cqebd.student.live.entity.PullAddress
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.test.BlankFragment
import com.google.gson.Gson
import com.netease.nimlib.sdk.AbortableFuture
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.ResponseCode
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

    override fun setContentView() {
        setContentView(R.layout.activity_live)
    }

    override fun bindEvents() {
        mToolbar.setNavigationOnClickListener {
            this.finish()
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
//        pageLoadView.show = true
//        pageLoadView.load()
        pageLoadView.hide()

        parseIntent()
        videoView.setLiveMode(true)// 该界面全部为直播界面
        initTag(true)
        initAdapter()
        getLiveInfo(id)
    }

    private fun parseIntent() {
        id = intent.getIntExtra("id", 0)
        hasChat = intent.getIntExtra("hasChat", 0) != 0 // 暂时对我来说没有用
        hasIWB = intent.getIntExtra("hasChat", 0) != 0
        hasVchat = intent.getIntExtra("hasChat", 0) != 0// 是否是互动直播
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

    private fun initAdapter() {
        val mChat = LiveChatFragment()
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
        mNonScrollVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
//                    0 -> mChat.onCurrentInit()
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })
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
                            videoView.setVideoPath(mPullAddress.rtmpPullUrl, "", R.drawable.ic_login_logo)
                            Logger.i(mPullAddress.hlsPullUrl)

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


}
