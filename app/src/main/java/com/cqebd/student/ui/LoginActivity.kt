package com.cqebd.student.ui

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.PopupWindow
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.constant.Constant
import com.cqebd.student.net.NetClient
import com.cqebd.student.shortcuts.GuidePageActivity
import com.cqebd.student.tools.savePassword
import com.cqebd.student.tools.toastError
import com.cqebd.student.tools.versionName
import com.cqebd.student.widget.LoadingDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gorden.lib.anko.static.startActivity
import gorden.util.PreferencesUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 *
 * Created by gorden on 2018/3/20.
 */
class LoginActivity : BaseActivity() {
    private val loadingDialog by lazy { LoadingDialog() }
    override fun setContentView() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        version_name.text = "V".plus(versionName)
        edit_username.setText(PreferencesUtil.getInstance(applicationContext).getString(Constant.LAST_USER_NAME, ""))
        edit_pwd.setText(PreferencesUtil.getInstance(applicationContext).getString(Constant.LAST_USER_PASSWORD, ""))
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
                            //----应用间共享数据
                            sharedUser(Gson().toJson(it.body))
                            it.body?.save()
                            savePassword(edit_pwd.text.toString())
                            PreferencesUtil.getInstance(applicationContext).putString(Constant.LAST_USER_NAME, edit_username.text.toString())
                            PreferencesUtil.getInstance(applicationContext).putString(Constant.LAST_USER_PASSWORD, edit_pwd.text.toString())
                            saveUserList(edit_username.text.toString())
                            startActivity<GuidePageActivity>()
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
            startActivity<AgentWebActivity>("url" to "http://student.cqebd.cn/Account/FindLoginName")
        }

//        btn_choose_user.setOnClickListener {
//            showUserList()
//        }
    }

    private var pop: PopupWindow? = null
//    private fun showUserList() {
//        if (pop == null) {
//            pop = PopupWindow(this)
//            val container = FrameLayout(this)
//            container.setBackgroundColor(resources.getColor(R.color.white))
//            val rv = RecyclerView(this)
//            val lp = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
//            container.addView(rv, lp)
//
//            val gson = Gson()
//            val userList: List<String>
//            val list = PreferencesUtil.getInstance(applicationContext).getString(Constant.USER_NAME_LIST, "")
//            userList = if (list == "") {
//                ArrayList()
//            } else {
//                gson.fromJson(list, object : TypeToken<List<String>>() {
//                }.type)
//            }
//
//            val adapter = AccountListAdapter(this, userList)
//            rv.layoutManager = LinearLayoutManager(this)
//            rv.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//            rv.adapter = adapter
//            rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
//
//            adapter.setItemListener { v ->
//                edit_username.setText((v as TextView).text.toString())
//
//                if (PreferencesUtil.getInstance(applicationContext).getString(Constant.LAST_USER_NAME, "") == v.text.toString()) {
//                    edit_pwd.setText(PreferencesUtil.getInstance(applicationContext).getString(Constant.LAST_USER_PASSWORD, ""))
//                } else {
//                    edit_pwd.setText("")
//                }
//
//                if (pop != null && pop!!.isShowing)
//                    pop!!.dismiss()
//            }
//
//            pop!!.contentView = container
//            pop!!.isFocusable = true
//            pop!!.isOutsideTouchable = true
//            pop!!.setBackgroundDrawable(BitmapDrawable())
//            pop!!.width = edit_username_layout.width
//            pop!!.height = DensityUtil.dip2px(160, this)
//
//            pop!!.showAsDropDown(edit_username_layout)
//        }
//
//        pop?.let {
//            if (it.isShowing) {
//                it.dismiss()
//            } else {
//                it.showAsDropDown(edit_username_layout)
//            }
//        }
//    }

    /**
     * 保存登录过的账号
     * @param user
     */
    private fun saveUserList(user: String) {
        val gson = Gson()
        val userList: ArrayList<String>
        val list = PreferencesUtil.getInstance(applicationContext).getString(Constant.USER_NAME_LIST, "")
        if (list == "") {
            userList = ArrayList()
            userList.add(user)
        } else {
            userList = gson.fromJson<ArrayList<String>>(list, object : TypeToken<List<String>>() {
            }.type)
            userList.add(user)
        }

        val hashSet = HashSet(userList)
        userList.clear()
        userList.addAll(hashSet)

        val jsonList = gson.toJson(userList)
        PreferencesUtil.getInstance(applicationContext).putString(Constant.USER_NAME_LIST, jsonList)
    }

    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast("再按一次退出点点课")
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                System.exit(0)
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }


    private fun sharedUser(data: String) {
        val path = Environment.getExternalStorageDirectory().absolutePath.plus("/yunketang/shared")
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }

        val mFile = File(path,"user")
        val outStream = FileOutputStream(mFile)
        outStream.write(data.toByteArray())
        outStream.close()
    }
}