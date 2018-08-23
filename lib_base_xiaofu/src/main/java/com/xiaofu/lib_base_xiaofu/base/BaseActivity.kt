package com.xiaofu.lib_base_xiaofu.base

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.jaeger.library.StatusBarUtil
import com.xiaofu.lib_base_xiaofu.R

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getView(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView()
        window.setBackgroundDrawable(null)
        initialize()
        bindEvent()
    }

    protected open fun statusBarSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        StatusBarUtil.setColor(this, resources.getColor(R.color.white), 0)
    }

    protected open fun setView() {
        setContentView(getView())
    }

    protected open fun initialize() {

    }

    protected open fun bindEvent() {

    }

    protected open fun setFullScreen() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    protected open fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

}