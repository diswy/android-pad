package com.cqebd.student.ui

import android.content.res.Configuration
import android.os.Bundle
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.vo.entity.PeriodResponse
import gorden.util.XLog
import kotlinx.android.synthetic.main.activity_video.*

/**
 * 描述
 * Created by gorden on 2018/3/15.
 */
class VideoActivity : BaseActivity() {

    override fun setContentView() {
        setContentView(R.layout.activity_video)
    }


    override fun initialize(savedInstanceState: Bundle?) {
        loadVideo()
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

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun bindEvents() {

    }

    private fun loadVideo() {
        NetClient.videoService()
                .getPeriodByID(intent.getIntExtra("id", 0))
                .enqueue(object : NetCallBack<BaseResponse<PeriodResponse>>() {
                    override fun onSucceed(response: BaseResponse<PeriodResponse>?) {

                        XLog.d("xiaofu",response!!.data.VodPlayList[0].Url)
                        videoView.setVideoPath(response!!.data.VodPlayList[0].Url, response.data.Name, R.drawable.ic_login_logo)

                    }

                    override fun onFailure() {

                    }
                }

                )

    }
}