package com.cqebd.student.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.cqebd.student.vo.entity.CourseInfo

/**
 * 课程表
 * Created by gorden on 2018/3/12.
 */
@Entity(tableName = "ex_schedule")
data class ClassSchedule (@PrimaryKey
                          val date:String,
                          val courses:List<CourseInfo>)