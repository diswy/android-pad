package com.cqebd.student.live

import android.os.Bundle
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity

class VideoLiveActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_video_live)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction().add(R.id.mRtsContainer, LiveRtsFragment()).commit()
    }
}
