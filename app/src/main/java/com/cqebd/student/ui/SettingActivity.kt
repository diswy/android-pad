package com.cqebd.student.ui

import com.cqebd.student.PersonalActivity
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import kotlinx.android.synthetic.main.activity_setting.*
import org.jetbrains.anko.startActivity

class SettingActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_setting)
    }

    override fun bindEvents() {
        btnUserAccount.setOnClickListener {
            startActivity<PersonalActivity>()
        }
    }

}
