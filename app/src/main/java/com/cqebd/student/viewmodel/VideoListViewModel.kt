package com.cqebd.student.viewmodel

import android.arch.lifecycle.*
import com.cqebd.student.repository.VideoRepository
import com.cqebd.student.tools.formatTime
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.VideoInfo

/**
 * 视频列表
 */
class VideoListViewModel(private val filterViewModel: FilterViewModel) : ViewModel() {
    private val repository = VideoRepository()
    //过滤后的数据
    val videoList = MutableLiveData<List<VideoInfo>>()

    fun getCourseList(): LiveData<Resource<List<VideoInfo>>> {
        return repository.getCourseList()
    }

    fun filter(data: List<VideoInfo>?) {
        val day7 = 7 * 24 * 60 * 60 * 1000L
        val month1 = 30 * 24 * 60 * 60 * 1000L
        val month2 = 60 * 24 * 60 * 60 * 1000L
        videoList.value = data?.filter {
            val timeDif = Math.abs(System.currentTimeMillis() - formatTime(it.StartDate))
            val dateId = if (timeDif < day7) 1 else if (timeDif < month1) 2 else if (timeDif < month2) 3 else 4
            val status = if (it.IsFeedback) 2 else 1
            formatTime(it.StartDate)
            it.GradeId == filterViewModel.grade.value?.status ?: it.GradeId
                    && it.SubjectTypeId == filterViewModel.subject.value?.status ?: it.SubjectTypeId
                    && dateId <= filterViewModel.dateTime.value?.status ?: dateId
                    && status == filterViewModel.subscribeStatus.value?.status ?: status
        }
    }

    class Factory(private val filterViewModel: FilterViewModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(FilterViewModel::class.java)
                    .newInstance(filterViewModel)
        }
    }
}