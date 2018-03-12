package com.cqebd.student.net.converter

import com.cqebd.student.net.BaseResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 描述
 * Created by gorden on 2017/11/8.
 */
class APIResponseConverter(private val gson: Gson, val type: Type?) : Converter<ResponseBody, Any> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody?): Any? {
        value.use {
            val resultType = if (type is ParameterizedType && type.rawType == BaseResponse::class.java) type
            else object : ParameterizedType {
                override fun getRawType(): Type {
                    return BaseResponse::class.java
                }

                override fun getOwnerType(): Type? {
                    return null
                }

                override fun getActualTypeArguments(): Array<Type?> {
                    return arrayOf(type)
                }
            }
            val reader = gson.newJsonReader(value?.charStream())
            val newAdapter = gson.getAdapter(TypeToken.get(resultType))
            return newAdapter.read(reader)
        }
    }
}