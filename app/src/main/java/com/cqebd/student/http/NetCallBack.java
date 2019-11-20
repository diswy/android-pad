package com.cqebd.student.http;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.cqebd.student.tools.KToastKt;
import com.fancy.xiaofu.mailutil.MailManager;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gorden.behavior.LoadingDialog;
import gorden.util.XLog;
import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * document
 * Created by Gordn on 2017/3/8.
 */

public abstract class NetCallBack<T> implements Callback<T> {

    public abstract void onSucceed(T response);

    public abstract void onFailure();

    public void onFailure(int code, String msg) {
    }

    public void parse(T response) {
        onSucceed(response);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        LoadingDialog.stop();
        if (response.body() != null && response.code() == 200) {
            System.out.println("!@#--------------1");
            parse(response.body());
        } else {
            System.out.println("!@#--------------2");
            String errMsg = "数据解析失败";
            try {
                errMsg = response.errorBody().string();
            } catch (IOException e) {
            }
            if (TextUtils.isEmpty(errMsg)) {
                Headers headers = response.headers();
                Set<String> names = headers.names();
                if (names.contains("X-Ca-Error-Message")) {
                    XLog.e(headers.get("X-Ca-Error-Message"));
                    errMsg = "签名校验失败";
                    if (headers.get("X-Ca-Error-Message").contains("Timestamp Expired")) {
                        errMsg = "请校准系统时间";
                    } else {
                        Flowable.just("318718433@qq.com").subscribe(s -> {
                            MailManager manager = new MailManager();
                            List<String> list = new ArrayList<>();
                            list.add(s);
                            try {
                                manager.sendMail(list,"点点课合成-错误报告",headers.get("X-Ca-Error-Message"));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        });
                    }
                } else {
                    errMsg = "网络请求失败，错误码：" + response.code();
                }
                KToastKt.toast(errMsg);
            } else {
                NetClient.netErr(errMsg);
            }
            onFailure();
            onFailure(response.code(), errMsg);
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        System.out.println("!@#--------------3");
        System.out.println("!@#--------------"+t.getMessage());
        LoadingDialog.stop();
        if (!call.isCanceled()) {
            KToastKt.toast("网络请求失败，请稍后重试");
        }
        NetClient.netErr(t.getMessage());
        onFailure();
        onFailure(-1, t.getMessage());
    }
}
