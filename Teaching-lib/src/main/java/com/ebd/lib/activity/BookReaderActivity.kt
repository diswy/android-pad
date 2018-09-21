package com.ebd.lib.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.ebd.lib.R
import com.ebd.lib.fragment.ReaderFragment
import com.xiaofu.lib_base_xiaofu.base.BaseActivity
import kotlinx.android.synthetic.main.layout_book_reader_main.*

class BookReaderActivity : BaseActivity() {

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
    }
}
