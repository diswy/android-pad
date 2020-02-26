package gorden.lib.video

import android.content.Context
import android.net.Uri
import android.support.annotation.RawRes
import android.text.TextUtils
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource


class ExAudioPlayer constructor(val context: Context) {

    private var player: SimpleExoPlayer? = null
    private val defaultDataSourceFactory = DefaultDataSourceFactory(context, "audio/mpeg")

    fun openAudio(url: String) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        val mediaSource = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(Uri.parse(url))
        player!!.addListener(videoListener)
        player!!.playWhenReady = true
        player!!.prepare(mediaSource)
    }

    fun openRaw(context: Context, @RawRes resId: Int) {
        //构建Raw文件播放源--RawResourceDataSource
        val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(resId))
        val rawResourceDataSource = RawResourceDataSource(context)
        try {
            rawResourceDataSource.open(dataSpec)
        } catch (e: RawResourceDataSource.RawResourceDataSourceException) {
            e.printStackTrace()
        }
        player = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        //构建ExoPlayer能识别的播放源--MediaSource
        val mediaSource = ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(rawResourceDataSource.uri)
        player!!.addListener(videoListener)
        //给ExoPlayer设置播放源，并准备播放
        player!!.prepare(mediaSource)
        //让ExoPlayer准备好后就开始播放
        player!!.playWhenReady = true
    }

    private val videoListener: Player.DefaultEventListener = object : Player.DefaultEventListener() {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                }
                Player.STATE_BUFFERING -> {
                }
                Player.STATE_ENDED -> {
                    mListener?.invoke()
                    player?.release()
                    player = null
                }
                Player.STATE_IDLE -> {
                }
            }
        }
    }


    fun release() {
        player?.release()
        player = null
    }

    private var mListener: (() -> Unit)? = null

    fun setOnCompletionListener(listener: (() -> Unit)?) {
        this.mListener = listener
    }

    fun isPlaying() = player?.playWhenReady ?: false
}