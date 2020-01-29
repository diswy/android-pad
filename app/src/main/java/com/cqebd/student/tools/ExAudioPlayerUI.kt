package com.cqebd.student.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.cqebd.student.R
import com.cqebd.student.app.App
import com.cqebd.student.db.dao.Attachment
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class ExAudioPlayerUI constructor(val context: Context, val mBtnPlay: ImageButton, val mProgressBar: ProgressBar, val mTextProgress: TextView) {
    private val STATE_ERROR = -1
    private val STATE_IDLE = 0
    private val STATE_PREPARING = 1
    private val STATE_PREPARED = 2
    private val STATE_PLAYING = 3
    private val STATE_PAUSE = 4
    private val STATE_PLAYBACK_COMPLETED = 5

    private var mCurrentState = STATE_IDLE


    private val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
    private val defaultDataSourceFactory = DefaultDataSourceFactory(context, "audio/mpeg")

    var attachment: Attachment? = null

    private var sharedPreferences: SharedPreferences = App.mContext.getSharedPreferences("audio_cache", Context.MODE_PRIVATE)
    private val attachmentDao = App.getDaoSession().attachmentDao

    init {
        mBtnPlay.setOnClickListener {
            start()
        }
    }

    fun start() {
        if (isInPlaybackState() && attachment != null) {
            if (attachment!!.canWatchCount > 0 && attachment!!.watchCount >= attachment!!.canWatchCount) {
                toast("你最多只能听 " + attachment!!.canWatchCount + " 遍")
                return
            }
            mProgressBar.post(progressRunnable)
            mBtnPlay.setImageResource(R.drawable.ic_stop1)

            mCurrentState = STATE_PLAYING
            val mediaSource = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(Uri.parse(attachment!!.url))
            player.playWhenReady = true
            player.addListener(videoListener)
            player.prepare(mediaSource)
        } else if (mCurrentState == STATE_PLAYING) {
            mBtnPlay.setImageResource(R.drawable.ic_play1)
            player.playWhenReady = false
            mCurrentState = STATE_PAUSE
        } else if (mCurrentState == STATE_PAUSE) {
            mBtnPlay.setImageResource(R.drawable.ic_stop1)
            player.playWhenReady = true
            mCurrentState = STATE_PLAYING
        }
    }

    private fun isInPlaybackState(): Boolean {
        return mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PLAYING &&
                mCurrentState != STATE_PREPARING &&
                mCurrentState != STATE_PAUSE
    }

    fun openAudio(mAttachment: Attachment) {
        if (this.attachment == null) {
            this.attachment = mAttachment
            mCurrentState = STATE_PREPARED
        } else if (this.attachment!!.id != mAttachment.id) {
            this.attachment = mAttachment
            mCurrentState = STATE_PREPARED
        }
    }


    fun release() {
        mProgressBar.progress = 0
        setProgress(0, 0)
        mProgressBar.removeCallbacks(progressRunnable)
        attachment = null
        mCurrentState = STATE_IDLE
        player.seekTo(0)
        player.stop()
        mBtnPlay.setImageResource(R.drawable.ic_play1)
    }

    private val videoListener: Player.DefaultEventListener = object : Player.DefaultEventListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    mProgressBar.max = player.duration.toInt()
                    val progress = sharedPreferences.getInt(attachment?.id, 0)
                    if (progress != 0) {
                        mProgressBar.progress = progress
                        player.seekTo(progress.toLong())
                        setProgress(progress, player.duration.toInt())
                    } else {
                        setProgress(0, player.duration.toInt())
                    }
                }
                Player.STATE_BUFFERING -> {
                }
                Player.STATE_ENDED -> {
                    attachment?.let {
                        it.watchCount = it.watchCount + 1
                        attachmentDao.insertOrReplace(it)
                    }

                    mBtnPlay.setImageResource(R.drawable.ic_play1)
                    mCurrentState = STATE_PLAYBACK_COMPLETED
                    mListener?.invoke()
                }
                Player.STATE_IDLE -> {
                }
            }
        }

    }

    fun onDestroy() {
        release()
        player.release()
        progressRunnable = null
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun setProgress(current: Int, max: Int) {
        mTextProgress.text = (String.format("%02d", current / 1000 / 60) + ":" + String.format("%02d", current / 1000 % 60)
                + "/" + String.format("%02d", max / 1000 / 60) + ":" + String.format("%02d", max / 1000 % 60))
    }

    private var progressRunnable: Runnable? = object : Runnable {
        override fun run() {
            val progress = player.currentPosition
            sharedPreferences.edit().putInt(attachment?.id, progress.toInt()).apply()
            mProgressBar.progress = progress.toInt()
            setProgress(progress.toInt(), player.duration.toInt())
            mProgressBar.postDelayed(this, (1000 - progress % 1000))
        }
    }

    private var mListener: (() -> Unit)? = null

    fun setOnCompletionListener(listener: () -> Unit) {
        this.mListener = listener
    }

}