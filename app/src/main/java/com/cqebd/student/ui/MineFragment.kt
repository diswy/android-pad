package com.cqebd.student.ui

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.app.BaseFragment
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.live.ui.VideoLiveActivity
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.test.TestChatRoomActivity
import com.cqebd.student.test.TestNetEaseActivity
import com.cqebd.student.tools.*
import com.cqebd.student.viewmodel.MineViewModel
import com.cqebd.student.vo.entity.UserAccount
import com.cqebd.student.widget.LoadingDialog
import com.cqebd.teacher.vo.Status
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import gorden.lib.anko.static.startActivity
import gorden.library.Album
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
            }
        })

        mineViewModel.refreshFlowers()
    }


    override fun bindEvents() {
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
            startActivity<AboutActivity>()
        }

        item_my_share.setOnClickListener {
            startActivity<BeSharedActivity>()
        }

        item_settings.setOnClickListener {
            startActivity<SettingActivity>()
        }

        item_exit.setOnClickListener {
            activity?.finish()
            UserAccount.clear()
            logoutNetease()
            startActivity<LoginActivity>()
        }

        btn_test_jump.setOnClickListener {
            startActivity<TestNetEaseActivity>()
//            startActivity<TestTablayout>()
        }
        btn_test_chat.setOnClickListener {
            TestChatRoomActivity.start(activity,"25154773",false)
        }
        btn_rts.setOnClickListener {
            startActivity<VideoLiveActivity>()
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
//                    this.setCircleDimmedLayer(true)
//                    this.setShowCropFrame(false)
//                    this.setShowCropGrid(false)
//                    this.setFreeStyleCropEnabled(true)
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
}