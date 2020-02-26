package com.cqebd.student.ui.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R
import com.cqebd.student.adapter.VideoCourseAdapter
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.live.ui.LiveActivity
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.VideoActivity
import com.cqebd.student.ui.VideoDetailsActivity
import com.cqebd.student.viewmodel.PeriodListViewModel
import com.cqebd.student.vo.entity.PeriodInfo
import com.cqebd.student.vo.entity.SectionPeriodInfo
import com.cqebd.teacher.vo.Status
import com.netease.nimlib.sdk.NIMClient
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.merge_refresh_layout.*


class CourseListFragment : BaseFragment() {
    private lateinit var periodListViewModel: PeriodListViewModel
    private lateinit var adapter: VideoCourseAdapter
    private var periodInfo: List<PeriodInfo>? = null
    private var courseId: Long = 0

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_course_list, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        courseId = arguments!!.getLong("id")

        periodListViewModel = ViewModelProviders.of(this).get(PeriodListViewModel::class.java)

        periodListViewModel.videoList.observe(this, Observer {

            if (it == null || it.isEmpty()) {
                pageLoadView.show = true
                pageLoadView.dataEmpty()
            } else {
                pageLoadView.hide()
            }

            it?.let {
                updateProgressbar(it)
                val list = ArrayList<SectionPeriodInfo>()
                list.add(SectionPeriodInfo("全部课程"))
                for (item in it) {
                    list.add(SectionPeriodInfo(item))
                }
                adapter.setNewData(list)
            }
        })

        val mVideoCourseAdapter = VideoCourseAdapter()
        adapter = mVideoCourseAdapter

        adapter.bindToRecyclerView(recyclerView)
        adapter.setOnItemClickListener { _, _, position ->

            val itemDataParent = adapter.data[position] as SectionPeriodInfo
            val itemData = itemDataParent.t
            when {
                // status 0 未播 1直播中 2直播结束 3转码结束
                // type 1 点播 2直播
                itemData.Status == 1 -> {
                    if (NIMClient.getStatus().value == 6) {
                        startActivity<LiveActivity>("id" to itemData.Id, "hasChat" to itemData.HasChat, "hasIWB" to itemData.HasIWB, "hasVchat" to itemData.HasVchat, "title" to itemData.Name)
                    } else {
                        toast("你的账号已在别的设备上登录，请退出应用重新登录后重试")
                    }
                }
//                itemData.Status == 3 -> startActivity<LiveActivity>()
//                itemData.Status == 1 -> startActivity<VideoActivity>("id" to itemData.Id, "status" to itemData.Status, "isLiveMode" to true, "listData" to adapter.getDataNoHeader(), "pos" to position - 1)
                itemData.Status == 3 -> startActivity<VideoActivity>("title" to itemData.Name, "id" to itemData.Id, "status" to itemData.Status, "listData" to adapter.getDataNoHeader(), "pos" to position - 1)
                else -> toast("视频未准备好哦~")
            }
        }

        getPeriodList(courseId)
    }

    private fun updateProgressbar(list: List<PeriodInfo>) {
        val totalSize = list.size// 课程总长度
        var finished = 0// 已完成的课程
        for (item: PeriodInfo in list) {
            if (item.Status == 2 || item.Status == 3)
                ++finished
        }
        val mProgress: Double = finished * 100.00 / totalSize
        (activity as VideoDetailsActivity).refreshProgress(mProgress.toInt())
    }

    override fun bindEvents() {
        smart_refresh_layout.setOnRefreshListener {
            getPeriodList(courseId)
        }
    }

    private fun getPeriodList(id: Long) {
        periodListViewModel.getPeriodList(id).observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    smart_refresh_layout.finishRefresh(true)
                    pageLoadView.hide()
                    periodInfo = it.data
                    periodInfo?.let {
                        periodListViewModel.setData(it)
                    }
                }
                Status.ERROR -> {
                    smart_refresh_layout.finishRefresh(false)
                    pageLoadView.error({
                        getPeriodList(id)
                    })
                }
                Status.LOADING -> pageLoadView.load()
            }
        })
    }
}
