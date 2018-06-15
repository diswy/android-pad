package com.cqebd.student.adapter

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.CardView
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.glide.GlideRoundTransform
import com.cqebd.student.ui.VideoDetailsActivity
import com.cqebd.student.vo.entity.RootHomeEntity

class RootHomeAdapter(data:List<RootHomeEntity>?) : BaseMultiItemQuickAdapter<RootHomeEntity, BaseViewHolder>(data){

    init {
        addItemType(RootHomeEntity.TITLE, R.layout.item_root_home_title)
        addItemType(RootHomeEntity.ITEM, R.layout.item_root_home_item)
        addItemType(RootHomeEntity.DOUBLE, R.layout.item_root_home_double)
    }

    override fun convert(helper: BaseViewHolder?, item: RootHomeEntity) {
        when (helper?.itemViewType){
            RootHomeEntity.TITLE -> {
                helper.setText(R.id.tv_root_home_title,item.title)
                if (item.title == "课程推荐")
                    helper.itemView.setBackgroundResource(R.color.color_f2f6f9)
            }
            RootHomeEntity.ITEM -> {
                helper.setText(R.id.mTitle,item.item?.Name)
                        .setText(R.id.mTeacher,"主讲老师：${item.item?.TeacherName}")
                        .setText(R.id.mCount,"课程节数：？？？ ")
                        .setText(R.id.mGrade,"年级：${item.item?.GradeName}")
                val ic = helper.getView(R.id.mCourseImg) as ImageView
                GlideApp.with(mContext)
                        .load(item.item?.Snapshoot)
                        .transforms(CenterInside(), GlideRoundTransform(mContext))
                        .into(ic)

                helper.itemView.setOnClickListener {
                    val i = Intent(mContext, VideoDetailsActivity::class.java)
                    i.putExtra("data",item.item)
                    mContext.startActivity(i)
                }
            }
            RootHomeEntity.DOUBLE -> {
                helper.setText(R.id.mTitle1,item.arg0?.Name)
                        .setText(R.id.mTeacher1,item.arg0?.TeacherName)
                        .setText(R.id.mCount1,"？？？")
                        .setText(R.id.mTitle2,item.arg1?.Name)
                        .setText(R.id.mTeacher2,item.arg1?.TeacherName)
                        .setText(R.id.mCount2,"？？？")

                (helper.getView(R.id.mItem1) as CardView).setOnClickListener {
                    val i = Intent(mContext, VideoDetailsActivity::class.java)
                    i.putExtra("data",item.arg0)
                    mContext.startActivity(i)
                }
                (helper.getView(R.id.mItem2) as CardView).setOnClickListener {
                    val i = Intent(mContext, VideoDetailsActivity::class.java)
                    i.putExtra("data",item.arg1)
                    mContext.startActivity(i)
                }
            }
        }
    }
}