package com.cqebd.student.widget

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Message
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.anko.static.dp
import com.cqebd.student.R
import com.cqebd.student.glide.GlideApp
import com.cqebd.student.glide.RoundedTransformation
import java.lang.ref.WeakReference

/**
 * 广告轮播ViewPager
 * Created by gorden on 2018/2/27.
 */
class AdViewPager(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var mViewPager: ViewPager
    //是否正在播放
    private var mIsAutoPlaying = false
    //网络图片资源
    private val mImageUrls = arrayListOf<String>()
    private val mAutoPlayHandler: AutoPlayHandler

    init {
        clipChildren = false
        //添加ViewPager
        mViewPager = ViewPager(context)
        addView(mViewPager, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            leftMargin = 20.dp
            rightMargin = 20.dp
        })
        mViewPager.pageMargin = 10.dp
        mViewPager.offscreenPageLimit = 2

        mViewPager.setPageTransformer(true, ZoomInPageTransformer())
        mAutoPlayHandler = AutoPlayHandler(mViewPager)
        startAutoPlay()
    }

    /**
     * 开始播放
     */
    private fun startAutoPlay() {
        if (!mIsAutoPlaying) {
            mIsAutoPlaying = true
            mAutoPlayHandler.sendEmptyMessageDelayed(WAIT_AUTO_PLAY, mAutoPlayTime.toLong())
        }
    }

    /**
     * 停止播放
     */
    private fun stopAutoPlay() {
        if (mIsAutoPlaying) {
            mIsAutoPlaying = false
            mAutoPlayHandler.removeMessages(WAIT_AUTO_PLAY)
        }
    }

    private class ZoomInPageTransformer : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {
            val pageWidth = page.width
            val pageHeight = page.height
            val mMinScale = 0.85f
            page.pivotY = pageHeight.toFloat() / 2
            page.pivotX = pageWidth.toFloat() / 2

            if (position < -1) {
                page.scaleX = mMinScale
                page.scaleY = mMinScale
                page.pivotX = pageWidth.toFloat()
            } else if (position <= 1) {
                if (position < 0) {
                    val scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale
                    page.scaleX = scaleFactor
                    page.scaleY = scaleFactor
                    page.pivotX = pageWidth * (0.5f + 0.5f * -position)
                } else {
                    val scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale
                    page.scaleX = scaleFactor
                    page.scaleY = scaleFactor
                    page.pivotX = pageWidth * ((1 - position) * 0.5f)
                }
            } else {
                page.pivotX = 0f
                page.scaleX = mMinScale
                page.scaleY = mMinScale
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (mImageUrls.size > 1) {
            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> stopAutoPlay()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL
                    , MotionEvent.ACTION_OUTSIDE -> startAutoPlay()
            }
        }
        mViewPager.dispatchTouchEvent(ev)
        return true
    }

    /**
     * 返回真实的位置
     */
    private fun toRealPosition(position: Int): Int {
        return position % mImageUrls.size
    }

    private inner class AdPageAdapter : PagerAdapter() {
        override fun getCount(): Int {
            if (mImageUrls.size == 1) {
                return 1
            }
            return Int.MAX_VALUE
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item = createAdView(mImageUrls[toRealPosition(position)])
            container.addView(item)
            return item
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    private fun createAdView(url:String):View{
        return FrameLayout(context).apply {
            addView(ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_XY
                GlideApp.with(context).asBitmap().load(url).placeholder(R.drawable.img_loading)
                        .transform(RoundedTransformation(5.dp)).into(this)
            })

            addView(TextView(context).apply {
                text=""
                setTextColor(Color.WHITE)
                textSize = 18f
                setPadding(10.dp,5.dp,0,5.dp)
                setBackgroundResource(R.drawable.bg_ad_title)
            },LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
                    .apply {
                        gravity = Gravity.BOTTOM
                    })
        }
    }

    fun setImagesUrl(urls: List<String>) {
        mImageUrls.clear()
        mImageUrls.addAll(urls)
        mViewPager.adapter = AdPageAdapter()

        val middle = Integer.MAX_VALUE / 2
        val extra = middle % mImageUrls.size
        val item = middle - extra
        mViewPager.currentItem = item
    }

    companion object {
        //自动播放时间
        private const val mAutoPlayTime = 3000
        private const val WAIT_AUTO_PLAY = 1000

        private class AutoPlayHandler(viewPager: ViewPager) : Handler() {
            private var weakReference: WeakReference<ViewPager>? = null

            init {
                weakReference = WeakReference(viewPager)
            }

            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                weakReference?.get()?.let {
                    if (it.currentItem < Int.MAX_VALUE - 1) {
                        it.currentItem = it.currentItem + 1
                        sendEmptyMessageDelayed(WAIT_AUTO_PLAY, mAutoPlayTime.toLong())
                    }
                }
            }
        }
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            if (mImageUrls.size > 1) {
                startAutoPlay()
            }
        } else {
            stopAutoPlay()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        mAutoPlayHandler.removeCallbacksAndMessages(null)
    }
}