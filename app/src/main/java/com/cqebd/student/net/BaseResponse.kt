package com.cqebd.student.net

import com.google.gson.annotations.SerializedName

/**
 * 描述
 * Created by gorden on 2017/11/5.
 */
data class BaseResponse<out T>(@SerializedName("errorId")
                               val status: Int,
                               @SerializedName("message")
                               val message: String,
                               @SerializedName("isSuccess")
                               val isSuccess: Boolean,
                               @SerializedName("data")
                               val data: T)