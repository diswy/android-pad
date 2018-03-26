package com.cqebd.student.ui

import android.content.res.Configuration
import android.os.Bundle
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import gorden.lib.anko.static.logError
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
        videoView.setVideoPath("http://ebd-ocrom.oss-cn-hangzhou.aliyuncs.com/media/初2%20寒假直播讲评%20讲座%20欧阳%20第1讲.mp4","寒假直播讲评")
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE){
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
}