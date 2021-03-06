package com.cqebd.student.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.http.NetCallBack
import com.cqebd.student.net.BaseResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.tools.RxCounter
import com.cqebd.student.tools.toast
import com.cqebd.student.vo.entity.UpdateBean
import com.daimajia.numberprogressbar.NumberProgressBar
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wuhangjia.firstlib.view.FancyDialogFragment
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.FileCallBack
import gorden.lib.anko.static.startActivity
import gorden.util.PackageUtils
import kotlinx.android.synthetic.main.dialog_update_layout.view.*
import okhttp3.Call
import java.io.File

/**
 * 启动页
 * Created by gorden on 2018/3/20.
 */
class StartActivity : BaseActivity() {
    override fun setContentView() {
        //取消状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_start)
    }

    override fun initialize(savedInstanceState: Bundle?) {
        if (!isTaskRoot) {
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intent.action == Intent.ACTION_MAIN) {
                finish()
                return
            }
        }

        RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.RECORD_AUDIO
                , Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (!granted)
                        toast("您拒绝了必要权限")
//                    appUpdate()
                    next()
                }
    }

    private fun next() {
        RxCounter.tick(1).doOnComplete {
            startActivity<LoginActivity>()
            finish()
        }.subscribe()
    }

    private fun appUpdate() {
        NetClient.workService().checkUpdate()
                .enqueue(object : NetCallBack<BaseResponse<String>>() {
                    override fun onSucceed(response: BaseResponse<String>?) {
                        Logger.json(response?.data)
                        try {
                            val mVersionCode = PackageUtils.getVersionCode(this@StartActivity)
                            val entity = Gson().fromJson(response?.data, UpdateBean::class.java)
                            if (entity.version_code > mVersionCode) {// 需要更新
                                showUpdateDialog(entity)
                            } else {
                                next()
                            }
                        } catch (e: Exception) {
                            Logger.e("版本更新错误:${e.message}")
                            next()
                        }
                    }

                    override fun onFailure() {
                        next()
                    }
                })
    }

    private fun showUpdateDialog(data: UpdateBean) {
        val dialog = FancyDialogFragment.create()
        dialog.isCancelable = false
        dialog.setCanCancelOutside(false)
                .setLayoutRes(R.layout.dialog_update_layout)
                .setWidth(this, 260)
                .setViewListener { v ->
                    v.apply {
                        mDivider.visibility = if (data.force_update) View.GONE else View.VISIBLE
                        mBtnCancel.visibility = if (data.force_update) View.GONE else View.VISIBLE
                        mTitle.text = data.name
                        mInfo.text = data.info
                        mBtnCancel.setOnClickListener {
                            dialog.dismiss()
                            startActivity<LoginActivity>()
                            finish()
                        }
                        mBtnConfirm.setOnClickListener {
                            dialog.dismiss()
                            //下载路径
                            val path = Environment.getExternalStorageDirectory().absolutePath.plus("/cqebd/")
                            download(data.download_url, path, data.file_name)
                        }
                    }
                }
                .show(fragmentManager, "Update")
    }

    private fun download(url: String, path: String, fileName: String) {
        var mDownloadProgress: NumberProgressBar? = null
        val dialog = FancyDialogFragment.create()
                .setCanCancelOutside(false)
                .setLayoutRes(R.layout.dialog_download_layout)
                .setWidth(this, 260)
                .setViewListener {
                    mDownloadProgress = it.findViewById(R.id.mDownloadProgress)
                }
        dialog.show(fragmentManager, "Download")

        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(object : FileCallBack(path, fileName) {
                    override fun inProgress(progress: Float, total: Long, id: Int) {
                        mDownloadProgress?.let {
                            val mProgress: Int = (progress * 100).toInt()
                            it.progress = mProgress
                            if (mProgress == 100) {
                                dialog.dismiss()

                                downloadPath = path
                                downloadFileName = fileName

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                    installOreo()
                                    newInstall()
                                } else {
                                    apkInstall()
                                }
                            }
                        }
                    }

                    override fun onResponse(response: File?, id: Int) {
                    }

                    override fun onError(call: Call?, e: java.lang.Exception?, id: Int) {
                    }
                })
    }

    private val GET_INSTALL_APP_PERMISSION = 111222
    @RequiresApi(Build.VERSION_CODES.O)
    private fun installOreo() {
        val canInstallApk = packageManager.canRequestPackageInstalls()
        if (canInstallApk) {
            apkInstall()
        } else {
            // 引导用户去打开权限 PS:部分机型无法跳转 目前小米有出现此情况
            val packageUri = Uri.parse("package:$packageName")
            val i = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageUri)
            startActivityForResult(i, GET_INSTALL_APP_PERMISSION)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GET_INSTALL_APP_PERMISSION) {
//            installOreo()
            newInstall()
        }
    }

    private fun apkInstall() {
        val i = Intent(Intent.ACTION_VIEW)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val data: Uri
        val filePath = downloadPath + downloadFileName
        val file = File(filePath)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {// 大于7.0
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            data = FileProvider.getUriForFile(this@StartActivity, "${applicationContext.packageName}.provider", file)
        } else {
            data = Uri.fromFile(file)
        }
        i.setDataAndType(data, "application/vnd.android.package-archive")
        startActivity(i)
    }

    private lateinit var downloadPath: String
    private lateinit var downloadFileName: String

    //---------------------新的安装方法
    private fun newInstall() {
        try {
            val filePath = downloadPath + downloadFileName
            val file = File(filePath)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                val fileUri = FileProvider.getUriForFile(this@StartActivity, "${applicationContext.packageName}.provider", file)
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
            }

            if (packageManager.queryIntentActivities(intent, 0).size > 0) {
                startActivity(intent)
            }

        } catch (e: Exception) {
            Logger.d(e.message)
        }
    }
}