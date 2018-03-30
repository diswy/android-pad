package com.cqebd.student.net.api

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.*
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

/**
 * 描述
 * Created by gorden on 2018/3/1.
 */
interface WorkService {
    companion object {
        const val BASE_WEB_URL = "http://service.student.cqebd.cn/"
    }

    /**
     * 用户登录
     */
    @GET("api/Account/Login")
    fun accountLogin(
            @Query("loginName") loginName: String,
            @Query("pwd") pwd: String)
            : LiveData<ApiResponse<UserAccount>>

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
            @Field("pagesieze") pageSize: Int)
            : LiveData<ApiResponse<List<WorkInfo>>>

    /**
     * 获取错题本列表
     */
    @POST("api/Students/ErrorQuestionsList")
    @FormUrlEncoded
    fun getWrongQuestionList(
            @Field("userid") loginId: Long,
            @Field("SubjectTypeID") SubjectTypeID: Int?,
            @Field("ExaminationPapersTypeID") ExaminationPapersTypeID: Int?,
            @Field("status") status: Int?)
            : LiveData<ApiResponse<List<WrongQuestion>>>

    /**
     * 获取红花数量
     */
    @GET("api/Account/GetFlower")
    fun getFlower(
            @Query("studentid") id: Long = loginId)
            : LiveData<ApiResponse<JsonObject>>

    /**
     * 开始答题
     */
    @GET("api/Students/StartWork")
    fun startWork(
            @Query("StudentQuestionsTasksID") taskId: Long)
            : LiveData<ApiResponse<JsonObject>>

    @GET("api/Students/GetExaminationPapersByID")
    fun getExaminationPaper(
            @Query("id") paperId: Long,
            @Query("tasksid") tasksid: Long)
            : LiveData<ApiResponse<List<ExaminationPaperInfo>>>

    /**
     * 分享作业列表
     */
    @POST("api/TaskShare/TaskShareToStudent")
    fun getShareHomeworkList(
            @Query("PageIndex") pageIndex: Int,
            @Query("PageSize") pageSize: Int,
            @Query("GradeId") grade: Int?,
            @Query("SubjectTypeid") subject: Int?,
            @Query("QuestionTypeId") question: Int?,
            @Query("Day") date: Int?,
            @Query("Studentid") studentId: Long = loginId)
            : LiveData<ApiResponse<ShareHomework>>

    /**
     * 被分享的作业列表
     */
    @POST("api/TaskShare/TaskShareList")
    fun getBeSharedList(
            @Query("PageIndex") pageIndex: Int,
            @Query("PageSize") pageSize: Int,
            @Query("SubjectTypeid") subject: Int?,
            @Query("QuestionTypeId") question: Int?,
            @Query("Day") date: Int?,
            @Query("Studentid") studentId: Long = loginId)
            : LiveData<ApiResponse<ShareHomework>>

    /**
     * 错题本 问题详情
     */
    @GET("api/Students/ErrorQuestions")
    fun getErrorQuestions(
            @Query("StudentQuestionsTasksID") StudentQuestionsTasksID: Int)
            : Call<BaseResponse<WrongQuestionDetails>>


}