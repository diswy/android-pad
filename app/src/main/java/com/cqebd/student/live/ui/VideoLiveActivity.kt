package com.cqebd.student.live.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.live.helper.MsgManager
import com.cqebd.student.test.BlankFragment
import gorden.util.RxCounter
import kotlinx.android.synthetic.main.activity_video_live.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

class VideoLiveActivity : BaseActivity() {
    private val titles = listOf("讨论", "白板")
    val mRts = LiveRtsFragment()
    val mChat = ChatRoomFragment()

    override fun setContentView() {
        setContentView(R.layout.activity_video_live)
    }

    override fun initialize(savedInstanceState: Bundle?) {
//        supportFragmentManager.beginTransaction().add(R.id.mRtsContainer, ChatRoomFragment()).commit()
        initView()
    }

    override fun bindEvents() {
        mLiveToolbar.setNavigationOnClickListener { finish() }
//        mTestNotification.setOnClickListener {
//            MsgManager.instance().sendP2PCustomNotification("Teacher_499")
//        }
    }

    private fun initView() {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = TitleNavigatorAdapter(this, titles, mNonScrollVp)
        mLiveIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(mLiveIndicator, mNonScrollVp)

        mNonScrollVp.offscreenPageLimit = 2
        mNonScrollVp.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> mChat
                    1 -> mRts
                    else -> BlankFragment()
                }
            }

            override fun getCount(): Int {
                return titles.size
            }
        }

        mNonScrollVp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                when (position) {
                    0 -> mChat.onCurrentInit()
                }
            }

            override fun onPageSelected(position: Int) {

            }
        })
    }
}
