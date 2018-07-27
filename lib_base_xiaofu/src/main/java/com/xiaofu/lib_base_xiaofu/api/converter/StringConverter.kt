package com.xiaofu.lib_base_xiaofu.api.converter

import okhttp3.ResponseBody
import retrofit2.Converter

class StringConverter : Converter<ResponseBody, String> {

    companion object {
        fun getInstance() = Instance.instance
    }

    private object Instance {
        val instance = StringConverter()
    }

    override fun convert(value: ResponseBody): String {
        return value.string()
    }
}