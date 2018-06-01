package com.cqebd.student.live.ui


import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.live.custom.DocAttachment
import com.cqebd.student.netease.doodle.*
import com.cqebd.student.netease.doodle.action.MyPath
import com.cqebd.student.netease.util.ScreenUtil
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
import kotlinx.android.synthetic.main.fragment_live_rts.*
import org.jetbrains.anko.support.v4.dip
import java.io.UnsupportedEncodingException


/**
 * 互动多人白板
 *
 */
class LiveRtsFragment : BaseFragment(), OnlineStatusObserver, DoodleView.FlipListener {
    private var docTransaction: Transaction? = null

    override fun onFlipPage(transaction: Transaction?) {
        pageFlip(transaction)
    }

    private fun pageFlip(transaction: Transaction?) {
        this.docTransaction = transaction
        if (transaction == null) {
            return
        }
    }

    override fun onNetWorkChange(isCreator: Boolean): Boolean {
        // 断网重连。主播断网重连上来，给观众发自己的同步数据
        // 观众先清空本地
        if (isCreator) {
//            mDoodleView.sendSyncPrepare()
//            postDelayed(Runnable { doodleView.sendSyncData(null) }, 50)
        } else {
            mDoodleView.clearAll()
        }
        return true
    }

    val mSessionId = "IWBRoomName_49"

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_live_rts, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {

        RTSManager2.getInstance().joinSession(mSessionId, false, object : RTSCallback<RTSData> {
            override fun onSuccess(rtsData: RTSData) {
                Logger.i("rts extra:" + rtsData.extra)
                Toast.makeText(activity, "加入多人白板房间成功", Toast.LENGTH_SHORT).show()
            }

            override fun onFailed(i: Int) {
                Toast.makeText(activity, "join rts session failed, code:$i", Toast.LENGTH_SHORT).show()
            }

            override fun onException(throwable: Throwable) {

            }
        })


        initDoodleView(null)
        registerObservers(true)
        registerObserver(true)
    }

    //-------------------监听-------------------
    private fun registerObserver(register: Boolean) {
        RTSManager2.getInstance().observeReceiveData(mSessionId, receiveDataObserver, register)

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


    override fun onResume() {
        super.onResume()
        mDoodleView.onResume()
    }

    override fun onDestroy() {
        registerObservers(false)
        super.onDestroy()
    }

    fun clearDoodleView() {
        Logger.e("clear!!!!!")
        mDoodleView?.end()
    }
//    override fun onDestroyView() {
//        mDoodleView?.end()
//        super.onDestroyView()
//    }


    private fun registerObservers(register: Boolean) {
        NIMClient.getService(ChatRoomServiceObserver::class.java).observeReceiveMessage(incomingChatRoomMsg, register)
//        ChatRoomMemberCache.getInstance().registerMeetingControlObserver(meetingControlObserver, register)
        TransactionCenter.getInstance().registerOnlineStatusObserver(mSessionId, this)
    }

    private fun initDoodleView(account: String?) {
        Toast.makeText(context, "init doodle success", Toast.LENGTH_SHORT).show()
        // add support ActionType
        SupportActionType.getInstance().addSupportActionType(ActionTypeEnum.Path.value, MyPath::class.java)
//        if (roomInfo.getCreator() == NetEaseCache.getAccount()) {
//            doodleView.init(sessionId, account, DoodleView.Mode.BOTH, Color.WHITE, Color.BLACK, context, this)
//        } else {
//            doodleView.init(sessionId, account, DoodleView.Mode.BOTH, Color.WHITE, colorMap.get(R.id.blue_color_image), context, this)
//        }

        mDoodleView.init(mSessionId, account, DoodleView.Mode.BOTH, Color.WHITE, Color.BLACK, context, this)
        mDoodleView.setPaintSize(3)
        mDoodleView.setPaintType(ActionTypeEnum.Path.value)

        // adjust paint offset
        Handler(getMainLooper()).postDelayed({
            val frame = Rect()
            activity!!.window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top
            Log.i("Doodle", "statusBarHeight =$statusBarHeight")

            val marginTop = mDoodleView.getTop()
            Log.i("Doodle", "doodleView marginTop =$marginTop")

            val marginLeft = mDoodleView.getLeft()
            Log.i("Doodle", "doodleView marginLeft =$marginLeft")

            val offsetX = marginLeft.toFloat()
            val offsetY = statusBarHeight + dip(265)
            mDoodleView.setPaintOffset(offsetX, offsetY.toFloat())
        }, 50)
    }


    private val incomingChatRoomMsg: Observer<List<ChatRoomMessage>> = Observer { messages ->
        val mMsgSingle = messages[messages.size - 1]
        when (mMsgSingle.msgType) {
            MsgTypeEnum.custom -> {
                if (mMsgSingle.attachment is DocAttachment) {
                    val attachment = mMsgSingle.attachment as DocAttachment

                    GlideApp.with(this@LiveRtsFragment)
                            .asBitmap()
                            .load(attachment.mPPTAddress)
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    mDoodleView.setImageView(resource)
                                }
                            })
                }
            }
            else -> {
            }
        }
    }

}
