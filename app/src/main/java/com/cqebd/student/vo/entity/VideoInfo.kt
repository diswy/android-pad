package com.cqebd.student.vo.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 描述
 * Created by gorden on 2018/3/5.
 */
@Parcelize
data class VideoInfo(
        val Id: Long,
        val CourseId: Long,
        val Name: String,
        val GradeId: Int,
        val GradeName: String,
        val SchoolId: Int,
        val SchoolTermTypeId: Int,
        val RegisterNumber: Int,
        val Price: Double,
        val TeacherId: Long,
        val TeacherName: String,
        val TeacherPhoto: String,
        val Snapshoot: String,
        val PeriodCount: Int,
        val SubjectTypeId: Int,
        val SubjectTypeName: String,
        val TeachingMaterialTypeName: String,
        var IsFeedback: Boolean,
        val StartDate: String,
        val SchoolTermTypeName: String,
        val SchoolName: String,
        val EndDateTime: String
) : Parcelable