package com.cqebd.student.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.fragment.RecommendFragment
import com.cqebd.student.ui.fragment.WebFragment
import com.cqebd.student.vo.entity.PeriodResponse
import kotlinx.android.synthetic.main.activity_video.*

/**
 * 描述
 * Created by gorden on 2018/3/15.
 */
class VideoActivity : BaseActivity() {

    private var status: Int = 0
    private var id: Int = 0

    override fun setContentView() {
        setContentView(R.layout.activity_video)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        status = intent!!.getIntExtra("status", 0)
        id = intent.getIntExtra("id", 0)
        println("xiaofu: id = $id ; status = $status")
        initStatus()
        loadVideo()
    }

    override fun initialize(savedInstanceState: Bundle?) {
        status = intent!!.getIntExtra("status", 0)
        id = intent.getIntExtra("id", 0)
        println("xiaofu: id = $id ; status = $status")

        toolbar.setNavigationOnClickListener { finish() }
        toolbar_title.text = "点点直播"

        initStatus()
        loadVideo()

        val mRecommendCourseFragment = RecommendFragment()
        val web1 = WebFragment()
        val web2 = WebFragment()

        tabLayout.setupWithViewPager(viewPager)
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

    override fun bindEvents() {

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

                        if (response?.data != null)
                            videoView.setVideoPath(response.data.VodPlayList[0].Url, response.data.Name, R.drawable.ic_login_logo)
                        else {
                            toast("视频未准备好~")
                        }
                    }

                    override fun onFailure() {

                    }
                }

                )
    }
}