package com.cqebd.student.net.api

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.UserAccount
import com.cqebd.student.vo.entity.WorkInfo
import com.google.gson.JsonObject
import retrofit2.http.*

/**
 * 描述
 * Created by gorden on 2018/3/1.
 */
interface WorkService {
    companion object {
        val BASE_WEB_URL = "http://service.student.cqebd.cn/"
    }
    /**
     * 用户登录
     */
    @GET("api/Account/Login")
    fun accountLogin(@Query("loginName") loginName: String, @Query("pwd") pwd: String): LiveData<ApiResponse<UserAccount>>

    /**
     * 获取作业列表
     */
    @POST("api/Students/GetExaminationTasks")
    @FormUrlEncoded
    fun getWorkList(
            @Field("userid") loginId: Long,
            @Field("SubjectTypeID") SubjectTypeID: Int?,
            @Field("ExaminationPapersTypeID") ExaminationPapersTypeID: Int?,
            @Field("status") status: Int?,
            @Field("pageindex") pageIndex: Int,
            @Field("pagesieze") pageSize: Int):LiveData<ApiResponse<List<WorkInfo>>>

    /**
     * 获取红花数量
     */
    @GET("api/Account/GetFlower")
    fun getFlower(@Query("studentid") id: Long=loginId):LiveData<ApiResponse<JsonObject>>


}