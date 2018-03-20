package com.cqebd.student.db.type

import android.arch.persistence.room.TypeConverter
import com.cqebd.student.vo.entity.CourseInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room 格式转化
 * Created by gorden on 2018/3/12.
 */
class Converters {
    @TypeConverter
    fun string2Courses(data:String?):List<CourseInfo>?{
        if (data.isNullOrEmpty()){
            return null
        }else{
            return Gson().fromJson<List<CourseInfo>>(data,object : TypeToken<List<CourseInfo>>(){}.type)
        }
    }

    @TypeConverter
    fun courses2String(courses:List<CourseInfo>?):String?{
        if (courses==null)
            return null
        return Gson().toJson(courses)
    }
}