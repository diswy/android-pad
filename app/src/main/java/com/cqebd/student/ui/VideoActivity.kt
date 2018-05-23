package com.cqebd.student.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.fragment.RecommendFragment
import com.cqebd.student.ui.fragment.VideoEvaluateFragment
import com.cqebd.student.ui.fragment.WebFragment
import com.cqebd.student.vo.entity.PeriodInfo
import com.cqebd.student.vo.entity.PeriodResponse
import com.cqebd.student.vo.entity.VodPlay
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import gorden.lib.video.ExDefinition
import kotlinx.android.synthetic.main.activity_video.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import java.util.*

/**
 * 描述
 * Created by gorden on 2018/3/15.
 */
class VideoActivity : BaseActivity() {
    private val titles = listOf("课时", "详细", "评价")

    private var status: Int = 0
    private lateinit var listData: ArrayList<PeriodInfo>

    override fun setContentView() {
        setContentView(R.layout.activity_video)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent?.let {
            initStatus(it)
        }
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = TitleNavigatorAdapter(this, titles, viewPager)
        video_player_indicator.navigator = commonNavigator
        ViewPagerHelper.bind(video_player_indicator, viewPager)

        toolbar.setNavigationOnClickListener { finish() }
        toolbar_title.text = "点点直播"

        intent?.let {
            initStatus(it)
        }

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            playerView.pare
        }
    }

    override fun onStop() {
        super.onStop()
        videoView.onStop()
    }

    override fun onBackPressed() {
        if (videoView.onBackPressed())
            return
        videoView.onStop()
        super.onBackPressed()
    }

    private fun initStatus(i: Intent) {
        listData = i.getParcelableArrayListExtra("listData")
        val mCurrentPos = i.getIntExtra("pos", 0)
        status = i.getIntExtra("status", 0)
        val isLiveMode = i.getBooleanExtra("isLiveMode", false)
        videoView.setLiveMode(isLiveMode)
        val mId = i.getIntExtra("id", 0)
        Logger.d("status = $status ；isLiveMode = $isLiveMode")
        Logger.d(listData)

        // 加载视频
        loadVideo(mId)

        val mData = Bundle()
        mData.putParcelableArrayList("listData", listData)
        mData.putInt("pos", mCurrentPos)
        mData.putInt("id", mId)

        val mRecommendCourseFragment = RecommendFragment()
        mRecommendCourseFragment.arguments = mData
        val web1 = WebFragment()
        val mEvaluateFragment = VideoEvaluateFragment()
        mEvaluateFragment.arguments = mData

        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> mRecommendCourseFragment
                    1 -> web1
                    2 -> mEvaluateFragment
                    else -> mRecommendCourseFragment
                }
            }

            override fun getCount(): Int {
                return 3
            }
        }

    }

    // 获取视频地址，并加载视频
    private fun loadVideo(id: Int) {
        NetClient.videoService()
                .getPeriodByID(id)
                .enqueue(object : NetCallBack<BaseResponse<PeriodResponse>>() {
                    override fun onSucceed(response: BaseResponse<PeriodResponse>?) {

                        if (response?.data != null) {
                            Logger.json(Gson().toJson(response.data.VodPlayList))
                            val definitions = filterVideoUrl(response.data.VodPlayList)

                            if (definitions != null && definitions.isNotEmpty())
                                videoView.setVideoPath(definitions, 0, response.data.Name, R.drawable.ic_login_logo)
                            else
                                toast("视频未准备好~")
                        } else {
                            toast("视频未准备好~")
                        }
                    }

                    override fun onFailure() {

                    }
                }
                )
    }

    /**
     * 取出视频文件中的m3u8文件
     */
    private fun filterVideoUrl(vodPlays: List<VodPlay>?): List<ExDefinition>? {
        if (vodPlays == null || vodPlays.isEmpty()) {
            return null
        }
        Collections.sort(vodPlays) { o1, o2 ->
            o2.Definition - o1.Definition
        }
        val definitionList: MutableList<ExDefinition> = ArrayList()
        for (vodPlay in vodPlays) {
            if (vodPlay.Url.substring(vodPlay.Url.lastIndexOf(".")).contains("m3u8")) {
                when {
                    definitionList.size == 0 -> definitionList.add(ExDefinition(vodPlay.Definition, "原画", vodPlay.Url))
                    definitionList.size == 1 -> definitionList.add(ExDefinition(vodPlay.Definition, "高清", vodPlay.Url))
                    definitionList.size == 2 -> definitionList.add(ExDefinition(vodPlay.Definition, "标清", vodPlay.Url))
                }
            }
        }
        return definitionList
    }
}