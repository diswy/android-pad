package com.cqebd.student.tools

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * 描述
 * Created by gorden on 2018/3/20.
 */
object RxCounter {
    fun counter(from: Long, to: Long): Flowable<Long> {
        return counter(from, to, 1, TimeUnit.SECONDS)
    }

    fun counter(from:Long,to:Long,delay:Int,time:TimeUnit):Flowable<Long>{
        return if (from == to)
            Flowable.empty()
        else
            Flowable.interval(0, delay.toLong(), time, AndroidSchedulers.mainThread())
                    .map {
                        if (from > to)
                            from - it
                        else
                            from + it
                    }.take(abs(from-to)+1)
    }

    /**
     * 倒计时
     *
     * @param time 倒计时多少秒
     * @return Flowable持有对象
     */
    fun tick(time: Long): Flowable<Long> {
        return if (time > 0)
            Flowable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                    .map {
                        time-it
                    }.take(time+1)
        else
            Flowable.empty()
    }

    fun tick(time: Long, timeUnit: TimeUnit): Flowable<Long> {
        return if (time > 0)
            Flowable.interval(0, 1, timeUnit, AndroidSchedulers.mainThread())
                    .map {
                        time-it
                    }.take(time+1)
        else
            Flowable.empty()
    }
}