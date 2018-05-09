package com.cqebd.student.ui.root


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.test.BlankFragment
import com.cqebd.student.ui.video.*
import kotlinx.android.synthetic.main.fragment_root_video.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.jetbrains.anko.support.v4.dip

/**
 * Video
 */
class RootVideoFragment : BaseFragment() {
    private val titles = listOf("视频", "订阅", "直播", "课表", "收藏")

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
                    2 -> LiveFragment()
                    3 -> ScheduleFragment()
                    4 -> MyVideoCollectFragment()
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
                mainActivity.filterLayoutItem(position,MainActivity.VIDEO)
                if (position == 0){
                    mainActivity.enableDrawerLayout()
                }else{
                    mainActivity.disableDrawerLayout()
                }

            }
        })

        // 主标题
        val commonNavigator = CommonNavigator(context)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = ColorTransitionPagerTitleView(context)
                titleView.normalColor = resources.getColor(R.color.color_title)
                titleView.selectedColor = resources.getColor(R.color.color_main)
                titleView.text = titles[index]
                titleView.textSize = 16f
                val tp = titleView.paint
                tp.isFakeBoldText = true
                titleView.setOnClickListener {
                    video_vp.currentItem = index
                }
                return titleView
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getIndicator(p0: Context?): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineHeight = dip(2).toFloat()
                indicator.lineWidth = dip(15).toFloat()
                indicator.roundRadius = dip(3).toFloat()
                indicator.startInterpolator = AccelerateInterpolator()
                indicator.endInterpolator = DecelerateInterpolator(2.0f)
                indicator.setColors(resources.getColor(R.color.color_main))
                return indicator
            }
        }
        video_magic_indicator.navigator = commonNavigator
        ViewPagerHelper.bind(video_magic_indicator, video_vp)
    }

}
