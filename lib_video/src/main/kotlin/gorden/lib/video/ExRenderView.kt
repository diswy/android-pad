package gorden.lib.video

import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.SurfaceHolder
import android.view.TextureView
import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * 描述
 * Created by gorden on 2018/3/14.
 */
interface ExRenderView {
    fun getView(): TextureView

    fun setVideoSize(videoWidth: Int, videoHeight: Int)
    fun setVideoRotation(degree: Int)
    //设置纵横比
    fun setAspectRatio(aspectRatio: AspectRatio)

    fun setVideoSampleAspectRatio(videoSarNum: Int, videoSarDen: Int)


    fun addRenderCallback(callback: IRenderCallback)
    fun removeRenderCallback(callback: IRenderCallback)

    interface ISurfaceHolder {
        val renderView: ExRenderView
        val surfaceHolder: SurfaceHolder?
        val surfaceTexture: SurfaceTexture?
        fun openSurface(): Surface?
        fun bindToExoPlayer(player: SimpleExoPlayer?)
    }

    interface IRenderCallback {
        fun onSurfaceCreated(holder:ISurfaceHolder,width:Int,height:Int)
        fun onSurfaceChanged(holder:ISurfaceHolder,width:Int,height:Int)
        fun onSurfaceDestroyed(holder:ISurfaceHolder)
    }



    enum class AspectRatio() {
        NONE,//原始大小
        FIT,//按比例拉伸，有一边会贴黑边
        FILL,//全屏，画面可能会变形
        FULL,//按比例拉伸至全屏，有一边会被裁剪
        FIT_16_9,//按16:9比例拉伸
        FIT_4_3,//按4:3比例拉伸
    }
}