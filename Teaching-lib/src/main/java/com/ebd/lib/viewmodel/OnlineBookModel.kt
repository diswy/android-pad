package com.ebd.lib.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.ebd.lib.App
import com.ebd.lib.bean.*
import com.ebd.lib.json.GsonFix
import com.ebd.lib.json.fromJson
import com.ebd.lib.json.fromJsonArray
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.xiaofu.lib_base_xiaofu.api.ApiManager
import com.xiaofu.lib_base_xiaofu.cache.ACache
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast

class OnlineBookModel : ViewModel() {
    private val mCache by lazy { ACache.get(App.instance) }

    val gradeList = MutableLiveData<List<Grade>>()// 年级列表
    val publishList = MutableLiveData<List<Publish>>()// 出版社列表
    val subjectList = MutableLiveData<List<Subject>>()// 科目列表
    val eBookList = MutableLiveData<List<OnlineEBook>>()// 电子书列表

    val gradeChecked = MutableLiveData<Int>()
    val publishChecked = MutableLiveData<Int>()
    val subjectChecked = MutableLiveData<Int>()


    fun clear() {
        gradeList.value = null
    }

    fun loadGrade() {
        val cacheGrade: String? = mCache.getAsString("grade")
        if (cacheGrade != null) {
            val list: List<Grade> = Gson().fromJson(cacheGrade, Array<Grade>::class.java).toMutableList()
            gradeList.value = list
        } else {
            ApiManager.getInstance().libService
                    .gradeGetAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        try {
                            val bean = Gson().fromJsonArray(it, Grade::class.java)
                            bean.data?.let {
                                gradeList.value = it
                                mCache.put("grade", GsonFix.toJson(it), 7 * ACache.TIME_DAY)
                            }
                        } catch (e: Exception) {
                            App.instance.toast("网络错误：${e.message}")
                        }
                    }
        }
    }

    fun loadPublish() {
        val cachePublish: String? = mCache.getAsString("publish")
        if (cachePublish != null) {
            val list: List<Publish> = Gson().fromJson(cachePublish, Array<Publish>::class.java).toMutableList()
            publishList.value = list
            return
        }
        ApiManager.getInstance().libService
                .publishGetAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    try {
                        val bean = Gson().fromJsonArray(it, Publish::class.java)
                        bean.data?.let {
                            publishList.value = it
                            mCache.put("publish", GsonFix.toJson(it), 7 * ACache.TIME_DAY)
                        }
                    } catch (e: Exception) {
                        App.instance.toast("网络请求错误：${e.message}")
                    }
                }
    }

    fun loadSubject() {
        val cacheSubject: String? = mCache.getAsString("subject")
        if (cacheSubject != null) {
            val list: List<Subject> = Gson().fromJson(cacheSubject, Array<Subject>::class.java).toMutableList()
            subjectList.value = list
            return
        }
        ApiManager.getInstance().libService
                .subjectGetAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    try {
                        val bean = Gson().fromJsonArray(it, Subject::class.java)
                        bean.data?.let {
                            subjectList.value = it
                            mCache.put("subject", GsonFix.toJson(it), 7 * ACache.TIME_DAY)
                        }
                    } catch (e: Exception) {
                        App.instance.toast("网络请求错误：${e.message}")
                    }
                }
    }

    fun loadEBook() {
        ApiManager.getInstance().libService
                .ebookGetAll(gradeChecked.value ?: 0,
                        subjectChecked.value ?: 0
                        , publishChecked.value ?: 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    try {
                        val bean = Gson().fromJsonArray(it, OnlineEBook::class.java)
                        bean.data?.let {
                            eBookList.value = it
                        }
                    } catch (e: Exception) {
                        App.instance.toast("网络请求错误：${e.message}")
                    }
                }
    }

}

