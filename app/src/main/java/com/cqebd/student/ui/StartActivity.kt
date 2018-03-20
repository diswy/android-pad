package com.cqebd.student.ui

import android.content.Intent
import android.os.Bundle
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.tools.RxCounter
import com.cqebd.student.tools.isLogin
import gorden.lib.anko.static.startActivity

/**
 * 启动页
 * Created by gorden on 2018/3/20.
 */
class StartActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_start)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        if (!isTaskRoot){
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER)&&intent.action==Intent.ACTION_MAIN){
                finish()
                return
            }
        }
        RxCounter.tick(1).doOnComplete {
            if (isLogin()){
                startActivity<MainActivity>()
            }else{
                startActivity<LoginActivity>()
            }
        }.subscribe()
    }
}