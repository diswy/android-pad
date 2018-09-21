package com.ebd.lib.json

import com.ebd.lib.bean.BaseResponse
import com.google.gson.Gson
import com.xiaofu.lib_base_xiaofu.parse.ParameterizedTypeImpl
import java.lang.reflect.Type

inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson(json, T::class.java)

inline fun <reified T> Gson.fromJsonObj(json: String, clazz: Class<T>): BaseResponse<T> {
    val type = ParameterizedTypeImpl(BaseResponse::class.java, arrayOf<Class<*>>(clazz))
    return this.fromJson(json, type)
}

inline fun <reified T> Gson.fromJsonArray(json: String, clazz: Class<T>): BaseResponse<List<T>> {
    val listType = ParameterizedTypeImpl(List::class.java, arrayOf<Class<*>>(clazz))
    val type = ParameterizedTypeImpl(BaseResponse::class.java, arrayOf<Type>(listType))
    return this.fromJson(json, type)
}