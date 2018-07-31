package com.cqebd.student.glide

import android.content.Context
import android.widget.ImageView
import com.xiaofu.lib_base_xiaofu.img.GlideApp


fun imageNormalLoad(context: Context, localRes: Int, iv: ImageView) {
    GlideApp.with(context)
            .load(localRes)
            .centerInside()
            .into(iv)
}

fun imageNormalLoad(context: Context, url: String, iv: ImageView) {
    GlideApp.with(context)
            .load(url)
            .centerInside()
            .into(iv)
}

fun imageCircleLoad(context: Context, localRes: Int, iv: ImageView) {
    GlideApp.with(context)
            .asBitmap()
            .circleCrop()
            .load(localRes)
            .into(iv)
}

fun imageCircleLoad(context: Context, url: String, iv: ImageView) {
    GlideApp.with(context)
            .asBitmap()
            .circleCrop()
            .load(url)
            .into(iv)
}