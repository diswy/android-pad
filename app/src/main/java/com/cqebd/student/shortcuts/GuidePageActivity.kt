package com.cqebd.student.shortcuts

import android.view.WindowManager
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import kotlinx.android.synthetic.main.activity_guide_page.*
import org.jetbrains.anko.startActivity

class GuidePageActivity : BaseActivity() {

    override fun setContentView() {
        //取消状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_guide_page)
    }

    override fun bindEvents() {
        testBtn.setOnClickListener {
            startActivity<MainActivity>("guide_position" to 1)
        }
    }

}
