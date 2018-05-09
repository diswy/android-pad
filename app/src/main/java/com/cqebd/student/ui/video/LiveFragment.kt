package com.cqebd.student.ui.video

import android.support.v7.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.glide.RoundedTransformation
import com.cqebd.student.ui.fragment.BaseLazyFragment
import kotlinx.android.synthetic.main.item_live_video.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*

class LiveFragment : BaseLazyFragment(){
    private lateinit var adapter: BaseQuickAdapter<String, BaseViewHolder>

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_subscribe_collect_live
    }

    override fun lazyLoad() {

        recyclerView.layoutManager = GridLayoutManager(context,2)

        adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_live_video) {
            override fun convert(helper: BaseViewHolder?, item: String) {
                helper?.itemView?.apply {
                    GlideApp.with(context)
                            .load("http://img.hb.aicdn.com/eacb4f1af40462fecccdf89c32f55f063da95490f79a-bwV4Vc_fw658")
                            .centerCrop()
                            .into(iv_live)

                    GlideApp.with(context)
                            .load("http://img.hb.aicdn.com/eacb4f1af40462fecccdf89c32f55f063da95490f79a-bwV4Vc_fw658")
                            .circleCrop()
                            .into(iv_live_teacher)

                    tv_live_title.text = "这是标题"
                    tv_live_grade_teacher.text = "年级：六年级\n主讲老师：张老师"
                    tv_live_price.text = "免费"
                    tv_live_count.text = "课程节数：1"
                }
            }
        }
        adapter.bindToRecyclerView(recyclerView)

        adapter.setNewData(listOf("1","2","3"))

        pageLoadView.hide()
    }

    override fun onInvisible() {

    }


}