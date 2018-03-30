package com.cqebd.student.vo.entity

/**
 * 课程信息
 * Created by gorden on 2018/3/12.
 */
data class CourseInfo(val Id:Long,
                      val CourseId:Long,
                      val Status:Int,
                      val Type:Int,
                      val SchoolId:Int,
                      val SchoolTermTypeId:Int,
                      val SubjectTypeId:Int,
                      val RegisterNumber:Int,
                      val Name:String,
                      val Day:String,
                      val PlanStartDate:String,
                      val StartDate:String,
                      val EndDateTime:String,
                      val SchoolTermTypeName:String,
                      val SubjectTypeName:String,
                      val TeacherPhoto:String,
                      val SchoolName:String,
                      val TeacherId:Long,
                      val TeacherName:String,
                      val GradeId:Long,
                      val Price:Double,
                      val GradeName:String,
                      val TeachingMaterialTypeName:String,
                      val Snapshoot:String,
                      val IsFeedback:Boolean
)