package com.cqebd.student.live.entity


data class EbdCustomNotification(
        val type: String,
        val ver: String,
        val name: String,
        val senderType: String,
        val sendId: Long,
        val recType: String,
        val recId: Long,
        val content: String
)