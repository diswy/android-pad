package gorden.lib.video

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.*
import android.widget.*
import gorden.lib.anko.static.logInfo
import kotlinx.android.synthetic.main.mediacontroller.view.*
import org.jetbrains.anko.dip
import java.util.*

@SuppressLint("ViewConstructor")
@Suppress("PrivatePropertyName", "unused")
/**
 * 描述
 * Created by gorden on 2018/3/14.
 */
class ExMediaController(context: Context?, val title: ExMediaTitle) : FrameLayout(context), SeekBar.OnSeekBarChangeListener {
    private var mShowing: Boolean = false
    private var mDragging: Boolean = false
    private var mPaused: Boolean = false
    private var mIsFullScreen = false
    private var mInstantSeeking = false //实时响应拖动
    private var mIsLiveMode = false// 直播模式下不可拖动不显示进度
    private var mDuration: Long = 0
    private val DEFAULT_TIME_OUT: Int = 5000//MediaController,显示 DEFAULT_TIMEOUT 时长后自动隐藏。
    private lateinit var mPlayer: MediaPlayerControl
    private var definitionList: List<ExDefinition>? = null // 清晰度列表
    private var mTitle: String = ""
    private var mCurrentDefinitionCode: Int = 0


    fun setDefinitionList(definitionList: List<ExDefinition>) {
        this.definitionList = definitionList
    }

    fun setTitle(title: String) {
        this.mTitle = title
    }

    fun setCurrentDefinitionCode(code: Int) {
        this.mCurrentDefinitionCode = code
    }

    fun setLiveMode(isLiveMode: Boolean) {
        this.mIsLiveMode = isLiveMode
        textStart.text = "00:00"
        textDuration.text = "--:--"
        seekBar.progress = 0
    }


    //handler message what
    private val FADE_OUT = 1
    private val SHOW_PROGRESS = 2

    init {
        LayoutInflater.from(context).inflate(R.layout.mediacontroller, this, true)

        btn_start.setOnClickListener {
            doPauseResume()
            show()
        }
        btn_fullscreen.setOnClickListener {
            if (mIsFullScreen) {
                mIsFullScreen = false
            } else {
                mIsFullScreen = true
                btn_fullscreen.visibility = View.GONE
            }
            mPlayer.fullScreen(mIsFullScreen)
        }
        text_code.setOnClickListener {
            if (definition_layout.visibility == View.VISIBLE) {
                definition_layout.visibility = View.GONE
                return@setOnClickListener
            }

            if (definitionList != null && definitionList!!.isNotEmpty()) {// safe init
                definition_layout.removeAllViews()
                for (definition in definitionList!!) {
                    val tv = TextView(context)
                    tv.text = definition.definitionName
                    tv.gravity = Gravity.CENTER
                    tv.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, dip(40f))
                    tv.setOnClickListener {
                        if (definition.definitionCode != mCurrentDefinitionCode) {
                            mPlayer.switchDefinition(definition.definitionPath, mTitle)
                            text_code.text = definition.definitionName
                            mCurrentDefinitionCode = definition.definitionCode
                            hide()
                        }

                    }
                    definition_layout.addView(tv)
                }
            }

            if (definitionList == null){
                definition_layout.removeAllViews()
                val tv = TextView(context)
                tv.text = "默认"
                tv.gravity = Gravity.CENTER
                tv.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, dip(40f))
                tv.setOnClickListener {
                    hide()
                }
                definition_layout.addView(tv)
            }

