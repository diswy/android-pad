package com.cqebd.student.live.entity

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
    var avatar: String = ""
    var nick: String = ""
    var account: String = ""
    var isMyself = false

    constructor(mType: Int, mAccount: String, mContent: String, mNick: String, isMyself: Boolean = false) {
        this.itemType = mType
        this.isMyself = isMyself
        this.nick = mNick
        this.account = mAccount
        when (itemType) {
            TEXT -> {
                this.content = mContent
            }
            IMG -> {
                this.imgSrc = mContent
            }
        }
    }

    constructor(mType: Int, mAccount: String, mContent: String, avatar: String, mNick: String, isMyself: Boolean = false) {
        this.itemType = mType
        this.isMyself = isMyself
        this.avatar = avatar
        this.nick = mNick
        this.account = mAccount
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