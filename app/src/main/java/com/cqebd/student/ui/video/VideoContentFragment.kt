package com.cqebd.student.ui.video

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.`interface`.CustomCallback
import com.cqebd.student.adapter.SubtitleNavigatorAdapter
import com.cqebd.student.event.STATUS_SUBJECT
import com.cqebd.student.event.STATUS_SUBSCRIBE
import com.cqebd.student.event.STATUS_TIME
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import com.cqebd.student.glide.GlideRoundTransform
import com.cqebd.student.ui.VideoDetailsActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.VideoListViewModel
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.VideoInfo
import com.cqebd.teacher.vo.Status
import gorden.lib.anko.static.startActivity
import gorden.rxbus.RxBus
import gorden.rxbus.Subscribe
import kotlinx.android.synthetic.main.fragment_video_content.*
import kotlinx.android.synthetic.main.item_new_video.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

class VideoContentFragment : BaseLazyFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var videoListViewModel: VideoListViewModel
    private var videoList: List<VideoInfo>? = null
    private lateinit var adapter: BaseQuickAdapter<VideoInfo, BaseViewHolder>

    override fun getLayoutRes(): Int {
        return R.layout.fragment_video_content
    }

    override fun initView() {
        super.initView()

        context?.let {
            // 副标题
            val subCommonNavigator = CommonNavigator(it)
            val mSubtitleNavigatorAdapter = SubtitleNavigatorAdapter(it, FilterData.grade, magic_indicator_subtitle)
            subCommonNavigator.adapter = mSubtitleNavigatorAdapter
            magic_indicator_subtitle.navigator = subCommonNavigator

            mSubtitleNavigatorAdapter.setOnTitleViewOnClickListener(object : CustomCallback.OnPositionListener {
                override fun onClickPos(pos: Int) {
                    filterViewModel.filterGrade(FilterData.grade[pos])
                }
            })
        }
    }

    override fun lazyLoad() {
        RxBus.get().register(this)
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)
        videoListViewModel = ViewModelProviders.of(this, VideoListViewModel.Factory(filterViewModel)).get(VideoListViewModel::class.java)

        videoListViewModel.videoList.observe(this, Observer {
            adapter.setNewData(it)
            if (it == null || it.isEmpty()) {
                pageLoadView.show = true
                pageLoadView.dataEmpty()
            } else {
                pageLoadView.hide()
            }
        })

        filterViewModel.grade.observe(this, Observer {
            videoListViewModel.filter(videoList)
        })
        filterViewModel.subscribeStatus.observe(this, Observer {
            videoListViewModel.filter(videoList)
        })

        adapter = object : BaseQuickAdapter<VideoInfo, BaseViewHolder>(R.layout.item_new_video) {
            override fun convert(helper: BaseViewHolder?, item: VideoInfo) {
                helper?.itemView?.apply {
                    GlideApp.with(context)
                            .load(item.Snapshoot)
                            .transforms(CenterCrop(), GlideRoundTransform(context))
//                            .placeholder(R.drawable.ic_avatar)
                            .into(iv_video_snapshot)
                    tv_video_title.text = item.Name
                    tv_video_teacher_grade.text = getString(R.string.teacher_and_grade, item.TeacherName, item.GradeName)
                    tv_video_count_subject.text = getString(R.string.count_and_subject, item.PeriodCount, item.SubjectTypeName)
                }
            }
        }
        adapter.bindToRecyclerView(recyclerView)
        adapter.setOnItemClickListener { _, _, position ->
            startActivity<VideoDetailsActivity>("data" to adapter.getItem(position))
        }
        smart_refresh_layout.setOnRefreshListener {
            getCourseList()
        }

        pageLoadView.show = true
        getCourseList()
    }

    override fun onInvisible() {
        RxBus.get().unRegister(this)
    }

    override fun bindEvents() {
        // 侧滑菜单处理
        val mainActivity = activity as MainActivity
        btn_filter.setOnClickListener {
            mainActivity.switchDrawerLayout()
        }
    }

    private fun getCourseList() {
        videoListViewModel.getCourseList().observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    smart_refresh_layout.finishRefresh(true)
                    pageLoadView.hide()
                    videoList = it.data
                    videoListViewModel.filter(videoList)
                }
                Status.ERROR -> {
                    smart_refresh_layout.finishRefresh(false)
                    pageLoadView.error({
                        getCourseList()
                    })
                }
                Status.LOADING -> pageLoadView.load()
            }
        })
    }

    @Subscribe(code = STATUS_SUBSCRIBE)
    fun filterSubscribeStatus(filter: FilterData) {
        filterViewModel.filterSubscribeStatus(filter)
    }

    @Subscribe(code = STATUS_SUBJECT)
    fun filterSubject(filter: FilterData) {
        filterViewModel.filterSubject(filter)
    }

    @Subscribe(code = STATUS_TIME)
    fun filterTime(filter: FilterData) {
        filterViewModel.filterDateTime(filter)
    }
}