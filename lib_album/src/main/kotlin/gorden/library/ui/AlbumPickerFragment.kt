package gorden.library.ui

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.request.RequestOptions
import gorden.library.Album
import gorden.library.Album.Companion.KEY_IMAGES
import gorden.library.Album.Companion.REQUEST_CAMERA
import gorden.library.Album.Companion.mCurrentPhotoPath
import gorden.library.R
import gorden.library.aac.PictureViewModel
import gorden.library.adapter.DirAdapter
import gorden.library.adapter.GridItemDecoration
import gorden.library.adapter.PictureAdapter
import gorden.library.entity.*
import kotlinx.android.synthetic.main.fragment_album_picker.*
import java.util.*

/**
 * 图片列表
 * Created by gorden on 2018/1/31.
 */
class AlbumPickerFragment : Fragment() {
    private lateinit var dirBehavior: BottomSheetBehavior<*>
    private lateinit var pictureViewModel: PictureViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pictureViewModel = ViewModelProviders.of(activity!!).get(PictureViewModel::class.java)

        dirBehavior = BottomSheetBehavior.from(behaviorView)
        dirBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    view_shadow.isClickable = false
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    view_shadow.isClickable = true
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val offset = if (slideOffset.isNaN()) 0f else (slideOffset - 1)
                recycler_dir.translationX = appWidth * offset
                view_shadow.alpha = (1 + offset) * 0.7f
            }
        })

        recycler_dir.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        pictureViewModel.directoryList.observe(this, android.arch.lifecycle.Observer {
            calculateDirHeight()
            recycler_dir.adapter = DirAdapter(pictureViewModel).apply {
                setItemClickListener {
                    toggleDir()
                }
            }
        })

        recycler_album.layoutManager = GridLayoutManager(context, pictureViewModel.orientationGridColumn)
        recycler_album.addItemDecoration(GridItemDecoration(Color.TRANSPARENT))
        (recycler_album.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recycler_album.addOnScrollListener(RecyclerViewPreloader(Glide.with(this), PicturePreloadModelProvider(), PicturePreloadSizeProvider(), pictureViewModel.orientationGridColumn * 3))
        pictureViewModel.currentDirectory.observe(this, android.arch.lifecycle.Observer {
            it?.apply {
                textDirName.text = it.dirName
                recycler_album.adapter = PictureAdapter(this@AlbumPickerFragment, pictureViewModel).apply {
                    setItemClickListener { position ->
                        viewModel.preview(this@AlbumPickerFragment, PREVIEW_ALBUM, position)
                    }
                }
            }
        })

        pictureViewModel.selectedObserver.observe(this,android.arch.lifecycle.Observer {
            recycler_album.adapter.notifyDataSetChanged()
            refreshConfirm()
        })

        pictureViewModel.scanPictures(activity)

        refreshConfirm()

        textDirName.setOnClickListener {
            toggleDir()
        }
        view_shadow.setOnClickListener {
            toggleDir()
        }
        view_shadow.isClickable = false

        btn_back.setOnClickListener {
            activity?.finish()
        }

        btn_confirm.setOnClickListener {
            val resultData = Intent()
            resultData.putStringArrayListExtra(KEY_IMAGES, pictureViewModel.selectedPaths)
            activity?.setResult(RESULT_OK, resultData)
            activity?.finish()
        }

        btn_preview.setOnClickListener {
            pictureViewModel.preview(this, PREVIEW_SELECT)
        }
    }

    /**
     * 计算dir高度
     */
    private fun calculateDirHeight() {
        if (pictureViewModel.directoryList.value != null) {
            val dirCount = pictureViewModel.directoryList.value!!.size
            val configuration = resources.configuration
            val maxHeight = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                configuration.screenHeightDp / 3 * 2
            } else {
                configuration.screenHeightDp - 100
            }
            if (100 * dirCount > maxHeight) {
                recycler_dir.layoutParams.height = maxHeight.dp
            } else {
                recycler_dir.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
            }
        } else {
            recycler_dir.layoutParams.height = FrameLayout.LayoutParams.WRAP_CONTENT
        }
        recycler_dir.requestLayout()
    }

    /**
     * 开关相册目录
     */
    private fun toggleDir() {
        if (dirBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            dirBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else if (dirBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            dirBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    /**
     * 刷新完成按钮
     */
    fun refreshConfirm() {
        if (pictureViewModel.selectMode == SINGLE_SELECT_MODE) {
            btn_confirm.visibility = View.GONE
            btn_preview.visibility = View.GONE
        } else {
            val size = pictureViewModel.selectedPaths.size
            btn_confirm.text = if (size > 0 && pictureViewModel.maxCount > 1)
                String.format(getString(R.string.album_str_complete), "($size/${pictureViewModel.maxCount})")
            else
                String.format(getString(R.string.album_str_complete), "")

            btn_confirm.isEnabled = size > 0
            btn_confirm.setTextColor(ContextCompat.getColor(context!!, if (size > 0) R.color.album_btn_textcolor else R.color.album_btn_textcolor_e))

            btn_preview.isEnabled = size > 0
            btn_preview.setTextColor(ContextCompat.getColor(context!!, if (size > 0) R.color.album_btn_textcolor else R.color.album_btn_textcolor_e))
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        calculateDirHeight()
        (recycler_album.layoutManager as GridLayoutManager).spanCount = pictureViewModel.orientationGridColumn
        recycler_album.adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (mCurrentPhotoPath.isNullOrEmpty()) return
            Album.scanFile(context!!, mCurrentPhotoPath!!)
        }
    }

    /**
     * 图片预加载到内存
     */
    private inner class PicturePreloadModelProvider : ListPreloader.PreloadModelProvider<Picture> {
        override fun getPreloadItems(position: Int): MutableList<Picture> {
            pictureViewModel.currentDirectory.value?.apply {
                return if (pictures.isEmpty() || showCamera && position == 0) {
                    Collections.emptyList()
                } else {
                    if (showCamera) {
                        Collections.singletonList(pictures[position - 1])
                    } else {
                        Collections.singletonList(pictures[position])
                    }
                }
            }
            return Collections.emptyList()
        }

        override fun getPreloadRequestBuilder(item: Picture): RequestBuilder<*>? {
            return Glide.with(this@AlbumPickerFragment).asBitmap().apply(RequestOptions.overrideOf(pictureViewModel.orientationItemSize)).load(item.path)
        }
    }

    private inner class PicturePreloadSizeProvider : ListPreloader.PreloadSizeProvider<Picture> {
        override fun getPreloadSize(item: Picture, adapterPosition: Int, perItemPosition: Int): IntArray? {
            return intArrayOf(pictureViewModel.orientationItemSize, pictureViewModel.orientationItemSize)
        }

    }
}