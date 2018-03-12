package com.cqebd.student.widget.flow

import android.view.View

/**
 * 描述
 * Created by gorden on 2018/2/28.
 */
abstract class FlowTagAdapter<T> {
    private var tagList: List<T>

    constructor(dataList: List<T>) {
        this.tagList = dataList
    }

    constructor(vararg dataList: T) {
        tagList = dataList.toList()
    }

    fun getCount() = tagList.size
    fun getItem(position: Int): T = tagList[position]

    abstract fun createView(parent:TagFlowLayout,position: Int, data: T): View
}