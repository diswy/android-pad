package com.cqebd.student.ui.root


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.test.BlankFragment
import com.cqebd.student.ui.work.HomeworkContentFragment
import com.cqebd.student.ui.work.WrongQuestionFragment
import com.cqebd.student.vo.entity.FilterData
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.jetbrains.anko.support.v4.dip
import org.jetbrains.anko.support.v4.toast
import kotlinx.android.synthetic.main.fragment_homework.*


/**
 *  作业
 */
class HomeworkFragment : BaseFragment() {
    private val titles = listOf("作业", "错题", "分享", "收藏", "推荐")
    private val subject: List<FilterData> by lazy {
        val list = ArrayList<FilterData>()
        list.add(FilterData(-1, "全部"))
        list.addAll(FilterData.subject)
        return@lazy list
    }


    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_homework, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {

        view_pager.adapter = object : FragmentStatePagerAdapter(fragmentManager) {
            override fun getItem(position: Int): Fragment {
                return when (position) {
                    0 -> HomeworkContentFragment()
                    1 -> WrongQuestionFragment()
                    else -> BlankFragment()
                }
            }

            override fun getCount(): Int {
                return titles.size
            }
        }

        // 主标题
        val commonNavigator = CommonNavigator(context)
        commonNavigator.scrollPivotX = 0.65f
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = ColorTransitionPagerTitleView(context)
                titleView.normalColor = resources.getColor(R.color.color_tab_title)
                titleView.selectedColor = resources.getColor(R.color.color_main)
                titleView.text = titles[index]
                titleView.textSize = 16f
                titleView.setOnClickListener { view_pager.currentItem = index }
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
        magic_indicator.navigator = commonNavigator
        ViewPagerHelper.bind(magic_indicator, view_pager)

        // 副标题
        val subCommonNavigator = CommonNavigator(context)
        subCommonNavigator.scrollPivotX = 0.65f
        subCommonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = ColorTransitionPagerTitleView(context)
                titleView.normalColor = resources.getColor(R.color.color_tab_title)
                titleView.selectedColor = resources.getColor(R.color.color_main)
                titleView.text = subject[index].Name
                titleView.textSize = 14f
                titleView.setOnClickListener {
                    // TODO
                    magic_indicator_subtitle.onPageSelected(index)
                    magic_indicator_subtitle.onPageScrollStateChanged(index)
                    magic_indicator_subtitle.onPageScrolled(index, 0f, 0)
                }
                return titleView
            }

            override fun getCount(): Int {
                return subject.size
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
        magic_indicator_subtitle.navigator = subCommonNavigator

    }

    override fun bindEvents() {
        // 侧滑菜单处理
        val mainActivity = activity as MainActivity
        btn_filter.setOnClickListener {
            mainActivity.switchDrawerLayout()
        }
        mainActivity.setOnDrawerListener(object : MainActivity.OnDrawerListener {
            override fun onClear() {
            }

            override fun onConfirm(id: Int) {
                toast("id = $id")
            }
        })
    }
}



