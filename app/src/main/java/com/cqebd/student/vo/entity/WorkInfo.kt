package com.cqebd.student.vo.entity

import com.google.gson.annotations.SerializedName

/**
 * 作业列表信息
 */
data class WorkInfo(
        @SerializedName("StudentQuestionsTasksID")
        val TaskId:Long,
        @SerializedName("ExaminationPapersId")
        val PapersId:Long,
        @SerializedName("ExaminationPapersPushId")
        val PushId:Long,
        @SerializedName("ExaminationPapersTypeId")
        val TypeId:Int,
        val TypeName:String,
        val Status:Int,
        val Name:String,
        @SerializedName("SubjectTypeId")
        val SubjectId:Int,
        @SerializedName("SubjectTypeName")
        val SubjectName:String,
        @SerializedName("CanStartDateTime")
        val StartTime:String,
        @SerializedName("CanEndDateTime")
        val EndTime:String,
        @SerializedName("Count")
        val QuestionCount:Int
)