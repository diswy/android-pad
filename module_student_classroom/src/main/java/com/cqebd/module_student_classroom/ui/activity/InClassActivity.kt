package com.cqebd.module_student_classroom.ui.activity

import android.support.v4.app.Fragment
import com.cqebd.lib_netease.helper.sendP2P
import com.cqebd.module_student_classroom.R
import com.cqebd.module_student_classroom.ui.fragment.LibsFragment
import com.cqebd.module_student_classroom.ui.fragment.RTSFragment
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.xiaofu.lib_base_xiaofu.base.BaseActivity
import kotlinx.android.synthetic.main.activity_in_class.*
import org.jetbrains.anko.toast

class InClassActivity : BaseActivity() {

    private val mRts by lazy { RTSFragment() }
    private val mLibs by lazy { LibsFragment() }

    override fun getView(): Int = R.layout.activity_in_class

    override fun setView() {
        setFullScreen()
        keepScreenOn()
        super.setView()
    }

    override fun initialize() {
        registerObservers(true)
    }

    override fun onDestroy() {
        registerObservers(false)
        super.onDestroy()
    }

    override fun bindEvent() {
        btnBack.setOnClickListener {
            mRadioGroup.clearCheck()
//            finish()
        }

        mRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.mBtnRts -> switchFragment(mRts)
                R.id.mBtnLibs -> switchFragment(mLibs)
                R.id.mBtnAnswers -> sendP2P("student_1420","学生发送的内容~")
                R.id.mBtnCheckIn -> toast("点名")
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.mRealContainer, fragment)
                .commit()
    }

    private fun registerObservers(register: Boolean) {
        NIMClient.getService(MsgServiceObserve::class.java).observeCustomNotification(customNotification, register)
    }

    private val customNotification: Observer<CustomNotification> = Observer {
        toast(it.content)
    }
}
