package com.cqebd.module_student_classroom.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.module_student_classroom.R
import com.cqebd.module_student_classroom.helper.getMyColor

class PeriodAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_period) {

    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setText(R.id.tvPeriodTitle, "标题")
                .setText(R.id.tvPeriodNote, "备注")
                .setText(R.id.tvTeacher, "张老师")
                .setText(R.id.tvTime, "开课时间：2018.8.8")
                .setText(R.id.tvStatus, "上课中")
                .setTextColor(R.id.tvStatus, mContext.getMyColor(R.color.myTheme))

    }

}