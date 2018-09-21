package com.ebd.lib.bean

data class OnlineEBook(
        val id: Int,
        val name: String,
        val serial: Any,
        val createdatetime: Any,
        val type: Int,
        val status: Int,
        val gradeId: Int,
        val subjectTypeId: Int,
        val fileSuffix: String,
        val fileUrl: String,
        val teachingMaterialTypeId: Int,
        val teachingMaterialPublishVerTypeId: Int
)