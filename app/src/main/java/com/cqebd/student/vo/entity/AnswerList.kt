package com.cqebd.student.vo.entity

/**
 * 作业分享  被分享的答案
 * 可能是图片形式
 * Created by diswy on 2018/3/29.
 */

data class AnswerItem(
		val Answer: String,
		val Id: String,
		val TypeId: Int
)