package com.cqebd.student.ui.week

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import kotlinx.android.synthetic.main.activity_week_file.*
import kotlinx.android.synthetic.main.activity_week_file.toolbar

class WeekFileActivity : BaseActivity() {

    override fun setContentView() {
        setContentView(R.layout.activity_week_file)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        mTab.setupWithViewPager(mVp)
        mVp.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> WeekDocListFragment()
                    1 -> DownloadFileFragment()
                    else -> WeekDocListFragment()
                }
            }

            override fun getCount(): Int = 2

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> "全部"
                    1 ->"已下载"
                    else -> "全部"
                }
            }

        }
    }

}
