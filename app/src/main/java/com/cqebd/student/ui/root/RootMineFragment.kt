package com.cqebd.student.ui.root


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import com.cqebd.student.app.BaseFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.tools.loginId
import com.cqebd.student.tools.logoutNetease
import com.cqebd.student.tools.toast
import com.cqebd.student.ui.AgentWebActivity
import com.cqebd.student.ui.LoginActivity
import com.cqebd.student.ui.mine.PadCallbackFragment
import com.cqebd.student.ui.mine.PadMineFragment
import com.cqebd.student.ui.mine.PadMsgFragment
import com.cqebd.student.ui.mine.PadShareFragment
import com.cqebd.student.utils.DateUtils
import com.cqebd.student.viewmodel.MineViewModel
import com.cqebd.student.vo.entity.UserAccount
import com.wuhangjia.firstlib.view.FancyDialogFragment
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import gorden.lib.anko.static.startActivity
import gorden.rxbus.RxBus
import gorden.util.PackageUtils
import kotlinx.android.synthetic.main.dialog_clear_cache.view.*
import kotlinx.android.synthetic.main.fragment_root_mine.*
import java.io.File

/**
 * 我的 for pad
 *
 */
class RootMineFragment : BaseFragment() {

    private lateinit var mineViewModel: MineViewModel
    private lateinit var btnLists: List<FrameLayout>
    private lateinit var tvLists: List<TextView>
    private lateinit var icResNormal: List<Int>
    private lateinit var icResSelected: List<Int>

    override fun setContentView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_root_mine, container, false)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        btnLists = listOf(btnMine, btnShare, btnMsg, btnHeadman, btnCallback, btnClear, btnExit)
        tvLists = listOf(tvShare, tvShare, tvMsg, tvHeadman, tvCallback, tvClear, tvExit)
        icResNormal = listOf(R.drawable.mine_share, R.drawable.mine_share, R.drawable.item_message, R.drawable.item_headman,
                R.drawable.item_opinion, R.drawable.item_cache, R.drawable.item_exit)
        icResSelected = listOf(R.drawable.item_share_selected, R.drawable.item_share_selected, R.drawable.item_message_selected, R.drawable.item_headman_selected,
                R.drawable.item_opinion_selected, R.drawable.item_cache_selected, R.drawable.item_exit_selected)

        mineViewModel = ViewModelProviders.of(this).get(MineViewModel::class.java)
        mineViewModel.userAccount.observe(this, Observer {
            it?.apply {
                tvName.text = Name
                GlideApp.with(App.mContext).asBitmap().circleCrop().load(Avatar).placeholder(R.drawable.ic_avatar).into(ivAvatar)
                btnHeadman.visibility = if (it.IsGroup) View.VISIBLE else View.GONE
            }
        })


        arguments?.let {
            val pos = it.getInt("pos", -1)
            if (pos == 2) {
                currentItem = 2
                changeStyle(currentItem)
                replaceFrag(PadMsgFragment())
            } else {
                replaceFrag(PadMineFragment())
            }
        }


    }

    private var currentItem = 0

    override fun bindEvents() {

        btnMine.setOnClickListener {
            if (currentItem != 0) {
                currentItem = 0
                changeStyle(currentItem)
                replaceFrag(PadMineFragment())
            }
        }
        btnShare.setOnClickListener {
            if (currentItem != 1) {
                currentItem = 1
                changeStyle(currentItem)
                replaceFrag(PadShareFragment())
            }
        }
        btnMsg.setOnClickListener {
            if (currentItem != 2) {
                currentItem = 2
                changeStyle(currentItem)
                replaceFrag(PadMsgFragment())
            }
        }
        btnHeadman.setOnClickListener {
            //            if (currentItem != 3) {
//                currentItem = 3
//                changeStyle(currentItem)
//            }
            startActivity<AgentWebActivity>("url" to "https://service-student.cqebd.cn/StudentGroup/task?GroupStudentId=$loginId")
        }
        btnCallback.setOnClickListener {
            if (currentItem != 4) {
                currentItem = 4
                changeStyle(currentItem)
                replaceFrag(PadCallbackFragment())
            }
        }
        btnClear.setOnClickListener {
            //            if (currentItem != 5) {
//                currentItem = 5
//                changeStyle(currentItem)
//            }
            showClearDialog()
        }
        btnExit.setOnClickListener {
            //            if (currentItem != 6) {
//                currentItem = 6
//                changeStyle(currentItem)
//            }
            activity?.finish()
            UserAccount.clear()
            logoutNetease()
            startActivity<LoginActivity>()
        }
    }

    private fun changeStyle(pos: Int) {
        for (i in 0 until btnLists.size) {
            if (i == pos) {
                btnLists[i].setBackgroundResource(R.color.mine_photo)
                if (pos != 0) {
                    tvLists[i].setTextColor(resources.getColor(R.color.white))
                    val ic = resources.getDrawable(icResSelected[i])
                    ic.setBounds(0, 0, ic.minimumWidth, ic.minimumHeight)
                    tvLists[i].setCompoundDrawables(ic, null, null, null)
                }
            } else {
                btnLists[i].setBackgroundResource(R.color.white)
                tvLists[i].setTextColor(resources.getColor(R.color.mine_menu_title))
                val ic = resources.getDrawable(icResNormal[i])
                ic.setBounds(0, 0, ic.minimumWidth, ic.minimumHeight)
                tvLists[i].setCompoundDrawables(ic, null, null, null)
            }
        }

        if (pos == 0) {
            tvName.setTextColor(resources.getColor(R.color.white))
        } else {
            tvName.setTextColor(resources.getColor(R.color.mine_menu_title))
        }
    }

    private fun replaceFrag(frag: Fragment) {
        fragmentManager?.beginTransaction()
                ?.replace(R.id.mineContainer, frag)
                ?.commit()
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
                                    val cache = File("data/data/" + PackageUtils.getPackageName(it))
                                    DateUtils.DeleteFile(cache)
                                    toast("清理成功，请重启本应用")
                                    dialog.dismiss()
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
