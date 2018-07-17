package com.cqebd.student.utils;

import android.content.Context;
import android.os.Environment;

import java.math.BigDecimal;

/**
 * document
 * Created by Gordn on 2017/5/27.
 */

public class CacheManager {
    public static String getCacheSize(Context context){
        long cacheSize = FileTools.getSize(context.getCacheDir().getPath());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += FileTools.getSize(context.getExternalCacheDir().getPath());
        }
        return getFormatSize(cacheSize);
    }

    public static void clearAllCache(Context context) {
        FileTools.clearDirectory(context.getCacheDir().getPath());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            FileTools.clearDirectory(context.getExternalCacheDir().getPath());
        }
    }


    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "b";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }
}
