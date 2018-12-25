package com.cqebd.student.ui.mine


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.tools.loginId
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.AgentWebActivity
import com.cqebd.student.viewmodel.MineViewModel
import com.cqebd.student.vo.entity.UserAccount
import com.xiaofu.lib_base_xiaofu.fancy.FancyDialogFragment
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import gorden.lib.anko.static.startActivity
import gorden.util.RxCounter
import gorden.widget.selector.SelectorButton
import kotlinx.android.synthetic.main.dialog_modify_pwd.view.*
import kotlinx.android.synthetic.main.fragment_pad_mine.*


/**
 * A simple [Fragment] subclass.
 *
 */
class PadMineFragment : BaseFragment() {
    private lateinit var mineViewModel: MineViewModel

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_pad_mine, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        mineViewModel = ViewModelProviders.of(this).get(MineViewModel::class.java)
        mineViewModel.userAccount.observe(this, Observer {
            it?.apply {
                stu_name.text = "姓名:".plus(Name)
                stu_phone.text = "电话:".plus(if (TextUtils.isEmpty(it.Phone)) "未绑定" else it.Phone)
                little_flower.text = "小红花\n".plus((Flower / 25).toString())
                big_flower.text = "大红花\n".plus((Flower % 25 / 5).toString())
                medal.text = "奖章\n".plus((Flower % 5).toString())
                diploma.text = "奖状\n".plus(Medal)
                GlideApp.with(App.mContext).asBitmap().circleCrop().load(Avatar).placeholder(R.drawable.ic_avatar).into(head_portrait)
            }
        })
        initDialogPhone()
    }

    override fun bindEvents() {
        stu_change_phone.setOnClickListener {
            if (stu_change_phone.text.toString() == "手机：未绑定") {
                bindPhone()
            } else {
                editPhone()
            }
        }
        change_pwd.setOnClickListener {
            FancyDialogFragment.create().setCanCancelOutside(true)
                    .setLayoutRes(R.layout.dialog_modify_pwd)
                    .setWidth(600)
                    .setViewListener { dialog, v ->
                        v.apply {
                            mBtnCommit.setOnClickListener {
                                modifyPwd(mOldPwd, mNewPwd, mNewPwd2, dialog)
                            }
                        }
                    }
                    .show(activity?.fragmentManager, "")
        }

        dianzan.setOnClickListener {
            val wonderFormat = "Report/ReportAppraisal?ID=$loginId"
            val url = WorkService.BASE_WEB_URL.plus(wonderFormat)
            startActivity<AgentWebActivity>("url" to url)
        }

        honghua.setOnClickListener {
            val flowerFormat = "Report/ReportFlower?ID=$loginId"
            val url = WorkService.BASE_WEB_URL.plus(flowerFormat)
            startActivity<AgentWebActivity>("url" to url)
        }
    }

    private lateinit var dialogPhone: AlertDialog
    private lateinit var editPhone: EditText
    private lateinit var editVerify: EditText
    private lateinit var editPwd: EditText
    private lateinit var btnVerify: SelectorButton

    private fun bindPhone() {
        dialogPhone.setTitle("绑定手机号")
        editPwd.visibility = View.GONE
        dialogPhone.show()
        dialogPhone.getButton(AlertDialog.BUTTON_POSITIVE).text = "绑定"
        dialogPhone.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (updatePhone(0))
                dialogPhone.dismiss()
        }
    }

    private fun editPhone() {
        dialogPhone.setTitle("修改手机号")
        editPwd.visibility = View.VISIBLE
        dialogPhone.show()
        dialogPhone.getButton(AlertDialog.BUTTON_POSITIVE).text = "修改"
        dialogPhone.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (updatePhone(1))
                dialogPhone.dismiss()
        }
    }


    private fun initDialogPhone() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_bindphone, null)
        dialogPhone = AlertDialog.Builder(context!!)
                .setTitle("绑定手机号").setView(dialogView)
                .setPositiveButton("绑定", null)
                .setNegativeButton("取消", null)
                .create()
        editPhone = dialogView.findViewById(R.id.edit_phone)
        editVerify = dialogView.findViewById(R.id.edit_verify)
        editPwd = dialogView.findViewById(R.id.edit_pwd)
        btnVerify = dialogView.findViewById(R.id.btn_verify)
        btnVerify.setOnClickListener {
            if (TextUtils.isEmpty(editPhone.text.toString()) || editPhone.text.toString().length < 11) {
                toast("请输入正确的手机号码")
            } else {
                getVerifyCode(editPhone.text.toString())
            }
        }
    }

    private fun updatePhone(status: Int): Boolean {
        val code = editVerify.text.toString()
        val phone = editPhone.text.toString()
        val pwd = editPwd.text.toString()

        if (TextUtils.isEmpty(phone) || phone.length < 11) {
            toast("请输入正确的手机号码")
            return false
        }

        if (TextUtils.isEmpty(code)) {
            toast("请输入手机验证码")
            return false
        }

        if (status == 1 && TextUtils.isEmpty(pwd)) {
            toast("请输入用户密码")
            return false
        }

        NetClient.workService().updatePhCode(status, code, phone, pwd)
                .enqueue(object : NetCallBack<BaseResponse<Unit>>() {
                    override fun onSucceed(response: BaseResponse<Unit>?) {
                        response?.let {
                            if (it.isSuccess) {
                                val mAccount = UserAccount.load()
                                mAccount?.let {
                                    mAccount.Phone = phone
                                    it.save()
                                }
                                toast(if (status == 0) "绑定成功" else "修改成功")
                                editPwd.text = null
                                editPhone.text = null
                                editVerify.text = null
                                stu_phone.text = phone
                            }
                        }
                    }

                    override fun onFailure() {

                    }
                })
        return true
    }

    /**
     * 获取验证码
     *
     * @param phone 手机号码
     */
    private fun getVerifyCode(phone: String) {
        btnVerify.isEnabled = false
        RxCounter.tick(59)
                .doOnSubscribe { subscription ->
                    NetClient.workService()
                            .getTelCode(phone, 1)
                            .enqueue(object : NetCallBack<BaseResponse<Unit>>() {
                                override fun onSucceed(response: BaseResponse<Unit>?) {
                                    response?.let {
                                        toast(it.message)
                                        subscription.cancel()
                                        btnVerify.isEnabled = true
                                        btnVerify.text = "获取验证码"
                                    }
                                }

                                override fun onFailure() {
                                    subscription.cancel()
                                    btnVerify.isEnabled = true
                                    btnVerify.text = "获取验证码"
                                }

                            })
                }
                .doOnNext { time -> btnVerify.text = String.format("%s s", time) }
                .doOnComplete {
                    btnVerify.isEnabled = true
                    btnVerify.text = "获取验证码"
                }
                .subscribe()
    }

    private fun modifyPwd(oldText: TextView, newText: TextView, new2Text: TextView, dialogFragment: FancyDialogFragment) {
        if (TextUtils.isEmpty(oldText.text.toString())) {
            toast("请输入旧密码")
            return
        }
        if (TextUtils.isEmpty(newText.text.toString())) {
            toast("新密码不能为空")
            return
        }
        if (TextUtils.isEmpty(new2Text.text.toString())) {
            toast("请再次确认密码")
            return
        }
        if (new2Text.text.toString() != newText.text.toString()) {
            toast("两次密码不一致")
            return
        }
        NetClient.videoService()
                .modifyPwd(oldText.text.toString(), newText.text.toString())
                .enqueue(object : NetCallBack<BaseResponse<Unit>>() {
                    override fun onSucceed(response: BaseResponse<Unit>?) {
                        response?.let {
                            toast(it.message)
                            if (it.isSuccess)
                                dialogFragment.dismiss()
                        }
                    }

                    override fun onFailure() {
                    }
                })
    }

}
