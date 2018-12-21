package com.cqebd.student.net.api

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.tools.loginId
import com.cqebd.student.vo.entity.*
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * 描述
 * Created by gorden on 2018/3/1.
 */
interface WorkService {
    companion object {
//        const val BASE_WEB_URL = "http://service.student.cqebd.cn/"
        const val BASE_WEB_URL = "https://service-student.cqebd.cn/"
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
            @Field("pagesieze") pageSize: Int = 20)
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
            @Field("status") status: Int?,
            @Field("pageindex") pageIndex: Int,
            @Field("pagesieze") pageSize: Int = 20)
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

    /**
     * 点点作业 获取验证码
     */
    @GET("api/Account/GetTelCode")
    fun getPhoneCode(
            @Query("loginName") loginName: String,
            @Query("type") type: Int)
            : Call<BaseResponse<Unit>>

    /**
     * 点点作业 修改密码
     */
    @POST("api/Account/UpdatePwdCode")
    fun updatePwd(
            @Query("LoginName") LoginName: String,
            @Query("NewPwd") NewPwd: String,
            @Query("Code") Code: String)
            : Call<BaseResponse<Unit>>

    /**
     * 检查更新
     */
    @GET("api/Setting/GetSetting")
    fun checkUpdate(
            @Query("keyName") keyName: String = "AndroidMergeUpdate")
            : Call<BaseResponse<String>>

    /**
     * 获取验证码
     */
    @GET("api/Account/GetTelCode")
    fun getTelCode(
            @Query("loginName") loginName: String
            , @Query("type") type: Int)
            : Call<BaseResponse<Unit>>

    @POST("api/Account/UpdatePhCode")
    fun updatePhCode(
            @Query("Status") status: Int,
            @Query("Code") code: String,
            @Query("Tel") tel: String,
            @Query("Pwd") Pwd: String,
            @Query("UserId") userId: Long = loginId)
            : Call<BaseResponse<Unit>>

    /**
     * 获取消息列表
     */
    @GET("/api/Account/GetMsgList")
    fun getMsgList(
            @Query("index") index: Int,
//            @Query("type") type: Int,
            @Query("day") day: Int? = null,
            @Query("status") status: Int? = null,
            @Query("studentid") studentid: Long = loginId)
            : Call<BaseResponse<MessageData>>

    /**
     * 消息阅读反馈
     */
    @GET("/api/Account/ReadrMsg")
    fun readMsg(
            @Query("type") type: Int,
            @Query("id") id: Int,
            @Query("studentid") studentid: Long = loginId)
            : Call<ResponseBody>


    @POST("api/Feedback/SubmitFeedback")
    fun submitFeedBk(
            @Query("WriteUserId") WriteUserId: Long,
            @Query("WriteUserName") WriteUserName: String,
            @Query("Title") Title: String,
            @Query("Countent") Countent: String,
            @Query("Classify") Classify: String,
            @Query("Type") type: Int,
            @Query("SourceType") SourceType: String)
            : Call<BaseResponse<Unit>>

    @POST("api/AnswerError/GroupExaminationPaper")
    fun retryWrongQuestion(
            @Query("SubjectTypeId") SubjectTypeId : Int,
//            @Query("count2") WriteUserName: String,// type=0时有效
//            @Query("count1") Title: String,// type=0时有效
//            @Query("strQuesion") Countent: String,// type=0时有效
            @Query("Type") Type : Int = 0,//  1手动组卷 0自动组卷
            @Query("StudentId") StudentId: Long = loginId)
            : Call<BaseResponse<WrongQuestionTask>>

}