package com.cqebd.student.adapter

import android.support.v4.content.ContextCompat
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.cqebd.student.R
import com.cqebd.student.tools.formatTimeYMD
import java.io.File
import java.text.DecimalFormat

/**
 *
 * Created by @author xiaofu on 2019/7/8.
 */
class FileAdapter : BaseQuickAdapter<File, BaseViewHolder>(R.layout.student_receive_file) {
    private var df: DecimalFormat = DecimalFormat(".00")

    override fun convert(helper: BaseViewHolder?, item: File?) {
        helper ?: return
        item ?: return


        val fileSize: Int = (item.length() / 1024).toInt()

        val size: String = if (fileSize > 1024) {
            val kb: Float = fileSize % 1024 / 1024f
            val mb: Int = fileSize / 1024
            "文件大小：${df.format(mb + kb)}Mb"
        } else {
            "文件大小：${fileSize}Kb"
        }

        helper.setText(R.id.file_name, item.name)
                .setText(R.id.tv_size, size)
                .setText(R.id.tv_time, formatTimeYMD(item.lastModified()))

        val tv: TextView = helper.getView(R.id.file_name)
        val drawable = ContextCompat.getDrawable(mContext, getIconRes(item.name)) ?: return
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