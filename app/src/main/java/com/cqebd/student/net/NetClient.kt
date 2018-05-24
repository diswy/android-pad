package com.cqebd.student.net

import com.cqebd.student.app.App
import com.cqebd.student.net.adapter.LiveDataCallAdapterFactory
import com.cqebd.student.net.api.ApiService
import com.cqebd.student.net.api.VideoService
import com.cqebd.student.net.api.WorkService
import com.cqebd.student.net.converter.ApiConverterFactory
import com.cqebd.student.net.gateway.GatewayInterceptor
import com.orhanobut.logger.Logger
import com.readystatesoftware.chuck.ChuckInterceptor
import gorden.lib.anko.static.logWarn
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

/**
 * 描述
 * Created by gorden on 2018/3/1.
 */
object NetClient {
    private const val TIME_OUT = 15L

    private val workClient by lazy {
        OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(ChuckInterceptor(App.mContext))
                .addInterceptor(GatewayInterceptor("23393048", "d0c983467d8ced6568e844c0b0a233ae"))
                .addInterceptor(HttpLoggingInterceptor { message ->
                    logWarn(message, "http_log")
                }.setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
    }

    private val videoClient by lazy {
        OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(ChuckInterceptor(App.mContext))
                .addInterceptor(GatewayInterceptor("23776862", "b5ffc0cc02a74953ea9091338117feda"))
                .addInterceptor(HttpLoggingInterceptor { message ->
                    logWarn(message, "http_log")
                }.setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
    }

    private var workService: WorkService? = null
    fun workService(): WorkService {
        if (workService == null) {
            synchronized(NetClient::class.java) {
                if (workService == null) {
                    workService = Retrofit.Builder().baseUrl("http://service.ex.cqebd.cn/")
                            .addCallAdapterFactory(LiveDataCallAdapterFactory())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(ApiConverterFactory.create())
                            .client(workClient)
                            .build().create(WorkService::class.java)
                }
            }
        }
        return workService!!
    }

    private var videoService: VideoService? = null
    fun videoService(): VideoService {
        if (videoService == null) {
            synchronized(NetClient::class.java) {
                if (videoService == null) {
                    videoService = Retrofit.Builder().baseUrl("http://service.onlin.cqebd.cn/")
                            .addCallAdapterFactory(LiveDataCallAdapterFactory())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(ApiConverterFactory.create())
                            .client(videoClient)
                            .build().create(VideoService::class.java)
                }
            }
        }
        return videoService!!
    }

    private var apiService: ApiService? = null
    fun apiService(): ApiService {
        if (apiService == null) {
            synchronized(NetClient::class.java) {
                if (apiService == null) {
                    apiService = Retrofit.Builder().baseUrl("http://service.student.cqebd.cn/")
                            .addCallAdapterFactory(LiveDataCallAdapterFactory())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(ApiConverterFactory.create())
                            .build().create(ApiService::class.java)
                }
            }
        }
        return apiService!!
    }
}