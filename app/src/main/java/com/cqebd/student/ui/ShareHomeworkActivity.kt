package com.cqebd.student.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.tools.PageProcess
import com.cqebd.student.tools.formatTimeYMDHM
import com.cqebd.student.viewmodel.FilterViewModel
import com.cqebd.student.viewmodel.ShareHomeworkViewModel
import com.cqebd.student.vo.entity.AnswerItem
import com.cqebd.student.vo.entity.ShareHomeworkItem
import com.cqebd.teacher.vo.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.logger.Logger
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.activity_share_homework.*
import kotlinx.android.synthetic.main.item_share_homework.view.*
import kotlinx.android.synthetic.main.merge_rv_refresh_layout.*

class ShareHomeworkActivity : BaseActivity() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var shareHomeworkViewModel: ShareHomeworkViewModel
    private lateinit var adapter: BaseQuickAdapter<ShareHomeworkItem, BaseViewHolder>
    private val pageProcess = PageProcess.build<ShareHomeworkItem> { it.Id }
    private var mPage = 1
    private var isRefresh = false
    private val mPageSize = 20
    private var gradeId: Int? = null
    private var subjectId: Int? = null
    private var problemType: Int? = null
    private var day: Int? = null

    override fun setContentView() {
        setContentView(R.layout.activity_share_homework)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        filterViewModel = ViewModelProviders.of(this).get(FilterViewModel::class.java)
        shareHomeworkViewModel = ViewModelProviders.of(this, ShareHomeworkViewModel.Factory(filterViewModel, pageProcess))
                .get(ShareHomeworkViewModel::class.java)

        filterViewModel.shareHomeworkGrade.observe(this, Observer {
            tv_grade.text = it?.Name ?: "年级"
            gradeId = if (it?.status == -1) null else it?.status
            filterRefresh()
        })

        filterViewModel.subject.observe(this, Observer {
            tv_subject.text = it?.Name ?: "学科"
            subjectId = if (it?.status == -1) null else it?.status
            filterRefresh()
        })

        filterViewModel.problemType.observe(this, Observer {
            tv_problem_type.text = it?.Name ?: "题型"
            problemType = if (it?.status == -1) null else it?.status
            filterRefresh()
        })

        filterViewModel.shareHomeworkDate.observe(this, Observer {
            tv_date.text = it?.Name ?: "日期"
            day = if (it?.status == -1) null else it?.status
            filterRefresh()
        })

        shareHomeworkViewModel.shareList.observe(this, Observer {

            if (isRefresh) {
                adapter.setNewData(it)
                isRefresh = false
            } else
                adapter.addData(it!!)


            if (adapter.data.isEmpty()) {
                pageLoadView.show = true
                pageLoadView.dataEmpty()
            } else {
                pageLoadView.hide()
            }
        })

        adapter = object : BaseQuickAdapter<ShareHomeworkItem, BaseViewHolder>(R.layout.item_share_homework) {
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
                                tvAnswer.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
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
        adapter.bindToRecyclerView(recyclerView)
        getShareHomeworkList()
    }

    private fun filterRefresh() {
        isRefresh = true
        mPage = 1
        pageLoadView.show = true
        pageLoadView.load()
        getShareHomeworkList()
    }

    fun toWebActivity(id: Int) {
        val url = "http://service.student.cqebd.cn/HomeWork/TaskShare?id=%s"// 跳转详情的URL
        startActivity<WebActivity>("url" to url.format(id))
    }

    override fun bindEvents() {
        btn_grade.setOnClickListener {
            filterViewModel.filterShareHomeworkGrade(it)
        }
        btn_subject.setOnClickListener {
            filterViewModel.filterSubject(it)
        }
        btn_problem_type.setOnClickListener {
            filterViewModel.filterProblemType(it)
        }
        btn_date.setOnClickListener {
            filterViewModel.filterShareHomeworkDate(it)
        }

        refreshLayout.setKRefreshListener {
            mPage = 1
            isRefresh = true
            adapter.setEnableLoadMore(false)
            getShareHomeworkList()
        }

        adapter.setOnLoadMoreListener({
            mPage++
            getShareHomeworkList()
        }, recyclerView)
    }

    private fun getShareHomeworkList() {
        shareHomeworkViewModel.getShareHomeworkList(mPage, mPageSize, gradeId, subjectId, problemType, day).observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    refreshLayout.isEnabled = true
                    refreshLayout.refreshComplete(true)
                    pageLoadView.hide()

                    it.data?.DataList?.let {
                        if (it.isNotEmpty()) {
                            adapter.loadMoreComplete()
                        } else {
                            adapter.loadMoreEnd()
                        }
                        shareHomeworkViewModel.setData(it)
                    }

                }
                Status.ERROR -> {
                    refreshLayout.isEnabled = true
                    refreshLayout.refreshComplete(false)
                    pageLoadView.error({
                        getShareHomeworkList()
                    })
                }
                Status.LOADING -> {
                    refreshLayout.isEnabled = false
                    pageLoadView.load()
                }
            }
        })
    }
}
