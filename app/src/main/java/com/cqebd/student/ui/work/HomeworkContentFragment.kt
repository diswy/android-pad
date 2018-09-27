package com.cqebd.student.ui.work


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.`interface`.CustomCallback
import com.cqebd.student.adapter.SubtitleNavigatorAdapter
import com.cqebd.student.constant.Constant
import com.cqebd.student.event.STATUS_SUBJECT
import com.cqebd.student.event.STATUS_TYPE
import com.cqebd.student.fix_system_bug.WrapContentLinearLayoutManager
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.tools.colorForRes
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.ui.JobPreviewActivity
import com.cqebd.student.ui.WebActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.WorkListViewModel
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.WorkInfo
import com.cqebd.teacher.vo.Status
import com.orhanobut.logger.Logger
import gorden.lib.anko.static.startActivity
import gorden.rxbus.RxBus
import gorden.rxbus.Subscribe
import kotlinx.android.synthetic.main.fragment_work_content.*
import kotlinx.android.synthetic.main.item_work.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.dip

/**
 * 作业内容
 *
 */
class HomeworkContentFragment : BaseLazyFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var workListViewModel: WorkListViewModel
    private val pageProcess = PageProcess.build<WorkInfo> { it.TaskId }
    //    private lateinit var adapter: BaseQuickAdapter<WorkInfo, BaseViewHolder>
    private val adapter by lazy { initAdapter() }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_work_content
    }

    override fun initView() {
        super.initView()

        context?.let {
            // 副标题
            val subCommonNavigator = CommonNavigator(it)
            val mSubtitleNavigatorAdapter = SubtitleNavigatorAdapter(it, FilterData.jobStatus, magic_indicator_subtitle)
            subCommonNavigator.adapter = mSubtitleNavigatorAdapter
            magic_indicator_subtitle.navigator = subCommonNavigator

            mSubtitleNavigatorAdapter.setOnTitleViewOnClickListener(object : CustomCallback.OnPositionListener {
                override fun onClickPos(pos: Int) {
                    filterViewModel.filterJobStatus(FilterData.jobStatus[pos])
                }
            })
        }
    }

    override fun bindEvents() {
        // 侧滑菜单处理
        val mainActivity = activity as MainActivity
        btn_filter.setOnClickListener {
            mainActivity.switchDrawerLayout()
        }
    }

    override fun lazyLoad() {
        RxBus.get().register(this)

        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java).apply { yOff = 0 }
        workListViewModel = ViewModelProviders.of(this, WorkListViewModel.Factory(filterViewModel, pageProcess))
                .get(WorkListViewModel::class.java)

        filterViewModel.jobStatus.observe(this, Observer {
            pageLoadView.show = true
            pageProcess.data.clear()
            adapter.notifyDataSetChanged()
            workListViewModel.getWorkList()
        })

        filterViewModel.jobType.observe(this, Observer {
            pageLoadView.show = true
            pageProcess.data.clear()
            adapter.notifyDataSetChanged()
            workListViewModel.getWorkList()
        })
        recyclerView.layoutManager = WrapContentLinearLayoutManager(activity)

        adapter.setOnLoadMoreListener({
            if (pageProcess.data.isNotEmpty() && pageProcess.data.size >= 20) {
                workListViewModel.getWorkList()
            }
        }, recyclerView)

        recyclerView.adapter = adapter

        workListViewModel.workInfoList.observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    val data = it.data
                    smart_refresh_layout.finishRefresh(true)

                    data?.let {
                        if (pageProcess.pageIndex == 1) {
                            pageProcess.refreshData(it)
                            adapter.setNewData(pageProcess.data)
                        } else {
                            pageProcess.loadMoreData(it)
                            adapter.notifyDataSetChanged()
                        }

                        if (it.size >= 20) {
                            adapter.loadMoreComplete()
                        } else {
                            adapter.loadMoreEnd()
                        }
                    }

                    if (pageProcess.data.isEmpty()) {
                        pageLoadView.dataEmpty()
                    } else {
                        pageLoadView.hide()
                    }
                }
                Status.ERROR -> {
                    smart_refresh_layout.finishRefresh(false)
                    adapter.loadMoreFail()
                    pageLoadView.error({
                        workListViewModel.getWorkList()
                    })
                }
                Status.LOADING -> {
                    pageLoadView.load()
                }
            }
        })

        smart_refresh_layout.setOnRefreshListener {
            pageProcess.data.clear()
            workListViewModel.getWorkList()
        }

        filterViewModel.filterJobStatus(FilterData(10, "默认"))
    }

    override fun onInvisible() {
        RxBus.get().unRegister(this)
    }

    @Subscribe(code = STATUS_TYPE)
    fun filterJobType(status: FilterData) {
        filterViewModel.filterJobType(status)
    }

    @Subscribe(code = STATUS_SUBJECT)
    fun filterSubject(status: FilterData) {
        Logger.wtf(status.Name)
        filterViewModel.filterSubject(status)
    }

    @Subscribe(code = Constant.BUS_FINISH_PREVIEW)
    fun refreshData() {
        pageLoadView.show = true
        pageProcess.data.clear()
        adapter.notifyDataSetChanged()
        workListViewModel.getWorkList()
    }

    private fun initAdapter(): BaseQuickAdapter<WorkInfo, BaseViewHolder> {
        return object : BaseQuickAdapter<WorkInfo, BaseViewHolder>(R.layout.item_work, pageProcess.data) {
            override fun convert(helper: BaseViewHolder?, item: WorkInfo) {
                helper?.itemView?.apply {
                    text_name.text = item.Name
                    text_subject.text = item.SubjectName.take(1)
                    when (text_subject.text.toString()) {
                        "数" -> text_subject.backgroundResource = R.drawable.shape_subject_math_bg
                        "英" -> text_subject.backgroundResource = R.drawable.shape_subject_en_bg
                        "语" -> text_subject.backgroundResource = R.drawable.shape_subject_cn_bg
                        "化" -> text_subject.backgroundResource = R.drawable.shape_subject_huaxue_bg
                        "物" -> text_subject.backgroundResource = R.drawable.shape_subject_wuli_bg
                        "音" -> text_subject.backgroundResource = R.drawable.shape_subject_music_bg
                    }
                    text_count.text = "共%s题".format(item.QuestionCount)
                    text_end_time.text = "截止时间: ".plus(formatTimeYMDHM(item.CanEndDateTime))
                    when (item.Status) {
                        -1 -> {
                            text_status.text = "新作业"
                            text_status.setTextColor(colorForRes(R.color.status_new))
                        }
                        0 -> {
                            text_status.text = "答题中"
                            text_status.setTextColor(colorForRes(R.color.status_run))
                        }
                        1 -> {
                            text_status.text = "已完成"
                            text_status.setTextColor(colorForRes(R.color.status_complete))
                        }
                        2 -> {
                            text_status.text = "已批阅"
                            text_status.setTextColor(colorForRes(R.color.status_read))
                            if (item.IsMedal) {
                                val medal = resources.getDrawable(R.drawable.ic_medal)
                                medal.setBounds(0, 0, medal.minimumWidth, medal.minimumHeight)
                                text_name.setCompoundDrawables(null, null, medal, null)
                                text_name.compoundDrawablePadding = dip(4)
                            } else {
                                text_name.setCompoundDrawables(null, null, null, null)
                                text_name.compoundDrawablePadding = dip(0)
                            }
                        }
                    }

                    text_status.text = FilterData.jobStatus.find { it.status == item.Status }?.Name
                    setOnClickListener {
                        if (item.Status <= 0) {
                            val urlFormat = "HomeWork/ExaminationPapers?id=%s&taskid=%s"
                            startActivity<JobPreviewActivity>("url" to WorkService.BASE_WEB_URL.plus(urlFormat.format(item.PapersId, item.TaskId)),
                                    "info" to item)

                        } else {
                            val urlFormat = "HomeWork/CheckPaper?StudentQuestionsTasksId=%s"
                            if (item.Status == 2) {
                                startActivity<WebActivity>("url" to WorkService.BASE_WEB_URL.plus(urlFormat.format(item.TaskId)), "medal" to item.IsMedal, "title" to item.Name)
                            } else {
                                startActivity<WebActivity>("url" to WorkService.BASE_WEB_URL.plus(urlFormat.format(item.TaskId)), "title" to item.Name)
                            }
                        }
                    }
                }
            }
        }
    }


}
