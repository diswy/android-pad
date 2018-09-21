package com.ebd.lib.fragment


import android.app.Activity
import android.support.v7.widget.GridLayoutManager
import com.ebd.lib.R
import com.ebd.lib.activity.BookReaderActivity
import com.ebd.lib.adapter.MyBookshelfAdapter
import com.ebd.lib.bean.BOOK_ITEM
import com.ebd.lib.bean.HomeBookshelf
import com.ebd.lib.data.getMyPath
import com.xiaofu.lib_base_xiaofu.base.BaseFragment
import kotlinx.android.synthetic.main.item_bookshelf.*
import kotlinx.android.synthetic.main.recycler_view_layout.*
import org.jetbrains.anko.support.v4.startActivity
import java.io.File


/**
 * 主页离线书籍
 *
 */
class MyBookshelfFragment : BaseFragment() {

    private val bookshelfAdapter by lazy { MyBookshelfAdapter() }

    private val myBookList = ArrayList<HomeBookshelf>()

    override fun getLayoutRes(): Int = R.layout.fragment_my_bookshelf

    override fun initialize(activity: Activity) {
        mRefreshLayout.isEnableRefresh = false
        mRefreshLayout.isEnableLoadMore = false

        mRv.layoutManager = GridLayoutManager(activity, 3)
        bookshelfAdapter.bindToRecyclerView(mRv)

        bookshelfAdapter.setOnItemClickListener { adapter, view, position ->
            startActivity<BookReaderActivity>()
        }
        bookshelfAdapter.addData(HomeBookshelf(0))


        val bookFile = File(getMyPath())// 遍历本地已下载书籍
        if (bookFile.exists()) {
            val files = bookFile.listFiles()
            for (book in files) {
                if (book.isDirectory) {
                    myBookList.add(HomeBookshelf(BOOK_ITEM, book.name, getMyPath().plus("${book.name}/cover.png")))
                    println("---->>>文件夹：${book.name}")
                }
            }
        }

        bookshelfAdapter.addData(myBookList)
    }


}
