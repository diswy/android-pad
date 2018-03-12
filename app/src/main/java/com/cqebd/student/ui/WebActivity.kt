package com.cqebd.student.ui

import android.os.Bundle
import android.view.View
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.widget.WebView
import kotlinx.android.synthetic.main.activity_webview.*

/**
 * 描述
 * Created by gorden on 2018/3/8.
 */
class WebActivity : BaseActivity() {
    private var webView:WebView?=null
    override fun setContentView() {
        setContentView(R.layout.activity_webview)
        webView = WebView(App.mContext)
        web_parent.addView(webView)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val url = intent.getStringExtra("url")
        webView?.load(url,object :WebView.LoadCallback{
            override fun onLoad() {
                pageLoadView.load()
            }

            override fun onProgress(progress: Int) {
                progressBar.visibility = View.VISIBLE
                progressBar.progress = progress
            }

            override fun onComplete() {
                pageLoadView.hide()
                progressBar.visibility = View.GONE
            }

            override fun onError() {
                pageLoadView.error({
                    webView?.loadUrl(url)
                })
                progressBar.visibility = View.GONE
            }

            override fun receivedTitle(title: String?) {
                text_title.text = title
            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()
        web_parent.removeAllViews()
        webView?.destroy()
        webView = null
    }
}