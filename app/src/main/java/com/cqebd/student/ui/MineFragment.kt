package com.cqebd.student.ui

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.tools.*
import com.cqebd.student.utils.DateUtils
import com.cqebd.student.viewmodel.MineViewModel
import com.cqebd.student.vo.entity.UserAccount
import com.cqebd.student.widget.LoadingDialog
import com.cqebd.teacher.vo.Status
import com.wuhangjia.firstlib.view.FancyDialogFragment
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import gorden.lib.anko.static.startActivity
import gorden.library.Album
import gorden.util.PackageUtils.getPackageName
import gorden.util.RxCounter
import gorden.widget.selector.SelectorButton
import kotlinx.android.synthetic.main.dialog_clear_cache.view.*
import kotlinx.android.synthetic.main.fragment_mine.*
import java.io.File

/**
 * 描述
 * Created by gorden on 2018/2/27.
 */
class MineFragment : BaseFragment() {
    private val loadingDialog by lazy { LoadingDialog() }
    private lateinit var mineViewModel: MineViewModel
    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_mine, container, false)
    }


    override fun initialize(savedInstanceState: Bundle?) {
        mineViewModel = ViewModelProviders.of(this).get(MineViewModel::class.java)
        initDialogPhone()
        mineViewModel.userAccount.observe(this, Observer {
            it?.apply {
                text_name.text = Name
                if (Gender == "女") {
                    val mSexRes = resources.getDrawable(R.drawable.ic_sex_woman)
                    mSexRes.setBounds(0, 0, mSexRes.minimumWidth, mSexRes.minimumHeight)
                    text_name.setCompoundDrawables(null, null, mSexRes, null)
                } else {
                    val mSexRes = resources.getDrawable(R.drawable.ic_sex_man)
                    mSexRes.setBounds(0, 0, mSexRes.minimumWidth, mSexRes.minimumHeight)
                    text_name.setCompoundDrawables(null, null, mSexRes, null)
                }
                text_flower1.text = (Flower / 25).toString()
                text_flower2.text = (Flower % 25 / 5).toString()
                text_flower3.text = (Flower % 5).toString()
                GlideApp.with(App.mContext).asBitmap().circleCrop().load(Avatar).placeholder(R.drawable.ic_avatar).into(img_avatar)
                item_leader.visibility = if (it.IsGroup) View.VISIBLE else View.GONE
                mBindPhone.text = if (TextUtils.isEmpty(it.Phone)) "手机：未绑定" else "手机：${it.Phone}"
            }
        })

        mineViewModel.refreshFlowers()
    }


    override fun bindEvents() {
        mBtnMsg.setOnClickListener {
            startActivity<MessageActivity>()
        }

        item_flower_list.setOnClickListener {
            val flowerFormat = "Report/ReportFlower?ID=$loginId"
            val url = WorkService.BASE_WEB_URL.plus(flowerFormat)
            startActivity<AgentWebActivity>("url" to url)
        }

        item_like_list.setOnClickListener {
            val wonderFormat = "Report/ReportAppraisal?ID=$loginId"
            val url = WorkService.BASE_WEB_URL.plus(wonderFormat)
            startActivity<AgentWebActivity>("url" to url)
        }

        img_avatar.setOnClickListener {
            Album.create().single().start(this)
        }
        item_about.setOnClickListener {
            //            startActivity<AboutActivity>()
//            startActivity<CallbackActivity>()
        }

        item_my_share.setOnClickListener {
            startActivity<BeSharedActivity>()
        }

        item_leader.setOnClickListener {
            startActivity<AgentWebActivity>("url" to "https://service-student.cqebd.cn/StudentGroup/task?GroupStudentId=$loginId")
        }

        item_modify_pwd.setOnClickListener {
            startActivity<ModifyPwdActivity>()
        }

        item_send_back.setOnClickListener {
            startActivity<CallbackActivity>()
        }

        item_exit.setOnClickListener {
            activity?.finish()
            UserAccount.clear()
            logoutNetease()
            startActivity<LoginActivity>()
        }

        item_clear_cache.setOnClickListener {
            showClearDialog()
        }

        mBindPhone.setOnClickListener {
            if (mBindPhone.text.toString() == "手机：未绑定") {
                bindPhone()
            } else {
                editPhone()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Album.REQUEST_CODE -> {
                    val pathList = data!!.getStringArrayListExtra(Album.KEY_IMAGES)
                    crop(pathList[0])
                }
                UCrop.REQUEST_CROP -> {
                    val uri = UCrop.getOutput(data!!)
                    updateAvatar(uri)
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            toastError("图片处理失败")
        }
    }

    private fun crop(path: String) {
        UCrop.of(Uri.fromFile(File(path)), cropPath)
                .withAspectRatio(1f, 1f)
                .withOptions(UCrop.Options().apply {
                    this.setStatusBarColor(colorForRes(R.color.colorPrimary))
                    this.setToolbarColor(colorForRes(R.color.colorPrimary))
                    this.setHideBottomControls(true)
                    this.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
                    this.setCompressionFormat(android.graphics.Bitmap.CompressFormat.JPEG)
                })
                .withMaxResultSize(80.dp, 80.dp)
                .start(App.mContext, this)
    }


    private fun updateAvatar(uri: Uri?) {
        mineViewModel.uploadAvatar(uri).observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    loadingDialog.dismiss()
                    mineViewModel.userAccount.value?.Avatar = it.data!!
                    mineViewModel.refreshUserAccount()
                }
                Status.ERROR -> {
                    loadingDialog.dismiss()
                    AlertDialog.Builder(activity!!).setMessage("头像上传失败,是否重新上传")
                            .setPositiveButton("上传", { _, _ ->
                                updateAvatar(uri)
                            })
                            .setNegativeButton("取消", null)
                            .show()
                }
                Status.LOADING -> loadingDialog.show(fragmentManager)
            }
        })
    }


    private lateinit var dialogPhone: AlertDialog
    private lateinit var editPhone: EditText
    private lateinit var editVerify: EditText
    private lateinit var editPwd: EditText
    private lateinit var btnVerify: SelectorButton


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
                                mBindPhone.text = phone
                            }
                        }
                    }

                    override fun onFailure() {

                    }
                })
        return true
    }


    private fun showClearDialog() {
        val dialog = FancyDialogFragment.create()
        dialog.setCanCancelOutside(false)
                .setLayoutRes(R.layout.dialog_clear_cache)
                .setWidth(activity, 230)
                .setViewListener {
                    it.apply {
                        mBtnConfirm.setOnClickListener {
                            try {
                                context?.let {
                                    val cache = File("data/data/"+getPackageName(it))
                                    DateUtils.DeleteFile(cache)
                                    dialog.dismiss()
                                    activity?.finish()
                                    System.exit(0)
                                }
                            } catch (e: Exception) {
                                toast("清理异常error:${e.message}")
                            }

                        }
                        mBtnCancel.setOnClickListener {
                            dialog.dismiss()
                        }
                    }
                }
                .show(activity?.fragmentManager, "")
    }

}