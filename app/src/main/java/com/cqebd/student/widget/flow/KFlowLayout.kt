@file:Suppress("PrivatePropertyName")

package com.cqebd.student.widget.flow

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.cqebd.student.R
import kotlin.math.max

/**
 * 描述
 * Created by gorden on 2018/1/12.
 */
open class KFlowLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr) {

    private val GRAVITY_LEFT = -1
    private val GRAVITY_CENTER = 0
    private val GRAVITY_RIGHT = 1

    private var gravity: Int
    protected var minSelect: Int //>=0
    protected var maxSelect: Int //-1 无限制
    private val flowLineData = ArrayList<FlowLineData>()


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.KFlowLayout)
        gravity = a.getInt(R.styleable.KFlowLayout_flow_gravity, GRAVITY_LEFT)
        minSelect = a.getInt(R.styleable.KFlowLayout_flow_min_select, 0)
        maxSelect = a.getInt(R.styleable.KFlowLayout_flow_max_select, -1)
        a.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeWidth = MeasureSpec.getSize(widthMeasureSpec)
        val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
        val sizeHeight = MeasureSpec.getSize(heightMeasureSpec)
        val modeHeight = MeasureSpec.getMode(heightMeasureSpec)

        // wrap_content
        var width = 0
        var height = 0

        var lineWidth = 0
        var lineHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                if (i == childCount - 1) {
                    width = Math.max(lineWidth, width)
                    height += lineHeight
                }
                continue
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val lp = child
                    .layoutParams as ViewGroup.MarginLayoutParams

            val childWidth = (child.measuredWidth + lp.leftMargin
                    + lp.rightMargin)
            val childHeight = (child.measuredHeight + lp.topMargin
                    + lp.bottomMargin)

            if (lineWidth + childWidth > sizeWidth - paddingLeft - paddingRight) {
                width = Math.max(width, lineWidth)
                lineWidth = childWidth
                height += lineHeight
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = Math.max(lineHeight, childHeight)
            }
            if (i == childCount - 1) {
                width = Math.max(lineWidth, width)
                height += lineHeight
            }
        }
        setMeasuredDimension(
                if (modeWidth == View.MeasureSpec.EXACTLY) sizeWidth else width + paddingLeft + paddingRight,
                if (modeHeight == View.MeasureSpec.EXACTLY) sizeHeight else height + paddingTop + paddingBottom//
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0) return

        flowLineCalculate()

        var top = paddingTop
        var left: Int
        flowLineData.forEach {
            left = when (gravity) {
                GRAVITY_LEFT -> paddingLeft
                GRAVITY_CENTER -> (width - it.lineWidth) / 2
                GRAVITY_RIGHT -> width - it.lineWidth - paddingRight
                else -> 0
            }
            it.lineList.forEach {
                val lc = left + it.marginLeft
                val tc = top + it.marginTop
                val rc = lc + it.itemWidth
                val bc = tc + it.itemHeight
                it.view.layout(lc, tc, rc, bc)
                left += it.itemWidth + it.marginLeft + it.marginRight
            }
            top += it.lineHeight
            it.reset()
        }
    }


    private fun flowLineCalculate() {
        flowLineData.clear()

        var lineData = FlowLineData()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            val leftMargin = (child.layoutParams as? MarginLayoutParams)?.leftMargin ?: 0
            val rightMargin = (child.layoutParams as? MarginLayoutParams)?.rightMargin ?: 0
            val topMargin = (child.layoutParams as? MarginLayoutParams)?.topMargin ?: 0
            val bottomMargin = (child.layoutParams as? MarginLayoutParams)?.bottomMargin ?: 0

            val item = FlowLineData.FlowItemData(child, childWidth, childHeight, leftMargin, rightMargin, topMargin, bottomMargin)

            if (lineData.lineWidth + childWidth + leftMargin + rightMargin > width - paddingLeft - paddingRight) {
                flowLineData.add(lineData)
                lineData = FlowLineData(childWidth + leftMargin + rightMargin, childHeight + topMargin + bottomMargin)
                lineData.lineList.add(item)
            } else {
                lineData.lineWidth += childWidth + leftMargin + rightMargin
                lineData.lineHeight = max(lineData.lineHeight, childHeight + topMargin + bottomMargin)
                lineData.lineList.add(item)
            }
        }
        flowLineData.add(lineData)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }
}