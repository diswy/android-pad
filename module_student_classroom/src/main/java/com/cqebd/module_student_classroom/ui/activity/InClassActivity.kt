package com.cqebd.module_student_classroom.ui.activity

import android.graphics.Color
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.cqebd.lib_netease.doodle.ActionTypeEnum
import com.cqebd.lib_netease.doodle.DoodleView
import com.cqebd.lib_netease.doodle.SupportActionType
import com.cqebd.lib_netease.doodle.TransactionCenter
import com.cqebd.lib_netease.doodle.action.MyPath
import com.cqebd.lib_netease.helper.IJoinListener
import com.cqebd.lib_netease.helper.joinRTSRoom
import com.cqebd.module_student_classroom.R
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.rts.RTSManager2
import com.netease.nimlib.sdk.rts.model.RTSTunData
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.base.BaseActivity
import kotlinx.android.synthetic.main.activity_in_class.*
import java.io.UnsupportedEncodingException

class InClassActivity : BaseActivity() {
    override fun getView(): Int = R.layout.activity_in_class

    override fun initialize() {
        joinRTSRoom(this, "xiaofudejiaoshi",object :IJoinListener{
            override fun joinSuccess() {

                initDoodle()

            }
        })
    }

    private fun initDoodle() {
        SupportActionType.getInstance().addSupportActionType(ActionTypeEnum.Path.value, MyPath::class.java)
        mDoodle.init("xiaofudejiaoshi", null, DoodleView.Mode.BOTH, Color.WHITE, Color.BLACK, this, null)
        mDoodle.setPaintSize(3)
        mDoodle.setPaintType(ActionTypeEnum.Path.value)

        // adjust paint offset
        Handler(Looper.getMainLooper()).postDelayed({
            val frame = Rect()
            window.decorView.getWindowVisibleDisplayFrame(frame)
            val statusBarHeight = frame.top
            Log.i("Doodle", "statusBarHeight =$statusBarHeight")

            val marginTop = mDoodle.getTop()
            Log.i("Doodle", "doodleView marginTop =$marginTop")

            val marginLeft = mDoodle.getLeft()
            Log.i("Doodle", "doodleView marginLeft =$marginLeft")

            val offsetX = marginLeft.toFloat()
            val offsetY = statusBarHeight
            mDoodle.setPaintOffset(offsetX, offsetY.toFloat())
        }, 50)


        RTSManager2.getInstance().observeReceiveData("xiaofudejiaoshi", receiveDataObserver, true)
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

        TransactionCenter.getInstance().onReceive("xiaofudejiaoshi", rtsTunData.account, data)
    }
}
