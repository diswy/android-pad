package com.ebd.lib.json;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * kotlin解析json有BUG
 * 改用java辅助
 */
public class GsonFix {
    static {
        gson = new Gson();
    }

    private static Gson gson;

    public static <T> List<T> toArray(String reader) {
        return gson.fromJson(reader, new TypeToken<List<T>>() {
        }.getType());
    }


    public static String toJson(Object obj){
        return gson.toJson(obj);
    }

}
