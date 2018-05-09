package com.cqebd.student.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.umeng.analytics.MobclickAgent

/**
 * 描述
 * Created by gorden on 2017/11/5.
 */
abstract class BaseActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        initialize(savedInstanceState)
        bindEvents()
    }

    override fun onResume() {
        super.onResume()
        //友盟统计
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    /**
     * 绑定布局
     */
    abstract fun setContentView()

    /**
     * 初始化工作
     */
    open fun initialize(savedInstanceState: Bundle?){}

    /**
     * 事件绑定
     */
    open fun bindEvents(){}

    fun backTop(view: View){
        onBackPressed()
    }
}