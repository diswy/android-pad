package com.cqebd.student.ui.fragment


import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import android.widget.ZoomButtonsController

import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.js.VideoJs
import gorden.util.XLog
import kotlinx.android.synthetic.main.fragment_web.*


class WebFragment : BaseFragment() {
    private val TAG = WebFragment::class.java.name

    private val mWebView: WebView by lazy { webView }
    private val mProgressBar: ProgressBar by lazy { progressBar }
    private var isError = false

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_web, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        initWebView()

        if (arguments != null) {
            val url = arguments?.getString("url")
            previewTask(url)
        }
    }

    private fun previewTask(url: String?) {
        if (TextUtils.isEmpty(url)) {
            throw IllegalArgumentException("H5地址不能为空")
        }
        XLog.i(TAG,url)
        mWebView.loadUrl(url)
    }

    private fun initWebView() {
        mProgressBar.max = 100
        mWebView.canGoBack()
//        web_error_layout.setOnClickListener(this)
        val settings = mWebView.settings
        settings.setSupportZoom(true) // 支持缩放
        settings.builtInZoomControls = true // 启用内置缩放装置
        settings.javaScriptEnabled = true // 启用JS脚本
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true// 设置概览模式
        settings.allowFileAccess = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.defaultTextEncodingName = "UTF-8"
        //最重要的方法，一定要设置，这就是出不来的主要原因
        settings.domStorageEnabled = true
        webView?.addJavascriptInterface(VideoJs(activity), "video")
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型：
         * 1、LayoutAlgorithm.NARROW_COLUMNS ：适应内容大小
         * 2、LayoutAlgorithm.SINGLE_COLUMN : 适应屏幕，内容将自动缩放
         */
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        setZoomControlGone(mWebView)

        //target 23 default false, so manual set true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true)
        }

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // TODO: 2016/6/1 处理h5与native交互
                view?.loadUrl(url)
                return true
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
//                showErrorPage()
            }
        }

        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    mProgressBar.visibility = View.GONE
                    if (!isError) {
                        mWebView.visibility = View.VISIBLE
                    }
                } else {
                    if (mProgressBar.visibility != View.VISIBLE) {
                        mProgressBar.visibility = View.VISIBLE
                    }
                    mProgressBar.progress = newProgress
                }
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                callback?.invoke(origin, true, false)
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }
        }
    }

    private fun setZoomControlGone(mWebView: WebView) {
        try {
            val classType = WebView::class.java
            val field = classType.getDeclaredField("mZoomButtonsController")
            field.isAccessible = true
            val mZoomButtonsController = ZoomButtonsController(view)
            mZoomButtonsController.zoomControls.visibility = View.GONE
            try {
                field.set(view, mZoomButtonsController)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }


}