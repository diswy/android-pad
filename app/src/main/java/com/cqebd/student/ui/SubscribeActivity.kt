package com.cqebd.student.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.tools.formatTimeYMD
import com.cqebd.student.viewmodel.SubscribeListViewModel
import com.cqebd.student.vo.entity.CourseInfo
import com.cqebd.teacher.vo.Status
import kotlinx.android.synthetic.main.activity_subcribe.*
import kotlinx.android.synthetic.main.item_course.view.*
import kotlinx.android.synthetic.main.merge_rv_refresh_layout.*

class SubscribeActivity : BaseActivity() {
    private lateinit var subscribeViewModel: SubscribeListViewModel
    private lateinit var adapter: BaseQuickAdapter<CourseInfo, BaseViewHolder>
    private var subscribeList:List<CourseInfo>? = null

    override fun setContentView() {
        setContentView(R.layout.activity_subcribe)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        subscribeViewModel = ViewModelProviders.of(this).get(SubscribeListViewModel::class.java)

        subscribeViewModel.subscribeList.observe(this, Observer {
            adapter.setNewData(it)
            if (it == null || it.isEmpty()) {
                pageLoadView.show = true
                pageLoadView.dataEmpty()
            } else {
                pageLoadView.hide()
            }
        })

        adapter = object : BaseQuickAdapter<CourseInfo, BaseViewHolder>(R.layout.item_course) {
            override fun convert(helper: BaseViewHolder?, item: CourseInfo) {
                helper?.itemView?.apply {
                    GlideApp.with(App.mContext).load(item.Snapshoot).centerInside().placeholder(R.drawable.ic_avatar).into(img_snapshoot)
                    text_name.text = item.Name
                    text_teacher.text = "主讲老师: ".plus(item.TeacherName)
                    text_start.text = "开课时间：".plus(formatTimeYMD(item.PlanStartDate))
                    text_grade.text = "年级: ".plus(item.GradeName)
                }
            }
        }
        adapter.bindToRecyclerView(recyclerView)

        getSubscribeList()

    }

    override fun bindEvents() {
        refreshLayout.setKRefreshListener {
            getSubscribeList()
        }
    }


    /**
     * 订阅列表
     */
    private fun getSubscribeList() {
        subscribeViewModel.getSubscribeList().observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    refreshLayout.refreshComplete(true)
                    pageLoadView.hide()
                    subscribeList = it.data
                    subscribeViewModel.setData(subscribeList!!)
                }
                Status.ERROR -> {
                    refreshLayout.refreshComplete(false)
                    pageLoadView.error({
                        getSubscribeList()
                    })
                }
                Status.LOADING -> {
                    pageLoadView.load()
                }
            }
        })
    }
}
