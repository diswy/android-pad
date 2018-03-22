package gorden.util;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static boolean isFile(String path) {
        return new File(path).isFile();
    }

    public static long getLength(String path) {
        return new File(path).length();
    }

    public static boolean renameTo(String path, String toPath) {
        return new File(path).renameTo(new File(toPath));
    }

    public static boolean delete(String path) {

        if (!exists(path)) return false;

        File fileRoot = new File(path);

        if (!fileRoot.isDirectory()) return fileRoot.delete();

        String[] fileList = fileRoot.list();

        if (fileList == null) return false;

        int i, length;

        for (i = 0, length = fileList.length; i < length; i++) delete(path.concat("/").concat(fileList[i]));

        return fileRoot.delete();

    }

    public static boolean mkdirs(String path) {
        return FileUtil.exists(path) || new File(path).mkdirs();
    }
    public static boolean createNewFile(String path) {
        try {
            return FileUtil.exists(path) || new File(path).createNewFile();
        } catch (IOException e) {
        }
        return false;
    }

    public static long directorySize(String path) {

        File file = new File(path);

        String[] fileList = file.list();
        String filePath;

        long size = 0;

        int i, length;

        for (i = 0, length = fileList.length; i < length; i++) {

            filePath = path.concat("/").concat(fileList[i]);

            file = new File(filePath);

            size += file.isDirectory() ? directorySize(filePath) : file.length();

        }

        return size;

    }

}
