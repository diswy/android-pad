package com.cqebd.student.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
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
import com.cqebd.student.tools.formatTimeMDHM
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.WorkListViewModel
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.WorkInfo
import com.cqebd.teacher.vo.Status
import gorden.lib.anko.static.logError
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_work.view.*

/**
 * 首页
 * Created by gorden on 2018/2/26.
 */
class HomeFragment : BaseFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var workListViewModel: WorkListViewModel
    private val pageProcess = PageProcess.build<WorkInfo> { it.TaskId }
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_home, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)
        workListViewModel = ViewModelProviders.of(this, WorkListViewModel.Factory(filterViewModel, pageProcess))
                .get(WorkListViewModel::class.java)

        filterViewModel.subject.observe(this, Observer {
            text_subject.text = it?.Name ?: "学科"
            pageLoadView.show = true
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        })

        filterViewModel.jobType.observe(this, Observer {
            text_job_type.text = it?.Name ?: "作业类型"
            pageLoadView.show = true
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        })

        filterViewModel.jobStatus.observe(this, Observer {
            text_job_status.text = it?.Name ?: "作业状态"
            pageLoadView.show = true
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        })

        workListViewModel.workInfoList.observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    pageProcess.refreshData(it.data!!)
                    refreshLayout.refreshComplete(true)
                    recyclerView.adapter.notifyDataSetChanged()
                    if (pageProcess.data.isEmpty()) {
                        pageLoadView.dataEmpty()
                    } else {
                        pageLoadView.hide()
                    }
                }
                Status.ERROR -> {
                    refreshLayout.refreshComplete(false)
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

        recyclerView.adapter = object : BaseQuickAdapter<WorkInfo, BaseViewHolder>(R.layout.item_work, pageProcess.data) {
            val subjectBg = arrayOf(R.drawable.bg_subject1, R.drawable.bg_subject2, R.drawable.bg_subject3, R.drawable.bg_subject4)
            override fun convert(helper: BaseViewHolder?, item: WorkInfo) {
                helper?.itemView?.apply {
                    text_name.text = item.Name
                    text_subject.text = item.SubjectName.take(1)
                    text_count.text = "共%s题".format(item.QuestionCount)
                    text_start_time.text = "布置时间: ".plus(formatTimeYMDHM(item.CanStartDateTime))
                    text_end_time.text = "截止时间: ".plus(formatTimeMDHM(item.CanEndDateTime))
                    text_subject.setBackgroundResource(subjectBg[helper.layoutPosition % 4])

                    when (item.Status) {
                        -1 -> {
                            text_status.text = "新作业"
                            text_status.setTextColor(colorForRes(R.color.status_new))
                        }
                        0 -> {
                            text_status.text = "答题中"
                            text_status.setTextColor(colorForRes(R.color.status_run))
                        }
                        1 -> {
                            text_status.text = "已完成"
                            text_status.setTextColor(colorForRes(R.color.status_complete))
                        }
                        2 -> {
                            text_status.text = "已批阅"
                            text_status.setTextColor(colorForRes(R.color.status_read))
                        }
                    }

                    text_status.text = FilterData.jobStatus.find { it.status == item.Status }?.Name
                    setOnClickListener {
                        if (item.Status<=0){
                            val urlFormat = "HomeWork/ExaminationPapers?id=%s&taskid=%s"
                            logError(item)
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

        pager_ad.setImagesUrl(arrayListOf("http://img.hb.aicdn.com/03db5cd4cd1bb2a311c5649060f91d84f59e7127e5ede-kzcL0Q_fw658"
                , "http://img.hb.aicdn.com/03db5cd4cd1bb2a311c5649060f91d84f59e7127e5ede-kzcL0Q_fw658"
                , "http://img.hb.aicdn.com/03db5cd4cd1bb2a311c5649060f91d84f59e7127e5ede-kzcL0Q_fw658"))
    }


    override fun bindEvents() {
        lin_share_work.setOnClickListener {
            startActivity<ShareHomeworkActivity>()
        }
        lin_class_schedule.setOnClickListener {
            //课程表
            startActivity<ClassScheduleActivity>()
        }
        lin_wrong_book.setOnClickListener {
            startActivity<WrongQuestionActivity>()
        }
        lin_my_subscription.setOnClickListener {
            startActivity<SubscribeActivity>()
        }

        frame_subject.setOnClickListener {
            filterViewModel.filterSubject(pop_window)
        }
        frame_job_type.setOnClickListener {
            filterViewModel.filterJobType(pop_window)
        }
        frame_job_status.setOnClickListener {
            filterViewModel.filterJobStatus(pop_window)
        }

        refreshLayout.setKRefreshListener {
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        }
    }

}