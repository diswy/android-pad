package com.cqebd.student.ui.work

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.TextUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.`interface`.CustomCallback
import com.cqebd.student.adapter.SubtitleNavigatorAdapter
import com.cqebd.student.event.STATUS_DATE
import com.cqebd.student.event.STATUS_QUESTION_TYPE
import com.cqebd.student.event.STATUS_SUBJECT
import com.cqebd.student.fix_system_bug.WrapContentLinearLayoutManager
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.ui.WebActivity
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.ShareHomeworkViewModel
import com.cqebd.student.vo.entity.AnswerItem
import com.cqebd.student.vo.entity.FilterData
import com.cqebd.student.vo.entity.ShareHomeworkItem
import com.cqebd.teacher.vo.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import gorden.lib.anko.static.startActivity
import gorden.rxbus.RxBus
import gorden.rxbus.Subscribe
import kotlinx.android.synthetic.main.fragment_work_content.*
import kotlinx.android.synthetic.main.item_share_homework.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

class BeSharedFragment : BaseLazyFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var shareHomeworkViewModel: ShareHomeworkViewModel
    private val pageProcess = PageProcess.build<ShareHomeworkItem> { it.Id }
    private lateinit var adapter: BaseQuickAdapter<ShareHomeworkItem, BaseViewHolder>

    override fun getLayoutRes(): Int {
        return R.layout.fragment_work_content
    }

    override fun initView() {
        super.initView()

        context?.let {
            // 副标题
            val subCommonNavigator = CommonNavigator(it)
            val mSubtitleNavigatorAdapter = SubtitleNavigatorAdapter(it, FilterData.gradeHomework, magic_indicator_subtitle)
            subCommonNavigator.adapter = mSubtitleNavigatorAdapter
            magic_indicator_subtitle.navigator = subCommonNavigator

            mSubtitleNavigatorAdapter.setOnTitleViewOnClickListener(object : CustomCallback.OnPositionListener {
                override fun onClickPos(pos: Int) {
                    filterViewModel.filterShareHomeworkGrade(FilterData.gradeHomework[pos])
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
        shareHomeworkViewModel = ViewModelProviders.of(this, ShareHomeworkViewModel.Factory(filterViewModel, pageProcess))
                .get(ShareHomeworkViewModel::class.java)

        filterViewModel.shareHomeworkGrade.observe(this, Observer {
            pageLoadView.show = true
            pageProcess.data.clear()
            adapter.notifyDataSetChanged()
            shareHomeworkViewModel.getShareHomeworkList()
        })

        filterViewModel.shareHomeworkDate.observe(this, Observer {
            pageLoadView.show = true
            pageProcess.data.clear()
            adapter.notifyDataSetChanged()
            shareHomeworkViewModel.getShareHomeworkList()
        })
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = object : BaseQuickAdapter<ShareHomeworkItem, BaseViewHolder>(R.layout.item_share_homework, pageProcess.data) {
            @SuppressLint("SetTextI18n")
            override fun convert(helper: BaseViewHolder?, item: ShareHomeworkItem) {
                helper?.itemView?.apply {

                    tv_time.text = formatTimeYMDHM(item.CreateDateTime)
                    tv_title.text = item.PapersName
                    tv_subtitle.text = item.PapersQuestion
                    tv_teacher.text = "老师:${item.TeacherName}"
                    tv_student.text = "同学:${item.StudentName}"
                    tv_problem_type.text = "题型:${item.QuestionTypeName}"

                    GlideApp.with(context)
                            .load(item.Photo)
                            .centerInside()
                            .placeholder(R.drawable.ic_avatar)
                            .into(img_avatar)

                    btn_more.setOnClickListener {
                        toWebActivity(item.Id)
                    }
                    //  处理H5部分
                    linear_container.removeAllViews()
                    item.AnswerHtml?.let {
                        val answers: List<AnswerItem> = Gson().fromJson(it, object : TypeToken<List<AnswerItem>>() {}.type)
                        if (answers.isNotEmpty()) {
                            val answer = answers[0].Answer
                            if (answer.contains("<img")) {
                                val imgPath = answer
                                        .replace("<img src=\"", "")
                                        .replace("\"/>", "")
                                        .replace("\" />", "")
                                Logger.d(imgPath)
                                val imgAnswer = ImageView(context)
                                GlideApp.with(context)
                                        .load(imgPath)
                                        .centerInside()
                                        .into(imgAnswer)
                                linear_container.addView(imgAnswer, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                            } else {
                                Logger.d(answer)
                                val tvAnswer = TextView(context)
                                tvAnswer.maxLines = 2
                                tvAnswer.ellipsize = TextUtils.TruncateAt.END
                                tvAnswer.setTextColor(ContextCompat.getColor(context, R.color.color_4b))
                                tvAnswer.text = answer
                                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                lp.topMargin = resources.getDimension(R.dimen.dp_10).toInt()
                                lp.bottomMargin = resources.getDimension(R.dimen.dp_10).toInt()
                                linear_container.addView(tvAnswer, lp)
                            }
                        }
                    }
                }
            }
        }

        recyclerView.adapter = adapter

        shareHomeworkViewModel.sharedList.observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    val data = it.data?.DataList
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
                        shareHomeworkViewModel.getShareHomeworkList()
                    })
                }
                Status.LOADING -> {
                    pageLoadView.load()
                }
            }
        })

        adapter.setOnLoadMoreListener({
            shareHomeworkViewModel.getShareHomeworkList()
        }, recyclerView)

        smart_refresh_layout.setOnRefreshListener {
            pageProcess.data.clear()
            shareHomeworkViewModel.getShareHomeworkList()
        }

        shareHomeworkViewModel.getShareHomeworkList()
    }

    override fun onInvisible() {
        RxBus.get().unRegister(this)
    }

    private fun toWebActivity(id: Int) {
        val url = "https://service-student.cqebd.cn/HomeWork/TaskShare?id=%s"// 跳转详情的URL
        startActivity<WebActivity>("url" to url.format(id))
    }

    @Subscribe(code = STATUS_SUBJECT)
    fun filterSubject(filter: FilterData) {
        filterViewModel.filterSubject(filter)
    }

    @Subscribe(code = STATUS_QUESTION_TYPE)
    fun filterQuestion(filter: FilterData) {
        filterViewModel.filterProblemType(filter)
    }

    @Subscribe(code = STATUS_DATE)
    fun filterDate(filter: FilterData) {
        filterViewModel.filterShareHomeworkDate(filter)
    }

}