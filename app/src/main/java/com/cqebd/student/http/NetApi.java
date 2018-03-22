package com.cqebd.student.http;

import com.cqebd.student.vo.entity.BaseBean;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * document
 * Created by Gordn on 2017/3/8.
 */

public interface NetApi {
    @Multipart
    @POST("http://service.student.cqebd.cn/HomeWork/UpdataFile")
    Call<ResponseBody> uploadFile(@Part("files\"; filename=\"image.jpg\"") RequestBody files);

    //检测版本
    @GET("api/Setting/GetSetting")
    Call<ResponseBody> checkUpdate(@Query("keyName") String keyName);

//    //作业本列表
//    @POST("api/Students/GetExaminationTasks")
//    Call<ExaminationTaskListInfo> getExaminationTasks(
//            @Query("userid") int userid,
//            @Query("SubjectTypeID") Integer SubjectTypeID,
//            @Query("ExaminationPapersTypeID") Integer ExaminationPapersTypeID,
//            @Query("status") Integer status,
//            @Query("pageindex") int pageIndex,
//            @Query("pagesieze") int pageSize);
//
//    //错题本列表
//    @POST("api/Students/ErrorQuestionsList")
//    Call<MistakeListInfo> getErrQuestionList(
//            @Query("userid") int userid,
//            @Query("SubjectTypeID") Integer SubjectTypeID,
//            @Query("ExaminationPapersTypeID") Integer ExaminationPapersTypeID,
//            @Query("status") Integer status);
//
//    //错题本问题
//    @GET("api/Students/ErrorQuestions")
//    Call<MistakeQuestionBean> getErrorQuestions(@Query("StudentQuestionsTasksID") int StudentQuestionsTasksID);
//
//    //丢分统计列表
//    @POST("api/FractionReport/GetExaminationTasks")
//    Call<LostScoreListInfo> getExaminationTasks(
//            @Query("userid") int userid,
//            @Query("SubjectTypeID") Integer SubjectTypeID,
//            @Query("ExaminationPapersTypeID") Integer ExaminationPapersTypeID);
//
//    //答题卡列表
//    @POST("api/ElectronicAnswer/GetExaminationTasks")
//    Call<ExaminationTaskListInfo> getAnswerCardTasks(
//            @Query("userid") int userid,
//            @Query("SubjectTypeID") Integer SubjectTypeID,
//            @Query("ExaminationPapersTypeID") Integer ExaminationPapersTypeID,
//            @Query("status") Integer status);
//
//    //获取答题卡详情
//    @GET("/api/ElectronicAnswer/GetExaminationPapersByID")
//    Call<AnswerCardDetailInfo> getAnswerCardPaperDetail(
//            @Query("tasksid") int tasksid);

    //提交答题卡
    @POST("api/ElectronicAnswer/SubmitAnswer")
    Call<BaseBean> submitAnswer(
            @Query("Stu_Id") int Stu_Id,
            @Query("PapersModelType") int PapersModelType,       //约定
            @Query("QuestionId") int QuestionId,
            @Query("ExaminationPapersId") int ExaminationPapersId,
            @Query("StudentQuestionsTasksId") int StudentQuestionsTasksId,
            @Query("Fraction") Double Fraction,        //得分
            @Query("IsMarking") String IsMarking,      //是否批阅
            @Query("IsTrue") int IsTrue,            //错误-1 半对0，正确1
            @Query("Comment") String Comment,
            @Query("Answer") String Answer
    );

    //作业本提交答案
    @POST("api/Students/SubmitAnswer")
    Call<BaseBean> submitAnswer(
            @Query("Stu_Id") int Stu_Id,
            @Query("QuestionId") int QuestionId,
            @Query("ExaminationPapersId") int ExaminationPapersId,
            @Query("StudentQuestionsTasksId") int StudentQuestionsTasksId,
            @Query("Answer") String Answer,
            @Query("QuestionAnswerTypeId") int QuestionAnswerTypeId,
            @Query("Version") String version,
            @Query("Source") String source);

    @FormUrlEncoded
    @POST("api/Students/SubmitAnswerList")
    Call<BaseBean> SubmitAnswers(@Field("Taskid") int taskId,
                                 @Field("AnswerList") String answerList,
                                 @Field("Type") int type,
                                 @Field("Status") int Status);

