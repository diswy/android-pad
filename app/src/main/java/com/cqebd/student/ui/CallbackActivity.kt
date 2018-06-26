package com.cqebd.student.ui

import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import kotlinx.android.synthetic.main.activity_callback.*

class CallbackActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_callback)
    }

    override fun bindEvents() {
        toolbar.setNavigationOnClickListener { finish() }
    }

}
