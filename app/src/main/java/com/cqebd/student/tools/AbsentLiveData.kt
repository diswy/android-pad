package com.cqebd.student.tools

import android.arch.lifecycle.LiveData

/**
 * a null value LiveData
 * Created by gorden on 2017/11/9.
 */
class AbsentLiveData<T> private constructor(): LiveData<T>() {
    init {
        postValue(null)
    }
    companion object {
        fun <T> create()=AbsentLiveData<T>()
    }
}