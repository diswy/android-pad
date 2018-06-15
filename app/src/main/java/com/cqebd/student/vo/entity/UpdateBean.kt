package com.cqebd.student.vo.entity


data class UpdateBean(
    val version_code: Int,
    val download_url: String,
    val name: String,
    val info: String,
    val file_name: String,
    val force_update: Boolean
)