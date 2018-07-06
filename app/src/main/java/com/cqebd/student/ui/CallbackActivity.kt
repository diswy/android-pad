package com.cqebd.student.ui

import android.os.Build
import android.text.TextUtils
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.loginId
import com.cqebd.student.tools.toast
import com.cqebd.student.vo.entity.UserAccount
import gorden.lib.anko.static.startActivity
import gorden.util.PackageUtils
import kotlinx.android.synthetic.main.activity_callback.*

class CallbackActivity : BaseActivity() {
    private val url = "https://service-student.cqebd.cn/Help/Feedback?id=%s"

    override fun setContentView() {
        setContentView(R.layout.activity_callback)
    }

    override fun bindEvents() {
        toolbar.setNavigationOnClickListener { finish() }

        text_feedback_see.setOnClickListener {
            startActivity<AgentWebActivity>("url" to String.format(url, loginId))
        }

        btn_confirm.setOnClickListener {
            if (check()) {
                var type = ""
                when (radio_group.checkedRadioButtonId) {
                    R.id.type_question -> type = "问题"
                    R.id.type_advise -> type = "建议"
                    R.id.type_other -> type = "其他"
                }
                submit(edit_content.text.toString().trim(), type)
            }
        }
    }

    private fun check(): Boolean {
        if (TextUtils.isEmpty(edit_content.text.toString().trim({ it <= ' ' }))) {
            toast("请输入反馈内容")
            return false
        }
        return true
    }

    private fun submit(content: String, type: String) {
        val user = UserAccount.load()
        val sourceType = PackageUtils.getVersionName(this) + "|" + Build.MODEL

        user?.let {
            NetClient.workService().submitFeedBk(loginId, it.Name, "", content, type, 0, sourceType)
                    .enqueue(object : NetCallBack<BaseResponse<Unit>>() {
                        override fun onSucceed(response: BaseResponse<Unit>?) {
                            response?.let {
                                toast(it.message)
                                finish()
                            }
                        }

                        override fun onFailure() {

                        }
                    })
        }


    }

}
