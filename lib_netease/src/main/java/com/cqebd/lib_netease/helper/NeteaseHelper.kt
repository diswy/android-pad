package com.cqebd.lib_netease.helper

import android.content.Context
import android.util.Log
import com.cqebd.lib_netease.cache.NCache
import com.cqebd.lib_netease.string.MD5
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.rts.RTSCallback
import com.netease.nimlib.sdk.rts.RTSManager2
import com.netease.nimlib.sdk.rts.model.RTSData
import com.netease.nimlib.sdk.util.NIMUtil
import org.jetbrains.anko.toast

/**
 * 网易云初始化
 */
fun neteaseInit(context: Context) {
    NIMClient.init(context, null, options())
    if (NIMUtil.isMainProcess(context)) {
        // 注册自定义消息附件解析器
    }
}

/**
 * 网易云密钥
 */
internal const val app_key = "5da8d352cb58fe86adc5b2b16e313c1c"

private fun options(): SDKOptions {
    val options = SDKOptions()
    options.appKey = app_key
    return options
}

/**
 * 网易云登录
 */
fun neteaseLogin(context: Context, account: String, token: String) {
    NIMClient.getService(AuthService::class.java)
            .login(LoginInfo(account, MD5.getStringMD5(token)))
            .setCallback(object : RequestCallback<LoginInfo> {
                override fun onSuccess(param: LoginInfo) {
                    NCache.getInstance().login(account)
                    context.toast("登陆成功")
                }

                override fun onFailed(code: Int) {
                    if (code == 302 || code == 404) {
                        context.toast("初始化失败 请联系管理员")
                    } else {
                        context.toast("初始化失败 错误代码$code")
                    }
                }

                override fun onException(exception: Throwable) {
                    context.toast("初始化异常 ${exception.message}")
                }

            })
}

/**
 * 预定白板对话
 */
fun createRTSRoom(context: Context, sessionId: String, listener: IJoinListener) {
    RTSManager2.getInstance().createSession(sessionId, "ex msg", object : RTSCallback<Void> {
        override fun onSuccess(t: Void?) {
            // 老师创建成功并加入房间，学生只需要进入房间
            joinRTSRoom(context, sessionId, listener, true)
        }

        override fun onFailed(code: Int) {
            // 房间已经被创建，直接进入
            if (code == 417) {
                joinRTSRoom(context, sessionId, listener, true)
            }
        }

        override fun onException(exception: Throwable?) {
        }

    })
}

/**
 * 加入多人白板房间
 */
fun joinRTSRoom(context: Context, sessionId: String, listener: IJoinListener, isCreator: Boolean = false) {
    RTSManager2.getInstance().joinSession(sessionId, false, object : RTSCallback<RTSData> {
        override fun onSuccess(t: RTSData?) {
            if (isCreator) {
                context.toast("系统初始化成功，同学们可以加入课堂了")
            } else {
                context.toast("系统初始化成功，现在开始上课")
            }

            listener.joinSuccess()
        }

        override fun onFailed(code: Int) {
            context.toast("系统初始化失败，错误代码$code")
        }

        override fun onException(exception: Throwable?) {
        }

    })
}