package com.cqebd.student.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.viewmodel.JobPreviewViewModel
import com.cqebd.student.vo.entity.WorkInfo
import com.cqebd.student.widget.WebView
import com.just.agentweb.AbsAgentWebSettings
import com.just.agentweb.AgentWeb
import com.just.agentweb.IAgentWebSettings
import kotlinx.android.synthetic.main.activity_job_preview.*

/**
 * 描述
 * Created by gorden on 2018/3/13.
 */
class JobPreviewActivity : BaseActivity() {
    //    private var webView: WebView?=null
    private lateinit var workInfo: WorkInfo
    private lateinit var viewModel: JobPreviewViewModel
    override fun setContentView() {
        setContentView(R.layout.activity_job_preview)
//        webView = WebView(App.mContext)
//        web_parent.addView(webView)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        val url = intent.getStringExtra("url")
        workInfo = intent.getParcelableExtra("info")
        viewModel = ViewModelProviders.of(this, JobPreviewViewModel.Factory(workInfo)).get(JobPreviewViewModel::class.java)

        val mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(web_parent, FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setAgentWebWebSettings(getSettings())
                .setWebChromeClient(
                        object : WebChromeClient() {

                            override fun onReceivedTitle(view: android.webkit.WebView, title: String?) {
                                super.onReceivedTitle(view, title)
                                if (title != null) {
                                    text_title.text = title
                                }
                            }
                        }
                )
                // 兼容低版本
                .setWebViewClient(object : WebViewClient() {
                    override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.let {
                            val title = it.title
                            if (!TextUtils.isEmpty(title)) {
                                text_title.text = title
                            }
                        }
                    }
                })
                .createAgentWeb()
                .ready()
                .go(url)


//        webView?.load(url,object :WebView.LoadCallback{
//            override fun onLoad() {
//                pageLoadView.load()
//            }
//
//            override fun onProgress(progress: Int) {
//                progressBar.visibility = View.VISIBLE
//                progressBar.progress = progress
//            }
//
//            override fun onComplete() {
//                pageLoadView.hide()
//                progressBar.visibility = View.GONE
//            }
//
//            override fun onError() {
//                pageLoadView.error({
//                    webView?.loadUrl(url)
//                })
//                progressBar.visibility = View.GONE
//            }
//
//            override fun receivedTitle(title: String?) {
//                text_title.text = title
//            }
//
//        })
    }

    override fun bindEvents() {
        btn_start.setOnClickListener {
            viewModel.startAnswer(this)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        web_parent.removeAllViews()
//        webView?.destroy()
//        webView = null
    }


    private fun getSettings(): IAgentWebSettings<*> {
        return object : AbsAgentWebSettings() {
            override fun toSetting(webView: android.webkit.WebView?): IAgentWebSettings<*> {
                super.toSetting(webView)
                webView?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                return this
            }

            override fun bindAgentWebSupport(agentWeb: AgentWeb?) {

            }

        }
    }
}