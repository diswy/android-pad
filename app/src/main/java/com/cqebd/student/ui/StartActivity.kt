package com.cqebd.student.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.tools.RxCounter
import com.cqebd.student.tools.isLogin
import com.cqebd.student.tools.toast
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import gorden.lib.anko.static.startActivity
import io.reactivex.functions.Consumer

/**
 * 启动页
 * Created by gorden on 2018/3/20.
 */
class StartActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_start)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        if (!isTaskRoot) {
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action == Intent.ACTION_MAIN) {
                finish()
                return
            }
        }

        RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                ,Manifest.permission.READ_EXTERNAL_STORAGE
                ,Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (!granted)
                        toast("您拒绝了必要权限")

                    RxCounter.tick(1).doOnComplete {
                        if (isLogin()) {
                            startActivity<MainActivity>()
                            finish()
                        } else {
                            startActivity<LoginActivity>()
                            finish()
                        }
                    }.subscribe()
                }
    }
}