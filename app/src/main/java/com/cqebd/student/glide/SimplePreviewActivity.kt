package com.cqebd.student.glide

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.cqebd.student.R
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import kotlinx.android.synthetic.main.activity_simple_preview.*

class SimplePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_simple_preview)

        val path = intent.getStringExtra("url")
        GlideApp.with(this)
                .load(path)
                .fitCenter()
                .into(photo_view)

        photo_view.setOnClickListener { onBackPressed() }
    }
}
