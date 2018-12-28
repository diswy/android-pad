package com.cqebd.student.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.ViewModel
import android.net.Uri
import android.os.Handler
import com.cqebd.student.net.ApiResponse
import com.cqebd.student.net.NetClient
import com.cqebd.student.repository.NetworkResource
import com.cqebd.student.vo.Resource
import com.cqebd.student.vo.entity.UserAccount
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File

/**
 * 个人中心
 * Created by gorden on 2018/3/12.
 */
class MineViewModel : ViewModel() {
    val userAccount: MediatorLiveData<UserAccount> = MediatorLiveData()

    init {
        userAccount.value = UserAccount.load()
    }

    fun refreshUser(){
        userAccount.value = null
        userAccount.value = UserAccount.load()
    }

    fun refreshFlowers(){
        val call = NetClient.workService().getFlower()
        userAccount.addSource(call, {
            userAccount.removeSource(call)
            if (it?.isSuccessful()==true){
                val flower = it.body!!.get("Flower").asInt
                if (userAccount.value?.Flower != flower) {
                    userAccount.value?.Flower = flower
                    refreshUserAccount()
                }
            }else{
                Handler().postDelayed({
                    refreshFlowers()
                },10000)
            }
        })
    }

    fun uploadAvatar(uri:Uri?):LiveData<Resource<String>> {
        val requestBody = RequestBody.create(MediaType.parse("image/jpeg"), File(uri?.path))
        return object :NetworkResource<String>(){
            override fun createCall(): LiveData<ApiResponse<String>> {
                return NetClient.apiService().uploadFile(requestBody)
            }
        }.asLiveData
    }

    fun refreshUserAccount(){
        userAccount.value?.also {
            it.save()
            userAccount.value = it
        }
    }
}