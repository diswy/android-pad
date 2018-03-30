package com.cqebd.student.ui.fragment


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.js.VideoJs
import kotlinx.android.synthetic.main.fragment_nested_web.*


/**
 * 嵌套NestedScrollerView 用于与toolbar交互
 */
class NestedWebFragment : BaseFragment() {
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_nested_web, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val setting = webView?.settings
        setting?.let {
            it.javaScriptEnabled = true// 启用JS脚本

            it.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            webView.isVerticalScrollBarEnabled = false
            webView.isHorizontalScrollBarEnabled = false
        }
        webView?.addJavascriptInterface(VideoJs(activity), "video")


        if (arguments != null) {
            val url = arguments?.getString("url")
            if (TextUtils.isEmpty(url)) {
                throw IllegalArgumentException("H5地址不能为空")
            }
            webView.loadUrl(url)
        }
    }

}
