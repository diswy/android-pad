package com.xiaofu.lib_base_xiaofu.api.viewmodel

import retrofit2.Call
import retrofit2.Callback
import java.net.ConnectException
import java.net.SocketTimeoutException

interface MyCallback<T> : Callback<T> {
    override fun onFailure(call: Call<T>, e: Throwable) {
        e.printStackTrace()
        when (e) {
            is SocketTimeoutException -> {
//                context.toast("网络中断，请检查您的网络状态")
            }
            is ConnectException -> {
//                context.toast("网络中断，请检查您的网络状态")
            }
            else -> {
//                context.toast("error:${e.message}")
            }
        }

    }
}