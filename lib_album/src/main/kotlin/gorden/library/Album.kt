@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package gorden.library

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.widget.Toast
import gorden.library.core.PermissionsTools
import gorden.library.entity.*
import gorden.library.ui.AlbumPickerActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * 默认单选模式、显示gif 每排3张图 显示相机
 * Created by gorden on 2018/1/31.
 */
class Album private constructor() {
    private val optionsBundle: Bundle = Bundle()
    private val pickerIntent: Intent = Intent()
    private var mRequestCode = REQUEST_CODE

    companion object {
        const val REQUEST_CODE = 10233
        const val REQUEST_CAMERA = 10234
        const val REQUEST_DEL_CODE = 10235
        const val KEY_IMAGES = "KEY_IMAGES"
        const val KEY_DEL_IMAGES = "KEY_DEL_IMAGES"
        var mCurrentPhotoPath: String? = null
        fun create(): Album {
            return Album()
        }

        /**
         * 扫描图片，刷新系统图库
         */
        fun scanFile(context: Context, vararg path: String) {
            MediaScannerConnection.scanFile(context, path, null, null)
        }
    }

    /**
     * 打开相册
     */
    fun start(activity: Activity, requestCode: Int = mRequestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PermissionsTools.build(activity).request(Manifest.permission.READ_EXTERNAL_STORAGE, callback = {
                if (it) {
                    activity.startActivityForResult(getIntent(activity), requestCode)
                }
            })
        } else {
            activity.startActivityForResult(getIntent(activity), requestCode)
        }
    }

    /**
     * 打开相册
     */
    fun start(fragment: Fragment, requestCode: Int = mRequestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PermissionsTools.build(fragment.activity!!).request(Manifest.permission.READ_EXTERNAL_STORAGE, callback = {
                if (it) {
                    fragment.startActivityForResult(getIntent(fragment.activity!!), requestCode)
                }
            })
        } else {
            fragment.startActivityForResult(getIntent(fragment.activity!!), requestCode)
        }
    }


    /**
     * 打开相机
     */
    fun openCamera(activity: Activity, requestCode: Int = REQUEST_CAMERA) {
        PermissionsTools.build(activity).request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, callback = {
            if (it) {
                try {
                    activity.startActivityForResult(dispatchTakePictureIntent(activity), requestCode)
                } catch (e: IOException) {
                    Toast.makeText(activity, "目录创建失败,请检查后再试", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    /**
     * 打开相机
     */
    fun openCamera(fragment: Fragment, requestCode: Int = REQUEST_CAMERA) {
        PermissionsTools.build(fragment.activity!!).request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, callback = {
            if (it) {
                try {
                    fragment.startActivityForResult(dispatchTakePictureIntent(fragment.activity!!), requestCode)
                } catch (e: IOException) {
                    Toast.makeText(fragment.activity, "目录创建失败,请检查后再试", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    /**
     * 适配Android7.0 拍照
     */
    @Throws(IOException::class)
    private fun dispatchTakePictureIntent(context: Context): Intent {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(context.packageManager) != null) {
            val file = createImageFile(context)
            val photoFile = if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(context.applicationContext, context.packageName + ".provider", file)
            } else {
                Uri.fromFile(file)
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)
            }
        }
        return takePictureIntent
    }

    /**
     * 创建拍照欲保存文件
     */
    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName = "IMG_$timeStamp.jpg"
        var storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/Camera")

        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                storageDir = File(context.filesDir, "/Camera")
                if (!storageDir.exists()) {
                    if (!storageDir.mkdirs()) {
                        throw IOException("no such dir")
                    }
                }
            }
        }

        val image = File(storageDir, imageFileName)
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    /**
     * 图片预览
     */
    fun preview(vararg path: String, index: Int = 0): Album {
        return preview(arrayListOf(*path), index)
    }

    /**
     * 图片预览
     */
    fun preview(pathList: ArrayList<String>, index: Int = 0): Album {
        optionsBundle.putStringArrayList(EXTRA_SELECTED_PATH, pathList)
        optionsBundle.putInt(EXTRA_PREVIEW_MODE, PREVIEW_PREVIEW)
        optionsBundle.putInt(EXTRA_PREVIEW_POSITION, index)
        return this
    }

    /**
     * 图片删除
     */
    fun delete(vararg path: String, index: Int = 0): Album {
        return delete(arrayListOf(*path), index)
    }

    /**
     * 图片删除
     */
    fun delete(pathList: ArrayList<String>, index: Int = 0): Album {
        optionsBundle.putStringArrayList(EXTRA_SELECTED_PATH, pathList)
        optionsBundle.putInt(EXTRA_PREVIEW_MODE, PREVIEW_DELETE)
        optionsBundle.putInt(EXTRA_PREVIEW_POSITION, index)
        mRequestCode = REQUEST_DEL_CODE
        return this
    }

    /**
     * 单选模式，一次只能选择一张图片
     */
    fun single(): Album {
        optionsBundle.putInt(EXTRA_SELECT_MODE, SINGLE_SELECT_MODE)
        return this
    }

    /**
     * 多选模式
     * @param count 可选择的图片数量
     */
    fun multi(count: Int): Album {
        optionsBundle.putInt(EXTRA_SELECT_MODE, MULTI_SELECT_MODE)
        optionsBundle.putInt(EXTRA_MAX_COUNT, count)
        return this
    }

    /**
     * 是否显示相机,默认显示
     */
    fun showCamera(show: Boolean): Album {
        optionsBundle.putBoolean(EXTRA_SHOW_CAMERA, show)
        return this
    }

    /**
     * 是否显示gif图,默认显示
     *
     * @param showGif if true show
     */
    fun showGif(showGif: Boolean): Album {
        optionsBundle.putBoolean(EXTRA_SHOW_GIF, showGif)
        return this
    }

    /**
     * @param count 单排展示多少张图
     */
    fun gridColumns(count: Int): Album {
        optionsBundle.putInt(EXTRA_GRID_COLUMN, count)
        return this
    }

    /**
     * 已经选择的图片地址
     *
     * @param pathArray 图片地址
     */
    fun selectedPaths(vararg pathArray: String): Album {
        return selectedPaths(ArrayList(pathArray.asList()))
    }

    fun selectedPaths(pathList: ArrayList<String>): Album {
        optionsBundle.putStringArrayList(EXTRA_SELECTED_PATH, pathList)
        return this
    }

    private fun getIntent(context: Context): Intent {
        pickerIntent.setClass(context, AlbumPickerActivity::class.java)
        pickerIntent.putExtras(optionsBundle)
        return pickerIntent
    }

}