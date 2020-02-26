package com.cqebd.student.live.mychat

import com.netease.neliveplayer.playerkit.sdk.LivePlayerObserver
import com.netease.neliveplayer.playerkit.sdk.constant.CauseCode
import com.netease.neliveplayer.playerkit.sdk.model.MediaInfo
import com.netease.neliveplayer.playerkit.sdk.model.StateInfo
import com.orhanobut.logger.Logger

interface INetPlayerKit : LivePlayerObserver {

    override fun onPreparing() {
        Logger.d("--->>>:onPreparing")
    }

    override fun onPrepared(info: MediaInfo) {
//            mediaInfo = info
        Logger.d("--->>>:onPrepared")
    }

    override fun onFirstVideoRendered() {
        Logger.d("--->>>:视频第一帧已解析")
//            showToast("视频第一帧已解析")
    }

    override fun onFirstAudioRendered() {
        Logger.d("--->>>:音频第一帧已解析")
        //            showToast("音频第一帧已解析");
    }

    override fun onBufferingStart() {
        Logger.d("--->>>:onBufferingStart")
//            mBuffer.setVisibility(View.VISIBLE)
    }

    override fun onBufferingEnd() {
        Logger.d("--->>>:onBufferingEnd")
//            mBuffer.setVisibility(View.GONE)
    }

    override fun onBuffering(percent: Int) {
        Logger.d("--->>>:缓冲中...")
//            Log.d(TAG, "缓冲中...$percent%")
    }

    override fun onVideoDecoderOpen(value: Int) {
        Logger.d("--->>>使用解码类型：" + if (value == 1) "硬件解码" else "软解解码")
//            showToast("使用解码类型：" + if (value == 1) "硬件解码" else "软解解码")
    }

    override fun onStateChanged(stateInfo: StateInfo?) {
        if (stateInfo != null && stateInfo.causeCode == CauseCode.CODE_VIDEO_STOPPED_AS_NET_UNAVAILABLE) {
//                showToast("网络已断开")
        }
    }

    override fun onHttpResponseInfo(code: Int, header: String) {
        Logger.d("--->>>onHttpResponseInfo,code:$code header:$header")
//            Log.i(TAG, "onHttpResponseInfo,code:$code header:$header")
    }
}