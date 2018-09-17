package com.xiaofu.lib_base_xiaofu.base

import android.graphics.Color
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import com.xiaofu.lib_base_xiaofu.R
import kotlinx.android.synthetic.main.base_layout_toolbar.*

abstract class BaseToolbarActivity : BaseActivity() {

    override fun setView() {
        setContentView(R.layout.base_layout_toolbar)
        LayoutInflater.from(this).inflate(getView(), mContainer)
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tvTitle.text = setTitle()
        setToolbarColor(Color.parseColor("#349aff"))
    }

    /**
     * 使用toolbar默认返回箭头时有用
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    abstract fun setTitle(): String

    protected fun getToolbar(): Toolbar = mToolbar

    open fun getToolbarMenu() = -1

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (getToolbarMenu() != -1)
            menuInflater.inflate(getToolbarMenu(), menu)
        return true
    }

    private fun setToolbarColor(color: Int) {
        mToolbar.setBackgroundColor(color)
    }

}