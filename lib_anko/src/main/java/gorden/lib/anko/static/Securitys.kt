package gorden.lib.anko.static

import java.security.MessageDigest
import java.security.MessageDigestSpi
import java.security.NoSuchAlgorithmException

/**
 * 描述
 * Created by gorden on 2017/11/13.
 */

fun md5(str:String):String{
    try {
        val instance = MessageDigest.getInstance("MD5")
        val bytes = instance.digest(str.toByteArray(Charsets.UTF_8))
        val sb = StringBuilder()
        bytes.forEach {
            var temp = Integer.toHexString(it.toInt() and 0xff)
            if (temp.length<2){
                temp = "0"+temp
            }
            sb.append(temp)
        }
        return sb.toString()
    }catch (e: NoSuchAlgorithmException){
        e.printStackTrace()
    }
    return ""
}