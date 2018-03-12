package com.cqebd.student.vo.entity

/**
 * 描述
 * Created by gorden on 2018/3/5.
 */
data class VideoInfo(val Id:Long,
                     val Name:String,
                     val GradeId:Int,
                     val GradeName:String,
                     val SchoolId:Int,
                     val Price:Double,
                     val TeacherId:Long,
                     val TeacherName:String,
                     val TeacherPhoto:String,
                     val PeriodCount:Int,
                     val SubjectTypeId:Int,
                     val SubjectTypeName:String,
                     val IsFeedback:Boolean,
                     val StartDate:String
                     )