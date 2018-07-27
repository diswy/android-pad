package com.cqebd.lib_netease.cache

/**
 * 网易云缓存
 * create by xiaofu
 */

internal const val TAG = "NCache"

class NCache {

    companion object {
        fun getInstance() = Instance.instance
    }

    private object Instance {
        val instance = NCache()
    }

    private var isLogin = false

    fun isLogin() = isLogin

    fun login(account:String) {
        this.isLogin = true
        setAccount(account)
    }

    fun logout() {
        this.isLogin = false
        this.account = "default"
    }

    /**
     * 网易云账号缓存
     */
    private var account: String = "default"

    private fun setAccount(account: String) {
        this.account = account
    }

    fun getAccount() = account
}