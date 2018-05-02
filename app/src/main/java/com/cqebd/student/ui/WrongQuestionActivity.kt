package com.cqebd.student.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.repository.NetworkResource
import com.cqebd.student.tools.*
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.vo.entity.WrongQuestion
import com.cqebd.teacher.vo.Status
import com.orhanobut.logger.Logger
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.activity_wrong_question.*
import kotlinx.android.synthetic.main.item_wrong_question.view.*

/**
 * 描述
 * Created by gorden on 2018/3/22.
 */
class WrongQuestionActivity : BaseActivity() {
    private lateinit var filterViewModel: FilterViewModel
    private val pageProcess = PageProcess.build<WrongQuestion> { it.StudentQuestionsTasksID}
    private lateinit var adapter: BaseQuickAdapter<WrongQuestion, BaseViewHolder>



    override fun setContentView() {
        setContentView(R.layout.activity_wrong_question)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)

        filterViewModel.subject.observe(this, Observer {
            text_subject.text = it?.Name ?: "学科"
            pageLoadView.show = true
            pageProcess.data.clear()
            getWrongQuestionList()
        })

        filterViewModel.jobType.observe(this, Observer {
            text_job_type.text = it?.Name ?: "作业类型"
            pageLoadView.show = true
            pageProcess.data.clear()
            getWrongQuestionList()
        })
        getWrongQuestionList()

        adapter = object : BaseQuickAdapter<WrongQuestion, BaseViewHolder>(R.layout.item_wrong_question, pageProcess.data) {
            val subjectBg = arrayOf(R.drawable.bg_subject1, R.drawable.bg_subject2, R.drawable.bg_subject3, R.drawable.bg_subject4)
            override fun convert(helper: BaseViewHolder?, item: WrongQuestion) {
                helper?.itemView?.apply {
                    text_name.text = item.Name
                    text_subject.text = item.SubjectTypeName.take(1)
                    text_count.text = "做错%s题".format(item.ErrorCount)
                    text_time.text = formatTimeYMD(item.DateTime)
                    text_job_type.text = item.PapersTypeName
                    text_subject.setBackgroundResource(subjectBg[helper.layoutPosition % 4])
                }
            }
        }
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { adapter, view, position ->
            val itemData = adapter.data[position] as WrongQuestion
            startActivity<WrongQuestionDetailsActivity>("taskId" to itemData.StudentQuestionsTasksID,"title" to itemData.Name)
        }
    }

    override fun bindEvents() {
        frame_subject.setOnClickListener {
            filterViewModel.filterSubject(frame_subject)
        }
        frame_job_type.setOnClickListener {
            filterViewModel.filterJobType(frame_job_type)
        }
        refreshLayout.setKRefreshListener {
            pageProcess.data.clear()
            getWrongQuestionList()
        }
    }


    private fun getWrongQuestionList(){
        object :NetworkResource<List<WrongQuestion>>(){
            override fun createCall(): LiveData<ApiResponse<List<WrongQuestion>>> {
                return  NetClient.workService().getWrongQuestionList(loginId,filterViewModel.subject.value?.status,filterViewModel.jobType.value?.status,null)
            }
        }.asLiveData.observe(this, Observer {
            when(it?.status){
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
                        getWrongQuestionList()
                    })
                }
                Status.LOADING -> pageLoadView.load()
            }
        })
    }
}