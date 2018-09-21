package com.ebd.lib.json;

import com.ebd.lib.bean.BaseResponse;
import com.google.gson.Gson;
import com.xiaofu.lib_base_xiaofu.parse.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Gson解析泛型封装
 */
public class MyParser {

    public static <T> BaseResponse<T> parserObj(String reader, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(BaseResponse.class, new Class[]{clazz});
        return new Gson().fromJson(reader, type);
    }

    public static <T> BaseResponse<List<T>> parserArray(String reader, Class<T> clazz) {
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        Type type = new ParameterizedTypeImpl(BaseResponse.class, new Type[]{listType});
        return new Gson().fromJson(reader, type);
    }


}
