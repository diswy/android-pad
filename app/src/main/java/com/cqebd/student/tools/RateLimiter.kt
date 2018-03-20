package com.cqebd.student.tools

import android.os.SystemClock
import android.support.v4.util.ArrayMap
import java.util.concurrent.TimeUnit


/**
 * 是否应该获取数据
 * Created by gorden on 2017/11/8.
 */
class RateLimiter<in KEY>(timeout: Long, timeUnit: TimeUnit = TimeUnit.MINUTES) {
    private val timestamps = ArrayMap<KEY,Long>()
    private val timeout:Long = timeUnit.toMillis(timeout)

    /**
     * 是否应该获取数据
     */
    @Synchronized
    fun shouldFetch(key: KEY):Boolean{
        val lastFetched = timestamps[key]
        val now = now()
        if (lastFetched==null){
            timestamps[key] = now
            return true
        }
        if (now - lastFetched > timeout){
            timestamps[key] = now
            return true
        }
        return false
    }

    private fun now():Long = SystemClock.uptimeMillis()

    @Synchronized
    fun reset(key: KEY) = timestamps.remove(key)
}