package com.cqebd.student.adapter

import android.content.Context
import android.support.v4.view.ViewPager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.cqebd.student.R
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView
import org.jetbrains.anko.dip

class TitleNavigatorAdapter(context: Context, titles: List<String>, vp: ViewPager, isTitleBold: Boolean = true) : CommonNavigatorAdapter() {
    private val mTitles = titles
    private val mContext = context
    private val mVp = vp
    private val mIsTextBold = isTitleBold

    override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
        val titleView = ColorTransitionPagerTitleView(context)
        titleView.normalColor = mContext.resources.getColor(R.color.color_title)
        titleView.selectedColor = mContext.resources.getColor(R.color.color_main)
        titleView.text = mTitles[index]
        titleView.textSize = 14f
        if (mIsTextBold){
            val tp = titleView.paint
            tp.isFakeBoldText = true
        }
        titleView.setOnClickListener {
            mVp.currentItem = index
        }
        return titleView
    }

    override fun getCount(): Int {
        return mTitles.size
    }

    override fun getIndicator(p0: Context?): IPagerIndicator {
        val indicator = LinePagerIndicator(mContext)
        indicator.mode = LinePagerIndicator.MODE_EXACTLY
        indicator.lineHeight = mContext.dip(2).toFloat()
        indicator.lineWidth = mContext.dip(15).toFloat()
        indicator.roundRadius = mContext.dip(3).toFloat()
        indicator.startInterpolator = AccelerateInterpolator()
        indicator.endInterpolator = DecelerateInterpolator(2.0f)
        indicator.setColors(mContext.resources.getColor(R.color.color_main))
        return indicator
    }
}