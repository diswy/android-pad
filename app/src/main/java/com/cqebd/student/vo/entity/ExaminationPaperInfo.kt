package com.cqebd.student.vo.entity

/**
 * 描述
 * Created by gorden on 2018/3/20.
 */
data class ExaminationPaperInfo(val Id:Int,
                                val Name:String,
                                val SubjectTypeName:String,
                                val SubjectTypeId:Int,
                                val Count:Int,
                                val QuestionGruop:List<QuestionGroupInfo> = ArrayList())