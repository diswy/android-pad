package com.cqebd.student.net.api

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.VideoInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * 描述
 * Created by gorden on 2018/3/1.
 */
interface VideoService {
    /**
     * 获取课程列表
     */
    @POST("api/CoursePeriod/GetCourseList")
    @FormUrlEncoded
    fun getCourseList(@Field("Type") type:Int=2,@Field("studentid") userId:Long = loginId):LiveData<ApiResponse<List<VideoInfo>>>
}