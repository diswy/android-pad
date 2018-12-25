package com.cqebd.student.ui.root


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.adapter.TitleNavigatorAdapter
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.test.BlankFragment
import com.cqebd.student.ui.video.*
import kotlinx.android.synthetic.main.fragment_root_video.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator

/**
 * Video
 */
class RootVideoFragment : BaseFragment() {
    private val titles = listOf("视频", "订阅", "课表", "收藏")

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_root_video, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val mainActivity = activity as MainActivity
        mainActivity.filterLayoutItem(0, MainActivity.VIDEO)

        video_vp.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> VideoContentFragment()
                    1 -> MySubscribeFragment()
//                    2 -> LiveFragment()
                    2 -> ScheduleFragment()
                    3 -> MyVideoCollectFragment()
                    else -> BlankFragment()
                }
            }

            override fun getCount(): Int {
                return titles.size
            }
        }

        video_vp.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mainActivity.filterLayoutItem(position, MainActivity.VIDEO)
                if (position == 0) {
                    mainActivity.enableDrawerLayout()
                } else {
                    mainActivity.disableDrawerLayout()
                }

            }
        })

        // 主标题
        context?.let {
            val commonNavigator = CommonNavigator(it)
            commonNavigator.adapter = TitleNavigatorAdapter(it, titles, video_vp)
            video_magic_indicator.navigator = commonNavigator
            ViewPagerHelper.bind(video_magic_indicator, video_vp)
        }

        arguments?.let {
            val pos = it.getInt("pos", -1)
            if (pos != -1) {
                video_vp.currentItem = pos
                video_magic_indicator.onPageScrollStateChanged(pos)
            }
        }

    }

}
