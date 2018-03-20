package com.cqebd.student.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import com.cqebd.student.db.ExDataBase
import com.cqebd.student.db.entity.ClassSchedule
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.RateLimiter
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.CourseInfo
import com.cqebd.student.vo.entity.VideoInfo

/**
 * 描述
 * Created by gorden on 2018/3/5.
 */
class VideoRepository {
    private val rateLimiter:RateLimiter<String> = RateLimiter(30)
    private val courseDao by lazy { ExDataBase.getInstance().courseDao() }

    fun getCourseList(): LiveData<Resource<List<VideoInfo>>> {
        return object : NetworkResource<List<VideoInfo>>() {
            override fun createCall(): LiveData<ApiResponse<List<VideoInfo>>> {
                return NetClient.videoService().getCourseList()
            }
        }.asLiveData
    }


    /**
     * 获取课程表
     * @param date date is yyyy-MM
     */
    fun getPeriodListMonth(date:String):LiveData<Resource<ClassSchedule>> {
        return object :NetworkBoundResource<ClassSchedule,List<CourseInfo>>(){
            override fun shouldFetch(data: ClassSchedule?): Boolean {
                return data==null||rateLimiter.shouldFetch(date)
            }

            override fun saveCallResult(item: List<CourseInfo>) {
                courseDao.insertCourses(ClassSchedule(date,item))
            }

            override fun loadFromDb(): LiveData<ClassSchedule> {
                return courseDao.loadCourses(date)
            }

            override fun createCall(): LiveData<ApiResponse<List<CourseInfo>>> {
                return NetClient.videoService().getPeriodListMonth(date)
            }
        }.asLiveData
    }
}