package com.xiaofu.lib_base_xiaofu.img

import android.app.Activity
import android.widget.ImageView
import com.bumptech.glide.load.DecodeFormat

/**
 * 说明：一键调用图片加载
 * 作者：xiaofu
 */

fun Activity.loadImage(url: String, iv: ImageView) =
        GlideApp.with(this)
                .load(url)
                .into(iv)

fun Activity.loadImageAsCircle(url: String, iv: ImageView) =
        GlideApp.with(this)
                .load(url)
                .circleCrop()
                .into(iv)

fun Activity.loadHighImage(url: String, iv: ImageView) =
        GlideApp.with(this)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(url)
                .into(iv)

