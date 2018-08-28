package com.xiaofu.lib_base_xiaofu.img

import android.media.ExifInterface

class PhotoUtils {

    companion object {
        fun getPictureDegree(photoPath: String): Int {
            var degree: Int = 0
            val exifInterface = ExifInterface(photoPath)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 90
            }
            return degree
        }
    }
}