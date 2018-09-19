package com.ebd.lib.activity

import com.ebd.lib.R
import com.xiaofu.lib_base_xiaofu.base.BaseActivity

class BookReaderActivity : BaseActivity() {

    override fun getView(): Int = R.layout.activity_book_reader

    override fun initialize() {
        setFullScreen()

    }
}
