@file:Suppress("NOTHING_TO_INLINE", "unused")
package com.xiaofu.lib_base_xiaofu.helper

import android.content.Context
import android.os.Build

/**
 * 快速获取颜色主要为了兼容低版本
 */
inline fun Context.getMyColor(id: Int): Int {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        return this.getColor(id)
    }
    return this.resources.getColor(id)
}