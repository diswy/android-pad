package com.ebd.lib.fragment


import android.app.Activity
import com.ebd.lib.R
import com.xiaofu.lib_base_xiaofu.base.BaseFragment
import com.xiaofu.lib_base_xiaofu.img.loadImage
import kotlinx.android.synthetic.main.fragment_reader.*


/**
 * 阅读器
 *
 */
class ReaderFragment : BaseFragment() {

    override fun getLayoutRes(): Int = R.layout.fragment_reader

    override fun initialize(activity: Activity) {
        arguments?.let {
            loadImage(it.getString("book_page_url"), mIv)
        }
    }
}
