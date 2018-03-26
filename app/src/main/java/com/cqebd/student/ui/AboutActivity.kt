package com.cqebd.student.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.viewmodel.AboutViewModel

/**
 * 描述
 * Created by gorden on 2018/3/23.
 */
class AboutActivity : BaseActivity() {
    private lateinit var viewModel: AboutViewModel
    override fun setContentView() {
        setContentView(R.layout.activity_start)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(this).get(AboutViewModel::class.java)
    }
}