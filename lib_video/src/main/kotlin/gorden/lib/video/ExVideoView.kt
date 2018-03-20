package gorden.lib.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.anko.static.dp
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ExtractorMediaSource
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
    private lateinit var player: SimpleExoPlayer
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
    private lateinit var controlDispatcher: ControlDispatcher

    private var isPlayError: Boolean = false
    private var mDuration: Int = 0

    /**
     * 是否可以调节进度
     */
    var canSeek: Boolean = true
    /**
     * 是否保存播放进度
     */
    var saveProgress: Boolean = true
    private lateinit var saveKey: String

    constructor(context: Context?) : super(context) {
        initVideoView(context)
        player.videoComponent
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


        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        renderView.addRenderCallback(object : ExRenderView.IRenderCallback {
            override fun onSurfaceCreated(holder: ExRenderView.ISurfaceHolder, width: Int, height: Int) {
                player.setVideoSurface(holder.openSurface())
            }

            override fun onSurfaceChanged(holder: ExRenderView.ISurfaceHolder, width: Int, height: Int) {
            }

            override fun onSurfaceDestroyed(holder: ExRenderView.ISurfaceHolder) {
                player.clearVideoSurface()
            }
        })

        renderView.setAspectRatio(ExRenderView.AspectRatio.FIT)
        componentListener = ComponentListener()
        player.audioAttributes = AudioAttributes.DEFAULT
        player.addListener(componentListener)
        player.addVideoListener(componentListener)

        mediaDataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, context?.applicationInfo?.name), DefaultBandwidthMeter())
        controlDispatcher = DefaultControlDispatcher()

        videoContent.setOnClickListener {
            if (mediaController.isShowing()) {
                mediaController.hide()
            } else {
                mediaController.show()
            }
        }
        mediaController.setMediaPlayer(this)
        mediaController.show()
        requestFocus()
        audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * 设置视频路径
     * @param path 视频播放地址
     * @param thumb 视频加载前显示图片
     */
    fun setVideoPath(path: String, title: String, thumb: Int? = null, saveKey: String = path) {
        setVideoPath(Uri.parse(path), title, thumb, saveKey)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setVideoPath(uri: Uri, title: String, thumb: Int? = null, saveKey: String = uri.path) {
        mUri = uri
        mThumb = thumb
        this.saveKey = saveKey
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
    }


    private fun openVideo(positionMs: Long? = null, clearState: Boolean = true) {
        val mediaSource = ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(mUri)
        player.prepare(mediaSource, true, true)
        if (positionMs != null) {
            player.seekTo(positionMs)
        }else if (saveProgress){
            val progress = loadProgress()
            if (progress>0){
                player.seekTo(progress)
            }
        }
        if (clearState) {
            isPlayError = false
            player.playWhenReady = true
            mediaController.isEnabled = true
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
            if (playbackState == Player.STATE_READY) {
                isPlayError = false
            }
            if (playbackState == Player.STATE_BUFFERING) {
                loadingView.visibility = View.VISIBLE
            } else {
                loadingView.visibility = View.GONE
            }
            if (playWhenReady){
                (context as? Activity)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }else{
                (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            saveProgress(player.currentPosition)
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            isPlayError = true
            openVideo(player.currentPosition, false)
        }

    }

    /**
     * 获取音频焦点
     */
    private fun requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(AudioFocusRequest.Builder(AudioManager.STREAM_MUSIC).setFocusGain(AudioManager.AUDIOFOCUS_GAIN)
                    .setOnAudioFocusChangeListener {
                        if (it == AudioManager.AUDIOFOCUS_LOSS) {
                            pause()
                        }
                    }
                    .build())
        } else {
            audioManager.requestAudioFocus({
                if (it == AudioManager.AUDIOFOCUS_LOSS)
                    pause()
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        logError("dispatchKeyEvent  ${event.keyCode}")
        return mediaController.dispatchKeyEvent(event)
    }

    fun release() {
        player.release()
        mediaController.release()
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
     * 退出全屏模式
     */
    internal fun exitFullScreen() {
        if (screenMode == SCREEN_WINDOW) return
        screenMode = SCREEN_WINDOW
        mediaController.updateControllerBar(false)
        (context as? Activity)?.requestedOrientation = PORTRAIT_ORIENTATION
        (videoContent.parent as ViewGroup).removeView(videoContent)
        addView(videoContent, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        videoContent.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        (context as? Activity)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


    private val mPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("video_progress", Context.MODE_PRIVATE)
    }

    /**
     * 保存播放进度
     */
    private fun saveProgress(progress:Long) {
        if (saveProgress&&progress>0){
            mPreferences.edit().putLong(saveKey,progress).apply()
        }
    }

    /**
     * 获取播放进度
     */
    private fun loadProgress(): Long {
        return mPreferences.getLong(saveKey,-1)
    }

    override val duration: Int
        get() {
            return if (isPlayError) {
                mDuration
            } else {
                mDuration = player.duration.toInt()
                mDuration
            }
        }
    override val currentPosition: Int
        get() = player.currentPosition.toInt()
    override val isPlaying: Boolean
        get() = player.playbackState != Player.STATE_ENDED
                && player.playbackState != Player.STATE_IDLE
                && player.playWhenReady
    override val bufferPercentage: Int
        get() = player.bufferedPercentage
    override val mediaType: String
        get() = ""
    override val isHardware: Boolean
        get() = true
    override val isInBackground: Boolean
        get() = true

    override fun start() {
        controlDispatcher.dispatchSetPlayWhenReady(player, true)
    }

    override fun pause() {
        controlDispatcher.dispatchSetPlayWhenReady(player, false)
    }

    override fun seekTo(pos: Long) {
        controlDispatcher.dispatchSeekTo(player, player.currentWindowIndex, pos)
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
}