package com.cqebd.student.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.cqebd.student.repository.ShareHomeworkRepository
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.ShareHomework
import com.cqebd.student.vo.entity.ShareHomeworkItem

/**
 * 作业分享列表
 * Created by diswy on 2018/3/28.
 */
class ShareHomeworkViewModel : ViewModel() {
    private val repository = ShareHomeworkRepository()

    var shareList = MutableLiveData<List<ShareHomeworkItem>>()

    fun getShareHomeworkList(pageIndex: Int, pageSize: Int, gradeId: Int?, subjectId: Int?, problemType: Int?, day: Int?): LiveData<Resource<ShareHomework>> {
        return repository.getShareHomeworkList(pageIndex, pageSize, gradeId, subjectId, problemType, day)
    }

    fun getBeShareHomeworkList(pageIndex: Int, pageSize: Int, subjectId: Int?, problemType: Int?, day: Int?): LiveData<Resource<ShareHomework>> {
        return repository.getBeShareHomeworkList(pageIndex, pageSize, subjectId, problemType, day)
    }

    fun setData(shareList: List<ShareHomeworkItem>) {
        this.shareList.value = shareList
    }

}