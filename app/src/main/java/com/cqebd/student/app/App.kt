package com.cqebd.student.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.cqebd.student.db.dao.DaoMaster
import com.cqebd.student.db.dao.DaoSession
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
                val mHelper = DaoMaster.DevOpenHelper(mContext,DB_NAME)
                val daoMaster = DaoMaster(mHelper.writableDatabase)
                mDaoSession = daoMaster.newSession()
            }
            return mDaoSession!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        logInit(true,"app_log")
        XLog.init(false,"app_log")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);
    }
}