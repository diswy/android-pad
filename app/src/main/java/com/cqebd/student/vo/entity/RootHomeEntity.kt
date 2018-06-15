package com.cqebd.student.vo.entity

import com.chad.library.adapter.base.entity.MultiItemEntity

class RootHomeEntity : MultiItemEntity {
    companion object {
        const val TITLE = 1
        const val ITEM = 2
        const val DOUBLE = 3
    }

    private var itemType: Int? = null

    override fun getItemType(): Int {
        return itemType ?: -1
    }


    var title = ""
    var arg0: VideoInfo? = null
    var arg1: VideoInfo? = null
    var item: VideoInfo? = null

    constructor(mTitle: String) {
        this.itemType = TITLE
        this.title = mTitle
    }

    constructor(arg0: VideoInfo, arg1: VideoInfo?) {
        this.itemType = DOUBLE
        this.arg0 = arg0
        this.arg1 = arg1
    }

    constructor(item: VideoInfo) {
        this.itemType = ITEM
        this.item = item
    }
}