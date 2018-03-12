package com.cqebd.student.vo

import com.cqebd.teacher.vo.Status

/**
 * 包含状态值的泛型类
 * Created by gorden on 2017/11/5.
 */
data class Resource<out T>(val status: Status, val data:T?, val message:String?="") {

    companion object {
        fun <T> success(data:T?): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(msg:String?,data:T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data:T?): Resource<T> {
            return Resource(Status.LOADING, data)
        }
    }

}