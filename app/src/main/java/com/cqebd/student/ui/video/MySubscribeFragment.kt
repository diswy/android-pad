package com.cqebd.student.ui.video


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.glide.GlideRoundTransform
import com.cqebd.student.ui.VideoDetailsActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.viewmodel.SubscribeListViewModel
import com.cqebd.student.vo.entity.VideoInfo
import com.cqebd.teacher.vo.Status
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.item_new_video.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*

/**
 *  我的订阅
 */
class MySubscribeFragment : BaseLazyFragment() {
    private lateinit var subscribeViewModel: SubscribeListViewModel
    private lateinit var adapter: BaseQuickAdapter<VideoInfo, BaseViewHolder>
    private var subscribeList: List<VideoInfo>? = null

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_subscribe_collect_live
    }

    override fun lazyLoad() {
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
        adapter.setOnItemClickListener { adapter, _, position ->
            startActivity<VideoDetailsActivity>("data" to adapter.getItem(position))
        }

        smart_refresh_layout.setOnRefreshListener {
            getSubscribeList()
        }
        pageLoadView.show = true
        getSubscribeList()
    }

    override fun onInvisible() {

    }

    /**
     * 订阅列表
     */
    private fun getSubscribeList() {
        subscribeViewModel.getSubscribeList().observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    smart_refresh_layout.finishRefresh(true)
                    subscribeList = it.data
                    subscribeList?.let {
                        subscribeViewModel.setData(it)
                    }
                }
                Status.ERROR -> {
                    smart_refresh_layout.finishRefresh(false)
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
