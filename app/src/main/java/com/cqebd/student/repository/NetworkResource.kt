package com.cqebd.student.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.NonNull
import android.support.annotation.WorkerThread
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.vo.Resource
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 提供网络资源的泛型类
 * Created by gorden on 2017/11/8.
 */
@Suppress("LeakingThis")
abstract class NetworkResource<ResultType> @MainThread constructor() {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        val apiResponse = createCall()
        result.addSource(apiResponse, { response ->
            result.removeSource(apiResponse)
            if (response?.isSuccessful() == true) {
                Flowable.fromCallable { processResponse(response) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            setValue(Resource.success(it))
                        }, {
                            setValue(Resource.success(null))
                        })
            } else {
                setValue(Resource.error(response?.errorMessage, null))
            }
        })
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (newValue != result.value){
            result.value = newValue
        }
    }

    @Suppress("unused")
    val asLiveData
        get() = result

    @WorkerThread
    protected open fun processResponse(response: ApiResponse<ResultType>): ResultType? {
        return response.body
    }

    @NonNull
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<ResultType>>
}