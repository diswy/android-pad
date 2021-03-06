package com.cqebd.module_student_classroom.ui.activity

import android.app.ActivityManager
import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.view.KeyEvent
import android.view.View
import com.cqebd.lib_netease.helper.neteaseLogin
import com.cqebd.module_student_classroom.HomeReceiver
import com.cqebd.module_student_classroom.R
import com.cqebd.module_student_classroom.adapter.PeriodAdapter
import com.cqebd.module_student_classroom.helper.showPeriodDialog
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.api.ApiManager
import com.xiaofu.lib_base_xiaofu.api.viewmodel.MyCallback
import com.xiaofu.lib_base_xiaofu.base.BaseToolbarActivity
import kotlinx.android.synthetic.main.base_refresh_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response
import android.content.Intent
import android.content.IntentFilter



class ClassroomActivity : BaseToolbarActivity() {
    private val periodAdapter by lazy { PeriodAdapter() }

    override fun setTitle(): String = "我的课程"

    override fun getView(): Int = R.layout.activity_classroom

    override fun initialize() {

        val receiver = HomeReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        registerReceiver(receiver, intentFilter)

//        unregisterReceiver(innerReceiver)

        mRv.layoutManager = GridLayoutManager(this, 2)
        periodAdapter.bindToRecyclerView(mRv)

        periodAdapter.setNewData(listOf("1", "1"))


        ApiManager.getInstance().classService
                .test()
                .enqueue(object : MyCallback<String> {
                    override fun onResponse(call: Call<String>?, response: Response<String>?) {
                        Logger.e(response?.body()!!)
                    }
                })

        neteaseLogin(this, "student_1419", "123456")

    }

    override fun bindEvent() {
        periodAdapter.setOnItemClickListener { adapter, view, position ->
            showPeriodDialog("开始上课？", View.OnClickListener {
                startActivity<InClassActivity>()
            })
        }
    }

    override fun onPause() {
        super.onPause()
        for (i in 0..49) {
            val am: ActivityManager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.moveTaskToFront(taskId, 0)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            toast("你点击了back键")
            return false
        }
        return super.onKeyDown(keyCode, event)
    }
}
