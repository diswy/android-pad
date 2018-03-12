package com.cqebd.student.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.anko.static.dp
import com.cqebd.student.widget.flow.FlowTagAdapter
import com.cqebd.student.widget.flow.TagFlowLayout

/**
 * 筛选Adapter
 */
class FilterTagAdapter :FlowTagAdapter<String>{
    override fun createView(parent: TagFlowLayout, position: Int, data: String): View {
        val tag = TextView(parent.context)
        tag.text = data
        tag.setPadding(10, 5, 10, 5)
        tag.textSize = 15f
        tag.gravity = Gravity.CENTER
        tag.minWidth = 70.dp

        val defaultDrawable = GradientDrawable()
        defaultDrawable.cornerRadius = 10f
        defaultDrawable.setColor(Color.parseColor("#88FFFFFF"))
        defaultDrawable.setStroke(2, Color.LTGRAY)

        val checkedDrawable = GradientDrawable()
        checkedDrawable.cornerRadius = 10f
        checkedDrawable.setColor(Color.parseColor("#FFBE4F"))
        checkedDrawable.setStroke(2, Color.parseColor("#f2a829"))

        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_checked), checkedDrawable)
        stateListDrawable.addState(intArrayOf(0), defaultDrawable)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tag.background = stateListDrawable
        } else {
            tag.setBackgroundDrawable(stateListDrawable)
        }
        val stateList = ColorStateList(arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf(0)),
                intArrayOf(Color.WHITE, Color.parseColor("#666666")))
        tag.setTextColor(stateList)

        return tag
    }

    constructor(dataList: List<String>):super(dataList)
    constructor(vararg dataList: String):super(*dataList)
}