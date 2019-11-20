package com.cqebd.student.ui.week


import android.app.Activity
import android.os.Environment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.cqebd.student.R
import com.cqebd.student.adapter.FileAdapter
import com.cqebd.student.tools.OpenFileUtil
import com.xiaofu.lib_base_xiaofu.base.BaseFragment
import com.xiaofu.lib_base_xiaofu.fancy.FancyDialogFragment
import kotlinx.android.synthetic.main.dialog_confirm_options.view.*
import kotlinx.android.synthetic.main.merge_refresh_layout.*
import org.jetbrains.anko.support.v4.toast
import java.io.File

/**
 * 每周文件已下载的
 */
class DownloadFileFragment : BaseFragment() {
    override fun getLayoutRes(): Int = R.layout.fragment_download_file

    private lateinit var fileAdapter: FileAdapter

    override fun onResume() {
        super.onResume()
        getFiles()
    }

    override fun initialize(activity: Activity) {

        fileAdapter = FileAdapter()

        recyclerView.layoutManager = LinearLayoutManager(activity)
        // 分割线初始化
        val divider = DividerItemDecoration(activity, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(divider)
        recyclerView.adapter = fileAdapter

        val path = Environment.getExternalStorageDirectory().absolutePath.plus("/cqebd/doc/")
        val directory = File(path)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        fileAdapter.setOnItemClickListener { _, _, pos ->
            val file = fileAdapter.getItem(pos) ?: return@setOnItemClickListener
            OpenFileUtil.openFile(requireContext(), file)
        }

        fileAdapter.setOnItemLongClickListener { _, view, pos ->
            delFile(fileAdapter.getItem(pos), pos)
            return@setOnItemLongClickListener true
        }

        smart_refresh_layout.setOnRefreshListener {
            getFiles()
            smart_refresh_layout.finishRefresh(true)
        }
    }

    private fun getFiles() {
        val path = Environment.getExternalStorageDirectory().absolutePath.plus("/cqebd/doc/")
        val f = File(path)
        if (!f.exists()) {
            return
        }
        val files = f.listFiles()
        val new = ArrayList<File>()
        files.forEach {
            new.add(it)
        }
        fileAdapter.setNewData(new)
    }

    private fun delFile(file: File?, pos: Int) {
        FancyDialogFragment.create()
                .setLayoutRes(R.layout.dialog_confirm_options)
                .setWidth(600)
                .setViewListener { dialog, v ->
                    v.apply {
                        mBtnCancel.setOnClickListener { dialog.dismiss() }
                        mBtnConfirm.setOnClickListener {
                            if (file?.delete() == true) {
                                toast("文件刪除成功")
                                fileAdapter.remove(pos)
                            } else {
                                toast("文件刪除失败，请重新尝试")
                            }
                            dialog.dismiss()
                        }
                    }
                }
                .show(requireActivity().fragmentManager, "show")
    }


}
