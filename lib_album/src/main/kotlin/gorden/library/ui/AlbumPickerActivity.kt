package gorden.library.ui

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.widget.FrameLayout
import gorden.library.Album
import gorden.library.R.color.album_bgcolor
import gorden.library.aac.PictureViewModel
import gorden.library.aac.PictureViewModelFactory
import gorden.library.entity.PREVIEW_ALBUM
import gorden.library.entity.PREVIEW_DELETE
import gorden.library.entity.contentId

/**
 * 描述
 * Created by gorden on 2018/1/31.
 */
class AlbumPickerActivity : AppCompatActivity() {

    private lateinit var pictureViewModel: PictureViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this).apply {
            id = contentId
            fitsSystemWindows = true
            setBackgroundResource(album_bgcolor)
        })
        pictureViewModel = ViewModelProviders.of(this, PictureViewModelFactory(intent.extras)).get(PictureViewModel::class.java)

        pictureViewModel.onConfigurationChanged(resources.configuration)

        when (pictureViewModel.previewMode) {
            PREVIEW_ALBUM -> {
                supportFragmentManager.beginTransaction()
                        .add(contentId, AlbumPickerFragment())
                        .commitNowAllowingStateLoss()
            }
            else -> {
                supportFragmentManager.beginTransaction()
                        .add(contentId, AlbumPreviewFragment())
                        .commitNowAllowingStateLoss()
            }

        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        pictureViewModel.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var result = true
        try {
            result = super.dispatchTouchEvent(ev)
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        return result
    }

    override fun onBackPressed() {
        if (pictureViewModel.previewMode == PREVIEW_DELETE&&pictureViewModel.delList.isNotEmpty()) {
            val resultData = Intent()
            resultData.putStringArrayListExtra(Album.KEY_DEL_IMAGES, pictureViewModel.delList)
            setResult(Activity.RESULT_OK, resultData)
        }
        super.onBackPressed()
    }
}