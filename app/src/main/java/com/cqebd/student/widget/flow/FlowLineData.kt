package com.cqebd.student.widget.flow

import android.view.View

/**
 * 描述
 * Created by gorden on 2018/1/12.
 */
data class FlowLineData(var lineWidth: Int = 0,//行宽
                        var lineHeight: Int = 0,//行高
                        val lineList: ArrayList<FlowItemData> = ArrayList()) {
    fun reset() {
        lineWidth = 0
        lineHeight = 0
        lineList.clear()
    }

    data class FlowItemData(val view: View,
                            var itemWidth: Int,
                            var itemHeight: Int,
                            var marginLeft: Int = 0,
                            var marginRight: Int = 0,
                            var marginTop: Int = 0,
                            var marginBottom: Int = 0
    )
}