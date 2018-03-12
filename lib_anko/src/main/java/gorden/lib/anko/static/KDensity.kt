package com.anko.static

import android.content.res.Resources

/**
* 密度计算工具
* Created by gorden on 2017/9/14.
*/
private val density = Resources.getSystem().displayMetrics.density
private val scaleDensity = Resources.getSystem().displayMetrics.scaledDensity

val Int.dp:Int get() = (this* density).toInt()
val Float.dp:Int get() = (this* density).toInt()
val Int.sp:Int get() = (this* scaleDensity).toInt()
val Float.sp:Int get()= (this* scaleDensity).toInt()
fun Int.px2dip():Float = this/ density
fun Int.px2sp():Float = this/ scaleDensity
val appWidth = Resources.getSystem().displayMetrics.widthPixels
val appHeight = Resources.getSystem().displayMetrics.heightPixels