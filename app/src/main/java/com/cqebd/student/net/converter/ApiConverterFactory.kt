package com.cqebd.student.net.converter

import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 描述
 * Created by gorden on 2017/11/8.
 */
class ApiConverterFactory(private val gson: Gson) : Converter.Factory() {
    companion object {
        fun create(): ApiConverterFactory = create(Gson())

        private fun create(gson: Gson?): ApiConverterFactory {
            if (gson == null) throw NullPointerException("gson == null")
            return ApiConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?)
            : Converter<ResponseBody, *>? {
        return APIResponseConverter(gson, type)
    }
}