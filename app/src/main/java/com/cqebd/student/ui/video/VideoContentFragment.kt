package com.cqebd.student.ui.video

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.glide.GlideRoundTransform
import com.cqebd.student.glide.RoundedTransformation
import com.cqebd.student.ui.VideoDetailsActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.VideoListViewModel
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.VideoInfo
import com.cqebd.teacher.vo.Status
import gorden.lib.anko.static.startActivity
import gorden.rxbus.RxBus
import kotlinx.android.synthetic.main.fragment_video_content.*
import kotlinx.android.synthetic.main.item_new_video.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.jetbrains.anko.support.v4.dip

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
        // 副标题
        val subCommonNavigator = CommonNavigator(context)
        subCommonNavigator.scrollPivotX = 0.65f
        subCommonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = ColorTransitionPagerTitleView(context)
                titleView.normalColor = resources.getColor(R.color.color_tab_title)
                titleView.selectedColor = resources.getColor(R.color.color_main)
                titleView.text = FilterData.subjectAll[index].Name
                titleView.textSize = 14f
                titleView.setOnClickListener {
                    magic_indicator_subtitle.onPageSelected(index)
                    magic_indicator_subtitle.onPageScrollStateChanged(index)
                    magic_indicator_subtitle.onPageScrolled(index, 0f, 0)
//                    filterViewModel.filterJobStatus(FilterData(FilterData.jobStatus[index].status, FilterData.jobStatus[index].Name))
                }
                return titleView
            }

            override fun getCount(): Int {
                return FilterData.subjectAll.size
            }

            override fun getIndicator(p0: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = dip(2).toFloat()
                indicator.lineWidth = dip(15).toFloat()
                indicator.roundRadius = dip(3).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(resources.getColor(R.color.color_main))
                return indicator
            }
        }
        magic_indicator_subtitle.navigator = subCommonNavigator
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

        adapter = object : BaseQuickAdapter<VideoInfo, BaseViewHolder>(R.layout.item_new_video) {
            override fun convert(helper: BaseViewHolder?, item: VideoInfo) {
                helper?.itemView?.apply {
                    GlideApp.with(context)
                            .load(item.Snapshoot)
                            .transforms(CenterCrop(),GlideRoundTransform(context))
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
}