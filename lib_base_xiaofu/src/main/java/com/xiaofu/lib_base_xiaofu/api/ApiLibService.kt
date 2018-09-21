package com.xiaofu.lib_base_xiaofu.api

import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiLibService {
    /**
     * 获取年级
     */
    @GET("grade/getall")
    fun gradeGetAll(): Flowable<String>

    /**
     * 获取出版社
     */
    @GET("publishver/getall")
    fun publishGetAll(): Flowable<String>

    /**
     * 获取科目
     */
    @GET("subjecttype/getall")
    fun subjectGetAll(): Flowable<String>

    /**
     * 获取电子书列表
     */
    @POST("resourcelibrary/getall")
    fun ebookGetAll(
            @Query("gradeId") gradeId: Int,
            @Query("subjectTypeId") subjectId: Int,
            @Query("teachingMaterialPublishVerTypeId") publishId: Int)
            : Flowable<String>

}