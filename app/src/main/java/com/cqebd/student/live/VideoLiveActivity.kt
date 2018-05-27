package com.cqebd.student.live

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.test.BlankFragment
import kotlinx.android.synthetic.main.activity_video_live.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

class VideoLiveActivity : BaseActivity() {
    private val titles = listOf("白板", "讨论")

    override fun setContentView() {
        setContentView(R.layout.activity_video_live)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        supportFragmentManager.beginTransaction().add(R.id.mRtsContainer, LiveRtsFragment()).commit()
        initView()

    }

    private fun initView() {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = TitleNavigatorAdapter(this, titles, mNonScrollVp)
        mLiveIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(mLiveIndicator, mNonScrollVp)

        mNonScrollVp.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> BlankFragment()
                    1 -> BlankFragment()
                    else -> BlankFragment()
                }
            }

            override fun getCount(): Int {
                return titles.size
            }

        }
    }
}
