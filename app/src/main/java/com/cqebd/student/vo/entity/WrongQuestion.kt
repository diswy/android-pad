package com.cqebd.student.vo.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 描述
 * Created by gorden on 2018/3/22.
 */
@Parcelize
data class WrongQuestion(val Name:String,
                         val DateTime:String,
                         val ErrorCount:Int,
                         val SubjectTypeName:String,
                         val PapersTypeName:String,
                         val ExaminationPapersId:Int,
                         val StudentQuestionsTasksID:Int) : Parcelable