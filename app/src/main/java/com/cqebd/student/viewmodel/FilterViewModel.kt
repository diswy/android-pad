package com.cqebd.student.viewmodel

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.anko.static.appWidth
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.adapter.FilterTagAdapter
import com.cqebd.student.app.App
import com.cqebd.student.vo.entity.FilterData
import kotlinx.android.synthetic.main.window_filter.view.*

/**
 * 筛选ViewModel
 */
class FilterViewModel : ViewModel() {
    //科目
    var subject: MutableLiveData<FilterData> = MutableLiveData()
    //作业类型
    var jobType: MutableLiveData<FilterData> = MutableLiveData()
    //作业状态
    var jobStatus: MutableLiveData<FilterData> = MutableLiveData()
    //年级
    var grade: MutableLiveData<FilterData> = MutableLiveData()
    //时段
    var dateTime: MutableLiveData<FilterData> = MutableLiveData()
    //状态
    var subscribeStatus: MutableLiveData<FilterData> = MutableLiveData()

    var yOff:Int = 0.5f.dp

    @SuppressLint("StaticFieldLeak", "InflateParams")
    private val filterView: View = LayoutInflater.from(App.mContext).inflate(R.layout.window_filter, null)
    private val filterWindow: PopupWindow by lazy {
        PopupWindow(appWidth, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = true
            isFocusable = true
            contentView = filterView
            filterView.flow_filter.setSelectedRange(0, 1)
        }
    }

    /**
     * 作业状态筛选
     */
    fun filterJobStatus(view: View) {
        filter(view, jobStatus, FilterData.jobStatus)
    }

    /**
     * 时段筛选
     */
    fun filterDateTime(view: View) {
        filter(view, dateTime, FilterData.dateTime)
    }

    /**
     * 年级筛选
     */
    fun filterGrade(view: View) {
        filter(view, grade, FilterData.grade)
    }

    /**
     * 科目筛选
     */
    fun filterSubject(view: View) {
        filter(view, subject, FilterData.subject)
    }

    /**
     * 作业类型筛选
     */
    fun filterJobType(view: View) {
        filter(view, jobType, FilterData.jobType)
    }

    /**
     * 订阅状态筛选
     */
    fun filterSubscribeStatus(view: View) {
        filter(view, subscribeStatus, FilterData.subscribeStatus)
    }

    private fun filter(view: View, filterData: MutableLiveData<FilterData>, filterDataList: List<FilterData>) {
        val adapter = FilterTagAdapter(filterDataList.map { it.Name })
        filterView.flow_filter.setAdapter(adapter)
        val position = filterDataList.indexOf(filterData.value)
        if (position != -1) {
            filterView.flow_filter.setSelectedList(position)
        }
        filterView.btn_clean.setOnClickListener {
            filterView.flow_filter.setSelectedList(null)
        }

        filterView.btn_confirm.setOnClickListener {
            val selected = filterView.flow_filter.getSelectedList()
            if (selected.isEmpty()) {
                filterData.value = null
            } else {
                filterData.value = filterDataList[selected[0]]
            }
            filterWindow.dismiss()
        }

        filterWindow.showAsDropDown(view, 0, yOff)
    }
}