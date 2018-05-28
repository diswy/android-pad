package com.cqebd.student.live

import com.chad.library.adapter.base.entity.MultiItemEntity

class ChatRoomEntity : MultiItemEntity {
    companion object {
        const val TEXT = 1
        const val IMG = 2
    }

    private var itemType: Int? = null

    override fun getItemType(): Int {
        return itemType ?: -1
    }

    var content: String = ""
    var imgSrc: String = ""
    var isMyself = false

    constructor(mType: Int, mContent: String, isMyself:Boolean = false) {
        this.itemType = mType
        this.isMyself = isMyself
        when (itemType) {
            TEXT -> {
                this.content = mContent
            }
            IMG -> {
                this.imgSrc = mContent
            }
        }
    }


}