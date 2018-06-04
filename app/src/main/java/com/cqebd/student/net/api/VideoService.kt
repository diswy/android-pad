package com.cqebd.student.net.api

import android.arch.lifecycle.LiveData
import com.cqebd.student.live.entity.LiveByRemote
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 描述
 * Created by gorden on 2018/3/1.
 */
interface VideoService {
    /**
     * 获取课程列表
     */
    @FormUrlEncoded
    @POST("api/CoursePeriod/GetCourseList")
    fun getCourseList(
            @Field("Type") type: Int = 2,
            @Field("studentid") userId: Long = loginId)
            : LiveData<ApiResponse<List<VideoInfo>>>

    /**
     * 获取课程表
     */
    @FormUrlEncoded
    @POST("api/CoursePeriod/GetPeriodList")
    fun getPeriodListMonth(
            @Field("Month") date: String,
            @Field("studentid") userId: Long = loginId)
            : LiveData<ApiResponse<List<CourseInfo>>>

    /**
     * 获取最近课程列表
     */
    @FormUrlEncoded
    @POST("api/CoursePeriod/GetPeriodList")
    fun getPeriodList(
            @Field("CourseId") id: Long,
            @Field("studentid") userId: Long = loginId)
            : LiveData<ApiResponse<List<PeriodInfo>>>

    /**
     * 获取推荐课程列表
     */
    @FormUrlEncoded
    @POST("api/CoursePeriod/GetPeriodList")
    fun getPeriodListRecommend(
            @Field("Status") status: Int,
            @Field("Type") type: Int,
            @Field("studentid") userId: Long = loginId)
            : LiveData<ApiResponse<List<PeriodInfo>>>

    /**
     * 课程订阅
     * status 0订阅 1取消
     */
    @FormUrlEncoded
    @POST("api/SubscribeCourse/Add")
    fun addSubscribe(
            @Field("CourseId") id: Long,
            @Field("Status") status: Int,
            @Field("StudentId") studentid: Long = loginId)
            : Call<BaseResponse<String>>

    /**
     * 获取订阅列表
     */
    @GET("api/SubscribeCourse/SubscribeCourseList")
    fun getSubscribeList(
            @Query("studentid") studentid: Long = loginId)
            : LiveData<ApiResponse<List<VideoInfo>>>

    /**
     * 获取课时详情
     */
    @GET("api/CoursePeriod/GetPeriodByID")
    fun getPeriodByID(
            @Query("id") id: Int,
            @Query("studentid") studentid: Long = loginId)
            : Call<BaseResponse<PeriodResponse>>

    /**
     * 获取收藏列表
     */
    @GET("api/CoursePeriod/GetCollectByStudentId")
    fun getCollectList(
            @Query("studentid") studentId: Long = loginId)
            : Call<BaseResponse<List<VideoInfo>>>

    /**
     * 获取讨论列表
     */
    @GET("/api/Feedback/GetListByPeriodId")
    fun getVideoEvaluate(
            @Query("id") id: Int)
            : Call<BaseResponse<List<VideoEvaluate>>>

    @POST("/api/Feedback/Add")
    fun addEvaluate(
            @Query("PeriodId") periodId: Int,
            @Query("NickName") nickName: String,
            @Query("Comment") comment: String,
            @Query("StudentId") studentId: Long = loginId)
            : Call<ResponseBody>

    /**
     * 获取直播
     */
    @GET("/api/PeriodLive/GetPeriodLiveByPerodId")
    fun getLive(
            @Query("periodId") id: Int)
            : Call<BaseResponse<LiveByRemote>>

}