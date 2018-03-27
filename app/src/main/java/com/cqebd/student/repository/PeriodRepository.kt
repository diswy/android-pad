package com.cqebd.student.repository

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.PeriodInfo

/**
 * 最近课程
 * Created by xiaofu on 2018/3/21.
 */
class PeriodRepository {

    fun getPeriodList(id:Long):LiveData<Resource<List<PeriodInfo>>>{
        return object : NetworkResource<List<PeriodInfo>>() {
            override fun createCall(): LiveData<ApiResponse<List<PeriodInfo>>> {
                return NetClient.videoService().getPeriodList(id)
            }
        }.asLiveData
    }
}