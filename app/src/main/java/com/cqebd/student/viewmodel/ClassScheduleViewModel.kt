package com.cqebd.student.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.cqebd.student.db.entity.ClassSchedule
import com.cqebd.student.repository.VideoRepository
import com.cqebd.student.vo.Resource
import java.util.*

/**
 * 描述
 * Created by gorden on 2018/3/12.
 */
class ClassScheduleViewModel : ViewModel() {
    private val repository = VideoRepository()

    /**
     * 获取课程表
     */
    fun getPeriodListMonth(date: Calendar): LiveData<Resource<ClassSchedule>> {
        val dateStr = "${date.get(Calendar.YEAR)}-${date.get(Calendar.MONTH) + 1}"
        return repository.getPeriodListMonth(dateStr)
    }
}