    //提交答案
    @POST("api/FractionReport/SubmitAnswer")
    Call<BaseBean> submitResult(
            @Query("StudentQuestionsTasksId") Integer StudentQuestionsTasksId,
            @Query("SumFraction") Integer SumFraction,
            @Query("AnswerSnapshoot") String AnswerSnapshoot
    );

    @POST("api/Account/UpdataStudent")
    Call<BaseBean> updateStudent(@Query("Id") int UserId, @Query("Photo") String Photo);

    @POST("api/Account/EditPwd")
    Call<BaseBean> modifyPwd(@Query("UserId") int UserId, @Query("Pwd") String Pwd, @Query("NewPwd") String NewPwd);

    @POST("api/Feedback/SubmitFeedback")
    Call<BaseBean> submitFeedBk(@Query("WriteUserId") int WriteUserId, @Query("WriteUserName") String WriteUserName,
                                @Query("Title") String Title, @Query("Countent") String Countent,
                                @Query("Classify") String Classify, @Query("Type") int type,
                                @Query("SourceType") String SourceType);



    @GET("api/Students/EndWork")
    Call<BaseBean> endWork(@Query("StudentQuestionsTasksID") int StudentQuestionsTasksID);
//
//    @GET("api/Students/GetExaminationPapersByID")
//    Call<ExaminationPaperListInfo> getExaminationPaper(@Query("id") int id, @Query("tasksid") int tasksid);
//
//    @GET("api/Account/GetFlower")
//    Call<ResponseBody> getFlower(@Query("studentid") int studentid);
//
//    @GET("api/Pay/PayAdd")
//    Call<PayResponse> payAdd(@Query("Balance") double amount, @Query("UserID") int userid);
//
//    @GET("api/WeChatPay/WeChatPayAdd")
//    Call<WeChatPayResponse> wechatPayAdd(@Query("Balance") double amount, @Query("StuID") int userid);
//
//    @GET("api/Account/GetStudentMoney")
//    Call<PayResponse> getMoney(@Query("UserID") int userid);
//
//    @GET("/api/Account/GetMsgList")
//    Call<MessageData> getMsgList(@Query("studentid") int studentid, @Query("index") int index, @Query("type") int type,
//                                 @Query("day") Integer day, @Query("status") Integer status);
//
//    @GET("/api/Account/ReadrMsg")
//    Call<MessageCount> ReadrMsg(@Query("studentid") int studentid, @Query("type") int type, @Query("id") int id);
//
//    @GET("/api/Account/ReadrMsgCount")
//    Call<MessageCount> readrMsgCount(@Query("studentid") int studentid, @Query("type") int type);
//
//    @GET("api/Account/GetTelCode")
//    Call<BaseBean> GetTelCode(@Query("loginName") String loginName, @Query("type") int type);
//
//    @POST("api/Account/UpdatePwdCode")
//    Call<BaseBean> UpdatePwdCode(@Query("LoginName") String LoginName, @Query("NewPwd") String NewPwd, @Query("Code") String Code);
//
//    @POST("api/Account/UpdatePhCode")
//    Call<BaseBean> UpdatePhCode(@Query("UserId") int userId, @Query("Status") int status,
//                                @Query("Code") String code, @Query("Tel") String tel, @Query("Pwd") String Pwd);
//
//    @POST("api/TaskShare/TaskShareList")
//    Call<ShareQuestionResponse> myShareList(@Query("Studentid") int studentId, @Query("PageIndex") int pageIndex, @Query("PageSize") int pageSize,
//                                            @Query("SubjectTypeid") Integer subject, @Query("QuestionTypeId") Integer question, @Query("Day") Integer date);
//
//    @POST("api/TaskShare/TaskShareToStudent")
//    Call<ShareQuestionResponse> niceShareList(@Query("Studentid") int studentId, @Query("PageIndex") int pageIndex, @Query("PageSize") int pageSize,
//                                              @Query("SubjectTypeid") Integer subject, @Query("GradeId") Integer grade, @Query("QuestionTypeId") Integer question, @Query("Day") Integer date);

}
