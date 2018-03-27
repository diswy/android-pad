package com.cqebd.student.net

import com.google.gson.JsonParseException
import gorden.lib.anko.static.logError
import gorden.lib.anko.static.logWarn
import org.json.JSONException
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException


/**
 * Common class used by API responses.
 * Created by gorden on 2017/11/8.
 */
open class ApiResponse<T> {

    var code: Int
    var body: T? = null
    var errorMessage: String? = null

    constructor(response:Response<BaseResponse<T>>){
        code = response.code()
        if (response.isSuccessful){
            val responseBody = response.body()
            logWarn("responseBody  is  $responseBody")
            when {
                responseBody==null -> {
                    code = 1001
                    errorMessage = "数据格式异常"
                }
                responseBody.isSuccess -> {
                    this.body = responseBody.data
                    errorMessage = null
                }
                else -> {
                    code = responseBody.status+1000
                    errorMessage = responseBody.message
                }
            }
        }else{
            var message: String? = null
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody()?.string()
                    errorMessage = "网络请求失败,错误码:$code"
                } catch (ignored: IOException) {
                    logError(ignored, "error while parsing response")
                }
            }

            if (message.isNullOrBlank()){
                val headers = response.headers()
                val names = headers.names()
                if (names.contains("X-Ca-Error-Message")){
                    errorMessage = "签名校验失败"
                    if (headers.get("X-Ca-Error-Message")!!.contains("Timestamp Expired")) {
                        errorMessage = "请校准系统时间"
                    }
                }else{
                    errorMessage = "网络请求失败,错误码:$code"
                }
            }

            body = null
        }
    }

    constructor(error:Throwable?){
        body = null
        code = 500
        if (error is HttpException) {
            code = error.code()
            errorMessage = error.message()
        } else if (error is JsonParseException || error is JSONException || error is ParseException) {
            errorMessage = "数据解析失败"
        } else if (error is UnknownHostException || error is ConnectException) {
            errorMessage = "网络连接失败，请检查网络"
        } else if (error is SocketTimeoutException || error is SocketException) {
            errorMessage = "网络连接超时"
        } else {
            errorMessage = error?.message?:"服务器连接失败"
        }
    }

    constructor(code:Int,t:T?,error:String?){
        this.code = code
        this.body = t
        this.errorMessage = error
    }

    fun isSuccessful(): Boolean {
        return code in 200..299
    }
}