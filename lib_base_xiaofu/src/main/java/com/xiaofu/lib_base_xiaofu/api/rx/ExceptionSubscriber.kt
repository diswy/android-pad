package com.xiaofu.lib_base_xiaofu.api.rx

import android.content.Context
import org.jetbrains.anko.toast
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.net.ConnectException
import java.net.SocketTimeoutException

class ExceptionSubscriber<T>(private val context: Context, private val callback: SimpleCallback<T>) : Subscriber<T> {

    override fun onComplete() {
        callback.onComplete()
    }

    override fun onSubscribe(s: Subscription?) {
        callback.onStart()
    }

    override fun onNext(t: T) {
        callback.onNext(t)
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        when (e) {
            is SocketTimeoutException -> {
                context.toast("网络中断，请检查您的网络状态")
            }
            is ConnectException -> {
                context.toast("网络中断，请检查您的网络状态")
            }
            else -> {
                context.toast("error:${e.message}")
            }
        }
    }
}