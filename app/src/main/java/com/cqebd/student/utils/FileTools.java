package com.cqebd.student.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FileTools {

    public static boolean isExist(String path) {
        return new File(path).exists();
    }

    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    public static boolean renameTo(String path, String toPath) {
        return new File(path).renameTo(new File(toPath));
    }

    private static boolean clearDirectory(String directoryPath, boolean deleteRootDirectory) {

        if (!isExist(directoryPath)) {
            return false;
        }

        File file = new File(directoryPath);
        if (!file.isDirectory()) {
            return file.delete();
        }

        String[] fileList = file.list();
        if (fileList == null) {
            return false;
        }

        for (String fileName : fileList) {
            delete(directoryPath + "/" + fileName);
        }

        return !deleteRootDirectory || file.delete();

    }

    public static boolean clearDirectory(String directoryPath) {
        return clearDirectory(directoryPath, false);
    }

    public static void cleanCacheDirectory(String directoryPath, long maxSize, long retentionSize) {

        long size = FileTools.getSize(directoryPath);

        if (size > maxSize) {

            File file = new File(directoryPath);
            String[] fileArray = file.list();

            for (int i = 0; i < fileArray.length; i++) {

                for (int j = 0; j < fileArray.length - i - 1; j++) {

                    if (new File(directoryPath + "/" + fileArray[j]).lastModified() > new File(directoryPath + "/" + fileArray[j + 1]).lastModified()) {

                        String path = fileArray[j];
                        fileArray[j] = fileArray[j + 1];
                        fileArray[j + 1] = path;

                    }

                }

            }

            for (String path : fileArray) {

                file = new File(directoryPath + "/" + path);
                if (file.delete()) {
                    size -= file.length();
                }

                if (size < retentionSize) {
                    break;
                }

            }

        }

    }

    public static boolean delete(String path) {
        return clearDirectory(path, true);
    }

    public static boolean mkdirs(String path) {
        return FileTools.isExist(path) || new File(path).mkdirs();
    }

    public static long getSize(String path) {

        if (!isExist(path)) {
            return 0;
        }

        File file = new File(path);
        if (!file.isDirectory()) {
            return file.length();
        }

        String[] fileList = file.list();
        long size = 0;

        for (String filePath : fileList) {

            filePath = path + "/" + filePath;
            file = new File(filePath);
            size += file.isDirectory() ? getSize(filePath) : file.length();

        }

        return size;

    }


    /**
     * 创建临时文件
     *
     * @param type 文件类型
     */
    public static File getTempFile(Context context, FileType type){
        File cacheDir = !isExternalStorageWritable()?context.getFilesDir(): context.getExternalCacheDir();
        try{
            File file = File.createTempFile(type.toString(), null, cacheDir);
            file.deleteOnExit();
            return file;
        }catch (IOException e){
            return null;
        }
    }
    /**
     * 判断外部存储是否可用
     *
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 获取缓存文件地址
     */
    public static String getCacheFilePath(Context context, String fileName){
        File cacheDir = !isExternalStorageWritable()?context.getFilesDir(): context.getExternalCacheDir();
        return cacheDir.getAbsolutePath().concat(File.separator).concat(fileName);
    }


    /**
     * 判断缓存文件是否存在
     */
    public static boolean isCacheFileExist(Context context, String fileName){
        File file = new File(getCacheFilePath(context,fileName));
        return file.exists();
    }

    public enum FileType{
        IMG,
        AUDIO,
        VIDEO,
        FILE,
    }
}
