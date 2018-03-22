package gorden.album;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import gorden.album.fragment.AlbumPickerFragment;
import io.reactivex.functions.Consumer;

import static gorden.album.fragment.AlbumPreViewFragment.MODE_ALBUM_DELETE;
import static gorden.album.fragment.AlbumPreViewFragment.MODE_ONLY_PREVIEW;

/**
 * 默认单选模式、显示gif 每排3张图 显示相机
 * requestCode =10233 resultCode = -1;
 * resultData  ArrayList<String>
 * <p>
 * 如果直接调用相机  返回 AlbumPicker.mCurrentPhotoPath
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AlbumPicker {
    public static final int REQUEST_CODE = 10233;
    public static final int REQUEST_CAMERA = 10234;
    public static final int REQUEST_DEL_CODE = 10235;

    private static int mRequestCode;

    //选择图片后返回Key
    public static final String KEY_IMAGES = "KEY_IMAGES";
    public static final String KEY_DEL_IMAGES = "KEY_DEL_IMAGES";

    public final static String EXTRA_SELECT_MODE = "EXTRA_SELECT_MODE"; //选择模式、单选 多选
    public final static String EXTRA_MAX_COUNT = "MAX_COUNT";   //多选模式,最多选择多少张
    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";//显示相机
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";//显示gif
    public final static String EXTRA_GRID_COLUMN = "GRID_COLUMN";//每排显示多少张图
    public final static String EXTRA_SELECTED_PATH = "SELECTED_PATH";//已选择图片的地址
    public final static String EXTRA_PREVIEW_ENABLED = "PREVIEW_ENABLED";//是否可以预览

    public final static String EXTRA_PREVIEW_MODE = "EXTRA_PREVIEW_MODE";//图片浏览模式
    public final static String EXTRA_PREVIEW_LIST = "EXTRA_PREVIEW_LIST";//图片浏览地址
    public final static String EXTRA_PREVIEW_POSITION = "EXTRA_PREVIEW_POSITION";//图片浏览position

    public static String mCurrentPhotoPath = null;

    public static AlbumPickerBuilder builder() {
        mRequestCode = REQUEST_CODE;
        return new AlbumPickerBuilder();
    }

    public static class AlbumPickerBuilder {
        private Bundle optionsBundle;
        private Intent pickerIntent;

        public AlbumPickerBuilder() {
            this.optionsBundle = new Bundle();
            this.pickerIntent = new Intent();
        }

        public void start(@NonNull Activity activity, int requestCode) {
            mRequestCode = requestCode;
            start(activity);
        }

        public void start(@NonNull final Activity activity) {
            new RxPermissions(activity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                activity.startActivityForResult(getIntent(activity), mRequestCode);
                            } else {
                                Toast.makeText(activity, "该功能需要文件读写权限", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        public void start(@NonNull Fragment fragment, int requestCode) {
            mRequestCode = requestCode;
            start(fragment);
        }

        public void start(@NonNull final Fragment fragment) {
            new RxPermissions(fragment.getActivity()).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                fragment.startActivityForResult(getIntent(fragment.getActivity()), mRequestCode);
                            } else {
                                Toast.makeText(fragment.getContext(), "该功能需要文件读写权限", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        /**
         * 单选模式
         */
        public AlbumPickerBuilder single() {
            optionsBundle.putInt(EXTRA_SELECT_MODE, AlbumPickerFragment.SINGLE_SELECT_MODE);
            return this;
        }

        /**
         * 多选模式
         *
         * @param count 可选择的图片数量
         */
        public AlbumPickerBuilder multi(int count) {
            optionsBundle.putInt(EXTRA_SELECT_MODE, AlbumPickerFragment.MULTI_SELECT_MODE);
            optionsBundle.putInt(EXTRA_MAX_COUNT, count);
            return this;
        }

        /**
         * 是否显示相机,默认显示
         *
         * @param showCamera if true show
         */
        public AlbumPickerBuilder showCamera(boolean showCamera) {
            optionsBundle.putBoolean(EXTRA_SHOW_CAMERA, showCamera);
            return this;
        }

        /**
         * 是否显示gif图,默认显示
         *
         * @param showGif if true show
         */
        public AlbumPickerBuilder showGif(boolean showGif) {
            optionsBundle.putBoolean(EXTRA_SHOW_GIF, showGif);
            return this;
        }

        /**
         * @param count 单排展示多少张图
         */
        public AlbumPickerBuilder gridColumns(int count) {
            optionsBundle.putInt(EXTRA_GRID_COLUMN, count);
            return this;
        }

        /**
         * 已经选择的图片地址
         *
         * @param pathArray 图片地址
         */
        public AlbumPickerBuilder selectedPaths(String[] pathArray) {
            return selectedPaths(new ArrayList<>(Arrays.asList(pathArray)));
        }

        public AlbumPickerBuilder selectedPaths(ArrayList<String> pathList) {
            optionsBundle.putStringArrayList(EXTRA_SELECTED_PATH, pathList);
            return this;
        }

        /**
         * 是否支持预览
         */
        public AlbumPickerBuilder previewEnabled(boolean previewEnabled) {
            optionsBundle.putBoolean(EXTRA_PREVIEW_ENABLED, previewEnabled);
            return this;
        }

        Intent getIntent(@NonNull Context context) {
            pickerIntent.setClass(context, AlbumPickerActivity.class);
            pickerIntent.putExtras(optionsBundle);
            return pickerIntent;
        }

        /**
         * 打开相机
         * new SingleMediaScanner(this).scanFile(AlbumPicker.mCurrentPhotoPath);可以刷新图库
         */
        public void openCamera(final Activity activity) {
            mCurrentPhotoPath = null;

            new RxPermissions(activity).request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                Intent intent = dispatchTakePictureIntent(activity);
                                activity.startActivityForResult(intent, REQUEST_CAMERA);
                            } else {
                                Toast.makeText(activity, "该功能需要相机和文件读写权限", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        public void openCamera(final Fragment fragment) {
            mCurrentPhotoPath = null;

            new RxPermissions(fragment.getActivity()).request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean granted) throws Exception {
                            if (granted) {
                                Intent intent = dispatchTakePictureIntent(fragment.getActivity());
                                fragment.startActivityForResult(intent, REQUEST_CAMERA);
                            } else {
                                Toast.makeText(fragment.getActivity(), "该功能需要相机和文件读写权限", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        /**
         * 图片编辑
         *
         * @param imglist  图片列表
         * @param position 当前position
         */
        public AlbumPickerBuilder previewDelete(@NonNull ArrayList<String> imglist, int position) {
            mRequestCode = REQUEST_DEL_CODE;
            optionsBundle.putStringArrayList(EXTRA_PREVIEW_LIST, imglist);
            optionsBundle.putInt(EXTRA_PREVIEW_POSITION, position);
            optionsBundle.putInt(EXTRA_PREVIEW_MODE, MODE_ALBUM_DELETE);
            return this;
        }

        /**
         * 图片预览
         *
         * @param imgPath 预览图片地址
         */
        public AlbumPickerBuilder preview(@NonNull String imgPath) {
            mRequestCode = REQUEST_DEL_CODE;
            ArrayList<String> imgList = new ArrayList<>();
            imgList.add(imgPath);
            optionsBundle.putStringArrayList(EXTRA_PREVIEW_LIST, imgList);
            optionsBundle.putInt(EXTRA_PREVIEW_MODE, MODE_ONLY_PREVIEW);
            return this;
        }

        /**
         * 图片预览
         *
         * @param imglist 预览图片地址
         */
        public AlbumPickerBuilder preview(@NonNull ArrayList<String> imglist, int position) {
            mRequestCode = REQUEST_DEL_CODE;
            optionsBundle.putStringArrayList(EXTRA_PREVIEW_LIST, imglist);
            optionsBundle.putInt(EXTRA_PREVIEW_MODE, MODE_ONLY_PREVIEW);
            optionsBundle.putInt(EXTRA_PREVIEW_POSITION, position);
            return this;
        }

        private Intent dispatchTakePictureIntent(Context mContext) throws IOException {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
                File file = createImageFile(mContext);
                Uri photoFile;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String authority = mContext.getApplicationInfo().packageName + ".provider";
                    photoFile = FileProvider.getUriForFile(mContext.getApplicationContext(), authority, file);
                } else {
                    photoFile = Uri.fromFile(file);
                }

                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
                }
            }
            return takePictureIntent;
        }

        private File createImageFile(Context context) throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "IMG_" + timeStamp + ".jpg";
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/Camera");

            if (!storageDir.exists()) {
                if (!storageDir.mkdirs()) {
                    storageDir = new File(context.getCacheDir(), "/Camera");
                    if (!storageDir.exists()) {
                        if (!storageDir.mkdirs()) {
                            throw new IOException("no such dir");
                        }
                    }
                }
            }

            File image = new File(storageDir, imageFileName);
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }
    }
}
