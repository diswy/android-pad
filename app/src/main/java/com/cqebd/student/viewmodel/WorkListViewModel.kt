package com.cqebd.student.viewmodel

import android.arch.lifecycle.*
import com.cqebd.student.repository.WorkTaskRepository
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.WorkInfo
import com.cqebd.teacher.vo.Status

/**
 * 作业列表
 * Created by gorden on 2018/3/1.
 */
class WorkListViewModel(private val filterViewModel: FilterViewModel,private val pageProcess: PageProcess<*>) : ViewModel() {
    private val repository = WorkTaskRepository()
    var workInfoList: MediatorLiveData<Resource<List<WorkInfo>>> = MediatorLiveData()

    fun getWorkList(isDefaultStatus : Boolean = false){
        val call = repository.getWorkList(filterViewModel, pageProcess.pageIndex,isDefaultStatus)
        workInfoList.addSource(call, {
            workInfoList.value = it
            if (it?.status == Status.SUCCESS||it?.status==Status.ERROR){
                workInfoList.removeSource(call)
            }
        })
    }


    class Factory(private val filterViewModel: FilterViewModel,
                  private val pageProcess: PageProcess<*>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(FilterViewModel::class.java, PageProcess::class.java)
                    .newInstance(filterViewModel, pageProcess)
        }
    }
}