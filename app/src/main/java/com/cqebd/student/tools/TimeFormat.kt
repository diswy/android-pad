package com.cqebd.student.tools

import java.text.SimpleDateFormat
import java.util.*

/**
 * 描述
 * Created by gorden on 2017/11/17.
 */

fun formatTime(time:Long):String{
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA)
    return format.format(Date(time))
}

fun formatTimeYMD(time:String?):String{
    if (time==null)
        return ""
    val format = SimpleDateFormat("yyyy.MM.dd", Locale.CHINA)
    return format.format(Date(formatTime(time)))
}

fun formatTimeYMD2(time:String?):String{
    if (time==null)
        return ""
    val format = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
    return format.format(Date(formatTime(time)))
}

fun formatTimeYMDHM(time:String?):String{
    if (time==null)
        return ""
    val format = SimpleDateFormat("yyyy.MM.dd  HH:mm", Locale.CHINA)
    return format.format(Date(formatTime(time)))
}

fun formatTimeMDHM(time:String?):String{
    if (time==null)
        return ""
    val format = SimpleDateFormat("MM.dd  HH:mm", Locale.CHINA)
    return format.format(Date(formatTime(time)))
}

fun formatTime(time:String):Long{
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA)
    return format.parse(time).time
}

fun formatTimeYMD(time:Long): String{
    val format = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
    return format.format(Date(time))
}