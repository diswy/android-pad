package com.cqebd.student.adapter

import android.view.View
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.vo.entity.SectionPeriodInfo
import kotlinx.android.synthetic.main.item_new_course.view.*

class VideoCourseAdapter : BaseSectionQuickAdapter<SectionPeriodInfo, BaseViewHolder>(R.layout.item_new_course, R.layout.item_new_course_header, null) {

    override fun convertHead(helper: BaseViewHolder?, item: SectionPeriodInfo?) {
        helper?.itemView?.isEnabled = false
    }

    override fun convert(helper: BaseViewHolder?, item: SectionPeriodInfo?) {

        item?.let {
            val mItem = it.t
            helper?.itemView?.apply {
                item_course_tv_count.text = "课时".plus(helper.layoutPosition)
                item_course_tv_title.text = mItem.Name
                item_course_tv_time.text = formatTimeYMDHM(mItem.PlanStartDate)
                if (helper.layoutPosition == data.size-1){// 最后一项
                    item_course_line_end.visibility = View.GONE
                }
            }
        }
    }

}