package com.cqebd.student.net.adapter

import android.arch.lifecycle.LiveData
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.BaseResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 描述
 * Created by gorden on 2017/11/5.
 */
class LiveDataCallAdapter<R>(private val responseType: Type) : CallAdapter<BaseResponse<R>, LiveData<ApiResponse<R>>> {
    override fun adapt(call: Call<BaseResponse<R>>?): LiveData<ApiResponse<R>> {
        return object : LiveData<ApiResponse<R>>() {
            val started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call?.enqueue(object : Callback<BaseResponse<R>> {
                        override fun onResponse(call: Call<BaseResponse<R>>?, response: Response<BaseResponse<R>>) {
                            postValue(ApiResponse(response))
                        }

                        override fun onFailure(call: Call<BaseResponse<R>>?, t: Throwable?) {
                            postValue(ApiResponse(t))
                        }
                    })
                }
            }
        }
    }

    override fun responseType(): Type {
        return responseType
    }
}