package gorden.util;

import android.content.Context;

import java.io.File;

/**
 * 图片后续处理工具类
 * 1、图片压缩处理
 * Created by Gordn on 2017/4/10.
 */

public class BitmapUtils {

    private static String DEFAULT_DISK_CACHE_DIR = "disk_cache";

    private static volatile BitmapUtils INSTANCE;

    private final File mCacheDir;

    private File mFile;

    public BitmapUtils(File cacheDir) {
        this.mCacheDir = cacheDir;
    }

    private static synchronized File getPhotoCacheDir(Context context) {
        return getPhotoCacheDir(context, BitmapUtils.DEFAULT_DISK_CACHE_DIR);
    }

    private static File getPhotoCacheDir(Context context, String cacheName) {
        File cacheDir = context.getCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }

            File noMedia = new File(cacheDir + "/.nomedia");
            if (!noMedia.mkdirs() && (!noMedia.exists() || !noMedia.isDirectory())) {
                return null;
            }
            XLog.e("create disk chche dir succeed :"+result.getAbsolutePath());
            return result;
        }
        XLog.e("default disk cache dir is null");
        return null;
    }

    public static BitmapUtils get(Context context) {
        if (INSTANCE == null) INSTANCE = new BitmapUtils(BitmapUtils.getPhotoCacheDir(context));
        return INSTANCE;
    }

    public BitmapUtils load(File file) {
        mFile = file;
        return this;
    }
}
