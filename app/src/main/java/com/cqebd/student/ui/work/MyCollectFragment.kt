package com.cqebd.student.ui.work


import android.content.Context
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import com.cqebd.student.MainActivity

import com.cqebd.student.R
import com.cqebd.student.constant.Constant
import com.cqebd.student.tools.loginId
import com.cqebd.student.ui.fragment.BaseLazyFragment
import com.cqebd.student.vo.entity.FilterData
import com.just.agentweb.AgentWeb
import kotlinx.android.synthetic.main.fragment_my_collect.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.jetbrains.anko.support.v4.dip


/**
 *  收藏
 */
class MyCollectFragment : BaseLazyFragment() {
    private val collectUrlNoSubject = "studentCollect/StudentCollectList?studentid=%s"
    val collectUrlSubject = "studentCollect/StudentCollectList?studentid=%s&SubjectTypeId=%d"

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_collect
    }

    override fun initView() {
        super.initView()
        // 副标题
        val subCommonNavigator = CommonNavigator(context)
        subCommonNavigator.scrollPivotX = 0.65f
        subCommonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val titleView = ColorTransitionPagerTitleView(context)
                titleView.normalColor = resources.getColor(R.color.color_tab_title)
                titleView.selectedColor = resources.getColor(R.color.color_main)
                titleView.text = FilterData.subjectAll[index].Name
                titleView.textSize = 14f
                titleView.setOnClickListener {
                    magic_indicator_subtitle.onPageSelected(index)
                    magic_indicator_subtitle.onPageScrollStateChanged(index)
                    magic_indicator_subtitle.onPageScrolled(index, 0f, 0)
                    when (index) {
                        0 -> loadWeb(Constant.BASE_WEB_URL + String.format(collectUrlNoSubject, loginId))
                        else -> loadWeb(Constant.BASE_WEB_URL + String.format(collectUrlSubject, loginId, FilterData.subjectAll[index].status))
                    }
                }
                return titleView
            }

            override fun getCount(): Int {
                return FilterData.subjectAll.size
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

    override fun lazyLoad() {


        loadWeb(Constant.BASE_WEB_URL + String.format(collectUrlNoSubject, loginId))
    }

    override fun onInvisible() {
        // 侧滑菜单启用

    }

    private fun loadWeb(url: String) {
        web_container.removeAllViews()
        AgentWeb.with(this)
                .setAgentWebParent(web_container, FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(url)
    }


}
