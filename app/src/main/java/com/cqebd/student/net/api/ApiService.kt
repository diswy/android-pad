package com.cqebd.student.net.api

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * 描述
 * Created by gorden on 2018/3/12.
 */
interface ApiService {
    @Multipart
    @POST("http://service.student.cqebd.cn/HomeWork/UpdataFile")
    fun uploadFile(@Part("files\"; filename=\"image.jpg\"") files: RequestBody): LiveData<ApiResponse<String>>
}