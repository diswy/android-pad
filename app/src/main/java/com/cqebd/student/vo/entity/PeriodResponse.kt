package com.cqebd.student.vo.entity

/**
 * 课时详情
 * Created by diswy on 2018/3/26.
 */

data class PeriodResponse(
		var Id: Int,
		var SchoolId: Int,
		var Name: String,
		var Type: Int,
		var Status: Int,
		var PlanStartDate: String,
		var Day: String,
		var CourseId: Int,
		var GradeId: Int,
		var SubjectTypeId: Int,
		var TeacherId: Int,
		var TeachingMaterialSectionId: Int,
		var TeachingMaterialTypeId: Int,
		var Durartion: Int,
		var IsFeedback: Boolean,
		var VodPlayList: List<VodPlay>
)

data class VodPlay(
		var Url: String,
		var Definition: Int,
		var VBitrate: Int,
		var VHeight: Int,
		var VWidth: Int
)