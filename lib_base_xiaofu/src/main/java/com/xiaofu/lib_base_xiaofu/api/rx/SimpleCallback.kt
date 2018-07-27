package com.xiaofu.lib_base_xiaofu.api.rx

interface SimpleCallback<T> {
    fun onStart()
    fun onNext(t: T)
    fun onComplete()
}