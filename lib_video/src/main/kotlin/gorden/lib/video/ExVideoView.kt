package gorden.lib.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.*
import com.anko.static.dp
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.exoplayer2.video.VideoListener
import gorden.lib.anko.static.logError

class ExVideoView : FrameLayout, ExMediaController.MediaPlayerControl {
    //The container that actually holds the content
    private lateinit var videoContent: View
    //current display mode SCREEN_WINDOW or SCREEN_FULL
    private var screenMode: Int = SCREEN_WINDOW
    private lateinit var renderView: ExRenderView
    private lateinit var mediaTitle: ExMediaTitle
    private lateinit var mediaController: ExMediaController
    private lateinit var loadingView: ProgressBar
    private lateinit var thumbImageView: ImageView

    private var mUri: Uri? = null
    private var mThumb: Int? = null
    private var definitions: List<ExDefinition>? = null
    private var currentDefinition: ExDefinition? = null
    private lateinit var audioManager: AudioManager
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private lateinit var componentListener: ComponentListener

    //当前播放位置,用于恢复播放进度
    private var contentPosition: Long = 0
    private var contentDuration: Long = 0
    private var mPlayer: SimpleExoPlayer? = null

    /*----------------------open parameter-------------------------*/

    /**
     * 是否可以调节进度
     */
    var canSeek: Boolean = true
    /**
     * 是否保存播放进度
     * @saveProgress.first 是否保存
     * @saveProgress.second 用于存储的Key
     */
    var saveProgress: Pair<Boolean, String?> = false to null
        set(value) {
            if (field.first) {
                saveProgress(mPlayer?.contentPosition ?: 0)
            }
            field = value
        }


    constructor(context: Context?) : super(context) {
        initVideoView(context)
        saveProgress.first
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        if (isInEditMode) {
            setBackgroundColor(Color.BLACK)
            return
        }
        initVideoView(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (isInEditMode) {
            setBackgroundColor(Color.BLACK)
            return
        }
        initVideoView(context)
    }

    private fun initVideoView(context: Context?) {
        videoContent = FrameLayout(context)
        videoContent.setBackgroundColor(Color.BLACK)
        addView(videoContent, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        renderView = ExTextureRenderView(context)
        renderView.setAspectRatio(ExRenderView.AspectRatio.FIT)
        (videoContent as FrameLayout).addView(renderView.getView(), LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER))

        mediaTitle = ExMediaTitle(this)
        (videoContent as FrameLayout).addView(mediaTitle)

        mediaController = ExMediaController(context, mediaTitle)
        (videoContent as FrameLayout).addView(mediaController, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM))

        thumbImageView = ImageView(context)
        thumbImageView.visibility = View.GONE
        (videoContent as FrameLayout).addView(thumbImageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        loadingView = ProgressBar(context)
        (videoContent as FrameLayout).addView(loadingView, LayoutParams(40.dp, 40.dp, Gravity.CENTER))


        componentListener = ComponentListener()
        mediaDataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context?.applicationInfo?.name), DefaultBandwidthMeter())

