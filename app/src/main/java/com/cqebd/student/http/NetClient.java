package com.cqebd.student.http;

import android.text.TextUtils;
import android.util.SparseArray;

import com.cqebd.student.net.gateway.GatewayInterceptor;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gorden.util.XLog;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * document
 * Created by Gordn on 2017/2/28.
 */
public class NetClient {

    private static SparseArray<List<Call>> attachCalls = new SparseArray<>();

    private static final String BASE_URL = "http://service.ex.cqebd.cn/";
    private static final String TAG = "http_log";

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .addInterceptor(new GatewayInterceptor("23393048", "d0c983467d8ced6568e844c0b0a233ae"))
            .addInterceptor(new HttpLoggingInterceptor(message -> {
                if (TextUtils.isEmpty(message)) return;
                if (message.startsWith("{") || message.startsWith("[")) {
                    XLog.e(TAG, message);
                }
            }).setLevel(HttpLoggingInterceptor.Level.BODY))
            .build();

    private static Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();

    private NetClient() {
    }

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public static <T> T createApi(Class<T> clazz) {
        return retrofit.create(clazz);
    }

    public static void netErr(String errMsg) {
        if (!TextUtils.isEmpty(errMsg)) {
            Logger.d(errMsg);
        }
    }

    public static void cancel(int key) {
        List<Call> calls = attachCalls.get(key);
        if (calls != null) {
            for (Call call : calls) {
                call.cancel();
                XLog.e("取消一个请求");
            }
            attachCalls.remove(key);
            XLog.e("移除key = " + key + "  的请求");
        }
    }

    public static void request(int key, Call call, Callback callback) {
        if (attachCalls.get(key) != null) {
            XLog.e("已有key 里添加请求");
            attachCalls.get(key).add(call);
        } else {
            List<Call> calls = new ArrayList<>();
            calls.add(call);
            XLog.e("生成key的请求");
            attachCalls.append(key, calls);
        }
        XLog.e("当前存放的请求:" + Arrays.toString(attachCalls.get(key).toArray()));
        call.enqueue(callback);
    }
}
