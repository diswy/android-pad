package com.cqebd.student.ui.work


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.`interface`.CustomCallback
import com.cqebd.student.adapter.SubtitleNavigatorAdapter
import com.cqebd.student.event.STATUS_TYPE
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.repository.NetworkResource
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.tools.formatTimeYMD
import com.cqebd.student.tools.loginId
import com.cqebd.student.ui.WrongQuestionDetailsActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.WrongQuestion
import com.cqebd.teacher.vo.Status
import gorden.lib.anko.static.startActivity
import gorden.rxbus.RxBus
import gorden.rxbus.Subscribe
import kotlinx.android.synthetic.main.fragment_work_content.*
import kotlinx.android.synthetic.main.item_wrong_question.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

/**
 * 错题本
 *
 */
class WrongQuestionFragment : BaseLazyFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private val pageProcess = PageProcess.build<WrongQuestion> { it.StudentQuestionsTasksID }
    private lateinit var adapter: BaseQuickAdapter<WrongQuestion, BaseViewHolder>

    override fun getLayoutRes(): Int {
        return R.layout.fragment_work_content
    }

    override fun initView() {
        super.initView()

        context?.let {
            // 副标题
            val subCommonNavigator = CommonNavigator(it)
            val mSubtitleNavigatorAdapter = SubtitleNavigatorAdapter(it, FilterData.subjectAll, magic_indicator_subtitle)
            subCommonNavigator.adapter = mSubtitleNavigatorAdapter
            magic_indicator_subtitle.navigator = subCommonNavigator

            mSubtitleNavigatorAdapter.setOnTitleViewOnClickListener(object : CustomCallback.OnPositionListener {
                override fun onClickPos(pos: Int) {
                    filterViewModel.filterSubject(FilterData.subjectAll[pos])
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
        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)

        filterViewModel.subject.observe(this, Observer {
            pageLoadView.show = true
            pageProcess.data.clear()
            getWrongQuestionList()
        })

        filterViewModel.jobType.observe(this, Observer {
            pageLoadView.show = true
            pageProcess.data.clear()
            getWrongQuestionList()
        })

        adapter = object : BaseQuickAdapter<WrongQuestion, BaseViewHolder>(R.layout.item_wrong_question, pageProcess.data) {
            override fun convert(helper: BaseViewHolder?, item: WrongQuestion) {
                helper?.itemView?.apply {
                    text_name.text = item.Name
                    text_subject.text = item.SubjectTypeName.take(1)
                    text_count.text = "做错%s题".format(item.ErrorCount)
                    text_time.text = formatTimeYMD(item.DateTime)
                    text_job_type.text = item.PapersTypeName
                }
            }
        }
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener { adapter, view, position ->
            val itemData = adapter.data[position] as WrongQuestion
            startActivity<WrongQuestionDetailsActivity>("taskId" to itemData.StudentQuestionsTasksID, "title" to itemData.Name)
        }

        adapter.setOnLoadMoreListener({
            getWrongQuestionList()
        }, recyclerView)

        smart_refresh_layout.setOnRefreshListener {
            pageProcess.data.clear()
            getWrongQuestionList()
        }

        pageLoadView.show = true
        getWrongQuestionList()

    }

    override fun onInvisible() {
        RxBus.get().unRegister(this)
    }

    @Subscribe(code = STATUS_TYPE)
    fun filterJobType(status: FilterData) {
        filterViewModel.filterJobType(status)
    }

    private fun getWrongQuestionList() {
        object : NetworkResource<List<WrongQuestion>>() {
            override fun createCall(): LiveData<ApiResponse<List<WrongQuestion>>> {
                return NetClient.workService().getWrongQuestionList(loginId, filterViewModel.subject.value?.status, filterViewModel.jobType.value?.status, null, pageProcess.pageIndex)
            }
        }.asLiveData.observe(this, Observer {
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
                        getWrongQuestionList()
                    })
                }
                Status.LOADING -> pageLoadView.load()
            }
        })
    }

}
