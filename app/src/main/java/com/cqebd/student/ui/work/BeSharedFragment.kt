package com.cqebd.student.ui.work

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.event.STATUS_DATE
import com.cqebd.student.event.STATUS_QUESTION_TYPE
import com.cqebd.student.event.STATUS_SUBJECT
import com.cqebd.student.glide.GlideApp
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
import kotlinx.android.synthetic.main.fragment_homework_content.*
import kotlinx.android.synthetic.main.item_share_homework.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.jetbrains.anko.support.v4.dip

class BeSharedFragment : BaseLazyFragment() {
    private lateinit var filterViewModel: FilterViewModel
    private lateinit var shareHomeworkViewModel: ShareHomeworkViewModel
    private val pageProcess = PageProcess.build<ShareHomeworkItem> { it.Id }
    private lateinit var adapter: BaseQuickAdapter<ShareHomeworkItem, BaseViewHolder>

    override fun getLayoutRes(): Int {
        return R.layout.fragment_homework_content
    }

    override fun initView() {
        super.initView()
        // 副标题
        val subCommonNavigator = CommonNavigator(context)
        subCommonNavigator.scrollPivotX = 0.65f
        subCommonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = ColorTransitionPagerTitleView(context)
                titleView.normalColor = resources.getColor(R.color.color_tab_title)
                titleView.selectedColor = resources.getColor(R.color.color_main)
                titleView.text = FilterData.gradeHomework[index].Name
                titleView.textSize = 14f
                titleView.setOnClickListener {
                    magic_indicator_subtitle.onPageSelected(index)
                    magic_indicator_subtitle.onPageScrollStateChanged(index)
                    magic_indicator_subtitle.onPageScrolled(index, 0f, 0)
                    filterViewModel.filterShareHomeworkGrade(FilterData(FilterData.gradeHomework[index].status, FilterData.gradeHomework[index].Name))
                }
                return titleView
            }

            override fun getCount(): Int {
                return FilterData.gradeHomework.size
            }

            override fun getIndicator(p0: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = dip(2).toFloat()
                indicator.lineWidth = dip(15).toFloat()
                indicator.roundRadius = dip(3).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(resources.getColor(R.color.color_main))
                return indicator
            }
        }
        magic_indicator_subtitle.navigator = subCommonNavigator
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
            shareHomeworkViewModel.getShareHomeworkList()
        })

        filterViewModel.shareHomeworkDate.observe(this, Observer {
            pageLoadView.show = true
            pageProcess.data.clear()
            shareHomeworkViewModel.getShareHomeworkList()
        })

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
                    data?.let {
                        if (pageProcess.pageIndex==1){
                            pageProcess.refreshData(it)
                            smart_refresh_layout.finishRefresh(true)
                            adapter.setNewData(pageProcess.data)
                            Logger.d(pageProcess.data)
                            if (pageProcess.data.isEmpty()){
                                pageLoadView.dataEmpty()
                            }else{
                                pageLoadView.hide()
                            }
                            if (it.size<20){
                                adapter.loadMoreEnd()
                            }
                        }else{
                            pageProcess.loadMoreData(it)
                            if (it.size>=20){
                                adapter.loadMoreComplete()
                            }else{
                                adapter.loadMoreEnd()
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }



//                    smart_refresh_layout.finishRefresh(true)
//                    pageLoadView.hide()
//
//                    val data = it.data?.DataList
//                    if (pageProcess.pageIndex == 1) {
//                        pageProcess.refreshData(data!!)
//                        smart_refresh_layout.finishRefresh(true)
//                        adapter.setNewData(pageProcess.data)
//                        Logger.d(pageProcess.data)
//                        if (pageProcess.data.isEmpty()) {
//                            pageLoadView.dataEmpty()
//                        } else {
//                            pageLoadView.hide()
//                        }
//                        if (data.size < 20) {
//                            adapter.loadMoreEnd()
//                        }
//                    } else {
//                        pageProcess.loadMoreData(data!!)
//                        if (data.size >= 20) {
//                            adapter.loadMoreComplete()
//                        } else {
//                            adapter.loadMoreEnd()
//                        }
//                        adapter.notifyDataSetChanged()
//
//                    }
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
        val url = "http://service.student.cqebd.cn/HomeWork/TaskShare?id=%s"// 跳转详情的URL
        startActivity<WebActivity>("url" to url.format(id))
    }

    @Subscribe(code = STATUS_SUBJECT)
    fun filterSubject(filter:FilterData) {
        filterViewModel.filterSubject(filter)
    }
    @Subscribe(code = STATUS_QUESTION_TYPE)
    fun filterQuestion(filter:FilterData) {
        filterViewModel.filterProblemType(filter)
    }
    @Subscribe(code = STATUS_DATE)
    fun filterDate(filter:FilterData) {
        filterViewModel.filterShareHomeworkDate(filter)
    }

}