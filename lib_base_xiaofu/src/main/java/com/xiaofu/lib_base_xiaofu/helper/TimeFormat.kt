package com.xiaofu.lib_base_xiaofu.helper

import java.text.SimpleDateFormat
import java.util.*


fun formatTime(time: Long): String {
    val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.CHINA)
    return format.format(Date(time))
}