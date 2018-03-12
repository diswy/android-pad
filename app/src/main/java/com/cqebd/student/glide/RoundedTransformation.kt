package com.cqebd.student.glide

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.TransformationUtils


/**
 * 描述
 * Created by gorden on 2018/2/27.
 */
class RoundedTransformation(val radius:Int) : BitmapTransformation() {
    private val VERSION = 1
    private val ID = "com.bumptech.glide.load.resource.bitmap.RoundedTransformation." + VERSION
    private val ID_BYTES = ID.toByteArray(CHARSET)
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)

    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return TransformationUtils.roundedCorners(pool,toTransform,radius)
//
//        val width = toTransform.width
//        val height = toTransform.height
//
//        val bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888)
//        bitmap.setHasAlpha(true)
//
//        val canvas = Canvas(bitmap)
//        val paint = Paint()
//        paint.isAntiAlias = true
//        paint.shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
//        canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), radius.toFloat(), radius.toFloat(), paint)
//        return bitmap
    }

    override fun equals(other: Any?): Boolean {
        return other is CircleCrop
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }
}