package com.cqebd.student.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * 描述
 * Created by gorden on 2018/3/20.
 */
@Entity(tableName = "ex_attachment")
data class Attachment(
        @PrimaryKey
        val id: String,
        val taskId:Int,
        val url:String,
        val name:String,
        val watchCount:Int,//当前观看次数
        val AnswerType:Int,//1播放中答、2是必须播放完后可答
        val canWatchCount:Int)//最多观看次数，0无限制 >0至少看1次，最多看count次