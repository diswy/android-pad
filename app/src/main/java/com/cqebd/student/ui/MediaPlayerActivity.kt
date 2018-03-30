package com.cqebd.student.ui

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_media_player.*

class MediaPlayerActivity : BaseActivity() {

    override fun setContentView() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_media_player)
    }


    override fun initialize(savedInstanceState: Bundle?) {
        intent.getStringExtra("url")?.let {
            videoView.setVideoPath(it, "", R.drawable.ic_login_logo)
        }

        btn_media_back.setOnClickListener { finish() }
        videoView.setShowListener {
            Logger.d(it)
            btn_media_back.visibility = if (it) View.VISIBLE else View.GONE
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
}
