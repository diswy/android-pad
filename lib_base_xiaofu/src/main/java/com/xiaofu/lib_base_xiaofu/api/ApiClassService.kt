package com.xiaofu.lib_base_xiaofu.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiClassService {

    /**
     * 登录接口
     */
    @GET("api/Account/Login")
    fun login(@Query("loginName") loginName: String, @Query("pwd") pwd: String): Call<String>

    /**
     * test
     */
    @GET("period/getperiodbyid")
    fun test(@Query("id") id: Int = 1): Call<String>



}