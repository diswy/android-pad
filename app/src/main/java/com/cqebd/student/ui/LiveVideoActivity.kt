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
import com.cqebd.student.ui.fragment.WebFragment
import com.cqebd.student.vo.entity.PeriodResponse
import com.cqebd.student.vo.entity.VodPlay
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import gorden.lib.video.ExDefinition
import kotlinx.android.synthetic.main.activity_live_video.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import java.util.*

class LiveVideoActivity : BaseActivity() {
    private val titles = listOf("课时", "详细", "评价")

    private var status: Int = 0
    private var id: Int = 0

    override fun setContentView() {
        setContentView(R.layout.activity_live_video)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        status = intent!!.getIntExtra("status", 0)
        id = intent.getIntExtra("id", 0)
        val isLiveMode= intent.getBooleanExtra("isLiveMode",false)
        Logger.d("--->>>: id = $id ; status = $status ；isLiveMode = $isLiveMode")
        videoView.setLiveMode(isLiveMode)

        initStatus()
        loadVideo()
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = TitleNavigatorAdapter(this, titles, viewPager)
        video_player_indicator.navigator = commonNavigator
        ViewPagerHelper.bind(video_player_indicator, viewPager)

        intent?.let {
            status = it.getIntExtra("status", 0)
            id = it.getIntExtra("id", 0)
            val isLiveMode= it.getBooleanExtra("isLiveMode",false)
            Logger.d("--->>>: id = $id ; status = $status ；isLiveMode = $isLiveMode")
            videoView.setLiveMode(isLiveMode)

            toolbar.setNavigationOnClickListener { finish() }
            toolbar_title.text = "点点直播"

            initStatus()
            loadVideo()

            val mRecommendCourseFragment = WebFragment()
            val web1 = WebFragment()
            val web2 = WebFragment()

            viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getItem(position: Int): Fragment {
                    return when (position) {
                        0 -> mRecommendCourseFragment
                        1 -> web1
                        2 -> {
                            if (status == 1)
                                web2
                            else
                                web2
                        }
                        else -> mRecommendCourseFragment
                    }
                }

                override fun getCount(): Int {
                    return 3
                }

                override fun getPageTitle(position: Int): CharSequence? {
                    return when (position) {
                        0 -> "推荐"
                        1 -> "详细"
                        2 -> {
                            if (status == 1)
                                "提问"
                            else
                                "评价"
                        }
                        else -> "推荐"
                    }
                }

            }
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

    private fun initStatus() {
//        val item = tabLayout.getTabAt(2)
//        if (status == 1){
//            item?.text = "提问"
//        }else{
//            item?.text = "评价"
//        }

        val bundle = Bundle()
        bundle.putInt("id", id)
    }


    private fun loadVideo() {
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
