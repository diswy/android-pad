package com.cqebd.student.db.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.cqebd.student.db.entity.ClassSchedule
import com.cqebd.student.vo.entity.CourseInfo

/**
 * 描述
 * Created by gorden on 2018/3/12.
 */
@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCourses(courses:ClassSchedule)

    @Query("SELECT * from ex_schedule WHERE date = :date")
    fun loadCourses(date:String):LiveData<ClassSchedule>
}