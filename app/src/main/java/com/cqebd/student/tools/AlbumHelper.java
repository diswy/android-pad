package com.cqebd.student.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ImageView;


import com.cqebd.student.app.App;
import com.cqebd.student.dialog.CropDialog;

import java.io.File;
import java.util.ArrayList;

import gorden.album.AlbumPicker;
import gorden.album.crop.Crop;
import gorden.album.utils.SingleMediaScanner;
import gorden.util.FileUtil;

import static android.app.Activity.RESULT_OK;

/**
 * document
 * Created by Gordn on 2017/4/11.
 */

public class AlbumHelper {
    private Activity mContext;
    private AlbumCallBack albumCallBack;
    private Fragment fragment;
    private ImageView imageView;

    public AlbumHelper(Activity context) {
        mContext = context;
    }

    public AlbumHelper(Fragment fragment) {
        this.fragment = fragment;
        this.mContext = fragment.getActivity();
    }

    public void handleResult(int requestCode, int resultCode, Intent data, AlbumCallBack callBack) {
        if (callBack == null) return;
        albumCallBack = callBack;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AlbumPicker.REQUEST_CAMERA:
                    new SingleMediaScanner(App.mContext).scanFile(AlbumPicker.mCurrentPhotoPath);
                    showCrop(AlbumPicker.mCurrentPhotoPath);
                    break;
                case AlbumPicker.REQUEST_CODE:
                    if (data == null) return;
                    ArrayList<String> imgs = data.getStringArrayListExtra(AlbumPicker.KEY_IMAGES);
                    if (imgs != null && imgs.size() > 0) {
                        showCrop(imgs.get(0));
                    }
                    break;
                case Crop.REQUEST_CROP:
                    String path = Crop.getOutput(data).getPath();
                    if (albumCallBack != null && !TextUtils.isEmpty(path))
                        albumCallBack.pathResult(path, imageView);
                    break;
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.show("图片裁剪失败");
        }
    }

    public void handleResult(int requestCode, int resultCode, Intent data, AlbumCallBack callBack, ImageView imageView) {
        this.imageView = imageView;
        handleResult(requestCode, resultCode, data, callBack);
    }

    private void showCrop(String path) {
        if (TextUtils.isEmpty(path)){
            Toast.show("图片处理失败");
            return;
        }
        CropDialog dialog = new CropDialog(mContext, "是否需要裁剪") {
            @Override
            public void onOk() {
                if (fragment != null) {
                    Crop.of(Uri.fromFile(new File(path)), Crop.getDefaultResultUri(mContext))
                            .start(mContext, fragment);
                } else {
                    Crop.of(Uri.fromFile(new File(path)), Crop.getDefaultResultUri(mContext))
                            .start(mContext);
                }

            }

            @Override
            public void onCancel() {
                if (albumCallBack != null && !TextUtils.isEmpty(path))
                    albumCallBack.pathResult(path, imageView);
            }
        };
        dialog.show();
    }

    public interface AlbumCallBack {
        void pathResult(String path, ImageView imageViews);
    }

    public static void clearCache(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, "luban_disk_cache");
            FileUtil.delete(result.getPath());
        }
    }
}
