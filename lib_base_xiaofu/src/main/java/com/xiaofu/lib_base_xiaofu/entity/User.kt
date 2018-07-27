package com.xiaofu.lib_base_xiaofu.entity


data class User(
    val errorId: Int,
    val message: String,
    val isSuccess: Boolean,
    val data: Data
)

data class Data(
    val studentId: Int,
    val Name: String,
    val LoginName: String,
    val NickName: Any,
    val Tel: String,
    val SchoolName: String,
    val Photo: String,
    val Gender: String,
    val GradeName: String,
    val TeamName: String,
    val ClassGroupName: String,
    val SubjectType: List<SubjectType>,
    val ExaminationPapersType: List<ExaminationPapersType>,
    val ImagesUrl: String,
    val ImagesTag: String,
    val OssAccessUrl: String,
    val OssAccessUrlTag: String,
    val Flower: Int,
    val IsGroup: Boolean
)

data class ExaminationPapersType(
    val Id: Int,
    val Name: String
)

data class SubjectType(
    val Id: Int,
    val Name: String,
    val Status: Int
)