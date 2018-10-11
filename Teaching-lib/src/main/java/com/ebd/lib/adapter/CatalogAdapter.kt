package com.ebd.lib.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ebd.lib.R
import com.ebd.lib.bean.BookCatalog

class CatalogAdapter : BaseQuickAdapter<BookCatalog, BaseViewHolder>(R.layout.item_catalog) {
    override fun convert(helper: BaseViewHolder?, item: BookCatalog?) {
        helper?.setText(R.id.tvCatalog, item?.name)
    }
}