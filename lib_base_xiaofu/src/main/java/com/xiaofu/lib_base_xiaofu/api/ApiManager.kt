package com.xiaofu.lib_base_xiaofu.api

import android.content.Context
import com.readystatesoftware.chuck.ChuckInterceptor
import com.xiaofu.lib_base_xiaofu.api.converter.StringConverterFactory
import com.xiaofu.lib_base_xiaofu.api.gateway.GatewayInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

internal const val CLASS_SERVICE_BASE_URL = "http://icr-service.cqebd.cn/"

internal const val LIB_SERVICE_BASE_URL = "http://ebook-service.cqebd.cn/"

class ApiManager {

    companion object {
        fun getInstance() = Instance.instance
    }

    private object Instance {
        val instance = ApiManager()
    }

    private val gsonConverterFactory = GsonConverterFactory.create()
    private val rxJavaCallAdapterFactory = RxJava2CallAdapterFactory.create()
    private val stringConverterFactory = StringConverterFactory.create()

    /**
     * 默认的客户端
     */
    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(GatewayInterceptor("25071301", "8db28e83272f04b38135d15af60d4759"))
            .build()

    /**
     * 添加了插件的客户端，需要先初始化
     */
    private var chuckOkHttpClient: OkHttpClient? = null

    fun initChuckOkHttpClient(context: Context) {
        chuckOkHttpClient = OkHttpClient.Builder()
                .addInterceptor(ChuckInterceptor(context))
                .addInterceptor(GatewayInterceptor("25071301", "8db28e83272f04b38135d15af60d4759"))
                .build()
    }

    private fun getOkHttpClient(): OkHttpClient {
        return chuckOkHttpClient ?: okHttpClient
    }

    val classService: ApiClassService by lazy {
        Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(CLASS_SERVICE_BASE_URL)
                .addConverterFactory(stringConverterFactory)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build()
                .create(ApiClassService::class.java)
    }

    val libService: ApiLibService by lazy {
        Retrofit.Builder()
                .client(getOkHttpClient())
                .baseUrl(LIB_SERVICE_BASE_URL)
                .addConverterFactory(stringConverterFactory)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build()
                .create(ApiLibService::class.java)
    }
}