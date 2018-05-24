package com.cqebd.student.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.support.multidex.MultiDex
import android.text.TextUtils
import com.cqebd.student.BuildConfig
import com.cqebd.student.R
import com.cqebd.student.db.dao.DaoMaster
import com.cqebd.student.db.dao.DaoSession
import com.cqebd.student.netease.helper.ChatRoomHelper
import com.cqebd.student.netease.modle.custom.CustomAttachParser
import com.cqebd.student.tools.getNeteaseLoginInfo
import com.cqebd.student.tools.getValue
import com.cqebd.student.ui.StartActivity
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.StatusBarNotificationConfig
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.util.NIMUtil
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.squareup.leakcanary.LeakCanary
import com.umeng.commonsdk.UMConfigure
import gorden.lib.anko.static.logInit
import gorden.util.XLog

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

        println("BuildConfig.SHOW_LOG = ${BuildConfig.SHOW_LOG}")
        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .tag("diswy")
                .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.SHOW_LOG
            }
        })
        // TODO:遗留代码，额外的日志打印
        logInit(true, "app_log")
        XLog.init(false, "app_log")

        // 网易云信
        NIMClient.init(this, getNeteaseLoginInfo(), null)
        if (NIMUtil.isMainProcess(this)) {
            // 注册自定义消息附件解析器
            NIMClient.getService(MsgService::class.java).registerCustomAttachmentParser(CustomAttachParser())

            ChatRoomHelper.init()
        }

        // 友盟
        UMConfigure.setLogEnabled(true)
        UMConfigure.init(this, "5af25b66b27b0a5574000090", "server", UMConfigure.DEVICE_TYPE_PHONE, null)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun options(): SDKOptions {
        val options = SDKOptions()

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        val config = StatusBarNotificationConfig()
        config.notificationEntrance = StartActivity::class.java // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.ic_launcher
        // 呼吸灯配置
        config.ledARGB = Color.GREEN
        config.ledOnMs = 1000
        config.ledOffMs = 1500
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.netease.nim.demo/raw/msg";
        options.statusBarNotificationConfig = config

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用采用默认路径作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
//        val sdkPath = getAppCacheDir(this) +"/nim" // 可以不设置，那么将采用默认路径
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
//        options.sdkStorageRootPath = sdkPath

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
//        options.thumbnailSize = ${Screen.width} / 2

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
//        options.userInfoProvider = object :UserInfoProvider {
//            override fun getUserInfo(account: String?): UserInfo {
//                return UserInfo()
//            }
//
//            override fun getAvatarForMessageNotifier(sessionType: SessionTypeEnum?, sessionId: String?): Bitmap {
//            }
//
//            override fun getDisplayNameForMessageNotifier(account: String?, sessionId: String?, sessionType: SessionTypeEnum?): String {
//            }
//        }
        return options
    }
}