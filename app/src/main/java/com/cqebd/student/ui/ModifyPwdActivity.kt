package com.cqebd.student.ui

import android.os.Bundle
import android.text.TextUtils
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.NetClient
import com.cqebd.student.vo.entity.BaseBean
import kotlinx.android.synthetic.main.activity_modify_pwd.*
import org.jetbrains.anko.toast

class ModifyPwdActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_modify_pwd)
    }

    override fun initialize(savedInstanceState: Bundle?) {

    }

    override fun bindEvents() {
        toolbar.setNavigationOnClickListener { this.finish() }
        mBtnCommit.setOnClickListener {
            if (check()){
                NetClient.videoService()
                        .modifyPwd(mOldPwd.text.toString(), mNewPwd.text.toString())
                        .enqueue(object : NetCallBack<BaseBean>() {
                            override fun onSucceed(response: BaseBean?) {
                                response?.let {
                                    toast(it.message)
                                    if (it.isSuccess)
                                        finish()
                                }
                            }

                            override fun onFailure() {
                            }
                        })
            }
        }
    }

    private fun check(): Boolean {
        if (TextUtils.isEmpty(mOldPwd.text.toString())){
            toast("请输入旧密码")
            return false
        }
        if (TextUtils.isEmpty(mNewPwd.text.toString())){
            toast("新密码不能为空")
            return false
        }
        if (TextUtils.isEmpty(mNewPwd2.text.toString())){
            toast("请再次确认密码")
            return false
        }
        if (mNewPwd2.text.toString() != mNewPwd.text.toString()){
            toast("两次密码不一致")
            return false
        }
        return true
    }

}
