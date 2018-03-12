package gorden.library.entity

import android.content.res.Resources

/**
 * 保存常量
 * Created by gorden on 2018/1/31.
 */
internal const val SINGLE_SELECT_MODE = 701
internal const val MULTI_SELECT_MODE = 702
internal const val DEFAULT_MAX_COUNT = 9
internal const val DEFAULT_GRID_COLUMN = 3


internal const val EXTRA_SELECT_MODE = "EXTRA_SELECT_MODE" //选择模式、单选 多选
internal const val EXTRA_MAX_COUNT = "MAX_COUNT"   //多选模式,最多选择多少张
internal const val EXTRA_SHOW_CAMERA = "SHOW_CAMERA"//显示相机
internal const val EXTRA_SHOW_GIF = "SHOW_GIF"//显示gif
internal const val EXTRA_GRID_COLUMN = "GRID_COLUMN"//每排显示多少张图
internal const val EXTRA_SELECTED_PATH = "SELECTED_PATH"//已选择图片的地址
internal const val EXTRA_PREVIEW_MODE = "EXTRA_PREVIEW_MODE"//图片浏览模式
internal const val EXTRA_PREVIEW_POSITION = "EXTRA_PREVIEW_POSITION"//图片浏览position

internal const val PREVIEW_ALBUM = 702
internal const val PREVIEW_SELECT = 703
internal const val PREVIEW_PREVIEW = 704
internal const val PREVIEW_DELETE = 705

internal const val contentId = 0xC0200E
private val density = Resources.getSystem().displayMetrics.density
internal val Int.dp:Int get() = (this* density).toInt()
val appWidth = Resources.getSystem().displayMetrics.widthPixels
val appHeight = Resources.getSystem().displayMetrics.heightPixels