package com.cqebd.student.ui

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.js.VideoJs
import com.cqebd.student.vo.entity.UserAccount
import com.cqebd.student.widget.WebView
import com.orhanobut.logger.Logger
import com.wuhangjia.firstlib.view.FancyDialogFragment
import kotlinx.android.synthetic.main.activity_webview.*

/**
 * 描述
 * Created by gorden on 2018/3/8.
 */
class WebActivity : BaseActivity() {
    private var webView: WebView? = null
    override fun setContentView() {
        setContentView(R.layout.activity_webview)
        webView = WebView(App.mContext)
        web_parent.addView(webView)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        initWebView()
        val url = intent.getStringExtra("url")
        val title = intent.getStringExtra("title")
        text_title.text = title
        Logger.d(url)
        webView?.load(url, object : WebView.LoadCallback {
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
                // 显示奖状
                showMedal(intent.getBooleanExtra("medal", false))
            }

            override fun onError() {
                pageLoadView.error({
                    webView?.loadUrl(url)
                })
                progressBar.visibility = View.GONE
            }

            override fun receivedTitle(title: String?) {
            }

        })
    }

    private fun initWebView() {
        val setting = webView?.settings
        setting?.let {
            setting.javaScriptEnabled = true// 启用JS脚本
        }
        webView?.addJavascriptInterface(VideoJs(this), "video")

    }


    override fun onDestroy() {
        web_parent.removeAllViews()
        webView?.destroy()
        webView = null
        super.onDestroy()
    }

    private fun showMedal(isShowMedal: Boolean) {
        if (isShowMedal) {
            val resources = this.resources
            val dm = resources.displayMetrics
            val width = dm.widthPixels
            FancyDialogFragment.create()
                    .setLayoutRes(R.layout.medal_view)
                    .setViewListener { v ->
                        val tv = v.findViewById<TextView>(R.id.tv_content)
                        val title = text_title.text.toString()
                        val mUser = UserAccount.load()
                        var name = "同学:"
                        mUser?.let {
                            name = it.Name+"同学:"
                        }
                        val content = String.format("%s\n        在%s培优学习中，成绩优异，特发此状，以资鼓励。", name, title)
                        val builder = SpannableStringBuilder(content)
                        val nameColor = ForegroundColorSpan(Color.BLACK)
                        val contentColor = ForegroundColorSpan(-0xaeaeaf)
                        builder.setSpan(nameColor, 0, name.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        builder.setSpan(contentColor, name.length, content.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        tv.text = builder
                    }
                    .setAnimation(R.style.FadeAndScaleDialogAnimation)
                    .setCanCancelOutside(true)
                    .setWidth((width * 0.6).toInt())// 适配屏幕 90%
                    .setHeight((width.toDouble() * 0.4).toInt())// 根据图片比例适配屏幕
                    .show(fragmentManager, "medal")

        }
    }
}