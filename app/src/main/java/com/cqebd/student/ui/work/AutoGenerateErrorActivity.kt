package com.cqebd.student.ui.work

import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.event.STATUS_WRONG
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.tools.loginId
import com.cqebd.student.tools.toast
import com.cqebd.student.vo.entity.WrongQuestionTask
import com.just.agentweb.AgentWeb
import gorden.rxbus.RxBus
import gorden.rxbus.Subscribe
import kotlinx.android.synthetic.main.activity_auto_generate_error.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutoGenerateErrorActivity : BaseActivity() {


    private var subjectId = -1
    override fun setContentView() {
        setContentView(R.layout.activity_auto_generate_error)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        toolbar.setNavigationOnClickListener { finish() }
        subjectId = intent.getIntExtra("subjectId", -1)
        val url = WorkService.BASE_WEB_URL.plus("HomeWork/AnswerError?StudentId=$loginId&SubjectTypeId=$subjectId")
        loadWeb(url)
    }

    override fun bindEvents() {
        btnStart.setOnClickListener {
            alert("生成后可在作业中重新练习，取消可返回上层界面重新生成","确定要生成此套错题么?") {
                yesButton {
                    getRetryList(subjectId)
                }
                noButton {  }
            }.show()
        }
    }

    private fun loadWeb(url: String) {
        AgentWeb.with(this)
                .setAgentWebParent(container, FrameLayout.LayoutParams(-1, -1))
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
                .setWebViewClient(object : WebViewClient() {
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
                .clearWebCache()
    }

    private fun getRetryList(id: Int) {
        NetClient.workService()
                .retryWrongQuestion(id)
                .enqueue(object : Callback<BaseResponse<WrongQuestionTask>> {
                    override fun onFailure(call: Call<BaseResponse<WrongQuestionTask>>, t: Throwable) {
                    }

                    override fun onResponse(call: Call<BaseResponse<WrongQuestionTask>>, response: Response<BaseResponse<WrongQuestionTask>>) {
                        response.body()?.let {
                            toast(it.message)
                            if (it.isSuccess){
                                RxBus.get().send(STATUS_WRONG)
                                finish()
                            }
                        }
                    }
                })
    }

}