        videoContent.setOnClickListener {
            if (mediaController.isShowing()) {
                mediaController.hide()
            } else {
                mediaController.show()
            }
        }
        mediaController.setMediaPlayer(this)
        mediaController.show()
        audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mediaController.setShowListener{
            showListener?.invoke(it)
        }
    }

    /**
     * 设置视频路径
     * @param path 视频播放地址
     * @param thumb 视频加载前显示图片
     */
    fun setVideoPath(path: String, title: String, thumb: Int? = null) {
        setVideoPath(Uri.parse(path), title, thumb)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setVideoPath(uri: Uri, title: String, thumb: Int? = null) {
        mUri = uri
        mThumb = thumb
        definitions = null
        currentDefinition = null
        openVideo()
        if (thumb != null) {
            thumbImageView.visibility = View.VISIBLE
            thumbImageView.setImageResource(thumb)
        }
        mediaTitle.setTitle(title)
    }

    @Suppress("unused")
    fun setVideoPath(definitions: List<ExDefinition>, current: Int = 0, title: String, thumb: Int? = null) {
        setVideoPath(Uri.parse(definitions[current].definitionPath), title, thumb)
        this.definitions = definitions
        currentDefinition = definitions[current]
        mediaController.setDefinitionList(definitions)
    }


    private fun openVideo(play: Boolean = true) {
        release()
        if (onInterceptPlayingListener?.invoke() == true || mUri == null) return

        mPlayer = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        mPlayer?.setVideoTextureView(renderView.getView())
        mPlayer?.audioAttributes = AudioAttributes.DEFAULT
        mPlayer?.addListener(componentListener)
        mPlayer?.addVideoListener(componentListener)
        val mediaSource = buildMediaSource(mUri!!)

        if (contentPosition > 0) {
            mPlayer?.seekTo(contentPosition)
        } else if (saveProgress.first) {
            mPlayer?.seekTo(loadProgress())
        }
        mPlayer?.prepare(mediaSource, false, false)
        mediaController.isEnabled = true

        if (play) {
            logError("准备播放")
            mPlayer?.playWhenReady = true
            requestAudioFocus()
        }
    }

    private inner class ComponentListener : Player.DefaultEventListener(), VideoListener {
        override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
            renderView.setVideoSize(width, height)
            renderView.setVideoRotation(unappliedRotationDegrees)
        }

        override fun onRenderedFirstFrame() {
            if (thumbImageView.isShown) {
                thumbImageView.visibility = View.GONE
            }
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            logError("playWhenReady  $playWhenReady")
            if (playWhenReady) {
                (context as? Activity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }

            when (playbackState) {
                Player.STATE_READY -> {
                    loadingView.visibility = View.GONE
                    contentDuration = mPlayer?.duration ?: 0
                }
                Player.STATE_BUFFERING -> {
                    loadingView.visibility = View.VISIBLE
                }
                Player.STATE_ENDED -> {
                    loadingView.visibility = View.GONE
                    onCompletionListener?.invoke()
                    saveProgress(0)
                    release(true)
                    mediaController.show(0)
                }
                Player.STATE_IDLE -> {
                    loadingView.visibility = View.GONE
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            Toast.makeText(context, "视频播放错误", Toast.LENGTH_SHORT).show()
            release(false)
        }
    }

    /**
     * 获取音频焦点
     */
    private fun requestAudioFocus() {
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        logError("dispatchKeyEvent  ${event.keyCode}")
        return mediaController.dispatchKeyEvent(event)
    }

    /**
     * 开启全屏模式
     */
    @SuppressLint("InlinedApi")
    private fun startFullScreen() {
        if (screenMode == SCREEN_FULL) return
        screenMode = SCREEN_FULL
        mediaController.updateControllerBar(true)
        (context as? Activity)?.requestedOrientation = FULLSCREEN_ORIENTATION
        val windowRoot = (context as? Activity)?.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
        (videoContent.parent as ViewGroup).removeView(videoContent)
        windowRoot?.addView(videoContent, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        videoContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        (context as? Activity)?.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * 释放ExoPlayer
     */
    private fun release(clearPosition: Boolean = true) {
        mPlayer?.let {
            if (clearPosition) {
                contentPosition = 0
                contentDuration = 0
            }
            it.release()
            mPlayer = null
            mediaController.release()
        }
    }

    /**
     * Activity or Fragment onStop release(true)
     */
    fun onStop() {
        mPlayer?.let {
            contentPosition = it.currentPosition
            saveProgress(contentPosition)
            it.release()
            mPlayer = null
            mediaController.release()
        }
    }

    /**
     * 用户按返回键时调用该方法
     * @return true videoView消耗掉该返回事件，用于退出全屏
     */
    fun onBackPressed(): Boolean {
        if (screenMode == SCREEN_FULL) {
            exitFullScreen()
            return true
        }
        return false
    }

    /**
     * 退出全屏模式
     */
    internal fun exitFullScreen() {
        if (screenMode == SCREEN_WINDOW) return
        screenMode = SCREEN_WINDOW
        mediaController.updateControllerBar(false)
        (context as? Activity)?.requestedOrientation = PORTRAIT_ORIENTATION
        (videoContent.parent as ViewGroup).removeView(videoContent)
        addView(videoContent, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
//        videoContent.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        videoContent.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


    private val mPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("video_progress", Context.MODE_PRIVATE)
    }

    /**
     * 保存播放进度
     */
    private fun saveProgress(progress: Long) {
        if (saveProgress.first) {
            if (progress > 0) {
                mPreferences.edit().putLong(saveProgress.second, progress).apply()
            } else {
                mPreferences.edit().remove(saveProgress.second).apply()
            }
        }
    }

    /**
     * 获取播放进度
     */
    private fun loadProgress(): Long {
        return mPreferences.getLong(saveProgress.second, -1)
    }

    override val duration: Int
        get() = contentDuration.toInt()
    override val currentPosition: Int
        get() {
            mPlayer?.let {
                contentPosition = it.contentPosition
            }
            return contentPosition.toInt()
        }
    override val isPlaying: Boolean
        get() = mPlayer?.playbackState != Player.STATE_ENDED
                && mPlayer?.playbackState != Player.STATE_IDLE
                && mPlayer?.playWhenReady == true
    override val bufferPercentage: Int
        get() = mPlayer?.bufferedPercentage ?: 0

    override val mediaType: String
        get() = ""

    override fun start() {
        if (mPlayer == null) {
            openVideo(true)
        } else {
            mPlayer?.playWhenReady = true
        }
    }

    override fun pause() {
        mPlayer?.playWhenReady = false
    }

    override fun seekTo(pos: Long) {
        mPlayer?.seekTo(pos)
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeek(): Boolean {
        return canSeek
    }

    override fun fullScreen(full: Boolean) {
        if (full) {
            startFullScreen()
        } else {
            exitFullScreen()
        }
    }

    companion object {
        private const val SCREEN_FULL = 1
        private const val SCREEN_WINDOW = 0
        private const val FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        private const val PORTRAIT_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /*-----------------------  open api  --------------------------------*/

    private var onCompletionListener: (() -> Unit)? = null
    /**
     * 播放完成监听
     */
    fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

    private var onInterceptPlayingListener: (() -> Boolean)? = null
    /**
     * 是否拦截播放，用于处理特殊情况
     * @return true 拦截播放
     */
    fun setOnInterceptPlayingListener(listener: () -> Boolean) {
        onInterceptPlayingListener = listener
    }

    /**
     * 添加HLS流的支持
     */
    private fun buildMediaSource(uri: Uri): MediaSource {
        @C.ContentType
        val type:Int = Util.inferContentType(uri)
        return when(type){
            C.TYPE_HLS -> HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(mUri)
            else -> ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(mUri)
        }
    }

    /**
     * 显示隐藏状态监听
     */
    private var showListener: ((Boolean) -> Unit)? = null

    fun setShowListener(listener: (Boolean) -> Unit) {
        showListener = listener
    }
}