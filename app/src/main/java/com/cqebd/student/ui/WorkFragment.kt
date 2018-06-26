package com.cqebd.student.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.tools.colorForRes
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.WorkListViewModel
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.WorkInfo
import com.cqebd.teacher.vo.Status
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.fragment_work.*
import kotlinx.android.synthetic.main.item_work.view.*

/**
 * 作业
 * Created by gorden on 2018/2/27.
 */
class WorkFragment : BaseFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var workListViewModel: WorkListViewModel
    private val pageProcess = PageProcess.build<WorkInfo> { it.TaskId }
    private lateinit var adapter: BaseQuickAdapter<WorkInfo,BaseViewHolder>
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_work,container,false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java).apply { yOff=0 }
        workListViewModel = ViewModelProviders.of(this,WorkListViewModel.Factory(filterViewModel,pageProcess))
                .get(WorkListViewModel::class.java)

        filterViewModel.subject.observe(this, Observer {
            text_subject.text = it?.Name?:"学科"
            pageLoadView.show = true
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        })

        filterViewModel.jobType.observe(this, Observer {
            text_job_type.text = it?.Name?:"作业类型"
            pageLoadView.show = true
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        })

        filterViewModel.jobStatus.observe(this, Observer {
            text_job_status.text = it?.Name?:"作业状态"
            pageLoadView.show = true
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        })

        workListViewModel.workInfoList.observe(this, Observer {
            when(it?.status){
                Status.SUCCESS -> {
                    val data = it.data
                    if (pageProcess.pageIndex==1){
                        pageProcess.refreshData(data!!)
                        refreshLayout.refreshComplete(true)
                        adapter.setNewData(pageProcess.data)
                        if (pageProcess.data.isEmpty()){
                            pageLoadView.dataEmpty()
                        }else{
                            pageLoadView.hide()
                        }
                        if (data.size<20){
                            adapter.loadMoreEnd(true)
                        }
                    }else{
                        pageProcess.loadMoreData(data!!)
                        if (data.size>=20){
                            adapter.loadMoreComplete()
                        }else{
                            adapter.loadMoreEnd()
                        }
                        adapter.notifyDataSetChanged()

                    }
                }
                Status.ERROR -> {
                    refreshLayout.refreshComplete(false)
                    adapter.loadMoreFail()
                    pageLoadView.error({
                        workListViewModel.getWorkList()
                    })
                }
                Status.LOADING -> {
                    pageLoadView.load()
                }
            }
        })
        workListViewModel.getWorkList(true)

        adapter = object : BaseQuickAdapter<WorkInfo, BaseViewHolder>(R.layout.item_work,pageProcess.data){
            val subjectBg = arrayOf(R.drawable.bg_subject1,R.drawable.bg_subject2,R.drawable.bg_subject3,R.drawable.bg_subject4)
            override fun convert(helper: BaseViewHolder?, item: WorkInfo) {
                helper?.itemView?.apply {
                    text_name.text = item.Name
                    text_subject.text = item.SubjectName.take(1)
                    text_count.text = "共%s题".format(item.QuestionCount)
//                    text_start_time.text = "布置时间: ".plus(formatTimeYMDHM(item.publishTime))
                    text_end_time.text = "截止时间: ".plus(formatTimeYMDHM(item.CanEndDateTime))
                    text_subject.setBackgroundResource(subjectBg[helper.layoutPosition%4])
                    when(item.Status){
                        -1->{
                            text_status.text = "新作业"
                            text_status.setTextColor(colorForRes(R.color.status_new))
                        }
                        0->{
                            text_status.text = "答题中"
                            text_status.setTextColor(colorForRes(R.color.status_run))
                        }
                        1->{
                            text_status.text = "已完成"
                            text_status.setTextColor(colorForRes(R.color.status_complete))
                        }
                        2->{
                            text_status.text = "已批阅"
                            text_status.setTextColor(colorForRes(R.color.status_read))
                        }
                    }

                    text_status.text = FilterData.jobStatus.find { it.status==item.Status}?.Name
                    setOnClickListener {
                        if (item.Status<=0){
                            val urlFormat = "HomeWork/ExaminationPapers?id=%s&taskid=%s"
                            startActivity<JobPreviewActivity>("url" to WorkService.BASE_WEB_URL.plus(urlFormat.format(item.PapersId,item.TaskId)),
                                    "info" to item)
                        }else{
                            val urlFormat = "HomeWork/CheckPaper?StudentQuestionsTasksId=%s"
                            startActivity<WebActivity>("url" to WorkService.BASE_WEB_URL.plus(urlFormat.format(item.TaskId)))
                        }
                    }
                }
            }

        }
        recyclerView.adapter = adapter
    }

    override fun bindEvents() {
        frame_subject.setOnClickListener {
            filterViewModel.filterSubject(frame_subject)
        }
        frame_job_type.setOnClickListener {
            filterViewModel.filterJobType(frame_job_type)
        }
        frame_job_status.setOnClickListener {
            filterViewModel.filterJobStatus(frame_job_status)
        }

        refreshLayout.setKRefreshListener {
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        }
        adapter.setOnLoadMoreListener({
            workListViewModel.getWorkList()
        },recyclerView)
    }
}