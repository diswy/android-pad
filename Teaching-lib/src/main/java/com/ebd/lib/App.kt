package com.ebd.lib

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.squareup.leakcanary.LeakCanary
import com.xiaofu.lib_base_xiaofu.api.ApiManager

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        LeakCanary.install(this)
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