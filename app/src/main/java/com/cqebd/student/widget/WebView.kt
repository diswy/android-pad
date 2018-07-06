package com.cqebd.student.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.webkit.*
import android.webkit.WebView
import com.cqebd.student.MainActivity
import com.cqebd.student.app.App
import gorden.lib.anko.static.startActivity

/**
 * 描述
 * Created by gorden on 2018/3/8.
 */
@SuppressLint("SetJavaScriptEnabled")
class WebView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : WebView(context, attrs, defStyleAttr) {
    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)

    init {
        overScrollMode = View.OVER_SCROLL_NEVER
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true

        webViewClient = object :WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    loadUrl(request?.url?.toString())
                    return true
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                loadUrl(url)
                return true
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                loadCallback?.onError()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadCallback?.onComplete()
            }
        }


        webChromeClient = object :WebChromeClient(){
            override fun onProgressChanged(view: WebView?, progress: Int) {
                if (progress==100){
//                    loadCallback?.onComplete()
                }else{
                    loadCallback?.onProgress(progress)
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                loadCallback?.receivedTitle(title)
            }
        }
    }

    private var loadCallback:LoadCallback?=null
    interface LoadCallback{
        fun onLoad()
        fun onProgress(progress:Int)
        fun onComplete()
        fun onError()
        fun receivedTitle(title:String?)
    }

    fun load(url:String,callback: LoadCallback){
        this.loadCallback = callback
        loadCallback?.onLoad()
        loadUrl(url)
    }

    class VideoJS{
        fun play(url:String){
            App.mContext.startActivity<MainActivity>("url" to url)
        }
    }
}

