package com.cqebd.student.netease.base;

public interface IScrollStateListener {

    /**
     * move to scrap heap
     */
    void reclaim();


    /**
     * on idle
     */
    void onImmutable();
}
