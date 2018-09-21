package com.ebd.lib.bean


data class BaseResponse<out T>(
        val errorId: Int,
        val message: String,
        val success: Boolean,
        val data: T?
)