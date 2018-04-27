package com.wuhangjia.firstlib.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Author:          小夫
 * Date:            2017/9/15 13:57
 * Description:     测量屏幕大小工具
 * <p>
 * 大人者，言不必信，行不必果，惟义所在
 */

public class MeasureUtils {
    private MeasureUtils() {
        throw new AssertionError();
    }

    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }
}
