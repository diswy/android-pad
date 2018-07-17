package com.cqebd.student.utils;

import android.text.TextUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * document
 * Created by Gordn on 2017/5/2.
 */

@SuppressWarnings("unused")
public class DateUtils {
    public static long getDurationWithGMT(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return System.currentTimeMillis();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        Date date;
        try {
            date = formatter.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return System.currentTimeMillis();
    }

    public static String getYMDWithGMT(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            Date date = formatter.parse(dateStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getYMDHMSWithGMT(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        try {
            Date date = formatter.parse(dateStr);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CHINA);
        return dateFormat.format(new Date());
    }


    public static void DeleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
    }

}
