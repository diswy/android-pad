package com.cqebd.student.ui

import android.arch.lifecycle.Observer
import android.os.Bundle
import com.cqebd.student.MainActivity
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.savePassword
import com.cqebd.student.tools.toastError
import com.cqebd.student.tools.versionName
import com.cqebd.student.widget.LoadingDialog
import gorden.lib.anko.static.startActivity
import kotlinx.android.synthetic.main.activity_login.*

/**
 * TODO 需要完善登录
 * Created by gorden on 2018/3/20.
 */
class LoginActivity : BaseActivity() {
    private val loadingDialog by lazy { LoadingDialog() }
    override fun setContentView() {
        setContentView(R.layout.activity_login)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        text_version.text = "V".plus(versionName)
        edit_username.setText("xsebd01")
        edit_pwd.setText("123456")
    }

    override fun bindEvents() {
        btn_login.setOnClickListener {
            if (edit_username.text.isNullOrEmpty()) {
                toastError("请输入用户名")
                return@setOnClickListener
            }
            if (edit_pwd.text.isNullOrEmpty()) {
                toastError("请输入用户密码")
                return@setOnClickListener
            }

            loadingDialog.progressMsg = "正在登录..."
            loadingDialog.show(supportFragmentManager)
            NetClient.workService().accountLogin(edit_username.text.toString(), edit_pwd.text.toString())
                    .observe(this, Observer {
                        loadingDialog.dismiss()
                        if (it?.isSuccessful() == true) {
                            it.body?.save()
                            savePassword(edit_pwd.text.toString())
                            startActivity<MainActivity>()
                            finish()
                        } else {
                            toastError(it?.errorMessage ?: "登录失败")
                        }
                    })
        }

        text_find_pwd.setOnClickListener {
            startActivity<FindPasswordActivity>()
        }

        text_find_account.setOnClickListener {
            startActivity<WebActivity>("url" to "http://student.cqebd.cn/Account/FindLoginName")
        }
    }
}