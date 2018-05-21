package com.cqebd.student.ui.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.adapter.VideoCourseAdapter
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.VideoActivity
import com.cqebd.student.viewmodel.PeriodListViewModel
import com.cqebd.student.vo.entity.PeriodInfo
import com.cqebd.student.vo.entity.SectionPeriodInfo
import com.cqebd.teacher.vo.Status
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.item_course.view.*
import kotlinx.android.synthetic.main.merge_rv_refresh_layout.*


class CourseListFragment : BaseFragment() {
    private lateinit var periodListViewModel: PeriodListViewModel
    //    private lateinit var adapter: BaseQuickAdapter<PeriodInfo, BaseViewHolder>
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

//        adapter = object : BaseQuickAdapter<PeriodInfo, BaseViewHolder>(R.layout.item_course) {
//            override fun convert(helper: BaseViewHolder?, item: PeriodInfo) {
//                helper?.itemView?.apply {
//                    GlideApp.with(App.mContext).load(item.Snapshoot).centerInside().placeholder(R.drawable.ic_avatar).into(img_snapshoot)
//                    text_name.text = item.Name
//                    text_teacher.text = "主讲老师: ".plus(item.TeacherName)
//                    text_start.text = "开课时间：".plus(formatTimeYMDHM(item.PlanStartDate))
//                    text_grade.text = "年级: ".plus(item.GradeName)
//                    when (item.Status) {
//                        0 -> {
//                            text_label.text = "未开始"
//                            text_label.isEnabled = false
//                        }
//                        1 -> {
//                            text_label.text = "直播中"
//
//                        }
//                        2 -> {
//                            text_label.text = "直播结束"
//                            text_label.isEnabled = false
//                        }
//                        3 -> {
//                            text_label.text = "回放"
//                        }
//                    }
//                }
//            }
//        }
        adapter.bindToRecyclerView(recyclerView)
        adapter.setOnItemClickListener { _, _, position ->
            //            val mSectionPeriodInfo = adapter.data[position] as SectionPeriodInfo
//            if (mSectionPeriodInfo)
//            val mItem = mSectionPeriodInfo.t

            val itemDataParent = adapter.data[position] as SectionPeriodInfo
            val itemData = itemDataParent.t
            when {
                itemData.Status == 1 -> startActivity<VideoActivity>("id" to itemData.Id, "status" to itemData.Status, "isLiveMode" to true, "listData" to adapter.getDataNoHeader(), "pos" to position - 1)
                itemData.Status == 3 -> startActivity<VideoActivity>("id" to itemData.Id, "status" to itemData.Status, "listData" to adapter.getDataNoHeader(), "pos" to position - 1)
                else -> toast("视频未准备好哦~")
            }
        }

        getPeriodList(courseId)
    }

    override fun bindEvents() {
        refreshLayout.setKRefreshListener {
            getPeriodList(courseId)
        }
    }

    private fun getPeriodList(id: Long) {
        periodListViewModel.getPeriodList(id).observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    refreshLayout.refreshComplete(true)
                    pageLoadView.hide()
                    periodInfo = it.data
                    periodInfo?.let {
                        periodListViewModel.setData(it)
                    }
                }
                Status.ERROR -> {
                    refreshLayout.refreshComplete(false)
                    pageLoadView.error({
                        getPeriodList(id)
                    })
                }
                Status.LOADING -> pageLoadView.load()
            }
        })
    }
}
