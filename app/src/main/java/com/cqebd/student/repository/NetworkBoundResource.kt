package com.cqebd.student.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.support.annotation.MainThread
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.annotation.WorkerThread
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.vo.Resource
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * 提供数据库和网络资源的泛型类
 * Created by gorden on 2017/11/8.
 */
abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor() {
    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        val dbSource: LiveData<ResultType> = loadFromDb()
        result.addSource(dbSource, {
            result.removeSource(dbSource)
            if (shouldFetch(it)) {//从网络获取数据
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource, {
                    setValue(Resource.success(it))
                })
            }
        })

    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (newValue != result.value){
            result.value = newValue
        }
    }

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()

        result.addSource(dbSource, {
            setValue(Resource.loading(it))
        })

        result.addSource(apiResponse, { response ->
            result.removeSource(dbSource)
            result.removeSource(apiResponse)

            if (response?.isSuccessful() == true) {
                Flowable.just(response)
                        .observeOn(Schedulers.io())
                        .doOnNext {
                            processResponse(it)?.let {
                                saveCallResult(it)
                            }
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete {
                            result.addSource(loadFromDb(), {
                                setValue(Resource.success(it))
                            })
                        }.subscribe()
            } else {
                onFetchFailed()
                result.addSource(dbSource, {
                    setValue(Resource.error(response?.errorMessage, it))
                })
            }
        })

    }

    protected open fun onFetchFailed() {
    }

    @Suppress("unused")
    val asLiveData
        get() = result

    @WorkerThread
    protected open fun processResponse(response: ApiResponse<RequestType>): RequestType? {
        return response.body
    }

    @WorkerThread
    protected abstract fun saveCallResult(@Nullable item: RequestType)

    @MainThread
    protected abstract fun shouldFetch(@Nullable data: ResultType?): Boolean

    @NonNull
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>


    @NonNull
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}