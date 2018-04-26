package com.cqebd.student.ui.work


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.tools.colorForRes
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.JobPreviewActivity
import com.cqebd.student.ui.WebActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.WorkListViewModel
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.WorkInfo
import com.cqebd.teacher.vo.Status
import com.orhanobut.logger.Logger
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.item_work.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*

/**
 * 作业内容
 *
 */
class HomeworkContentFragment : BaseLazyFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var workListViewModel: WorkListViewModel
    private val pageProcess = PageProcess.build<WorkInfo> { it.TaskId }
    private lateinit var adapter: BaseQuickAdapter<WorkInfo, BaseViewHolder>


    override fun getLayoutRes(): Int {
        return R.layout.fragment_homework_content
    }

    override fun lazyLoad() {
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java).apply { yOff=0 }
        workListViewModel = ViewModelProviders.of(this,WorkListViewModel.Factory(filterViewModel,pageProcess))
                .get(WorkListViewModel::class.java)
        Logger.d("-->>>lazyLoad")

        adapter = object : BaseQuickAdapter<WorkInfo, BaseViewHolder>(R.layout.item_work,pageProcess.data){
            val subjectBg = arrayOf(R.drawable.bg_subject1,R.drawable.bg_subject2,R.drawable.bg_subject3,R.drawable.bg_subject4)
            override fun convert(helper: BaseViewHolder?, item: WorkInfo) {
                helper?.itemView?.apply {
                    text_name.text = item.Name
                    text_subject.text = item.SubjectName.take(1)
                    text_count.text = "共%s题".format(item.QuestionCount)
                    text_start_time.text = "布置时间: ".plus(formatTimeYMDHM(item.publishTime))
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

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        smart_refresh_layout.setOnRefreshListener {
            workListViewModel.getWorkList(true)
//            it.finishRefresh(3000)
        }


        workListViewModel.workInfoList.observe(this, Observer {
            when(it?.status){
                Status.SUCCESS -> {
                    val data = it.data
                    if (pageProcess.pageIndex==1){
                        pageProcess.refreshData(data!!)
//                        refreshLayout.refreshComplete(true)
                        smart_refresh_layout.finishRefresh(true)
                        adapter.setNewData(pageProcess.data)
                        Logger.d(pageProcess.data)
                        if (pageProcess.data.isEmpty()){
//                            pageLoadView.dataEmpty()
                        }else{
//                            pageLoadView.hide()
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
                    smart_refresh_layout.finishRefresh(false)
//                    refreshLayout.refreshComplete(false)
                    adapter.loadMoreFail()
//                    pageLoadView.error({
//                        workListViewModel.getWorkList()
//                    })

                }
                Status.LOADING -> {
//                    pageLoadView.load() 
                }
            }
        })

        workListViewModel.getWorkList(true)

    }

    override fun onInvisible() {

    }

    override fun initView() {
        arguments?.let {

        }
        Logger.d("-->>>initView")

        super.initView()
    }


}
