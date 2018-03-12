package com.cqebd.student.tools

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.support.annotation.ColorInt
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.cqebd.student.R
import com.cqebd.student.app.App

/**
 * 描述
 * Created by gorden on 2017/11/8.
 */
private val toast: Toast by lazyOf(Toast.makeText(App.mContext,"", Toast.LENGTH_SHORT))

@SuppressLint("ShowToast")
fun toast(message:CharSequence?){
    toastCustom(message, Color.WHITE, colorForRes(R.color.toast_normal))
}

fun toastSuccess(message: CharSequence?){
    toastCustom(message, Color.WHITE, colorForRes(R.color.toast_success))
}

fun toastError(message: CharSequence?){
    toastCustom(message, Color.WHITE,colorForRes(R.color.toast_error))
}

@SuppressLint("ShowToast")
fun toastCustom(message:CharSequence?, @ColorInt textColor:Int, @ColorInt tintColor:Int){
    toast.setText(message)
    toast.view.background?.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
    val textView = (toast.view as? ViewGroup)?.getChildAt(0) as? TextView
    textView?.setTextColor(textColor)
    toast.show()
}
