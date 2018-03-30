package com.cqebd.student.repository

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.VideoInfo

/**
 * 订阅列表
 * Created by xiaofu on 2018/3/21.
 */
class SubscribeRepository {
     fun getSubscribeList():LiveData<Resource<List<VideoInfo>>>{
         return object : NetworkResource<List<VideoInfo>>(){
             override fun createCall(): LiveData<ApiResponse<List<VideoInfo>>> {
                 return NetClient.videoService().getSubscribeList()
             }
         }.asLiveData
     }
}