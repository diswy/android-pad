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
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.VideoListViewModel
import com.cqebd.student.vo.entity.VideoInfo
import com.cqebd.teacher.vo.Status
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.item_video.view.*


/**
 * 课堂
 * Created by gorden on 2018/2/26.
 */
class VideoFragment : BaseFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var videoListViewModel: VideoListViewModel
    private var videoList:List<VideoInfo>?=null
    private lateinit var adapter: BaseQuickAdapter<VideoInfo,BaseViewHolder>
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_video,container,false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)
        videoListViewModel = ViewModelProviders.of(this,VideoListViewModel.Factory(filterViewModel)).get(VideoListViewModel::class.java)

        filterViewModel.grade.observe(this, Observer {
            text_grade.text = it?.Name?:"年级"
            videoListViewModel.filter(videoList)
        })
        filterViewModel.subject.observe(this, Observer {
            text_subject.text = it?.Name?:"学科"
            videoListViewModel.filter(videoList)
        })

        filterViewModel.dateTime.observe(this, Observer {
            text_datetime.text = it?.Name?:"时段"
            videoListViewModel.filter(videoList)
        })
        filterViewModel.subscribeStatus.observe(this, Observer {
            text_subscribe_status.text = it?.Name?:"状态"
            videoListViewModel.filter(videoList)
        })

        videoListViewModel.videoList.observe(this, Observer {
            adapter.setNewData(it)
            if (it==null||it.isEmpty()){
                pageLoadView.show = true
                pageLoadView.dataEmpty()
            }else{pageLoadView.hide()}
        })

        pager_ad.setImagesUrl(arrayListOf("http://img.hb.aicdn.com/03db5cd4cd1bb2a311c5649060f91d84f59e7127e5ede-kzcL0Q_fw658"
                , "http://img.hb.aicdn.com/03db5cd4cd1bb2a311c5649060f91d84f59e7127e5ede-kzcL0Q_fw658"
                , "http://img.hb.aicdn.com/03db5cd4cd1bb2a311c5649060f91d84f59e7127e5ede-kzcL0Q_fw658"))

        adapter = object : BaseQuickAdapter<VideoInfo,BaseViewHolder>(R.layout.item_video){
            override fun convert(helper: BaseViewHolder?, item: VideoInfo) {
                helper?.itemView?.apply {
                    GlideApp.with(App.mContext).load(item.TeacherPhoto).circleCrop().placeholder(R.drawable.ic_avatar).into(img_avatar)
                    text_name.text = item.Name
                    text_teacher.text = "主讲老师: ".plus(item.TeacherName)
                    text_count.text = "共%s节".format(item.PeriodCount)
                    text_grade.text = "年级: ".plus(item.GradeName)
                    text_subject.text = "科目: ".plus(item.SubjectTypeName)
                }
            }
        }
        adapter.bindToRecyclerView(recyclerView)
        adapter.setOnItemClickListener { _, _, position ->
            startActivity<VideoDetailsActivity>("data" to adapter.getItem(position))
        }
        getCourseList()
    }

    override fun bindEvents() {
        frame_grade.setOnClickListener {
            filterViewModel.filterGrade(frame_grade)
        }
        frame_subject.setOnClickListener {
            filterViewModel.filterSubject(frame_subject)
        }
        frame_datetime.setOnClickListener {
            filterViewModel.filterDateTime(frame_datetime)
        }
        frame_subscribe_status.setOnClickListener {
            filterViewModel.filterSubscribeStatus(frame_subscribe_status)
        }

        refreshLayout.setKRefreshListener {
            getCourseList()
        }
    }


    private fun getCourseList(){
        videoListViewModel.getCourseList().observe(this, Observer {
            when(it?.status){
                Status.SUCCESS -> {
                    refreshLayout.refreshComplete(true)
                    pageLoadView.hide()
                    videoList = it.data
                    videoListViewModel.filter(videoList)
                }
                Status.ERROR -> {
                    refreshLayout.refreshComplete(false)
                    pageLoadView.error({
                        getCourseList()
                    })
                }
                Status.LOADING -> pageLoadView.load()
            }
        })
    }

}