package com.cqebd.student.live.ui


import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.netease.doodle.ActionTypeEnum
import com.cqebd.student.netease.doodle.DoodleView
import com.cqebd.student.netease.doodle.SupportActionType
import com.cqebd.student.netease.doodle.action.MyPath
import kotlinx.android.synthetic.main.fragment_live_netease_rts.*
import org.jetbrains.anko.support.v4.dip


/**
 * Live Netease Rts
 *
 */
class LiveNeteaseRtsFragment : BaseFragment() {
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_live_netease_rts, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        initDoodleView(null)
    }

    private fun initDoodleView(account: String?) {
        // add support ActionType
        SupportActionType.getInstance().addSupportActionType(ActionTypeEnum.Path.value, MyPath::class.java)
        mDoodleView.init("", account, DoodleView.Mode.BOTH, Color.WHITE, Color.BLACK, context, null)
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


}
