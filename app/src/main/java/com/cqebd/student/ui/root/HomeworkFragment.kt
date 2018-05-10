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
import com.cqebd.student.ui.work.BeSharedFragment
import com.cqebd.student.ui.work.HomeworkContentFragment
import com.cqebd.student.ui.work.MyWorkCollectFragment
import com.cqebd.student.ui.work.WrongQuestionFragment
import kotlinx.android.synthetic.main.fragment_root_homework.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator


/**
 *  作业
 */
class HomeworkFragment : BaseFragment() {
    private val titles = listOf("作业", "错题", "分享", "收藏")

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_root_homework, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {

        val mainActivity = activity as MainActivity
        mainActivity.filterLayoutItem(0, MainActivity.WORK)
        view_pager.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> HomeworkContentFragment()
                    1 -> WrongQuestionFragment()
                    2 -> BeSharedFragment()
                    3 -> MyWorkCollectFragment()
                    else -> HomeworkContentFragment()
                }
            }

            override fun getCount(): Int {
                return titles.size
            }
        }

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                mainActivity.filterLayoutItem(position, MainActivity.WORK)

                // position = 3 屏蔽收藏侧滑 无内容
                if (position == 3) {
                    mainActivity.disableDrawerLayout()
                } else {
                    mainActivity.enableDrawerLayout()
                }

            }
        })

        // 主标题
        context?.let {
            val commonNavigator = CommonNavigator(it)
            commonNavigator.adapter = TitleNavigatorAdapter(it, titles, view_pager)
            magic_indicator.navigator = commonNavigator
            ViewPagerHelper.bind(magic_indicator, view_pager)
        }
    }

}



