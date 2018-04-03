package com.cqebd.student.netease.base;

public interface TAdapterDelegate {

    int getViewTypeCount();

    Class<? extends TViewHolder> viewHolderAtPosition(int position);

    boolean enabled(int position);
}