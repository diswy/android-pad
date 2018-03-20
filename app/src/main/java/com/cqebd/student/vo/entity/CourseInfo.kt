package com.cqebd.student.vo.entity

/**
 * 课程信息
 * Created by gorden on 2018/3/12.
 */
data class CourseInfo(val Id:Long,
                      val CourseId:Long,
                      val Status:Int,
                      val Type:Int,
                      val Name:String,
                      val Day:String,
                      val PlanStartDate:String,
                      val TeacherId:Long,
                      val TeacherName:String,
                      val GradeId:Long,
                      val GradeName:String,
                      val Snapshoot:String,
                      val IsFeedback:Boolean
)