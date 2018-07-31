package com.cqebd.student.ui.video


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import com.anko.static.dp
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.db.entity.ClassSchedule
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import com.cqebd.student.live.ui.LiveActivity
import com.cqebd.student.tools.formatTime
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.LiveVideoActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.viewmodel.ClassScheduleViewModel
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.CourseInfo
import com.cqebd.teacher.vo.Status
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.fragment_schedule.*
import kotlinx.android.synthetic.main.item_new_schedule.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import java.util.*


/**
 * 课表
 */
class ScheduleFragment : BaseLazyFragment(), Observer<Resource<ClassSchedule>> {
    private lateinit var currentDate: Calendar
    private lateinit var selectedDate: Calendar
    private val viewModel by lazy { ViewModelProviders.of(this).get(ClassScheduleViewModel::class.java) }
    private val courseList: ArrayList<CourseInfo> = ArrayList()
    private lateinit var adapter: BaseQuickAdapter<CourseInfo, BaseViewHolder>

    override fun getLayoutRes(): Int {
        return R.layout.fragment_schedule
    }

    override fun lazyLoad() {
        adapter = object : BaseQuickAdapter<CourseInfo, BaseViewHolder>(R.layout.item_new_schedule) {
            override fun convert(helper: BaseViewHolder?, item: CourseInfo) {
                helper?.itemView?.apply {
                    GlideApp.with(context)
                            .load(item.Snapshoot)
                            .centerCrop()
//                            .placeholder(R.drawable.ic_avatar)
                            .into(iv_schedule_snapshot)
                    tv_schedule_title.text = item.Name
                    tv_schedule_grade_time.text = getString(R.string.grade_and_time, item.GradeName, formatTimeYMDHM(item.PlanStartDate))
                    tv_schedule_teacher.text = "主讲老师:".plus(item.TeacherName)

                }
            }
        }

        adapter.bindToRecyclerView(recyclerView)
        adapter.setOnItemClickListener { adapter, _, position ->
            val itemData = adapter.data[position] as CourseInfo
            Logger.d(Gson().toJson(itemData))
            when {
//                itemData.Status == 1 -> startActivity<LiveVideoActivity>("id" to itemData.Id, "status" to itemData.Status, "isLiveMode" to true)
                itemData.Status == 1 -> startActivity<LiveActivity>("id" to itemData.Id, "hasChat" to itemData.HasChat, "hasIWB" to itemData.HasIWB, "hasVchat" to itemData.HasVchat, "title" to itemData.Name)
                itemData.Status == 3 -> startActivity<LiveVideoActivity>("id" to itemData.Id, "status" to itemData.Status, "title" to itemData.Name)
                else -> toast("视频未准备好哦~")
            }
        }
        //设置可查看前一年和后一年的课程表
        val min = Calendar.getInstance()
        min.set(min.get(Calendar.YEAR) - 1, Calendar.JANUARY, 1)
        val max = Calendar.getInstance()
        max.set(max.get(Calendar.YEAR) + 1, Calendar.DECEMBER, 31)
        schedule_calendar.state().edit().setMinimumDate(min).setMaximumDate(max).commit()

        currentDate = Calendar.getInstance()
        currentDate.set(Calendar.HOUR_OF_DAY, 0)
        currentDate.set(Calendar.MINUTE, 0)
        currentDate.set(Calendar.SECOND, 0)
        currentDate.set(Calendar.MILLISECOND, 0)
        selectedDate = currentDate
        schedule_calendar.selectedDate = CalendarDay.from(selectedDate)

        schedule_calendar.setOnMonthChangedListener { widget, date ->
            date?.calendar?.apply {
                this.set(Calendar.DAY_OF_MONTH, if (selectedDate.get(Calendar.DAY_OF_MONTH) > getActualMaximum(Calendar.DAY_OF_MONTH)) 1 else selectedDate.get(Calendar.DAY_OF_MONTH))
                selectedDate = this
                schedule_calendar.selectedDate = CalendarDay.from(selectedDate)
                viewModel.getPeriodListMonth(selectedDate).observe(this@ScheduleFragment, this@ScheduleFragment)
            }
        }
        schedule_calendar.setOnDateChangedListener { widget, date, selected ->
            selectedDate = date.calendar
            filterData()
        }

        smart_refresh_layout.setOnRefreshListener {
            viewModel.getPeriodListMonth(selectedDate).observe(this, this)
        }

        schedule_calendar.setCurrentDate(currentDate)

    }

    override fun onResume() {
        super.onResume()
        try {
            viewModel.getPeriodListMonth(selectedDate).observe(this, this)
        } catch (e: Exception) {
            Logger.e("${e.message}")
        }
    }

    override fun onInvisible() {

    }

    override fun onChanged(it: Resource<ClassSchedule>?) {
        when (it?.status) {
            Status.SUCCESS -> {
                smart_refresh_layout.finishRefresh(true)
                pageLoadView.hide()
                schedule_calendar.removeDecorators()
                it.data?.courses?.let {
                    val dates = ArrayList<CalendarDay>()
                    val afterDates = ArrayList<CalendarDay>()
                    for (item in it) {
                        val calendarDay = CalendarDay.from(Date(formatTime(item.PlanStartDate)))
                        if (currentDate.before(calendarDay.calendar) || currentDate == calendarDay.calendar) {
                            afterDates.add(calendarDay)
                        } else {
                            dates.add(calendarDay)
                        }
                    }
                    schedule_calendar.addDecorator(LiveDecorator(dates))
                    schedule_calendar.addDecorator(AfterDecorator(afterDates))
                }


                courseList.clear()
                it.data?.courses?.apply {
                    courseList.addAll(this)
                }
                filterData()
            }
            Status.ERROR -> {
                smart_refresh_layout.finishRefresh(false)
                pageLoadView.error({
                    viewModel.getPeriodListMonth(selectedDate).observe(this, this)
                })
            }
            Status.LOADING -> {
                pageLoadView.show = true
                pageLoadView.load()
            }
        }
    }

    private fun filterData() {
        courseList.filter {
            CalendarDay.from(Date(formatTime(it.PlanStartDate))).calendar == selectedDate
        }.apply {
            if (isEmpty()) {
                pageLoadView.show = true
                pageLoadView.dataEmpty()
            } else {
                pageLoadView.hide()
            }
            adapter.setNewData(this)
        }
    }

    /**
     * CalendarView存在直播点播的装饰
     */
    class LiveDecorator(private val dates: List<CalendarDay>) : DayViewDecorator {
        private var dotColor: Int = 0xFFCCCCCC.toInt()
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(DotSpan(3.dp.toFloat(), dotColor))
        }
    }

    class AfterDecorator(private val dates: List<CalendarDay>) : DayViewDecorator {
        private var dotColor: Int = Color.RED
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(DotSpan(3.dp.toFloat(), dotColor))
        }
    }


}