            definition_layout.visibility = View.VISIBLE

        }
        seekBar.setOnSeekBarChangeListener(this)
    }


    /**
     * 绑定MediaPlayerControl.MediaPlayerControl
     */
    fun setMediaPlayer(player: MediaPlayerControl) {
        this.mPlayer = player
        updatePlayStatus()
    }

    /**
     * 显示MediaPlayerControl面板
     * @param timeout 自动隐藏时间
     */
    fun show(timeout: Int = DEFAULT_TIME_OUT) {
        if (!mShowing) {
            visibility = View.VISIBLE
            title.visibility = View.VISIBLE
            mShowing = true
            showListener?.invoke(true)
            disableUnsupportedButtons()
        }
        updateControllerBar(mIsFullScreen)
        updatePlayStatus()
        mHandler.sendEmptyMessage(SHOW_PROGRESS)
        if (timeout > 0) {
            mHandler.removeMessages(FADE_OUT)
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout.toLong())
        }
    }

    fun isShowing() = mShowing

    /**
     * 隐藏MediaPlayerControl面板
     */
    fun hide() {
        if (mShowing) {
            try {
                visibility = View.GONE
                title.visibility = View.GONE
                if (definition_layout.visibility == View.VISIBLE) {
                    definition_layout.visibility = View.GONE
                }
                mHandler.removeMessages(SHOW_PROGRESS)
            } catch (ex: IllegalArgumentException) {
                logInfo("MediaController already removed")
            }
            mShowing = false
            showListener?.invoke(false)
        }
    }

    fun release() {
        mHandler.removeCallbacksAndMessages(null)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return dispatchMediaKeyEvent(event) || super.dispatchKeyEvent(event)
    }

    private fun dispatchMediaKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode
        if (!isHandledMediaKey(keyCode)) {
            return false
        }
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (event.repeatCount == 0) {
                when (keyCode) {
                    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> doPauseResume()
                    KeyEvent.KEYCODE_MEDIA_PLAY -> doPauseResume()
                    KeyEvent.KEYCODE_MEDIA_PAUSE -> doPauseResume()
                }
            }
        }
        return true
    }

    @SuppressLint("InlinedApi")
    private fun isHandledMediaKey(keyCode: Int): Boolean {
        return (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
                || keyCode == KeyEvent.KEYCODE_MEDIA_REWIND
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
                || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
                || keyCode == KeyEvent.KEYCODE_MEDIA_NEXT
                || keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    }

    /**
     * 刷新当前播放状态
     */
    private fun updatePlayStatus() {
        if (mPlayer.isPlaying) {
            btn_start.setImageResource(R.drawable.ic_pause)
        } else {
            btn_start.setImageResource(R.drawable.ic_play)
        }
    }

    fun updateControllerBar(isFull: Boolean) {
        mIsFullScreen = isFull
        if (isFull) {
            text_code.visibility = View.VISIBLE
            btn_fullscreen.visibility = View.GONE
            title.visibility(true)
        } else {
            text_code.visibility = View.GONE
            btn_fullscreen.visibility = View.VISIBLE
            title.visibility(false)
        }
    }

    private fun doPauseResume() {
        mPaused = if (mPlayer.isPlaying) {
            mPlayer.pause()
            true
        } else {
            mPlayer.start()
            false
        }
        updatePlayStatus()
    }

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                FADE_OUT -> {
                    hide()
                }
                SHOW_PROGRESS -> {
                    val position = setProgress()
                    if (!mDragging && mShowing) {
                        sendMessageDelayed(obtainMessage(SHOW_PROGRESS), 1000 - position % 1000)
                        updatePlayStatus()
                    }
                }
            }
        }
    }

    /**
     * 设置当前进度值
     */
    private fun setProgress(): Long {
        if (mDragging) return 0
        val position = mPlayer.currentPosition
        val duration = mPlayer.duration

        val percent = mPlayer.bufferPercentage
        if (!mIsLiveMode){
            seekBar.secondaryProgress = percent * 10
            textStart.text = formatTime(position)
        }

        if (duration > 0) {
            mDuration = duration.toLong()
            val pos = 1000L * position / duration
            if (!mIsLiveMode) {
                seekBar.progress = pos.toInt()
                textDuration.text = formatTime(duration)
            } else {
                textDuration.text = "--:--"
            }
        } else {
            textDuration.text = "--:--"
        }

        return position.toLong()
    }

    private fun formatTime(duration: Int): String {
        val totalSeconds = duration / 1000.0 + 0.5f
        val seconds = (totalSeconds % 60).toInt()
        val minutes = (totalSeconds / 60).toInt()
        return String.format(Locale.CHINA, "%02d:%02d", minutes, seconds)
    }

    /**
     * 禁用不受支持的按钮
     */
    private fun disableUnsupportedButtons() {
        if (!mPlayer.canPause())
            btn_start.isEnabled = false
        if (!mPlayer.canSeek()) {
            seekBar.isEnabled = false
        }
    }

    /**
     * 显示隐藏状态监听
     */
    private var showListener: ((Boolean) -> Unit)? = null

    fun setShowListener(listener: (Boolean) -> Unit) {
        showListener = listener
    }

    fun setInstantSeeking(seekWhenDragging: Boolean) {
        mInstantSeeking = seekWhenDragging
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
        show(3600000)
        mDragging = true
        mHandler.removeMessages(SHOW_PROGRESS)
    }

    private var lastRunnable: Runnable? = null
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromuser: Boolean) {
        if (!fromuser)
            return
        if (mPlayer.mediaType == "liveStream") {
            return
        }
        val newPosition = mDuration * progress / 1000
        val time = formatTime(newPosition.toInt())
        if (mInstantSeeking) {
            mHandler.removeCallbacks(lastRunnable)
            lastRunnable = Runnable {
                if (!mIsLiveMode) {
                    mPlayer.seekTo(newPosition)
                }
            }
            mHandler.postDelayed(lastRunnable, 200)
        }
        if (!mIsLiveMode) {
            textStart.text = time
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (mPlayer.mediaType == "liveStream") {
            return
        }
        if (!mInstantSeeking)
            mPlayer.seekTo(mDuration * seekBar.progress / 1000)
        show()
        mHandler.removeMessages(SHOW_PROGRESS)
        mDragging = false
        mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000)
    }

    override fun setEnabled(enabled: Boolean) {
        btn_start.isEnabled = enabled
        seekBar.isEnabled = enabled
        text_code.isEnabled = enabled
        btn_fullscreen.isEnabled = enabled
        disableUnsupportedButtons()
        super.setEnabled(enabled)
    }

    interface MediaPlayerControl {

        val duration: Int

        val currentPosition: Int

        val isPlaying: Boolean

        val bufferPercentage: Int

        val mediaType: String

        fun start()

        fun pause()

        fun seekTo(pos: Long)

        fun canPause(): Boolean

        fun canSeek(): Boolean

        fun fullScreen(full: Boolean)

        fun switchDefinition(srcPath: String, title: String)
    }
}