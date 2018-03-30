package com.cqebd.student.js;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.cqebd.student.ui.MediaPlayerActivity;
import com.orhanobut.logger.Logger;

/**
 * 播放Video JS交互
 * video.play("")
 */

public class VideoJs extends Object {
    private Context mContext;

    public VideoJs(Context mContext) {
        this.mContext = mContext;
    }

    @JavascriptInterface
    public void play(String url){
        Intent intent = new Intent(mContext, MediaPlayerActivity.class);
        Logger.d(url);
        intent.putExtra("url",url);
        mContext.startActivity(intent);
    }
}
