package gorden.lib.video

import android.view.View
import java.lang.ref.WeakReference
import kotlin.math.min


internal class ExMeasureHelper(view: View) {
    private val mWeakView: WeakReference<View> = WeakReference(view)

    private var mVideoWidth: Int = 0
    private var mVideoHeight: Int = 0
    private var mVideoSarNum: Int = 0
    private var mVideoSarDen: Int = 0

    private var mVideoRotationDegree: Int = 0

    private var mMeasuredWidth: Int = 0
    private var mMeasuredHeight: Int = 0
    private var mCurrentAspectRatio = ExRenderView.AspectRatio.FIT

    fun getView(): View? = mWeakView.get()

    fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        mVideoWidth = videoWidth
        mVideoHeight = videoHeight
    }

    fun setAspectRatio(aspectRatio: ExRenderView.AspectRatio) {
        mCurrentAspectRatio = aspectRatio
    }

    fun setVideoSampleAspectRatio(videoSarNum: Int, videoSarDen: Int) {
        mVideoSarNum = videoSarNum
        mVideoSarDen = videoSarDen
    }

    fun setVideoRotation(videoRotationDegree: Int) {
        mVideoRotationDegree = videoRotationDegree
    }

    fun getMeasuredWidth(): Int {
        return mMeasuredWidth
    }

    fun getMeasuredHeight(): Int {
        return mMeasuredHeight
    }

    fun measure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpec: Int;
        val heightSpec: Int
        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
            widthSpec = heightMeasureSpec
            heightSpec = widthMeasureSpec
        } else {
            widthSpec = widthMeasureSpec
            heightSpec = heightMeasureSpec
        }

        var width = View.getDefaultSize(mVideoWidth, widthSpec)
        var height = View.getDefaultSize(mVideoWidth, heightSpec)

        if (mCurrentAspectRatio == ExRenderView.AspectRatio.FILL) {
            width = widthSpec
            height = heightSpec
        } else if (mVideoWidth * mVideoHeight > 0) {
            val widthSpecMode = View.MeasureSpec.getMode(widthSpec)
            val widthSpecSize = View.MeasureSpec.getSize(widthSpec)
            val heightSpecMode = View.MeasureSpec.getMode(heightSpec)
            val heightSpecSize = View.MeasureSpec.getSize(heightSpec)
            if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
                val specAspectRatio = widthSpecSize.toFloat() / heightSpecSize.toFloat()
                var displayAspectRatio: Float
                when (mCurrentAspectRatio) {
                    ExRenderView.AspectRatio.FIT_16_9 -> {
                        displayAspectRatio = 16f / 9f
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                            displayAspectRatio = 1f / displayAspectRatio
                        }
                    }
                    ExRenderView.AspectRatio.FIT_4_3 -> {
                        displayAspectRatio = 4f / 3f
                        if (mVideoRotationDegree == 90 || mVideoRotationDegree == 270) {
                            displayAspectRatio = 1f / displayAspectRatio
                        }
                    }
                    else -> {
                        displayAspectRatio = mVideoWidth.toFloat() / mVideoHeight
                        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
                            displayAspectRatio = displayAspectRatio * mVideoSarNum / mVideoSarDen
                        }
                    }
                }
                val shouldBeWider = displayAspectRatio > specAspectRatio
                when (mCurrentAspectRatio) {
                    ExRenderView.AspectRatio.FIT,
                    ExRenderView.AspectRatio.FIT_16_9,
                    ExRenderView.AspectRatio.FIT_4_3 -> {
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = widthSpecSize
                            height = (width / displayAspectRatio).toInt()
                        } else {
                            // too high, fix height
                            height = heightSpecSize
                            width = (height * displayAspectRatio).toInt()
                        }
                    }

                    ExRenderView.AspectRatio.FILL -> {
                        if (shouldBeWider) {
                            // not high enough, fix height
                            height = heightSpecSize
                            width = (height * displayAspectRatio).toInt()
                        } else {
                            // not wide enough, fix width
                            width = widthSpecSize
                            height = (width / displayAspectRatio).toInt()
                        }
                    }
                    else -> {
                        if (shouldBeWider) {
                            // too wide, fix width
                            width = min(mVideoWidth, widthSpecSize)
                            height = (width / displayAspectRatio).toInt()
                        } else {
                            // too high, fix height
                            height = min(mVideoHeight, heightSpecSize)
                            width = (height * displayAspectRatio).toInt()
                        }
                    }
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY && heightSpecMode == View.MeasureSpec.EXACTLY) {
                width = widthSpecSize
                height = heightSpecSize

                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth
                }
            } else if (widthSpecMode == View.MeasureSpec.EXACTLY) {
                width = widthSpecSize
                height = width * mVideoHeight / mVideoWidth
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize
                }
            } else if (heightSpecMode == View.MeasureSpec.EXACTLY) {
                height = heightSpecSize
                width = height * mVideoWidth / mVideoHeight
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize
                }
            } else {
                width = mVideoWidth
                height = mVideoHeight
                if (heightSpecMode == View.MeasureSpec.AT_MOST && height > heightSpecSize) {
                    height = heightSpecSize
                    width = height * mVideoWidth / mVideoHeight
                }
                if (widthSpecMode == View.MeasureSpec.AT_MOST && width > widthSpecSize) {
                    width = widthSpecSize
                    height = width * mVideoHeight / mVideoWidth
                }
            }

        }
        mMeasuredWidth = width
        mMeasuredHeight = height
    }
}