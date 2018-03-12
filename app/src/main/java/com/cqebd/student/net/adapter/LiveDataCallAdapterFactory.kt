package com.cqebd.student.net.adapter

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
/**
 * 描述
 * Created by gorden on 2017/11/5.
 */
class LiveDataCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {

        if (getRawType(returnType) != LiveData::class.java){
            return null
        }

        val responseType = getParameterUpperBound(0, returnType as? ParameterizedType)
        val rawType = getRawType(responseType)

        if (rawType!= ApiResponse::class.java){
            throw IllegalArgumentException("type must is ApiResponse")
        }

        if (responseType !is ParameterizedType){//LiveData<TYPE> TYPE not is ParameterizedType
            throw IllegalArgumentException("resource must be parameterized")
        }

        val bodyType = CallAdapter.Factory.getParameterUpperBound(0, responseType)

        return LiveDataCallAdapter<LiveData<*>>(bodyType)
    }
}