package com.cqebd.student.tai;

import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

public class TAIAudioPlayer {
    private MediaPlayer audioPlayer;

    public TAIAudioPlayer() {
    }

    public void openAudio(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        try {
            audioPlayer = new MediaPlayer();
            audioPlayer.setDataSource(url);
            audioPlayer.setOnPreparedListener(mPreparedListener);
            audioPlayer.setOnCompletionListener(mCompletionListener);
            audioPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (audioPlayer != null) {
            audioPlayer.release();
        }
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            Log.i("xiaofu", "音频长度：" + mediaPlayer.getDuration());
            audioPlayer.start();
            if (mOnStart != null){
                mOnStart.onVideoStart();
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if (mOnPlayCompleteListener != null) {
                mOnPlayCompleteListener.onComplete();
            }
        }
    };

    private OnPlayComplete mOnPlayCompleteListener;
    private onStart mOnStart;

    public void setOnCompleteListener(OnPlayComplete listener) {
        this.mOnPlayCompleteListener = listener;
    }

    public void setOnStartListener(onStart listener){
        this.mOnStart = listener;
    }

    public interface OnPlayComplete {
        void onComplete();
    }

    public interface onStart {
        void onVideoStart();
    }
}
