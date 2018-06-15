package com.cqebd.student.vo.entity

import com.cqebd.student.tools.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * 登录用户信息
 */
@Suppress("unused")
data class UserAccount(
        @SerializedName("studentId")
        val ID: Long,
        val Name: String,//姓名
        @SerializedName("LoginName")
        val Account: String,//用户名
        @SerializedName("Tel")
        var Phone: String,//手机号
        @SerializedName("Photo")
        var Avatar: String,//头像
        val Gender: String,//性别
        @SerializedName("SubjectType")
        val SubjectList: List<Subject>,//科目
        @SerializedName("ExaminationPapersType")
        val JobTypeList: List<JobType>,//作业类型
        val ImagesUrl: String,
        val ImagesTag: String,
        val OssAccessUrl: String,
        val OssAccessUrlTag: String,
        var Flower: Int,//红花数量
        val IsGroup: Boolean//是否为小组长
) {
    //科目
    data class Subject(val Id: Int,
                       val Name: String,
                       val Status: Int)
    //作业类型
    data class JobType(val Id: Int,
                       val Name: String)




    /**
     * 保存用户信息
     */
    fun save(){
        setValue("id" to ID)
        setValue("account" to Gson().toJson(this))
    }
    companion object {
        /**
         * 读取本地用户信息
         */
        fun load():UserAccount?{
            val account = getValue("account","")
            if (account.isEmpty()){
                return null
            }
            return Gson().fromJson<UserAccount>(account,UserAccount::class.java)
        }

        /**
         * 清除本地用户信息
         */
        fun clear(){
            removeValue("id")
            removeValue("account")
        }
    }
}