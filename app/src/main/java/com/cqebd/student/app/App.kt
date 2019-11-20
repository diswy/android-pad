package com.cqebd.student.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.multidex.MultiDex
import com.cqebd.student.BuildConfig
import com.cqebd.student.R
import com.cqebd.student.db.dao.DaoMaster
import com.cqebd.student.db.dao.DaoSession
import com.cqebd.student.live.helper.MsgManager
import com.cqebd.student.netease.helper.ChatRoomHelper
import com.cqebd.student.live.custom.CustomAttachParser
import com.cqebd.student.tools.getNeteaseLoginInfo
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.StatusBarNotificationConfig
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.util.NIMUtil
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.squareup.leakcanary.LeakCanary
import com.umeng.commonsdk.UMConfigure
import gorden.lib.anko.static.logInit
import gorden.util.XLog
import org.jetbrains.anko.tableRow
import java.lang.reflect.InvocationTargetException

/**
 * 描述
 * Created by gorden on 2018/2/28.
 */
class App : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context

        private var mDaoSession: DaoSession? = null
        private val DB_NAME = "schoolex.db"
        fun getDaoSession(): DaoSession {
            if (mDaoSession == null) {
                val mHelper = DaoMaster.DevOpenHelper(mContext, DB_NAME)
                val daoMaster = DaoMaster(mHelper.writableDatabase)
                mDaoSession = daoMaster.newSession()
            }
            return mDaoSession!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)

        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("diswy")
                .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.SHOW_LOG
            }
        })
        // TODO:遗留代码，额外的日志打印
        logInit(false, "app_log")
        XLog.init(false, "app_log")

        // 网易云信
        // 每次都需要登录，这里不是用缓存登录getNeteaseLoginInfo()
        NIMClient.init(this, null, null)
        if (NIMUtil.isMainProcess(this)) {
            // 注册自定义消息附件解析器
            NIMClient.getService(MsgService::class.java).registerCustomAttachmentParser(CustomAttachParser())
//            ChatRoomHelper.init()
//            MsgManager.instance().init()
        }

        // 友盟
        UMConfigure.setLogEnabled(false)
        UMConfigure.init(this, "5af25b66b27b0a5574000090", "server", UMConfigure.DEVICE_TYPE_PHONE, null)

        fixSamsungClipboardUIManagerBug()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * 三星手机ClipboardUIManager内存泄漏BUG
     * 多出现在登录界面
     */
    private fun fixSamsungClipboardUIManagerBug() {

        if ("samsung" != Build.MANUFACTURER)
            return

        try {
            val cls = Class.forName("android.sec.clipboard.ClipboardUIManager")
            val method = cls.getDeclaredMethod("getInstance", Context::class.java)
            method.isAccessible = true
            method.invoke(null, this)
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.e("Samsung Bug：${e.message}")
        }

    }

}