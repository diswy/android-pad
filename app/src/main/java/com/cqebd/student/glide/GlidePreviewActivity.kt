package com.cqebd.student.glide

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.cqebd.student.R
import com.cqebd.student.app.BaseActivity
import com.cqebd.student.utils.DeleteHelper
import com.orhanobut.logger.Logger
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xiaofu.lib_base_xiaofu.img.GlideApp
import com.xiaofu.lib_base_xiaofu.img.PhotoUtils
import kotlinx.android.synthetic.main.activity_glide_preview.*
import me.panpf.sketch.Sketch
import org.jetbrains.anko.toast
import java.io.File
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
        Sketch.with(this).display(imgPath, iv)
                .disableCacheInDisk()
                .disableCacheInMemory()
                .disableBitmapPool()
                .commit()

        iv.isZoomEnabled = true
        GlideApp.with(this)
                .asBitmap()
                .load(imgPath)
                .skipMemoryCache(true)// 不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(target)

    }

    override fun bindEvents() {
        btnRotate.setOnClickListener {
            if (isLoadSuccess) {
                iv.zoomer?.rotateBy(90)
                rotate += 90f
            }
        }
        btnReRotate.setOnClickListener {
            if (isLoadSuccess) {
                iv.zoomer?.rotateBy(270)
                rotate -= 90f
            }
        }

        btnConfirm.setOnClickListener {
            if (rotate >= 360f || rotate <= -360f) {
                rotate = 0f
            }
            if (rotate == 0f) {
                setResult(888)
                finish()
            } else {
                RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe { granted ->
                            if (granted) {
                                if (DeleteHelper.delete(imgPath)){
                                    finalBitmap = rotateBitmap(mBitmap, rotate)
                                    saveBitmap()
                                }
                            } else {
                                toast("您拒绝了必要权限,请授权后尝试")
                            }
                        }
            }
        }
    }

    private val target = object : SimpleTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            mBitmap = resource
            isLoadSuccess = true
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
                file.createNewFile()
            }

            try {
                val fileOutputStream = FileOutputStream(file)
                finalBitmap?.let {
                    it.compress(Bitmap.CompressFormat.JPEG, 65, fileOutputStream)
                    fileOutputStream.flush()
                    fileOutputStream.close()
                    setResult(888)
                    finish()
                }
            } catch (e: Exception) {
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
