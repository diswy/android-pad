package com.ebd.lib.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ebd.lib.R
import com.xiaofu.lib_base_xiaofu.img.loadImage

class OnlineBookshelfAdapter : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_online_bookshelf, null) {

    override fun convert(helper: BaseViewHolder?, item: String?) {
        helper?.let {
            mContext.loadImage("http://img.hb.aicdn.com/49ad786bc3520c3feaea5aaa5e4a5298d6423f151e78f-zLpcyn_fw658", it.getView(R.id.bookCover))
            it.setText(R.id.bookName, "人教版初中语文七年级下册")
        }
    }
}