package gorden.album.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 获取图片格式
 * Created by Gordn on 2017/3/31.
 */

public enum ImageType {
    JPEG,
    PNG,
    GIF,
    BMP,
    WEBP,
    UNKNOWN;

    ImageType() {
    }


    public static ImageType getType(InputStream io){
        if (io == null) return UNKNOWN;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(io,null,options);
        String type = options.outMimeType;

        try {
            io.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return typeFormat(type);
    }


    public static ImageType getType(String pathName){
        if (TextUtils.isEmpty(pathName)){
            return UNKNOWN;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName,options);
        String type = options.outMimeType;

        return typeFormat(type);
    }

    public static ImageType getType(byte[] bytes){
        if (bytes==null||bytes.length==0){
            return UNKNOWN;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
        String type = options.outMimeType;

        return typeFormat(type);
    }

    public static ImageType getType(Context context,int res){

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(),res,options);
        String type = options.outMimeType;

        return typeFormat(type);
    }

    public static ImageType getType(Context context,String assetsName){
        try {
            return getType(context.getAssets().open(assetsName));
        } catch (IOException e) {
            e.printStackTrace();
            return UNKNOWN;
        }
    }

    private static ImageType typeFormat(String type){
        if (TextUtils.isEmpty(type)) return UNKNOWN;
        switch (type){
            case "image/jpeg":
                return JPEG;
            case "image/png":
                return PNG;
            case "image/gif":
                return GIF;
            case "image/bmp":
                return BMP;
            case "image/webp":
                return WEBP;
            default:
                return UNKNOWN;
        }
    }
}
