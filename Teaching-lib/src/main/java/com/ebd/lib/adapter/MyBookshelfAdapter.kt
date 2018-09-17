package com.ebd.lib.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate
import com.ebd.lib.R
import com.ebd.lib.activity.OnlineBookshelfActivity
import com.ebd.lib.bean.BOOK_ICON
import com.ebd.lib.bean.BOOK_ITEM
import com.ebd.lib.bean.HomeBookshelf
import kotlinx.android.synthetic.main.item_add_into_bookshelf.view.*
import org.jetbrains.anko.startActivity

class MyBookshelfAdapter : BaseQuickAdapter<HomeBookshelf, BaseViewHolder>(null) {

    init {
        multiTypeDelegate = object : MultiTypeDelegate<HomeBookshelf>() {
            override fun getItemType(t: HomeBookshelf): Int {
                return t.type
            }
        }

        multiTypeDelegate.registerItemType(BOOK_ITEM, R.layout.item_my_bookshelf)
                .registerItemType(BOOK_ICON, R.layout.item_add_into_bookshelf)

        setNewData(null)
    }

    /**
     * 添加单条数据
     * 保证添加课程按钮永远在最后一个
     */
    override fun addData(mData: HomeBookshelf) {
        if (data.size > 0) {
            data.removeAt(data.size - 1)
            super.addData(mData)
            super.addData(HomeBookshelf(BOOK_ICON))
        }
        super.addData(data)
    }

    /**
     * 添加多条数据
     */
    override fun addData(newData: MutableCollection<out HomeBookshelf>) {
        if (data.size > 0) {
            data.removeAt(data.size - 1)
            super.addData(newData)
            super.addData(HomeBookshelf(BOOK_ICON))
        }
    }

    /**
     * 添加新数据
     */
    override fun setNewData(data: MutableList<HomeBookshelf>?) {
        if (data != null) {
            data.add(HomeBookshelf(BOOK_ICON))
            super.setNewData(data)
        } else {
            super.setNewData(listOf(HomeBookshelf(BOOK_ICON)))
        }
    }

    override fun convert(helper: BaseViewHolder?, item: HomeBookshelf) {
        when (helper?.itemViewType) {
            BOOK_ITEM -> {

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