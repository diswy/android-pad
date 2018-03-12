package gorden.library.aac

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import gorden.library.core.PictureScanner
import gorden.library.entity.*
import gorden.library.ui.AlbumPreviewFragment


class PictureViewModel(extras: Bundle) : ViewModel() {
    //当前选择的照片目录
    val currentDirectory = MutableLiveData<PictureDirectory>()
    val directoryList = MutableLiveData<List<PictureDirectory>>()

    val selectedObserver = MutableLiveData<Unit>()
    val delList by lazy { ArrayList<String>() }

    //配置参数
    val showCamera: Boolean = extras.getBoolean(EXTRA_SHOW_CAMERA, true)
    val showGif: Boolean = extras.getBoolean(EXTRA_SHOW_GIF, true)
    val selectMode = extras.getInt(EXTRA_SELECT_MODE, SINGLE_SELECT_MODE)
    val maxCount = extras.getInt(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT)
    val selectedPaths = extras.getStringArrayList(EXTRA_SELECTED_PATH) ?: ArrayList()
    private val gridColumn = extras.getInt(EXTRA_GRID_COLUMN, DEFAULT_GRID_COLUMN)
    var previewMode = extras.getInt(EXTRA_PREVIEW_MODE, PREVIEW_ALBUM)
    var previewPosition = extras.getInt(EXTRA_PREVIEW_POSITION, 0)

    /**
     * 扫描系统图库
     */
    fun scanPictures(activity: FragmentActivity?) {
        activity?.apply {
            PictureScanner(activity).scan {
                directoryList.value = it
                if (it.isNotEmpty()) {
                    currentDirectory.value = it[0]
                }
            }
        }
    }

    //根据屏幕方向动态计算列数 和每列大小
    var orientationGridColumn: Int = 0
    var orientationItemSize: Int = 0
    var orientation: Int = Configuration.ORIENTATION_PORTRAIT
    fun onConfigurationChanged(newConfig: Configuration?) {
        orientation = newConfig?.orientation ?: Configuration.ORIENTATION_PORTRAIT
        newConfig?.apply {
            orientationGridColumn = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                gridColumn
            } else {
                (screenWidthDp.toFloat() / screenHeightDp * gridColumn).toInt()
            }
            orientationItemSize = (screenWidthDp / orientationGridColumn).dp
        }
    }

    fun preview(fragment: Fragment, mode: Int, position: Int = 0) {
        previewMode = mode
        previewPosition = position
        fragment.activity?.supportFragmentManager?.apply {
            beginTransaction()
                    .add(contentId, AlbumPreviewFragment())
                    .hide(fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss()
        }
    }

    fun notifySelected() {
        selectedObserver.value = null
    }
}