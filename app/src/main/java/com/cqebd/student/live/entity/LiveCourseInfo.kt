package com.cqebd.student.live.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LiveCourseInfo(
        val type: String,
        val ver: String,
        val name: String,
        val senderType: String,
        val sendId: String,
        val recType: String,
        val recId: String,
        val content: String
):Parcelable