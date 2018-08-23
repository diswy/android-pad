package com.cqebd.student.glide

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cqebd.student.R
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import kotlinx.android.synthetic.main.activity_glide_preview.*

class GlidePreviewActivity : AppCompatActivity() {

    private lateinit var mBitmap: Bitmap

    private var isLoadSuccess = false// 图片是否加载完毕

    private lateinit var imgPath: String

    private var rotate = 0f

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
//                , WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_glide_preview)

        imgPath = intent.getStringExtra("IMG_PATH")

        GlideApp.with(this)
                .asBitmap()
                .load(imgPath)
                .into(iv)

        btnRotate.setOnClickListener {
            if (isLoadSuccess) {
                rotate += 90f
                iv.setImageBitmap(rotateBitmap(mBitmap, rotate))
            }
        }
        btnReRotate.setOnClickListener {
            if (isLoadSuccess) {
                rotate -= 90f
                iv.setImageBitmap(rotateBitmap(mBitmap, rotate))
            }
        }
    }

    private val target = object : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            mBitmap = resource
            isLoadSuccess = true
            iv.setImageBitmap(resource)
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, rotate: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val matrix = Matrix()
        matrix.postRotate(rotate)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }


}
