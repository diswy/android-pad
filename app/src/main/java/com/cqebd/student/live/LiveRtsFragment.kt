package com.cqebd.student.live


import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.netease.NetEaseCache
import com.cqebd.student.netease.doodle.*
import com.cqebd.student.netease.doodle.action.MyPath
import com.cqebd.student.netease.helper.ChatRoomMemberCache
import com.cqebd.student.netease.modle.Document
import com.cqebd.student.netease.modle.FileDownloadStatusEnum
import com.cqebd.student.netease.util.ScreenUtil
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.document.DocumentManager
import com.netease.nimlib.sdk.document.model.DMData
import com.netease.nimlib.sdk.rts.RTSCallback
import com.netease.nimlib.sdk.rts.RTSManager2
import com.netease.nimlib.sdk.rts.model.RTSData
import com.netease.nimlib.sdk.rts.model.RTSTunData
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_live_rts.*
import java.io.UnsupportedEncodingException
import java.util.HashMap


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

//        activity!!.runOnUiThread { showLoadingText() }
//
//        // 文档第0页，表示退出文档分享
//        if (transaction.currentPageNum == 0) {
//            isFileMode = false
//            closeFileShare()
//            hideLoadingText()
//            return
//        }
//        // 如果文档信息已经下载过了，就不用载了。直接去载翻页图片
//        isFileMode = true
//
//        if (docData != null && docData.getDocId() == transaction.docId) {
//            doDownloadPage(document, transaction.currentPageNum)
//            return
//        }
//        Logger.i(TAG, "doc id:" + transaction.docId)
//        DocumentManager.getInstance().querySingleDocumentData(transaction.docId, object : RequestCallback<DMData> {
//            override fun onSuccess(dmData: DMData) {
//                Logger.i(TAG, "query doc success")
//                docData = dmData
//                document = Document(dmData, HashMap(), FileDownloadStatusEnum.NotDownload)
//                doDownloadPage(document, transaction.currentPageNum)
//            }
//
//            override fun onFailed(i: Int) {
//                Logger.i(TAG, "query doc failed, code:$i")
//                showRetryLoadingText()
//            }
//
//            override fun onException(throwable: Throwable) {
//                Logger.i(TAG, "query doc exception:" + throwable.toString())
//            }
//        })
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
                // 主播的白板默认为开启状态
//                if (roomInfo.getCreator() == NetEaseCache.getAccount()) {
//                    ChatRoomMemberCache.getInstance().isRTSOpen = true
//                    updateRTSFragment()
//                }
                Toast.makeText(activity, "加入多人白板房间成功", Toast.LENGTH_SHORT).show()
            }

            override fun onFailed(i: Int) {
                Logger.d("join rts session failed, code:$i")
                Toast.makeText(activity, "join rts session failed, code:$i", Toast.LENGTH_SHORT).show()
            }

            override fun onException(throwable: Throwable) {

            }
        })


        RTSManager2.getInstance().observeReceiveData(mSessionId, receiveDataObserver, true)

        initDoodleView(null)
        registerObservers(true)
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
        mDoodleView?.end()
        registerObservers(false)
        super.onDestroy()
    }


    private fun registerObservers(register: Boolean) {
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

        mDoodleView.init(mSessionId, account, DoodleView.Mode.BOTH, Color.WHITE, Color.RED, context, this)


        mDoodleView.setPaintSize(3)
        mDoodleView.setPaintType(ActionTypeEnum.Path.value)

        // adjust paint offset
        Handler(getMainLooper()).postDelayed(Runnable {
            val frame = Rect()
            activity!!.window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top
            Log.i("Doodle", "statusBarHeight =$statusBarHeight")

            val marginTop = mDoodleView.getTop()
            Log.i("Doodle", "doodleView marginTop =$marginTop")

            val marginLeft = mDoodleView.getLeft()
            Log.i("Doodle", "doodleView marginLeft =$marginLeft")

            val offsetX = marginLeft.toFloat()
            val offsetY = (statusBarHeight + marginTop + ScreenUtil.dip2px(220f) + ScreenUtil.dip2px(40f)).toFloat()

            mDoodleView.setPaintOffset(offsetX, offsetY)
            Log.i("Doodle", "client1 offsetX = $offsetX, offsetY = $offsetY")
        }, 50)
    }


}
