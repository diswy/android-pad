package com.ebd.lib.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate
import com.ebd.lib.R
import com.ebd.lib.activity.OnlineBookshelfActivity
import com.ebd.lib.bean.BOOK_ICON
import com.ebd.lib.bean.BOOK_ITEM
import com.ebd.lib.bean.HomeBookshelf
import com.xiaofu.lib_base_xiaofu.img.loadImage
import kotlinx.android.synthetic.main.item_add_into_bookshelf.view.*
import kotlinx.android.synthetic.main.item_bookshelf.view.*
import org.jetbrains.anko.startActivity

class MyBookshelfAdapter : BaseQuickAdapter<HomeBookshelf, BaseViewHolder>(null) {

    init {
        multiTypeDelegate = object : MultiTypeDelegate<HomeBookshelf>() {
            override fun getItemType(t: HomeBookshelf): Int {
                return t.type
            }
        }

        multiTypeDelegate.registerItemType(BOOK_ITEM, R.layout.item_bookshelf)
                .registerItemType(BOOK_ICON, R.layout.item_add_into_bookshelf)

        super.addData(HomeBookshelf(BOOK_ICON))
    }

    override fun addData(mData: HomeBookshelf) {
        if (data.size > 0)
            super.addData(data.size - 1, mData)
    }

    override fun addData(newData: MutableCollection<out HomeBookshelf>) {
        if (data.size > 0)
            super.addData(data.size - 1, newData)
    }

    /**
     * 添加新数据
     */
    override fun setNewData(data: MutableList<HomeBookshelf>?) {
        if (data != null) {
            super.setNewData(null)
            data.add(HomeBookshelf(BOOK_ICON))
            super.addData(data)
        } else {
            super.setNewData(null)
            super.addData(HomeBookshelf(BOOK_ICON))
        }
    }

    override fun convert(helper: BaseViewHolder?, item: HomeBookshelf) {
        when (helper?.itemViewType) {
            BOOK_ITEM -> {
                helper.itemView.apply {
                    mContext.loadImage(item.cover,bookCover)
                    bookName.text = item.name
                }
            }
            BOOK_ICON -> {
                helper.itemView.apply {
                    btnAddBook.setOnClickListener {
                        mContext.startActivity<OnlineBookshelfActivity>()
                    }
                }
            }
        }
    }
}