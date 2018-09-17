package com.ebd.lib.activity

import android.support.v7.widget.GridLayoutManager
import com.ebd.lib.R
import com.ebd.lib.adapter.OnlineBookshelfAdapter
import com.xiaofu.lib_base_xiaofu.base.BaseToolbarActivity
import kotlinx.android.synthetic.main.recycler_view_layout.*

class OnlineBookshelfActivity : BaseToolbarActivity() {

    private val onlineBookshelfAdapter by lazy { OnlineBookshelfAdapter() }

    override fun setTitle(): String = "书城"

    override fun getView(): Int = R.layout.activity_online_bookshelf

    override fun initialize() {
        mRv.layoutManager = GridLayoutManager(this, 3)
        onlineBookshelfAdapter.bindToRecyclerView(mRv)

        onlineBookshelfAdapter.addData("")
    }

    override fun bindEvent() {

    }

}
