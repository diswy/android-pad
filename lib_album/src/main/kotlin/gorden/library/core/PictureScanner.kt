package gorden.library.core

import android.arch.lifecycle.ViewModelProviders
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore.Images.Media.*
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import gorden.library.R
import gorden.library.aac.PictureViewModel
import gorden.library.entity.Picture
import gorden.library.entity.PictureDirectory
import java.io.File
import java.util.*

/**
 * 图片扫描
 */
internal class PictureScanner(private val context: FragmentActivity) : LoaderManager.LoaderCallbacks<Cursor> {
    private val viewModel: PictureViewModel = ViewModelProviders.of(context).get(PictureViewModel::class.java)
    private var loadedListener: ((imageFolders: List<PictureDirectory>) -> Unit)? = null
    @Suppress("PrivatePropertyName")
    private val IMAGE_PROJECTION = arrayOf(
            _ID,            //图片id
            DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            SIZE,           //图片的大小，long型  132492
            MIME_TYPE,      //图片的类型     image/jpeg
            DATE_ADDED)     //图片被添加的时间，long型  1450518608

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return CursorLoader(context, EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, if (viewModel.showGif) null else "$MIME_TYPE!=?",
                if (viewModel.showGif) null else arrayOf("image/gif"), IMAGE_PROJECTION[5] + " DESC")
    }

    fun scan(loadedListener: (imageFolders: List<PictureDirectory>) -> Unit) {
        this.loadedListener = loadedListener
        context.supportLoaderManager.initLoader(0, null, this)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
        val directories = ArrayList<PictureDirectory>()
        data?.apply {
            val allPictures = arrayListOf<Picture>()
            data.also {
                while (moveToNext()) {
                    val id = getString(getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                    val name = getString(getColumnIndexOrThrow(IMAGE_PROJECTION[1]))
                    val path = getString(getColumnIndexOrThrow(IMAGE_PROJECTION[2]))
                    val size = getLong(getColumnIndexOrThrow(IMAGE_PROJECTION[3]))
                    val type = getString(getColumnIndexOrThrow(IMAGE_PROJECTION[4]))
                    val date = getLong(getColumnIndexOrThrow(IMAGE_PROJECTION[5]))

                    if (path.isNullOrBlank() || !File(path).isFile || size == 0L)
                        continue
                    val picture = Picture(id, name, path, size, type, date)
                    allPictures.add(picture)

                    val parentFile = File(path).parentFile
                    var directory = directories.find { it.dirPath == parentFile.absolutePath }
                    if (directory == null) {
                        directory = PictureDirectory(parentFile.name, parentFile.absolutePath, picture, arrayListOf(picture))
                        directories.add(directory)
                    } else {
                        directory.pictures.add(picture)
                    }
                }
            }
            if (allPictures.size > 0) {
                //构造所有图片的集合
                val allImagesFolder = PictureDirectory(context.resources.getString(R.string.album_str_all_image), "/", allPictures[0], allPictures, viewModel.showCamera)
                directories.add(0, allImagesFolder)
            }
            loadedListener?.invoke(directories)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        Log.d("PictureScanner", "onLoaderReset")
    }
}