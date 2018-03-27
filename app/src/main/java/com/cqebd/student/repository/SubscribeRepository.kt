package com.cqebd.student.repository

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.CourseInfo

/**
 * 订阅列表
 * Created by xiaofu on 2018/3/21.
 */
class SubscribeRepository {
     fun getSubscribeList():LiveData<Resource<List<CourseInfo>>>{
         return object : NetworkResource<List<CourseInfo>>(){
             override fun createCall(): LiveData<ApiResponse<List<CourseInfo>>> {
                 return NetClient.videoService().getSubscribeList()
             }
         }.asLiveData
     }
}