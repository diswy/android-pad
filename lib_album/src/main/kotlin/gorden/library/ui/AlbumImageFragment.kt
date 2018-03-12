package gorden.library.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gorden.library.R
import kotlinx.android.synthetic.main.fragment_album_image.*
import me.panpf.sketch.decode.ImageAttrs
import me.panpf.sketch.display.FadeInImageDisplayer
import me.panpf.sketch.drawable.SketchGifDrawable
import me.panpf.sketch.request.*
import me.panpf.sketch.util.SketchUtils

/**
 * 描述
 * Created by gorden on 2018/3/9.
 */
class AlbumImageFragment : Fragment(), DisplayListener, DownloadProgressListener{
    private var imageUrl:String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageView.isZoomEnabled = true
        imageView.options.isDecodeGifImage = true
        imageView.zoomer?.blockDisplayer?.setPause(!isVisibleToUser())
        imageView.options.displayer = FadeInImageDisplayer()

        imageView.displayListener = this
        imageView.downloadProgressListener = this
        imageUrl = arguments!!.getString("url")
        imageView.displayImage(imageUrl)
    }


    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed) {
            onUserVisibleChanged(isVisibleToUser)
        }
    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            onUserVisibleChanged(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            onUserVisibleChanged(true)
        }
    }

    private fun onUserVisibleChanged(isVisibleToUser: Boolean) {
        imageView.zoomer?.blockDisplayer?.setPause(!isVisibleToUser)
        val drawable = SketchUtils.getLastDrawable(imageView.drawable)
        if (drawable is SketchGifDrawable) {
            drawable.followPageVisible(isVisibleToUser, false)
        }
    }

    private fun isVisibleToUser(): Boolean {
        return isResumed && userVisibleHint
    }


    override fun onStarted() {
        hintView.loading()
    }

    override fun onCanceled(cause: CancelCause) {
    }

    override fun onError(cause: ErrorCause) {
        hintView.hint(R.drawable.ic_error,"图片加载失败","重试", View.OnClickListener { imageView.displayImage(imageUrl) })
    }

    override fun onCompleted(drawable: Drawable, imageFrom: ImageFrom, imageAttrs: ImageAttrs) {
        hintView.hidden()
        val lastDrawable = SketchUtils.getLastDrawable(imageView.drawable)
        if (lastDrawable is SketchGifDrawable) {
            lastDrawable.followPageVisible(isVisibleToUser(), true)
        }
    }

    override fun onUpdateDownloadProgress(totalLength: Int, completedLength: Int) {
        hintView.setProgress(completedLength,totalLength)
    }
}