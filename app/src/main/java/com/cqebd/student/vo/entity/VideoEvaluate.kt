package com.cqebd.student.vo.entity


data class VideoEvaluate(
        val Id: Int,
        val PeriodId: Int,
        val Status: Int,
        val StudentId: Int,
        val CreateDateTime: String,
        val StudentPhoto: String,
        val NickName: String,
        val Comment: String
)