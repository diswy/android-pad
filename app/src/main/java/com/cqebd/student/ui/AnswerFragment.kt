package com.cqebd.student.ui


import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.vo.entity.Attachment
import gorden.lib.anko.static.logError
import gorden.lib.anko.static.startActivityForResult
import kotlinx.android.synthetic.main.fragment_answer_content.*
import kotlinx.android.synthetic.main.online_error.*
import java.util.*

/**
 * document
 * Created by Gordn on 2017/3/17.
 */

class AnswerFragment : BaseFragment(), View.OnClickListener {
    private var isError = false
    private var url: String = ""
    private val rectParent = Rect()
    private var attachment: ArrayList<Attachment>? = null
    private var taskId: Int = 0

    private var isClick = true
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_answer_content, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        initWebView()
        this.url = arguments!!.getString("url")
        this.taskId = arguments!!.getInt("taskId")
        this.attachment = arguments!!.getParcelableArrayList("attachment")
        previewTask(url)

        if ((attachment != null && attachment!!.size > 1) || ((if (attachment != null) attachment!!.size else 0) == 1 && !attachment!![0].mediaTypeName.toLowerCase().contains("mp3"))) {
            btn_media.visibility = View.VISIBLE
            logError(attachment)
        } else {
            btn_media.visibility = View.GONE
        }
    }

    override fun bindEvents() {
        refreshLayout.setOnRefreshListener { previewTask(url) }
        btn_media.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    webView!!.getGlobalVisibleRect(rectParent)
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    lastX = event.x
                    lastY = event.y
                    isClick = true
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffX = Math.abs(event.x - lastX)
                    val diffY = Math.abs(event.y - lastY)
                    if (Math.max(diffX, diffY) > 10 || !isClick) {
                        translateMediaButton(event.rawX - view.width / 2, event.rawY - rectParent.top.toFloat() - (view.height / 2).toFloat())
                        isClick = false
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isClick) {
                        startActivityForResult<AttachmentActivity>(212, "taskId" to taskId,
                                "attachment" to attachment)
                    }
                    val animator: ValueAnimator
                    if (btn_media!!.x + btn_media!!.width / 2 < rectParent.width() / 2) {
                        animator = ValueAnimator.ofFloat(btn_media!!.x, 0f).setDuration(100)
                    } else {
                        animator = ValueAnimator.ofFloat(btn_media!!.x, (rectParent.width() - btn_media!!.width).toFloat()).setDuration(300)
                    }
                    animator.addUpdateListener { animation -> btn_media!!.x = animation.animatedValue as Float }
                    animator.start()
                }
                MotionEvent.ACTION_CANCEL -> {
                    val animator: ValueAnimator
                    if (btn_media!!.x + btn_media!!.width / 2 < rectParent.width() / 2) {
                        animator = ValueAnimator.ofFloat(btn_media!!.x, 0f).setDuration(100)
                    } else {
                        animator = ValueAnimator.ofFloat(btn_media!!.x, (rectParent.width() - btn_media!!.width).toFloat()).setDuration(300)
                    }
                    animator.addUpdateListener { animation -> btn_media!!.x = animation.animatedValue as Float }
                    animator.start()
                }
            }
            true
        }
    }

    private fun initWebView() {
        progressBar.max = 100
        webView.canGoBack()
        web_error_layout.setOnClickListener(this)
        val settings = webView.settings
        settings.setSupportZoom(true) // 支持缩放
        settings.builtInZoomControls = true // 启用内置缩放装置
        settings.javaScriptEnabled = true // 启用JS脚本
        settings.loadWithOverviewMode = true//
        settings.useWideViewPort = true// 设置概览模式
        settings.allowFileAccess = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.defaultTextEncodingName = "UTF-8"
        settings.databaseEnabled = true
        val dir = activity!!.applicationContext.getDir("database", Context.MODE_PRIVATE).path
        //启用地理定位
        settings.setGeolocationEnabled(true)
        //设置定位的数据库路径
        settings.setGeolocationDatabasePath(dir)
        //最重要的方法，一定要设置，这就是出不来的主要原因
        settings.domStorageEnabled = true
        /**
         * 用WebView显示图片，可使用这个参数 设置网页布局类型：
         * 1、LayoutAlgorithm.NARROW_COLUMNS ：适应内容大小
         * 2、LayoutAlgorithm.SINGLE_COLUMN : 适应屏幕，内容将自动缩放
         */
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        settings.displayZoomControls = false
        //target 23 default false, so manual set true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // TODO: 2016/6/1 处理h5与native交互
                view.loadUrl(url)
                return true
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                showErrorPage()
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            // 当WebView进度改变时更新窗口进度
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar?.visibility = View.GONE
                    if (!isError) {
                        webView?.let {
                            it.visibility = View.VISIBLE
                        }
                        refreshLayout?.let {
                            it.isRefreshing = false
                        }
                    }
                } else {
                    if (progressBar?.visibility != View.VISIBLE) {
                        progressBar?.visibility = View.VISIBLE
                    }
                    progressBar?.progress = newProgress
                }
            }

            override fun onGeolocationPermissionsShowPrompt(origin: String,
                                                            callback: GeolocationPermissions.Callback) {
                callback.invoke(origin, true, false)
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }


        }

    }

    /**
     * 显示自定义错误提示页面，用一个View覆盖在WebView
     */
    protected fun showErrorPage() {
        isError = true
        webView!!.visibility = View.GONE
        web_error_layout!!.visibility = View.VISIBLE
    }

    protected fun hideErrorPage() {
        isError = false
        web_error_layout!!.visibility = View.GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.web_error_layout -> {
                hideErrorPage()
                refresh()
            }
        }
    }


    private fun translateMediaButton(x: Float, y: Float) {
        var y = y
        if (y < 0) {
            y = 0f
        } else if (y > rectParent.height() - btn_media!!.height) {
            y = (rectParent.height() - btn_media!!.height).toFloat()
        }
        btn_media!!.x = x
        btn_media!!.y = y
    }

    fun refresh() {
        previewTask(url)
    }

    fun previewTask(url: String) {
        if (TextUtils.isEmpty(url)) {
            throw IllegalArgumentException("H5地址不能为空")
        }
        logError("url  $url")
        this.url = url
        webView.loadUrl(url)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 212) {
            if (activity != null && activity is AnswerActivity) {
                (activity as AnswerActivity).answerCardState()
            }
        }
    }
}
