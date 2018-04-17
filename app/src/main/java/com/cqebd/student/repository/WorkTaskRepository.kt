package com.cqebd.student.repository

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.loginId
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.WorkInfo

/**
 * 作业任务
 * Created by gorden on 2018/3/1.
 */
class WorkTaskRepository {

    /**
     * 获取作业列表
     */
    fun getWorkList(filterViewModel: FilterViewModel,index:Int,isDefaultStatus : Boolean = false): LiveData<Resource<List<WorkInfo>>> {
        return object : NetworkResource<List<WorkInfo>>(){
            override fun createCall(): LiveData<ApiResponse<List<WorkInfo>>> {
                val default = if (isDefaultStatus) 10 else filterViewModel.jobStatus.value?.status
                return NetClient.workService().getWorkList(loginId,filterViewModel.subject.value?.status,
                        filterViewModel.jobType.value?.status,default,index,20)
            }

        }.asLiveData
    }
}