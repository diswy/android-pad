package com.cqebd.student.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.tools.formatTimeYMD
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.ShareHomeworkViewModel
import com.cqebd.student.vo.entity.ShareHomeworkItem
import com.cqebd.teacher.vo.Status
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.activity_be_shared.*
import kotlinx.android.synthetic.main.item_be_shared_homework.view.*
import kotlinx.android.synthetic.main.merge_rv_refresh_layout.*

class BeSharedActivity : BaseActivity() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var shareHomeworkViewModel: ShareHomeworkViewModel
    private lateinit var adapter: BaseQuickAdapter<ShareHomeworkItem, BaseViewHolder>
    private var mPage = 1
    private var isRefresh = false
    private val mPageSize = 20
    private var subjectId: Int? = null
    private var problemType: Int? = null
    private var day: Int? = null

    private val pageProcess = PageProcess.build<ShareHomeworkItem> { it.Id }


    override fun setContentView() {
        setContentView(R.layout.activity_be_shared)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)
        shareHomeworkViewModel = ViewModelProviders.of(this, ShareHomeworkViewModel.Factory(filterViewModel, pageProcess))
                .get(ShareHomeworkViewModel::class.java)


        filterViewModel.subject.observe(this, Observer {
            tv_subject.text = it?.Name ?: "学科"
            subjectId = if (it?.status == -1) null else it?.status
            filterRefresh()
        })

        filterViewModel.problemType.observe(this, Observer {
            tv_problem_type.text = it?.Name ?: "题型"
            problemType = if (it?.status == -1) null else it?.status
            filterRefresh()
        })

        filterViewModel.shareHomeworkDate.observe(this, Observer {
            tv_date.text = it?.Name ?: "日期"
            day = if (it?.status == -1) null else it?.status
            filterRefresh()
        })

        shareHomeworkViewModel.shareList.observe(this, Observer {
            if (isRefresh) {
                adapter.setNewData(it)
                isRefresh = false
            } else
                adapter.addData(it!!)


            if (adapter.data.isEmpty()) {
                pageLoadView.show = true
                pageLoadView.dataEmpty()
            } else {
                pageLoadView.hide()
            }
        })

        adapter = object : BaseQuickAdapter<ShareHomeworkItem, BaseViewHolder>(R.layout.item_be_shared_homework) {
            @SuppressLint("SetTextI18n")
            override fun convert(helper: BaseViewHolder?, item: ShareHomeworkItem) {
                helper?.itemView?.apply {
                    text_name.text = item.PapersName
                    text_question.text = item.PapersQuestion
                    text_subject.text = "题型：${item.QuestionTypeName}"
                    text_time.text = formatTimeYMD(item.CreateDateTime)

                    GlideApp.with(context)
                            .load(item.SubjectImage)
                            .centerInside()
                            .placeholder(R.drawable.ic_img_loading)
                            .into(img_subject)
                }
            }
        }
        adapter.bindToRecyclerView(recyclerView)
        getShareHomeworkList()
    }

    private fun filterRefresh() {
        isRefresh = true
        mPage = 1
        pageLoadView.show = true
        pageLoadView.load()
        getShareHomeworkList()
    }

    private fun toWebActivity(id: Int) {
        val url = "https://service-student.cqebd.cn/HomeWork/TaskShare?id=%s"// 跳转详情的URL
        startActivity<WebActivity>("url" to url.format(id))
    }

    override fun bindEvents() {
        btn_subject.setOnClickListener {
            filterViewModel.filterSubject(it)
        }
        btn_problem_type.setOnClickListener {
            filterViewModel.filterProblemType(it)
        }
        btn_date.setOnClickListener {
            filterViewModel.filterShareHomeworkDate(it)
        }

        refreshLayout.setKRefreshListener {
            mPage = 1
            isRefresh = true
            adapter.setEnableLoadMore(false)
            getShareHomeworkList()
        }

        adapter.setOnLoadMoreListener({
            mPage++
            getShareHomeworkList()
        }, recyclerView)

        adapter.setOnItemClickListener { adapter, _, position ->
            toWebActivity((adapter.getItem(position) as ShareHomeworkItem).Id)
        }
    }

    private fun getShareHomeworkList() {
        shareHomeworkViewModel.getBeShareHomeworkList(mPage, mPageSize, subjectId, problemType, day).observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    refreshLayout.isEnabled = true
                    refreshLayout.refreshComplete(true)
                    pageLoadView.hide()

                    it.data?.DataList?.let {
                        if (it.isNotEmpty()) {
                            adapter.loadMoreComplete()
                        } else {
                            adapter.loadMoreEnd()
                        }
                        shareHomeworkViewModel.setData(it)
                    }

                }
                Status.ERROR -> {
                    refreshLayout.isEnabled = true
                    refreshLayout.refreshComplete(false)
                    pageLoadView.error({
                        getShareHomeworkList()
                    })
                }
                Status.LOADING -> {
                    refreshLayout.isEnabled = false
                    pageLoadView.load()
                }
            }
        })
    }


}
