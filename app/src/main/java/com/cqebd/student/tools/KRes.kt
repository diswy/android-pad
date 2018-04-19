package com.cqebd.student.tools

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v4.content.ContextCompat
import com.cqebd.student.app.App
import gorden.lib.anko.KPath
import gorden.lib.anko.KPreferences
import java.io.File
import com.cqebd.student.MainActivity
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.ShapeDrawable



/**
 * 描述
 * Created by gorden on 2017/11/8.
 */
const val IMG_TAG = "ImgDoaminTag"
const val IMG_PATH = "ImagesUrl"

const val OSS_TAG = "OssAccessUrlTag"
const val OSS_PATH = "OssAccessUrl"

fun colorForRes(colorRes: Int) = ContextCompat.getColor(App.mContext, colorRes)

private val mPreferences by lazyOf(KPreferences.get(App.mContext))

fun <T> getValue(key: String, def: T): T {
    return mPreferences.getValue(key, def)
}

fun setValue(param: Pair<String, Any?>) {
    mPreferences.setValue(param)
}

fun removeValue(key: String) {
    mPreferences.remove(key)
}

fun isLogin(): Boolean {
    return mPreferences.getValue("id", -1L) != -1L
}

val loginId get() = getValue("id", -1L)

private val packageInfo by lazy {
    App.mContext.packageManager.getPackageInfo(App.mContext.packageName, PackageManager.GET_PERMISSIONS)
}

val versionCode = packageInfo.versionCode
val versionName = packageInfo.versionName

val cropPath: Uri = Uri.parse(KPath.externalRootDir(App.mContext).plus(File.separator).plus("crop_temp.jpg"))

fun safeString(string: String?): String {
    return safeString(string, "")
}

fun safeString(string: String?, default: String): String {
    return string ?: default
}

fun getDrawable(): Drawable {
    val drawable = ShapeDrawable(OvalShape())
    drawable.paint.color = App.mContext.resources.getColor(android.R.color.transparent)
    return drawable
}