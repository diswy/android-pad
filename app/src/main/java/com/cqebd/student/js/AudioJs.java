package com.cqebd.student.js;

import android.content.Context;
import android.webkit.JavascriptInterface;

import gorden.lib.video.ExAudioPlayer;

public class AudioJs extends Object {
    private Context mContext;

    public AudioJs(Context mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public void audioPlay(String url) {
        ExAudioPlayer player = new ExAudioPlayer(mContext);
        player.openAudio(url);
    }
}
