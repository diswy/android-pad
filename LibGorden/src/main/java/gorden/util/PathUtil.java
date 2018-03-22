package gorden.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

public class PathUtil {

    public static final String Image_Name = "Image_Name";
    public static final String Image_DirName = "Image_DirName";
    public static final String Image_Path = "Image_Path";
    public static final String Image_DirPath = "Image_DirPath";

    private PathUtil() {
    }

    public static String getRootDir() {
        return getPath(Environment.getRootDirectory());
    }

    public static String getDataDir() {
        return getPath(Environment.getDataDirectory());
    }

    public static String getExternalStorageDir() {
        return getPath(Environment.getExternalStorageDirectory());
    }

    public static String getApplCacheDir(Context context) {
        return getPath(context.getCacheDir());
    }

    public static String getAppRootDir(Context context) {
        return getPath(context.getCacheDir().getParentFile());
    }

    public static String getExternalAppCacheDir(Context context) {
        return getPath(context.getExternalCacheDir());
    }

    public static String getExternalAppRootDir(Context context) {
        return getPath(context.getExternalCacheDir() == null ? null : context.getExternalCacheDir().getParentFile());
    }

    public static String getExternalPublicDownloadDir() {
        return getPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
    }

    public static String getExternalDownloadDir(Context context) {
        return getPath(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS));
    }

    public static String getExternalMusicDir(Context context) {
        return getPath(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC));
    }

    public static String getExternalPublicMusicDir() {
        return getPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));
    }

    public static String getExternalMovieDir(Context context) {
        return getPath(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES));
    }

    public static String getExternalPublicMovieDir() {
        return getPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES));
    }

    public static String getExternalPicturesDir(Context context) {
        return getPath(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES));
    }

    public static String getExternalPublicPicturesDir() {
        return getPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }

    public static String getExternalPublicDcimDir() {
        return getPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
    }

    public static String getExternalDir(Context context, String type) {
        return getPath(context.getExternalFilesDir(type));
    }

    public static String getExternalPublicDir(String type) {
        return getPath(Environment.getExternalStoragePublicDirectory(type));
    }

    private static String getPath(File file) {

        if (file == null) return null;

        if (file.exists()) return file.getAbsolutePath();

        if (file.mkdirs()) return file.getAbsolutePath();

        return null;

    }

}
