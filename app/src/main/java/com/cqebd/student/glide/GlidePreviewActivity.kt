package com.cqebd.student.glide

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import com.xiaofu.lib_base_xiaofu.img.PhotoUtils
import kotlinx.android.synthetic.main.activity_glide_preview.*
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class GlidePreviewActivity : BaseActivity() {
    override fun setContentView() {
        setContentView(R.layout.activity_glide_preview)
    }

    private lateinit var mBitmap: Bitmap

    private var finalBitmap: Bitmap? = null

    private var isLoadSuccess = false// 图片是否加载完毕

    private lateinit var imgPath: String

    private var rotate = 0f

    override fun initialize(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN)
        imgPath = intent.getStringExtra("IMG_PATH")
        Logger.d("imgPath = $imgPath  degree = ${PhotoUtils.getPictureDegree(imgPath)}")
        GlideApp.with(this)
                .asBitmap()
                .load(imgPath)
                .into(target)
    }

    override fun bindEvents() {
        btnRotate.setOnClickListener {
            if (isLoadSuccess) {
                rotate += 90f
                iv.setImageBitmap(rotateBitmap(mBitmap, rotate))
                finalBitmap = rotateBitmap(mBitmap, rotate)
            }
        }
        btnReRotate.setOnClickListener {
            if (isLoadSuccess) {
                rotate -= 90f
                iv.setImageBitmap(rotateBitmap(mBitmap, rotate))
                finalBitmap = rotateBitmap(mBitmap, rotate)
            }
        }

        btnConfirm.setOnClickListener {
            saveBitmap()
        }
    }

    private val target = object : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            mBitmap = resource
            isLoadSuccess = true
            iv.setImageBitmap(resource)
            finalBitmap = resource
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, rotate: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val matrix = Matrix()
        matrix.postRotate(rotate)
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    private fun saveBitmap() {
        if (isLoadSuccess) {
            val file = File(imgPath)
            if (!file.exists()) {
                return
            }

            try {
                val fileOutputStream = FileOutputStream(file)
                finalBitmap?.let {
                    it.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()

                    setResult(888)
                    finish()
                }
            } catch (e : Exception) {
                e.printStackTrace()
            }

        }
    }

    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                exitTime = System.currentTimeMillis()
                toast("再按一次将会关闭此界面，但图片不会被保存，如需保存请点击确定按钮")
            } else {
                finish()
            }
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}
