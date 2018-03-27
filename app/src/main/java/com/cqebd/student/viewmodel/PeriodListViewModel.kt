package com.cqebd.student.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.cqebd.student.repository.PeriodRepository
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.PeriodInfo

/**
 * 最近课程
 * Created by Xiaofu on 2018/3/21.
 */

class PeriodListViewModel: ViewModel(){
    private val repository = PeriodRepository()

    var videoList = MutableLiveData<List<PeriodInfo>>()


    fun getPeriodList(id:Long): LiveData<Resource<List<PeriodInfo>>> {
        return repository.getPeriodList(id)
    }

    fun setData(videoList : List<PeriodInfo>){
        this.videoList.value = videoList
    }

}

