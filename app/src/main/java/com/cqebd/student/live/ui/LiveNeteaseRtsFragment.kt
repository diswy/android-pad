package com.cqebd.student.live.ui


import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.live.custom.NormalAttachment
import com.cqebd.student.live.entity.EbdCustomNotification
import com.cqebd.student.live.helper.IWB_CANCEL
import com.cqebd.student.live.helper.IWB_IN
import com.cqebd.student.live.helper.MsgManager
import com.cqebd.student.netease.doodle.ActionTypeEnum
import com.cqebd.student.netease.doodle.DoodleView
import com.cqebd.student.netease.doodle.SupportActionType
import com.cqebd.student.netease.doodle.TransactionCenter
import com.cqebd.student.netease.doodle.action.MyPath
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.UserAccount
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.rts.RTSCallback
import com.netease.nimlib.sdk.rts.RTSManager2
import com.netease.nimlib.sdk.rts.model.RTSData
import com.netease.nimlib.sdk.rts.model.RTSTunData
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import kotlinx.android.synthetic.main.fragment_live_netease_rts.*
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import java.io.UnsupportedEncodingException


/**
 * Live Netease Rts
 *
 */
class LiveNeteaseRtsFragment : BaseFragment() {
    private var mSessionId: String? = null
    private var mCreator: String? = null
    private var hasRtsPermission = false
    private var isOpenRts = false
    private var count = 0

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mSessionId = arguments?.getString("rtsName")
        mCreator = arguments?.getString("creator")
        return inflater?.inflate(R.layout.fragment_live_netease_rts, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        loginRts()
        registerObservers(true)
    }

    override fun bindEvents() {
        mBtnApply.setOnClickListener {
            if (!hasRtsPermission) {
                mCreator?.let {
                    val mData = EbdCustomNotification("live", "1", IWB_IN, "STUDENT", loginId,
                            "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                    MsgManager.instance().sendP2PCustomNotification(it, mData)
                    mBtnApply.isEnabled = false
                }
            } else {
                mCreator?.let {
                    val mData = EbdCustomNotification("live", "1", IWB_CANCEL, "STUDENT", loginId,
                            "TEACHER", 0, UserAccount.load()?.Name ?: "")// P2P自定义通知
                    MsgManager.instance().sendP2PCustomNotification(it, mData)
                }
            }
        }

        mBtnClear.setOnClickListener {
            mDoodleView.clear()
        }

        //paintback()
    }

    override fun onDestroy() {
        registerObservers(false)
        super.onDestroy()
    }

    private fun loginRts(){
        RTSManager2.getInstance().joinSession(mSessionId, false, object : RTSCallback<RTSData> {
            override fun onSuccess(rtsData: RTSData) {
                isOpenRts = true
                registerObservers(false)
                registerObservers(true)
                initDoodleView(null)
                Logger.e("这是${this@LiveNeteaseRtsFragment.count}次登录成功")
                Logger.i("join rts success rts extra:" + rtsData.extra)
            }

            override fun onFailed(i: Int) {
                isOpenRts = false
                count++
                Logger.e("这是${this@LiveNeteaseRtsFragment.count}次登录失败")
                if (count < 5){
                    loginRts()
                }
                Logger.i("join rts session failed, code:$i")
            }

            override fun onException(throwable: Throwable) {

            }
        })
    }

    private fun initDoodleView(account: String?) {
        mDoodleView.setEnableView(false)
        // add support ActionType
        SupportActionType.getInstance().addSupportActionType(ActionTypeEnum.Path.value, MyPath::class.java)
        mDoodleView.init(mSessionId, account, DoodleView.Mode.BOTH, Color.WHITE, Color.BLACK, context, null)
        mDoodleView.setPaintSize(3)
        mDoodleView.setPaintType(ActionTypeEnum.Path.value)

        // adjust paint offset
        Handler(Looper.getMainLooper()).postDelayed({
            val frame = Rect()
            activity!!.window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top
            Log.i("Doodle", "statusBarHeight =$statusBarHeight")

            val marginTop = mDoodleView.getTop()
            Log.i("Doodle", "doodleView marginTop =$marginTop")

            val marginLeft = mDoodleView.getLeft()
            Log.i("Doodle", "doodleView marginLeft =$marginLeft")

            val offsetX = marginLeft.toFloat()
            val offsetY = statusBarHeight + dip(285)
            mDoodleView.setPaintOffset(offsetX, offsetY.toFloat())
        }, 50)
    }

    private fun registerObservers(register: Boolean) {
        NIMClient.getService(ChatRoomServiceObserver::class.java).observeReceiveMessage(incomingChatRoomMsg, register)
        RTSManager2.getInstance().observeReceiveData(mSessionId, receiveDataObserver, register)
    }

    private val incomingChatRoomMsg: Observer<List<ChatRoomMessage>> = Observer { messages ->
        val mMsgSingle = messages[messages.size - 1]
        when (mMsgSingle.msgType) {
            MsgTypeEnum.custom -> {
                if (mMsgSingle.attachment is NormalAttachment) {
                    val attachment = mMsgSingle.attachment as NormalAttachment
                    if (attachment.mCustomMsg?.name == "ppt") {
                        if (!isOpenRts){
                            loginRts()
                        }
                        Logger.d(attachment.mCustomMsg?.content)
                        GlideApp.with(this@LiveNeteaseRtsFragment)
                                .asBitmap()
                                .load(attachment.mCustomMsg?.content)
                                .into(object : SimpleTarget<Bitmap>() {
                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                        mDoodleView.setImageView(resource)
                                    }
                                })
                    }else if (attachment.mCustomMsg?.name == "iwb"){
                        toast("收到iwb")
                        if (!isOpenRts){
                            println("执行登录")
                            loginRts()
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    /**
     * 监听收到对方发送的通道数据
     */
    private val receiveDataObserver = Observer<RTSTunData> { rtsTunData ->
        Logger.i("receive data")
        var data = "[parse bytes error]"
        try {
            data = String(rtsTunData.data, 0, rtsTunData.length)
            Logger.e(data)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        TransactionCenter.getInstance().onReceive(mSessionId, rtsTunData.account, data)
    }

    fun setRtsEnable(isEnable: Boolean) {
        mDoodleView.setEnableView(isEnable)
        mBtnApply.isEnabled = true
    }

    fun setBtnChecked(isChecked: Boolean) {
        mBtnApply.isEnabled = isChecked
    }

    fun setCreator(creator: String) {
        mCreator = creator
    }

}
