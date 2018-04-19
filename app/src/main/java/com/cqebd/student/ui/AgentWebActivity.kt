package com.cqebd.student.ui

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.just.agentweb.AgentWeb
import kotlinx.android.synthetic.main.activity_agent_web.*
import android.text.TextUtils



class AgentWebActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_agent_web)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }

        val url = intent.getStringExtra("url")

        AgentWeb.with(this)
                .setAgentWebParent(container,FrameLayout.LayoutParams(-1,-1))
                .useDefaultIndicator()
                .setWebChromeClient(
                        object : WebChromeClient() {

                            override fun onReceivedTitle(view: WebView, title: String?) {
                                super.onReceivedTitle(view, title)
                                if (title != null) {
                                    toolbar_title.text = title
                                }
                            }
                        }
                )
                // 兼容低版本
                .setWebViewClient(object : WebViewClient(){
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.let {
                            val title = it.title
                            if (!TextUtils.isEmpty(title)) {
                                toolbar_title.text = title
                            }
                        }

                    }
                })
                .createAgentWeb()
                .ready()
                .go(url)
    }
}
