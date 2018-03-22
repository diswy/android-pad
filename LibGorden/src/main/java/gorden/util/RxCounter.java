package gorden.util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

/**
 * document Rxjava2.0 计时计数
 * Created by Gordn on 2017/2/27.
 */

public class RxCounter {
    public static Flowable<Long> L(long from, long to) {
        return counter(from, to, 1, TimeUnit.SECONDS);
    }

    public static Flowable<Long> counter(final long from, final long to, int delay, TimeUnit time) {
        return from == to ? Flowable.<Long>empty() :
                Flowable.interval(0, delay, time, AndroidSchedulers.mainThread())
                        .map(new Function<Long, Long>() {
                            @Override
                            public Long apply(Long aLong) throws Exception {
                                if (from > to)
                                    return from - aLong;
                                else
                                    return from + aLong;
                            }
                        }).take(Math.abs(from - to) + 1);
    }

    /**
     * 倒计时
     *
     * @param time 倒计时多少秒
     * @return Flowable持有对象
     */
    public static Flowable<Long> tick(final long time) {
        return time > 0 ? Flowable.interval(0, 1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return time - aLong;
                    }
                }).take(time + 1) : Flowable.<Long>empty();
    }

    public static Flowable<Long> tick(final long time,TimeUnit timeUnit) {
        return time > 0 ? Flowable.interval(0, 1, timeUnit, AndroidSchedulers.mainThread())
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long aLong) throws Exception {
                        return time - aLong;
                    }
                }).take(time + 1) : Flowable.<Long>empty();
    }
}
