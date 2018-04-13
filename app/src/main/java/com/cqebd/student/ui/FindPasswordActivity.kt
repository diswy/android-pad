package com.cqebd.student.ui

import android.text.TextUtils
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.toast
import com.cqebd.student.vo.entity.BaseBean
import gorden.behavior.LoadingDialog
import gorden.util.RxCounter
import kotlinx.android.synthetic.main.activity_find_password.*

class FindPasswordActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_find_password)
    }

    override fun bindEvents() {
        toolbar.setNavigationOnClickListener { finish() }

        btn_verify.setOnClickListener {
            val userName = edit_username.text.toString()
            if (TextUtils.isEmpty(userName)) {
                toast("请输入用户名")
            } else {
                getVerifyCode(userName)
            }
        }

        btn_reset.setOnClickListener {
            val userName = edit_username.text.toString()
            if (check() && !TextUtils.isEmpty(userName)) {
                val pwd = edit_new_pwd.text.toString().trim()
                val code = edit_verify.text.toString().trim()
                LoadingDialog.show(this)
                NetClient.workService().updatePwd(userName, pwd, code)
                        .enqueue(object : NetCallBack<BaseResponse<Unit>>() {
                            override fun onSucceed(response: BaseResponse<Unit>) {
                                if (response.isSuccess) {
                                    toast("密码修改成功")
                                    finish()
                                } else {
                                    toast(response.message)
                                }
                            }

                            override fun onFailure() {

                            }
                        })
            }
        }
    }

    private fun check(): Boolean {
        if (TextUtils.isEmpty(edit_verify.text.toString().trim())) {
            toast("请输入验证码")
            return false
        }
        if (TextUtils.isEmpty(edit_new_pwd.text.toString().trim())) {
            toast("新密码不能为空")
            return false
        }
        if (edit_new_pwd.text.length < 6) {
            toast("请输入6位数以上的密码")
            return false
        }
        if (TextUtils.isEmpty(edit_confirm_pwd.text.toString().trim())) {
            toast("确认密码不能为空")
            return false
        }
        if (!TextUtils.equals(edit_new_pwd.text.toString(), edit_confirm_pwd.text.toString())) {
            toast("新密码与确认密码不一致")
            return false
        }
        return true
    }

    /**
     * 获取验证码
     *
     * @param userName 用户名
     */
    private fun getVerifyCode(userName: String) {
        btn_verify.isEnabled = false
        RxCounter.tick(59)
                .doOnSubscribe { subscription ->
                    NetClient.workService().getPhoneCode(userName, 0)
                            .enqueue(object : NetCallBack<BaseResponse<Unit>>() {
                                override fun onSucceed(response: BaseResponse<Unit>) {
                                    if (!response.isSuccess) {
                                        toast(response.message)
                                        subscription.cancel()
                                        btn_verify.isEnabled = true
                                        btn_verify.text = "获取验证码"
                                    }

                                }

                                override fun onFailure() {
                                    subscription.cancel()
                                    btn_verify.isEnabled = true
                                    btn_verify.text = "获取验证码"
                                }
                            })
                }
                .doOnNext { time -> btn_verify.text = String.format("%s s", time) }
                .doOnComplete {
                    btn_verify.isEnabled = true
                    btn_verify.text = "获取验证码"
                }
                .subscribe()
    }
}
