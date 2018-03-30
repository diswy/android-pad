package com.cqebd.student.vo.entity

/**
 *
 * Created by diswy on 2018/3/30.
 */
data class WrongQuestionDetails(
        val StudentQuestionsTasksID:Int,
        val ExaminationPapersID:Int,
        val Count:Int,
        val ExaminationPapersName:String,
        val ErrorList: List<WrongQuestionDetailsItem>
)

data class WrongQuestionDetailsItem(
        val querstionId:Int,
        val sortId:Int
)