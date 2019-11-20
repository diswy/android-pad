package com.cqebd.student.adapter

import android.os.Environment
import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.myview.ProgressButton
import com.cqebd.student.tools.formatTimeYMD2
import com.cqebd.student.vo.entity.DocItem
import java.io.File
import java.text.DecimalFormat

class RemoteFileListAdapter : BaseQuickAdapter<DocItem, BaseViewHolder>(R.layout.item_remote_file) {
    private var rootPath: String = ""
    private var df: DecimalFormat

    init {
        rootPath = Environment.getExternalStorageDirectory().absolutePath.plus("/cqebd/doc/")
        val directory = File(rootPath)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        df = DecimalFormat(".00")
    }

    override fun convert(helper: BaseViewHolder?, item: DocItem?) {
        helper ?: return
        item ?: return


        val progressBtn: ProgressButton = helper.getView(R.id.downloadBtn)
        progressBtn.setMaxProgress(100)

        val localFile = File(rootPath + item.FileName)
        if (localFile.exists()) {
            progressBtn.setProgress(100)
            progressBtn.text = "已下载"
        } else {
            progressBtn.text = "下载"
        }

        val size: String = if (item.Size > 1024) {
            val kb: Float = item.Size % 1024 / 1024f
            val mb: Int = item.Size / 1024
            "文件大小：${df.format(mb + kb)}Mb"
        } else {
            "文件大小：${item.Size}Kb"
        }

        helper.setText(R.id.remote_file_name, item.FileName)
                .setText(R.id.tv_size, size)
                .setText(R.id.tv_time, formatTimeYMD2(item.CreateDateTime))
                .addOnClickListener(R.id.downloadBtn)

        val tv: TextView = helper.getView(R.id.remote_file_name)
        val drawable = ContextCompat.getDrawable(mContext, getIconRes(item.FileName)) ?: return
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv.setCompoundDrawables(drawable, null, null, null)
    }

    private fun getIconRes(name: String): Int {
        return if (name.endsWith(".doc") || name.endsWith(".docx")) {
            R.drawable.ic_doc
        } else if (name.endsWith(".jpg") || name.endsWith(".png")) {
            R.drawable.ic_pic
        } else if (name.endsWith(".ppt") || name.endsWith(".pptx")) {
            R.drawable.ic_ppt
        } else if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
            R.drawable.ic_xls
        } else if (name.endsWith(".txt")) {
            R.drawable.ic_txt
        } else if (name.endsWith(".mp3")) {
            R.drawable.ic_mp3
        } else if (name.endsWith(".mp4")) {
            R.drawable.ic_mp4
        } else if (name.endsWith(".zip") || name.endsWith(".rar")) {
            R.drawable.ic_zip
        } else {
            R.drawable.ic_unknown
        }
    }
}