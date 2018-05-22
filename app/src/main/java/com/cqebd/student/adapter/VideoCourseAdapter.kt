package com.cqebd.student.adapter

import android.view.View
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.vo.entity.PeriodInfo
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

                when(mItem.Status){
                    0 -> item_course_iv_status.setImageResource(R.drawable.ic_video_status_start)
                    1 -> item_course_iv_status.setImageResource(R.drawable.ic_video_status_playing)
                    2 -> item_course_iv_status.setImageResource(R.drawable.ic_video_status_end)
                    3 -> item_course_iv_status.setImageResource(R.drawable.ic_video_status_end)
                }


                if (helper.layoutPosition == data.size-1){// 最后一项
                    item_course_line_end.visibility = View.GONE
                }else{
                    item_course_line_end.visibility = View.VISIBLE
                }
                if(helper.layoutPosition == 1){
                    item_course_line_start.visibility = View.GONE
                }else{
                    item_course_line_start.visibility = View.VISIBLE
                }
            }
        }
    }

    fun getDataNoHeader():ArrayList<PeriodInfo>{
        val filterData = ArrayList<PeriodInfo>()
        for (item :SectionPeriodInfo in data){
            if (!item.isHeader){
                filterData.add(item.t)
            }
        }
        return filterData
    }
}