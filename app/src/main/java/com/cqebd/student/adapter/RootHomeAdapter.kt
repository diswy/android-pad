package com.cqebd.student.adapter

import android.content.Intent
import android.support.v7.widget.CardView
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.glide.GlideRoundTransform
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.VideoDetailsActivity
import com.cqebd.student.vo.entity.RootHomeEntity
import com.xiaofu.lib_base_xiaofu.img.GlideApp

class RootHomeAdapter(data: List<RootHomeEntity>?) : BaseMultiItemQuickAdapter<RootHomeEntity, BaseViewHolder>(data) {

    init {
        addItemType(RootHomeEntity.TITLE, R.layout.item_root_home_title)
        addItemType(RootHomeEntity.ITEM, R.layout.item_root_home_item)
        addItemType(RootHomeEntity.DOUBLE, R.layout.item_root_home_double)
    }

    override fun convert(helper: BaseViewHolder?, item: RootHomeEntity) {
        when (helper?.itemViewType) {
            RootHomeEntity.TITLE -> {
                helper.setText(R.id.tv_root_home_title, item.title)
                if (item.title == "课程推荐")
                    helper.itemView.setBackgroundResource(R.color.bg_theme)
            }
            RootHomeEntity.ITEM -> {
                item.item?.let { data ->
                    helper.setText(R.id.mTitle, data.Name)
                            .setText(R.id.mTeacher, "主讲老师：${data.TeacherName}")
                            .setText(R.id.mCount, "课程节数：？？？ ")
                            .setText(R.id.mGrade, "年级：${data.GradeName}")
                    val ic = helper.getView(R.id.mCourseImg) as ImageView
                    GlideApp.with(mContext)
                            .load(data.Snapshoot)
                            .transforms(CenterInside(), GlideRoundTransform(mContext))
                            .into(ic)

                    helper.itemView.setOnClickListener {
                        val i = Intent(mContext, VideoDetailsActivity::class.java)
                        i.putExtra("data", data)
                        mContext.startActivity(i)
                    }
                }
            }
            RootHomeEntity.DOUBLE -> {
                helper.setText(R.id.mTitle1, item.arg0?.Name)
                        .setText(R.id.mTeacher1, item.arg0?.TeacherName)
                        .setText(R.id.mCount1, "？？？")
                        .setText(R.id.mTitle2, item.arg1?.Name)
                        .setText(R.id.mTeacher2, item.arg1?.TeacherName)
                        .setText(R.id.mCount2, "？？？")

                (helper.getView(R.id.mItem1) as CardView).setOnClickListener {
                    item.arg0?.let {
                        val i = Intent(mContext, VideoDetailsActivity::class.java)
                        i.putExtra("data", it)
                        mContext.startActivity(i)
                    }
                }
                (helper.getView(R.id.mItem2) as CardView).setOnClickListener {

                    if (item.arg1 != null) {
                        val i = Intent(mContext, VideoDetailsActivity::class.java)
                        i.putExtra("data", item.arg1)
                        mContext.startActivity(i)
                    } else {
                        toast("该位置无课程，请不要点击")
                    }
                }
            }
        }
    }
}