package com.cqebd.student.ui

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.formatTimeYMD
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.fragment.CourseListFragment
import com.cqebd.student.ui.fragment.WebFragment
import com.cqebd.student.vo.entity.VideoInfo
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import gorden.rxbus.RxBus
import kotlinx.android.synthetic.main.activity_video_details.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

class VideoDetailsActivity : BaseActivity() {
    private val url_teacher = "http://service.ocrm.student.cqebd.cn/Home/TeacherExplain?id=%s"
    private val url_course = "http://service.ocrm.student.cqebd.cn/Home/CourseExplain?id=%s"
    private val titles = listOf("目录", "课程介绍", "老师介绍")
    private lateinit var data: VideoInfo

    override fun setContentView() {
        setContentView(R.layout.activity_video_details)
    }

    override fun onDestroy() {
        RxBus.get().unRegister(this)
        super.onDestroy()
    }

    override fun initialize(savedInstanceState: Bundle?) {
        RxBus.get().register(this)

        data = intent.getParcelableExtra("data")

        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = TitleNavigatorAdapter(this, titles, viewPager)
        video_details_indicator.navigator = commonNavigator
        ViewPagerHelper.bind(video_details_indicator, viewPager)

        toolbar_title.text = data.Name
//        tv_title.text = data.Name
        tv_teacher_count.text = getString(R.string.format_teacher_count, data.TeacherName, data.PeriodCount)
        tv_times.text = getString(R.string.format_times, formatTimeYMD(data.StartDate), formatTimeYMD(data.EndDateTime))
        GlideApp.with(App.mContext).load(data.Snapshoot)
                .centerInside()
//                .placeholder(R.drawable.ic_avatar)
                .into(iv_snapshot)
        btn_subscribe.s_solid_color = if (data.IsFeedback)
            ContextCompat.getColor(this, R.color.color_line) else
            ContextCompat.getColor(this, R.color.colorPrimary)
        btn_subscribe.text = if (data.IsFeedback) "取消订阅" else "一键订阅"

        val mCourseListFragment = CourseListFragment()
        val mCourseInfoFragment = WebFragment()
        val mTeacherInfoFragment = WebFragment()

        val mDataBundle = Bundle()
        mDataBundle.putLong("id", data.Id)
        mCourseListFragment.arguments = mDataBundle

        val mCourseBundle = Bundle()
        mCourseBundle.putString("url", String.format(url_course, data.Id))
        mCourseInfoFragment.arguments = mCourseBundle

        val mTeacherBundle = Bundle()
        mTeacherBundle.putString("url", String.format(url_teacher, data.TeacherId))
        mTeacherInfoFragment.arguments = mTeacherBundle

        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> mCourseListFragment
                    1 -> mCourseInfoFragment
                    2 -> mTeacherInfoFragment
                    else -> mCourseListFragment
                }
            }

            override fun getCount(): Int {
                return titles.size
            }
        }
    }

    override fun bindEvents() {
        extra_video_toolbar.setNavigationOnClickListener { finish() }
        btn_subscribe.setOnClickListener {
            val status = if (data.IsFeedback) -1 else 0
            println("id".plus(data.Id).plus("   ;").plus(status))
            NetClient.videoService().addSubscribe(data.Id, status)
                    .enqueue(object : NetCallBack<BaseResponse<String>>() {
                        override fun onSucceed(response: BaseResponse<String>) {
                            toast(response.message)
                            data.IsFeedback = response.isSuccess
                            btn_subscribe.s_solid_color = if (data.IsFeedback)
                                ContextCompat.getColor(this@VideoDetailsActivity, R.color.colorPrimary) else
                                ContextCompat.getColor(this@VideoDetailsActivity, R.color.color_line)
                            btn_subscribe.text = if (data.IsFeedback) "一键订阅" else "取消订阅"
                        }

                        override fun onFailure() {

                        }
                    })
        }
    }

    fun refreshProgress(mProgress: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBarHorizontal.setProgress(mProgress, true)
        } else {
            progressBarHorizontal.progress = mProgress
        }
    }

}
