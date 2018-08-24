package com.ebd.lib.zip;

public class ZipProgressUtil {

    /***
     * 解压通用方法
     *
     * @param zipFileString
     *            文件路径
     * @param outPathString
     *            解压路径
     * @param listener
     *            加压监听
     */
    public static void UnZipFile(final String zipFileString, final String outPathString, final ZipListener listener) {
        Thread zipThread = new UnZipMainThread(zipFileString, outPathString, listener);
        zipThread.start();
    }
}