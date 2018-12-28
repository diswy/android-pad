package com.cqebd.student.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.VideoActivity
import com.cqebd.student.vo.entity.PeriodInfo
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.android.synthetic.main.item_video_new_course.view.*
import java.util.*


class RecommendFragment : BaseFragment() {
    private lateinit var adapter: CourseAdapter
    private lateinit var listData: ArrayList<PeriodInfo>


    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_recommend, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {

        adapter = CourseAdapter()
        adapter.bindToRecyclerView(recyclerView)
        adapter.setOnItemClickListener { _, _, position ->

            if (adapter.getCurrentPos() == position)// 避免重复点击
                return@setOnItemClickListener

            val itemData = adapter.data[position] as PeriodInfo
            if (itemData.Status == 1 || itemData.Status == 3) {
//                adapter.setCurrentPos(position)
                startActivity<VideoActivity>("id" to itemData.Id, "status" to itemData.Status, "listData" to adapter.data, "pos" to position, "title" to itemData.Name)
            } else {
                toast("视频未准备好哦~")
            }
        }


        arguments?.let {
            listData = it.getParcelableArrayList("listData")
            val mCurrentPos = it.getInt("pos", 0)
            adapter.setNewData(mCurrentPos, listData)
            recyclerView.scrollToPosition(mCurrentPos)
        }
    }

    override fun bindEvents() {

    }

    class CourseAdapter : BaseQuickAdapter<PeriodInfo, BaseViewHolder>(R.layout.item_video_new_course) {
        private var currentPos = 0

        override fun convert(helper: BaseViewHolder?, item: PeriodInfo) {
            helper?.itemView?.apply {

                //                tvSort.text = "课时".plus(helper.layoutPosition + 1)
                tvSort.text = "课时".plus(data.size - helper.layoutPosition)
                tvCourseName.text = item.Name

                if (helper.layoutPosition == currentPos) {
                    ivStatus.setImageResource(R.drawable.ic_video_playing)
                    tvSort.setTextColor(resources.getColor(R.color.color_main))
                    tvCourseName.setTextColor(resources.getColor(R.color.color_main))
                } else {
                    ivStatus.setImageResource(R.drawable.ic_video_play)
                    tvSort.setTextColor(resources.getColor(R.color.color_333a42))
                    tvCourseName.setTextColor(resources.getColor(R.color.color_333a42))
                }

            }
        }

        fun setCurrentPos(pos: Int) {
            currentPos = pos
            notifyDataSetChanged()
        }

        fun setNewData(pos: Int, data: MutableList<PeriodInfo>?) {
            currentPos = pos
            super.setNewData(data)
        }

        fun getCurrentPos() = currentPos
    }

}
