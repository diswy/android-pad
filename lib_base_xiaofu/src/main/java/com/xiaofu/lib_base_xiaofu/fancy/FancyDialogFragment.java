package com.xiaofu.lib_base_xiaofu.fancy;

import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Author:          小夫
 * Date:            2017/11/2 15:23
 * Description:     DialogFragment封装
 * <p>
 * 大人者，言不必信，行不必果，惟义所在
 */

public class FancyDialogFragment extends DialogFragment {

    private static final float DEFAULT_DIM = 0.2f;


    @LayoutRes
    private int mLayoutRes;
    private boolean canCancelOutside = true;
    private boolean isBottom;
    private int width = -1;
    private int height = -1;
    private int themeId = -1;
    private Context context;

    private ViewListener mViewListener;

    public interface ViewListener {
        void bindView(FancyDialogFragment dialog, View v);
    }

    public static FancyDialogFragment create() {
        return new FancyDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        if (isBottom) {// 底部显示
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        } else {
            params.dimAmount = DEFAULT_DIM;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            if (width != -1)
                params.width = width;
            else
                params.width = WindowManager.LayoutParams.MATCH_PARENT;

            if (height != -1)
                params.height = height;

        }

        if (themeId != -1) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = themeId;
        }

        window.setAttributes(params);
        // 这里用透明颜色替换掉系统自带背景
        int color = ContextCompat.getColor(getActivity(), android.R.color.transparent);
        window.setBackgroundDrawable(new ColorDrawable(color));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(canCancelOutside);

        try {
            View v = inflater.inflate(mLayoutRes, container, false);
            if (mViewListener != null)
                mViewListener.bindView(this, v);
            return v;
        } catch (Resources.NotFoundException e) {
            Toast.makeText(getActivity(),"系统未找到资源，请尝试关机重启",Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public FancyDialogFragment setLayoutRes(@LayoutRes int layoutRes) {
        mLayoutRes = layoutRes;
        return this;
    }

    public FancyDialogFragment setViewListener(ViewListener listener) {
        mViewListener = listener;
        return this;
    }

    public FancyDialogFragment setCanCancelOutside(Boolean bool) {
        this.canCancelOutside = bool;
        return this;
    }

    public FancyDialogFragment setBottomShow(Boolean bool) {
        this.isBottom = bool;
        return this;
    }

    public FancyDialogFragment setWidth(Context context, int widthDp) {
        width = MeasureHelperKt.dip2px(context, widthDp);
        return this;
    }

    public FancyDialogFragment setHeight(Context context, int heightDp) {
        height = MeasureHelperKt.dip2px(context, heightDp);
        return this;
    }

    public FancyDialogFragment setWidth(int widthPx) {
        width = widthPx;
        return this;
    }

    public FancyDialogFragment setHeight(int heightPx) {
        height = heightPx;
        return this;
    }

    public FancyDialogFragment setAnimation(int themeId) {
        this.themeId = themeId;
        return this;
    }
}
