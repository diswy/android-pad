package com.cqebd.student.viewmodel

import android.arch.lifecycle.*
import com.cqebd.student.repository.ShareHomeworkRepository
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.ShareHomework
import com.cqebd.student.vo.entity.ShareHomeworkItem
import com.cqebd.teacher.vo.Status

/**
 * 作业分享列表
 * Created by diswy on 2018/3/28.
 */
class ShareHomeworkViewModel(private val filterViewModel: FilterViewModel, private val pageProcess: PageProcess<*>) : ViewModel() {
    private val repository = ShareHomeworkRepository()

    var shareList = MutableLiveData<List<ShareHomeworkItem>>()
    var sharedList: MediatorLiveData<Resource<ShareHomework>> = MediatorLiveData()


    fun getShareHomeworkList(pageIndex: Int, pageSize: Int, gradeId: Int?, subjectId: Int?, problemType: Int?, day: Int?): LiveData<Resource<ShareHomework>> {
        return repository.getShareHomeworkList(pageIndex, pageSize, gradeId, subjectId, problemType, day)
    }


    fun getShareHomeworkList() {
        val call = repository.getShareHomeworkList(pageProcess.pageIndex, 20,
                filterViewModel.shareHomeworkGrade.value?.status,
                filterViewModel.subject.value?.status,
                filterViewModel.problemType.value?.status,
                filterViewModel.dateTime.value?.status)
        sharedList.addSource(call, {
            sharedList.value = it
            if (it?.status == Status.SUCCESS || it?.status == Status.ERROR) {
                sharedList.removeSource(call)
            }
        })
    }

    fun getBeShareHomeworkList(pageIndex: Int, pageSize: Int, subjectId: Int?, problemType: Int?, day: Int?): LiveData<Resource<ShareHomework>> {
        return repository.getBeShareHomeworkList(pageIndex, pageSize, subjectId, problemType, day)
    }

    fun setData(shareList: List<ShareHomeworkItem>) {
        this.shareList.value = shareList
    }

    class Factory(private val filterViewModel: FilterViewModel,
                  private val pageProcess: PageProcess<*>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(FilterViewModel::class.java, PageProcess::class.java)
                    .newInstance(filterViewModel, pageProcess)
        }
    }

}