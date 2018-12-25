package com.cqebd.student.ui.mine


import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.cqebd.student.R
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.loginId
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.AgentWebActivity
import com.cqebd.student.vo.entity.UserAccount
import gorden.lib.anko.static.startActivity
import gorden.util.PackageUtils
import kotlinx.android.synthetic.main.fragment_pad_callback.*


/**
 * A simple [Fragment] subclass.
 *
 */
class PadCallbackFragment : BaseFragment() {

    private val url = "https://service-student.cqebd.cn/Help/Feedback?id=%s"

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_pad_callback, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {

    }

    override fun bindEvents() {
        text_feedback_see.setOnClickListener {
            startActivity<AgentWebActivity>("url" to String.format(url, loginId))
        }
        opinion_submit.setOnClickListener {
            if (check()) {
                var type = ""
                when (rg.checkedRadioButtonId) {
                    R.id.rb1 -> type = "问题"
                    R.id.rb2 -> type = "建议"
                    R.id.rb3 -> type = "其他"
                }
                submit(edit_content.text.toString().trim(), type)
            }
        }
    }

    private fun check(): Boolean {
        if (TextUtils.isEmpty(edit_content.text.toString().trim { it <= ' ' })) {
            toast("请输入反馈内容")
            return false
        }
        return true
    }

    private fun submit(content: String, type: String) {
        val user = UserAccount.load()
        val sourceType = PackageUtils.getVersionName(activity) + "|" + Build.MODEL

        user?.let {
            NetClient.workService().submitFeedBk(loginId, it.Name, "", content, type, 0, sourceType)
                    .enqueue(object : NetCallBack<BaseResponse<Unit>>() {
                        override fun onSucceed(response: BaseResponse<Unit>?) {
                            response?.let { response ->
                                toast(response.message)
                            }
                        }

                        override fun onFailure() {

                        }
                    })
        }
    }
}
