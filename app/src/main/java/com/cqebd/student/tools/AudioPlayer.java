package com.cqebd.student.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cqebd.student.R;
import com.cqebd.student.app.App;
import com.cqebd.student.db.dao.Attachment;
import com.cqebd.student.db.dao.AttachmentDao;

import java.io.IOException;

/**
 * 描述
 * Created by gorden on 2017/12/13.
 */

public class AudioPlayer {
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    //    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;

    private MediaPlayer audioPlayer;
    private Attachment attachment = null;
    private ImageButton mBtnPlay;
    private ProgressBar mProgressBar;
    private TextView mTextProgress;
    //    private Map<String,Integer> progressData;
    private SharedPreferences sharedPreferences;
    private AttachmentDao attachmentDao = App.Companion.getDaoSession().getAttachmentDao();

    public AudioPlayer(ImageButton btnPlay, ProgressBar progressBar, TextView textProgress) {
        this.mBtnPlay = btnPlay;
        this.mProgressBar = progressBar;
        this.mTextProgress = textProgress;
        sharedPreferences = App.mContext.getSharedPreferences("audio_cache", Context.MODE_PRIVATE);
        mBtnPlay.setOnClickListener(v -> start());
    }

    public void openAudio(Attachment attachment) {
        if (this.attachment == null || !this.attachment.getId().equals(attachment.getId())) {
            release();
            AudioManager am = (AudioManager) App.mContext.getSystemService(Context.AUDIO_SERVICE);
            assert am != null;
            am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            this.attachment = attachment;
            try {
                audioPlayer = new MediaPlayer();
                audioPlayer.setDataSource(attachment.getUrl());
                audioPlayer.setOnPreparedListener(mPreparedListener);
                audioPlayer.setOnCompletionListener(mCompletionListener);
                audioPlayer.setOnErrorListener(mErrorListener);
                mCurrentState = STATE_PREPARING;
                audioPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                mCurrentState = STATE_ERROR;
                mTargetState = STATE_ERROR;
            }
        }

    }


    public void release() {
        if (audioPlayer != null) {
            Log.e("xiaofu", "音频被释放了");
            mProgressBar.setProgress(0);
            setProgress(0, 0);
            mProgressBar.removeCallbacks(progressRunnable);
            attachment = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
            audioPlayer.reset();
            audioPlayer.release();
            audioPlayer = null;
            mBtnPlay.setImageResource(R.drawable.ic_play1);
            AudioManager am = (AudioManager) App.mContext.getSystemService(Context.AUDIO_SERVICE);
            assert am != null;
            am.abandonAudioFocus(null);

        }
    }


    public void start() {
        if (isInPlaybackState()) {
            if (attachment.getCanWatchCount() > 0 && attachment.getWatchCount() >= attachment.getCanWatchCount()) {
                KToastKt.toast("你最多只能听 " + attachment.getCanWatchCount() + " 遍");
                return;
            }
            mCurrentState = STATE_PLAYING;
            mProgressBar.post(progressRunnable);
            mBtnPlay.setImageResource(R.drawable.ic_stop1);
            audioPlayer.start();
        }
        mTargetState = STATE_PLAYING;
    }

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;

            mProgressBar.setMax(audioPlayer.getDuration());
            int progress = sharedPreferences.getInt(attachment.getId(), 0);
            if (progress != 0) {
                mProgressBar.setProgress(progress);
                audioPlayer.seekTo(progress);
                setProgress(progress, audioPlayer.getDuration());
            } else {
                setProgress(0, audioPlayer.getDuration());
            }

            if (mTargetState == STATE_PLAYING) {
                start();
            }
        }
    };

    private MediaPlayer.OnCompletionListener mOnCompletionListener;

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.e("xiaofu", "音频播放完成了");

                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    mTargetState = STATE_PLAYBACK_COMPLETED;

                    attachment.setWatchCount(attachment.getWatchCount() + 1);
                    attachmentDao.insertOrReplace(attachment);

                    mProgressBar.removeCallbacks(progressRunnable);

                    mBtnPlay.setImageResource(R.drawable.ic_play1);
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mp);
                    }
                }
            };

    private MediaPlayer.OnErrorListener mErrorListener =
            new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("xiaofu", "音频播放错误：" + what + ";extra=" + extra);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    mBtnPlay.setImageResource(R.drawable.ic_play1);
                    return false;
                }
            };

    private boolean isInPlaybackState() {
        return (audioPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PLAYING &&
                mCurrentState != STATE_PREPARING);
    }

    public void onDestroy() {
        release();
        mProgressBar = null;
        mBtnPlay = null;
        progressRunnable = null;
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setProgress(int current, int max) {
        mTextProgress.setText(String.format("%02d", current / 1000 / 60) + ":" + String.format("%02d", current / 1000 % 60)
                + "/" + String.format("%02d", max / 1000 / 60) + ":" + String.format("%02d", max / 1000 % 60));
    }

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (audioPlayer != null) {
                int progress = audioPlayer.getCurrentPosition();
                sharedPreferences.edit().putInt(attachment.getId(), progress).apply();
                mProgressBar.setProgress(progress);
                setProgress(progress, audioPlayer.getDuration());
                mProgressBar.postDelayed(progressRunnable, 1000 - (progress % 1000));
            }
        }
    };
}
