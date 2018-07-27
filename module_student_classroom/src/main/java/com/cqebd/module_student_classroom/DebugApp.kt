package com.cqebd.module_student_classroom

import android.app.Application
import com.cqebd.lib_netease.helper.neteaseInit
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.xiaofu.lib_base_xiaofu.api.ApiManager

class DebugApp : Application() {

    companion object {
        lateinit var instance: DebugApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 网易云信初始化
        neteaseInit(this)

        // Log日志
        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("diswy")
                .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.SHOW_LOG
            }
        })

        ApiManager.getInstance().initChuckOkHttpClient(this)
    }
}