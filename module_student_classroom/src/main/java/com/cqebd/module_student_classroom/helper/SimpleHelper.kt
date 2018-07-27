@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.cqebd.module_student_classroom.helper

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import com.cqebd.module_student_classroom.R
import com.xiaofu.lib_base_xiaofu.fancy.FancyDialogFragment
import kotlinx.android.synthetic.main.dialog_period_layout.view.*

/**
 * 显示常用对话框
 */
inline fun Activity.showPeriodDialog(message: String, listener: View.OnClickListener) {
    FancyDialogFragment.create()
            .setLayoutRes(R.layout.dialog_period_layout)
            .setWidth(this, 300)
            .setViewListener { dialog, v ->
                v.apply {
                    tvMessage.text = message
                    btnCancel.setOnClickListener { dialog.dismiss() }
                    btnOk.setOnClickListener {
                        dialog.dismiss()
                        listener.onClick(it)
                    }
                }
            }
            .show(this.fragmentManager, "PeriodDialog")
}


/**
 * 快速获取颜色主要为了兼容低版本
 */
inline fun Context.getMyColor(id: Int): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return this.getColor(id)
    }
    return this.resources.getColor(id)
}

