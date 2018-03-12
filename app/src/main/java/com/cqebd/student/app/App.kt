package com.cqebd.student.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import gorden.lib.anko.static.logInit

/**
 * 描述
 * Created by gorden on 2018/2/28.
 */
class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        logInit(true,"app_log")
    }
}