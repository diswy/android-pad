package gorden.lib.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import android.view.View
import com.netease.neliveplayer.sdk.NELivePlayer
import kotlin.collections.HashMap


class ExTextureRenderView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
        TextureView(context, attrs, defStyleAttr),ExRenderView,TextureView.SurfaceTextureListener{

    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurfaceWidth:Int = 0
    private var mSurfaceHeight:Int = 0
    private var mTextureSizeChanged = false
    private val mMeasureHelper:ExMeasureHelper = ExMeasureHelper(this)
    private val mRenderCallbackMap:HashMap<ExRenderView.IRenderCallback,Any> = HashMap()

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)

    init {
        surfaceTextureListener = this
    }

    override fun getView(): View {
        return this
    }

    override fun setVideoSize(videoWidth: Int, videoHeight: Int) {
        if (videoWidth>0&&videoHeight>0){
            mMeasureHelper.setVideoSize(videoWidth,videoHeight)
            requestLayout()
        }
    }

    override fun setVideoRotation(degree: Int) {
        mMeasureHelper.setVideoRotation(degree)
        rotation = degree.toFloat()
    }

    override fun setAspectRatio(aspectRatio: ExRenderView.AspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio)
        requestLayout()
    }

    override fun setVideoSampleAspectRatio(videoSarNum: Int, videoSarDen: Int) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen)
            requestLayout()
        }
    }

    override fun addRenderCallback(callback: ExRenderView.IRenderCallback) {
        mRenderCallbackMap[callback] = callback
        val surfaceHolder: InternalSurfaceHolder by lazy { InternalSurfaceHolder(this, mSurfaceTexture) }
        if (mSurfaceTexture!=null){
            callback.onSurfaceCreated(surfaceHolder,mSurfaceWidth,mSurfaceHeight)
        }
        if (mTextureSizeChanged){
            callback.onSurfaceChanged(surfaceHolder,mSurfaceWidth,mSurfaceHeight)
        }
    }

    override fun removeRenderCallback(callback: ExRenderView.IRenderCallback) {
        mRenderCallbackMap.remove(callback)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mMeasureHelper.measure(widthMeasureSpec,heightMeasureSpec)
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(),mMeasureHelper.getMeasuredHeight())
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture?) {}
    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
        mSurfaceTexture = surfaceTexture
        mSurfaceWidth = 0
        mSurfaceHeight = 0
        mTextureSizeChanged = false
        mRenderCallbackMap.forEach {
            it.key.onSurfaceCreated(InternalSurfaceHolder(this, mSurfaceTexture),mSurfaceWidth,mSurfaceHeight)
        }
    }

    override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
        mSurfaceTexture = surfaceTexture
        mSurfaceWidth = width
        mSurfaceHeight = height
        mTextureSizeChanged = true
        mRenderCallbackMap.forEach {
            it.key.onSurfaceChanged(InternalSurfaceHolder(this, mSurfaceTexture),mSurfaceWidth,mSurfaceHeight)
        }
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture?): Boolean {
        mSurfaceTexture = surfaceTexture
        mSurfaceWidth = 0
        mSurfaceHeight = 0
        mTextureSizeChanged = false
        mRenderCallbackMap.forEach {
            it.key.onSurfaceDestroyed(InternalSurfaceHolder(this, mSurfaceTexture))
        }
        //TODO false
        return true
    }

    private class InternalSurfaceHolder(val mRenderView: ExRenderView,val mSurfaceTexture: SurfaceTexture?):ExRenderView.ISurfaceHolder{
        override val renderView: ExRenderView get() = mRenderView
        override val surfaceHolder: SurfaceHolder? = null
        override val surfaceTexture: SurfaceTexture? get() = mSurfaceTexture
        override fun openSurface(): Surface? {
            if (mSurfaceTexture==null){
                return null
            }
            return Surface(mSurfaceTexture)
        }

        override fun bindToMediaPlayer(mp: NELivePlayer) {
            mp.setSurface(openSurface())
        }
    }
}