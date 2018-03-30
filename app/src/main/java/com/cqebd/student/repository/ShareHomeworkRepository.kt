package com.cqebd.student.repository

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.ShareHomework

/**
 *
 * Created by diswy on 2018/3/28.
 */
class ShareHomeworkRepository {
    fun getShareHomeworkList(pageIndex: Int, pageSize: Int, gradeId: Int?, subjectId: Int?, problemType: Int?, day: Int?): LiveData<Resource<ShareHomework>> {
        return object : NetworkResource<ShareHomework>() {
            override fun createCall(): LiveData<ApiResponse<ShareHomework>> {
                return NetClient.workService().getShareHomeworkList(pageIndex,pageSize,gradeId,subjectId,problemType,day)
            }
        }.asLiveData
    }

    fun getBeShareHomeworkList(pageIndex: Int, pageSize: Int, subjectId: Int?, problemType: Int?, day: Int?): LiveData<Resource<ShareHomework>> {
        return object : NetworkResource<ShareHomework>() {
            override fun createCall(): LiveData<ApiResponse<ShareHomework>> {
                return NetClient.workService().getBeSharedList(pageIndex,pageSize,subjectId,problemType,day)
            }
        }.asLiveData
    }
}