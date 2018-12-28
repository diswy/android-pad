package com.cqebd.student.shortcuts

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.WindowManager
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.event.FINISH
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.tools.loginId
import com.cqebd.student.ui.AgentWebActivity
import com.cqebd.student.viewmodel.MineViewModel
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import gorden.rxbus.RxBus
import gorden.rxbus.Subscribe
import kotlinx.android.synthetic.main.index_fragment_layout.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class GuidePageActivity : BaseActivity() {
    private lateinit var userModel:MineViewModel

    override fun setContentView() {
        //取消状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.index_fragment_layout)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        updateTime()
        RxBus.get().register(this)
        userModel = ViewModelProviders.of(this).get(MineViewModel::class.java)
        userModel.userAccount.observe(this, Observer {
            it?.apply {
                index_mine_name.text = Name
                GlideApp.with(App.mContext).asBitmap().circleCrop().load(Avatar).placeholder(R.drawable.ic_avatar).into(index_mine_photo)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        userModel.refreshUser()
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.get().unRegister(this)
    }

    override fun bindEvents() {
        join_class.setOnClickListener {
            try {
                val intent = packageManager.getLaunchIntentForPackage("com.clovsoft.cqedu.student")
                startActivity(intent)
            } catch (e: Exception) {
                toast("请先安装互动课堂")
            }
        }

        // 视频
        video.setOnClickListener { startActivity<MainActivity>("guide_position" to 0, "child_guide_position" to 0) }
        subscrilbe.setOnClickListener { startActivity<MainActivity>("guide_position" to 0, "child_guide_position" to 1) }
        collection.setOnClickListener { startActivity<MainActivity>("guide_position" to 0, "child_guide_position" to 3) }
        // 作业
        homework.setOnClickListener { startActivity<MainActivity>("guide_position" to 1, "child_guide_position" to 0) }
        work_wrong.setOnClickListener { startActivity<MainActivity>("guide_position" to 1, "child_guide_position" to 1) }
        work_share.setOnClickListener { startActivity<MainActivity>("guide_position" to 1, "child_guide_position" to 2) }
        work_collection.setOnClickListener { startActivity<MainActivity>("guide_position" to 1, "child_guide_position" to 3) }
        // 我的
        index_mine.setOnClickListener { startActivity<MainActivity>("guide_position" to 2) }

        schedule.setOnClickListener { startActivity<MainActivity>("guide_position" to 0, "child_guide_position" to 2) }
        message.setOnClickListener { startActivity<MainActivity>("guide_position" to 2, "child_guide_position" to 2) }
        red_flower.setOnClickListener {
            val flowerFormat = "Report/ReportFlower?ID=$loginId"
            val url = WorkService.BASE_WEB_URL.plus(flowerFormat)
            startActivity<AgentWebActivity>("url" to url)
        }
        dianzan.setOnClickListener {
            val wonderFormat = "Report/ReportAppraisal?ID=$loginId"
            val url = WorkService.BASE_WEB_URL.plus(wonderFormat)
            startActivity<AgentWebActivity>("url" to url)
        }
    }

    private val mHandler = Handler()
    private fun updateTime() {
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                val format = SimpleDateFormat("HH : mm", Locale.CHINA)
                val times = format.format(Date())

                val format2 = SimpleDateFormat("MM月dd日", Locale.CHINA)
                val times2 = format2.format(Date())

                val cal = Calendar.getInstance()
                val i = cal.get(Calendar.DAY_OF_WEEK)

                mHandler.post {
                    title_time.text = times
                    title_date.text = times2
                    title_week.text = when (i) {
                        1 -> "星期日"
                        2 -> "星期一"
                        3 -> "星期二"
                        4 -> "星期三"
                        5 -> "星期四"
                        6 -> "星期五"
                        7 -> "星期六"
                        else -> "错误日期"
                    }
                }
            }

        }
        timer.schedule(timerTask, 0, 1000)
    }

    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次退出点点课")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                System.exit(0)
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

//    @Subscribe(code = 999)
//    fun myFinish(myCode: Int) {
//        if (myCode == 999){
//            finish()
//            System.exit(0)
//        }
//    }
    @Subscribe(code = FINISH)
    fun myFinish(myCode: String) {
        finish()
    }
}
