package com.ebd.lib.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.widget.TextView
import com.ebd.lib.R
import com.ebd.lib.adapter.CatalogAdapter
import com.ebd.lib.bean.Book
import com.ebd.lib.bean.BookCatalog
import com.ebd.lib.data.getMyPath
import com.ebd.lib.fragment.ReaderFragment
import com.ebd.lib.utils.XMLUtils
import com.google.gson.Gson
import com.xiaofu.lib_base_xiaofu.base.BaseActivity
import kotlinx.android.synthetic.main.activity_book_reader.*
import kotlinx.android.synthetic.main.layout_book_reader_main.*

class BookReaderActivity : BaseActivity() {

    private val catalogAdapter by lazy { CatalogAdapter() }

    private val headerView by lazy { LayoutInflater.from(this).inflate(R.layout.header_catalog_layout, null) }

    private val tvBookName by lazy { headerView.findViewById<TextView>(R.id.bookName) }

    override fun getView(): Int = R.layout.activity_book_reader

    override fun initialize() {
        setFullScreen()

        val list = ArrayList<String>()
        list.add("http://img.hb.aicdn.com/824132320b5e54ed5e6dd2edad17c052c37737c6196a3f-HwSCQe_fw658")
        list.add("http://img.hb.aicdn.com/b8d45254817c21d98df2f4bde94aa92f1cf26d4f1cd57-8on8JV_fw658")
        list.add("http://img.hb.aicdn.com/23f0dbb090585e0b7a983d24845fbae87dfcfe212974c-lB7AKK_fw658")
        list.add("asdasdasdad")

        vpBook.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val readerFragment = ReaderFragment()
                val bundle = Bundle()
                bundle.putString("book_page_url", list[position])
                readerFragment.arguments = bundle
                return readerFragment
            }

            override fun getCount(): Int {
                return list.size
            }
        }



        catalogRv.layoutManager = LinearLayoutManager(this)
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.my_divider)!!)
        catalogRv.addItemDecoration(divider)
        catalogAdapter.bindToRecyclerView(catalogRv)
        catalogAdapter.addHeaderView(headerView)
        val catalogList = ArrayList<BookCatalog>()

        val xml = XMLUtils()
        val string = xml.getXmlToJson(getMyPath().plus("test/index"))
        val book = Gson().fromJson<Book>(string, Book::class.java)
        tvBookName.text = book.ebook.info.name

        if (book.ebook.index.chapters.chapter != null) {
            for (section in book.ebook.index.chapters.chapter) {
                catalogList.add(BookCatalog(section.name, section.page))
                println("这是：${section.name} 页码：${section.page}")
                if (section.sections.section != null) {
                    for (item in section.sections.section) {
                        println("       section:名字：${item.name} 页码：${item.page}")
                        catalogList.add(BookCatalog("    " + item.name, item.page))
                        if (item.items != null) {
                            for (i in item.items.item) {
                                println("               item:名字：${i.content} 页码：${i.page}")
                                catalogList.add(BookCatalog("        " + i.content, i.page))

                            }
                        }
                    }
                }
            }
        }

        catalogAdapter.setNewData(catalogList)

//        if (book.Ebook.chapters.chapter != null) {
//            for (section in book.Ebook.chapters.chapter) {
//                println("这是：${section.name}")
//                if (section.sections.section != null) {
//                    for (item in section.sections.section) {
//                        println("section:名字：${item.name} 页码：${item.page}")
//                        if(item.items != null){
//                            for (i in item.items.item) {
//                                println("item:名字：${i.content} 页码：${i.page}")
//                            }
//                        }
//                    }
//                }
//            }
//        }


    }
}
