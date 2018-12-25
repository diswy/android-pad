package com.cqebd.student.ui.video

import android.support.v7.widget.GridLayoutManager
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import com.cqebd.student.glide.GlideRoundTransform
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.ui.VideoDetailsActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.vo.entity.VideoInfo
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.item_new_video.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import retrofit2.Call

class MyVideoCollectFragment : BaseLazyFragment() {
    private lateinit var adapter: BaseQuickAdapter<VideoInfo, BaseViewHolder>
    private var call: Call<BaseResponse<List<VideoInfo>>>? = null
    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_subscribe_collect_live
    }

    override fun lazyLoad() {
        call = NetClient.videoService().getCollectList()

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
        recyclerView.layoutManager = GridLayoutManager(activity,2)
        adapter.bindToRecyclerView(recyclerView)
        adapter.setOnItemClickListener { adapter, _, position ->
            startActivity<VideoDetailsActivity>("data" to adapter.getItem(position))
        }
        smart_refresh_layout.setOnRefreshListener {
            getMyCollect()
        }
        pageLoadView.show = true

        getMyCollect()
    }

    override fun onInvisible() {
        call?.cancel()
    }

    private fun getMyCollect() {
        pageLoadView.load()
        call?.let {
            if (it.isExecuted)
                call = NetClient.videoService().getCollectList()
        }
        call?.enqueue(object : NetCallBack<BaseResponse<List<VideoInfo>>>() {
                    override fun onSucceed(response: BaseResponse<List<VideoInfo>>?) {
                        smart_refresh_layout.finishRefresh(true)
                        response?.data?.let {
                            Logger.json(Gson().toJson(it))
                            adapter.setNewData(it)
                            if (it.isEmpty()) {
                                pageLoadView.show = true
                                pageLoadView.dataEmpty()
                            } else {
                                pageLoadView.hide()
                            }
                        }

                    }

                    override fun onFailure() {
                        try {
                            smart_refresh_layout.finishRefresh(false)
                        } catch (e: Exception) {

                        }
                    }

                })
    }
}