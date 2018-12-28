package com.cqebd.student.ui

import android.animation.ValueAnimator
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
import com.cqebd.student.ui.fragment.VideoEvaluateFragment
import com.cqebd.student.ui.fragment.WebFragment
import com.cqebd.student.vo.entity.PeriodResponse
import com.cqebd.student.vo.entity.VodPlay
import com.orhanobut.logger.Logger
import gorden.lib.video.ExDefinition
import kotlinx.android.synthetic.main.activity_live_video.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import okhttp3.ResponseBody
import java.util.*

class LiveVideoActivity : BaseActivity() {
    private val titles = listOf("课时", "详细", "评价")

    private var status: Int = 0
    private var id: Int = 0

    val mRecommendCourseFragment = WebFragment()
    val web1 = WebFragment()
    val mEvaluateFragment = VideoEvaluateFragment()

    override fun setContentView() {
        setContentView(R.layout.activity_live_video)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        status = intent!!.getIntExtra("status", 0)
        id = intent.getIntExtra("id", 0)
        val isLiveMode = intent.getBooleanExtra("isLiveMode", false)
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
            val isLiveMode = it.getBooleanExtra("isLiveMode", false)
            Logger.d("--->>>: id = $id ; status = $status ；isLiveMode = $isLiveMode")
            videoView.setLiveMode(isLiveMode)
            toolbar_title.text = intent.getStringExtra("title")

            toolbar.setNavigationOnClickListener { finish() }

            initStatus()
            loadVideo()

            val mData = Bundle()
            mData.putInt("id", id)
            mEvaluateFragment.arguments = mData

            viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getItem(position: Int): Fragment {
                    return when (position) {
                        0 -> mRecommendCourseFragment
                        1 -> web1
                        2 -> {
                            mEvaluateFragment
//                            mChatRoomFragment
//                            if (status == 1)
//                                web2
//                            else
//                                web2
                        }
                        else -> mRecommendCourseFragment
                    }
                }

                override fun getCount(): Int {
                    return 3
                }

//                override fun getPageTitle(position: Int): CharSequence? {
//                    return when (position) {
//                        0 -> "推荐"
//                        1 -> "详细"
//                        2 -> {
//                            if (status == 1)
//                                "提问"
//                            else
//                                "评价"
//                        }
//                        else -> "推荐"
//                    }
//                }

            }
        }

    }

    override fun bindEvents() {
        btnExpand.setOnCheckedChangeListener { _, isChecked ->
            val va: ValueAnimator = if (isChecked) {// 折叠
                ValueAnimator.ofInt(500, 0)
            } else {
                ValueAnimator.ofInt(0, 500)
            }

            va.addUpdateListener {
                val width: Int = it.animatedValue as Int
                hover.layoutParams.width = width
                hover.requestLayout()
            }
            va.duration = 300
            va.start()
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
        toolbar_title.text = intent.getStringExtra("title")
        val bundle = Bundle()
        bundle.putInt("id", id)
    }


    private fun loadVideo() {
        Logger.d("id = $id")
        NetClient.videoService()
                .getPeriodByID(id)
                .enqueue(object : NetCallBack<BaseResponse<PeriodResponse>>() {
                    override fun onFailure() {

                    }

                    override fun onSucceed(response: BaseResponse<PeriodResponse>?) {
                        response?.data?.let {
                            if (filterVideoUrl(it.VodPlayList) != null) {
                                videoView.setVideoPath(filterVideoUrl(it.VodPlayList)!!, 0, it.Name, R.drawable.ic_login_logo)
                            }
                        }
                    }

                })

        NetClient.videoService()
                .playRecord(id, 0)
                .enqueue(object : NetCallBack<ResponseBody>() {
                    override fun onSucceed(response: ResponseBody) {

                    }

                    override fun onFailure() {
                    }
                })
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
