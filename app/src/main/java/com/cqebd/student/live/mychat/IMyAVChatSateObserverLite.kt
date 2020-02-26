package com.cqebd.student.live.mychat

import com.netease.nimlib.sdk.avchat.AVChatStateObserverLite
import com.netease.nimlib.sdk.avchat.model.AVChatAudioFrame
import com.netease.nimlib.sdk.avchat.model.AVChatNetworkStats
import com.netease.nimlib.sdk.avchat.model.AVChatSessionStats
import com.netease.nimlib.sdk.avchat.model.AVChatVideoFrame
import com.orhanobut.logger.Logger

interface IMyAVChatSateObserverLite : AVChatStateObserverLite {
    override fun onUserLeave(account: String?, event: Int) {
        Logger.w("--->>>用户离开的ID = $account")
    }

    override fun onCallEstablished() {
    }

    override fun onLiveEvent(event: Int) {
    }

    override fun onAudioFrameFilter(frame: AVChatAudioFrame?): Boolean {
        return false
    }

    override fun onVideoFrameResolutionChanged(account: String?, width: Int, height: Int, rotate: Int) {
    }

    override fun onProtocolIncompatible(status: Int) {
    }

    override fun onNetworkQuality(account: String?, quality: Int, stats: AVChatNetworkStats?) {
    }

    override fun onVideoFrameFilter(frame: AVChatVideoFrame?, maybeDualInput: Boolean): Boolean {
        return false
    }

    override fun onJoinedChannel(code: Int, audioFile: String?, videoFile: String?, elapsed: Int) {
    }

    override fun onReportSpeaker(speakers: MutableMap<String, Int>?, mixedEnergy: Int) {
    }

    override fun onAudioDeviceChanged(device: Int) {
    }

    override fun onDisconnectServer(code: Int) {
    }

    override fun onSessionStats(sessionStats: AVChatSessionStats?) {
    }

    override fun onDeviceEvent(code: Int, desc: String?) {
    }

    override fun onConnectionTypeChanged(netType: Int) {
    }

    override fun onLeaveChannel() {
    }

    override fun onFirstVideoFrameAvailable(account: String?) {
    }

    override fun onVideoFpsReported(account: String?, fps: Int) {
    }

    override fun onFirstVideoFrameRendered(account: String?) {
    }

    override fun onUserJoined(account: String?) {
        Logger.w("--->>>用户加入进来的ID = $account")
    }
}