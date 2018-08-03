package com.cqebd.module_student_classroom.ui.fragment


import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import com.cqebd.lib_netease.doodle.ActionTypeEnum
import com.cqebd.lib_netease.doodle.DoodleView
import com.cqebd.lib_netease.doodle.SupportActionType
import com.cqebd.lib_netease.doodle.TransactionCenter
import com.cqebd.lib_netease.doodle.action.MyPath
import com.cqebd.lib_netease.helper.IJoinListener
import com.cqebd.lib_netease.helper.createRTSRoom
import com.cqebd.lib_netease.helper.joinRTSRoom
import com.cqebd.module_student_classroom.R
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.rts.RTSManager2
import com.netease.nimlib.sdk.rts.model.RTSTunData
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_rt.*
import org.jetbrains.anko.support.v4.dip
import java.io.UnsupportedEncodingException

/**
 * @Description:互动白板
 * @Author:小夫
 * @Date:2018/8/1 16:03
 */
class RTSFragment : BaseFragment() {

    override fun getLayoutRes(): Int = R.layout.fragment_rt

    override fun initialize(activity: Activity) {

        joinRTSRoom(activity, "baibanfangjian", object : IJoinListener {
            override fun joinSuccess() {
                initDoodle(activity)
            }
        })

        registerObservers(true)
    }

    private fun initDoodle(activity: Activity) {
        SupportActionType.getInstance().addSupportActionType(ActionTypeEnum.Path.value, MyPath::class.java)
        mDoodle.init("baibanfangjian", null, DoodleView.Mode.BOTH, Color.WHITE, Color.BLACK, context, null)
        mDoodle.setPaintSize(3)
        mDoodle.setPaintType(ActionTypeEnum.Path.value)

        // adjust paint offset
        Handler(Looper.getMainLooper()).postDelayed({
            val frame = Rect()
            activity.window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top
            val marginTop = mDoodle.top
            val marginLeft = mDoodle.left
            val offsetX = marginLeft.toFloat()
            val offsetY = statusBarHeight + marginTop + dip(100)
            mDoodle.setPaintOffset(offsetX, offsetY.toFloat())
        }, 50)
    }

    private fun registerObservers(register: Boolean) {
        RTSManager2.getInstance().observeReceiveData("baibanfangjian", receiveDataObserver, register)
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

        TransactionCenter.getInstance().onReceive("baibanfangjian", rtsTunData.account, data)
    }
}
