package gorden.album.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片目录
 * Created by Gordn on 2017/4/1.
 */

public class PictureDirectory {
    public String id;
    public Picture coverPicture;
    public String dirName;
    public String dirPath;
    public List<Picture> pictures = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PictureDirectory that = (PictureDirectory) o;

        return dirName != null ?
                dirName.equals(that.dirName) : that.dirName == null && (dirPath != null ?
                dirPath.equals(that.dirPath) : that.dirPath == null);

    }

    @Override
    public int hashCode() {
        int result = dirName != null ? dirName.hashCode() : 0;
        result = 31 * result + (dirPath != null ? dirPath.hashCode() : 0);
        return result;
    }
}
