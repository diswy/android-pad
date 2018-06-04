package com.cqebd.student.vo.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 最近课程
 * Created by xiaofu on 2018/3/21.
 */
@Parcelize
data class PeriodInfo(
        val Id: Int,
        val CourseId: Int,
        val SchoolId: Int,
        val PlanStartDate: String,
        val Name: String,
        val Day: String,
        val StartDateTime: Long,
        val SubjectTypeId: Int,
        val TeacherId: Int,
        val TeachingMaterialSectionId: Int,
        val GradeId: Int,
        val Price: Double,
        val TeachingMaterialTypeId: Int,
        val Durartion: Long,
        val RegisterNumber: Int,
        val TeacherName: String,
        val GradeName: String,
        val Snapshoot: String,
        val Status: Int,
        val Type: Int,
        val LiveProvider: String,
        val HasChannel: Int,
        val HasChat: Int,
        val HasVchat: Int,
        val HasIWB: Int,
        val SectionsId: Int,
        val IsFeedback: Boolean
) : Parcelable
