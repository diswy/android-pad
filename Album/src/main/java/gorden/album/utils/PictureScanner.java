package gorden.album.utils;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gorden.album.R;
import gorden.album.entity.Picture;
import gorden.album.entity.PictureDirectory;

import static android.provider.MediaStore.Images.Media.*;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

/**
 * 图片扫描
 * Created by Gordn on 2017/4/1.
 */

public class PictureScanner implements LoaderManager.LoaderCallbacks<Cursor> {

    private AppCompatActivity mContext;
    private OnPicturesLoadedListener loadedListener;
    private boolean showGif;

    private final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            _ID,                              //图片id
            DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            SIZE,           //图片的大小，long型  132492
            WIDTH,          //图片的宽度，int型  1920
            HEIGHT,         //图片的高度，int型  1080
            MIME_TYPE,      //图片的类型     image/jpeg
            DATE_ADDED};    //图片被添加的时间，long型  1450518608


    private ArrayList<PictureDirectory> directories = new ArrayList<>();

    private PictureScanner(AppCompatActivity mContext) {
        this.mContext = mContext;
    }

    public static PictureScanner getInstance(AppCompatActivity mContext) {
        return new PictureScanner(mContext);
    }

    /**
     * 扫描图片
     */
    public void scan(OnPicturesLoadedListener loadedListener,boolean showGif) {
        this.loadedListener = loadedListener;
        this.showGif = showGif;
        LoaderManager loaderManager = mContext.getSupportLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,showGif?null: MIME_TYPE + "!=?",showGif?null:new String[]{"image/gif"}, IMAGE_PROJECTION[7] + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        directories.clear();
        if (data != null) {
            ArrayList<Picture> allPictures = new ArrayList<>();   //所有图片的集合,不分文件夹
            while (data.moveToNext()) {
                //查询数据
                String imageId = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                long imageSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                int imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                int imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
                String imageMimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                long imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7]));

                Picture picture = new Picture();
                picture.id = imageId;
                picture.name = imageName;
                picture.addTime = imageAddTime;
                picture.path = imagePath;
                picture.size = imageSize;
                picture.mimeType = imageMimeType;
                picture.width = imageWidth;
                picture.height = imageHeight;
                if (!isEffective(picture)) continue;

                allPictures.add(picture);
                //根据父路径分类存放图片
                File imageFile = new File(imagePath);
                File imageParentFile = imageFile.getParentFile();
                PictureDirectory pictureDirectory = new PictureDirectory();
                pictureDirectory.dirName = imageParentFile.getName();
                pictureDirectory.dirPath = imageParentFile.getAbsolutePath();

                if (!directories.contains(pictureDirectory)) {
                    ArrayList<Picture> images = new ArrayList<>();
                    images.add(picture);
                    pictureDirectory.coverPicture = picture;
                    pictureDirectory.pictures = images;
                    directories.add(pictureDirectory);
                } else {
                    directories.get(directories.indexOf(pictureDirectory)).pictures.add(picture);
                }
            }

            if (data.getCount() > 0 && allPictures.size() > 0) {
                //构造所有图片的集合
                PictureDirectory allImagesFolder = new PictureDirectory();
                allImagesFolder.dirName = mContext.getResources().getString(R.string.album_str_all_image);
                allImagesFolder.dirPath = "/";
                allImagesFolder.coverPicture = allPictures.get(0);
                allImagesFolder.pictures = allPictures;
                directories.add(0, allImagesFolder);  //确保第一条是所有图片
            }
            loadedListener.onPicturesLoaded(directories);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("PictureScanner", "onLoaderReset");
    }

    private boolean isEffective(Picture picture) {
        return !TextUtils.isEmpty(picture.path)&&new File(picture.path).isFile() && picture.size > 0 && picture.width > 0;
    }

    /**
     * 所有图片加载完成的回调接口
     */
    public interface OnPicturesLoadedListener {
        void onPicturesLoaded(List<PictureDirectory> imageFolders);
    }
}
