package gorden.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class DensityUtil {

    private Context mContext;

    public DensityUtil(Context context) {
        mContext = context;
    }

    public static int displayWidth(Context context) {

        DisplayMetrics displayMetrics = new DisplayMetrics();

        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;

    }

    public static int displayHeight(Context context) {

        DisplayMetrics displayMetrics = new DisplayMetrics();

        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.heightPixels;

    }

    public static int appWidth(Context context) {

        Rect rect = new Rect();

        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return rect.width();

    }

    public static int appHeight(Context context) {

        Rect rect = new Rect();

        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        return rect.height();

    }

    public static int statusBarHeight(Context context) {

        Resources resources = context.getResources();

        return resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android"));

    }

    public static int navigationBarHeight(Context context) {

        Resources resources = context.getResources();

        return resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android"));

    }

    public static int dip2px(int dip, Context context) {
        return (int) (dip * context.getApplicationContext().getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int[] maxTextureSize() {

        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

        egl.eglInitialize(display, new int[2]);

        int[] totalConfigurations = new int[1];
        egl.eglGetConfigs(display, null, 0, totalConfigurations);

        EGLConfig[] configurationsList = new EGLConfig[totalConfigurations[0]];
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations);

        int[] size = new int[1];
        int maxWidth = 2048, maxHeight = 2048;
        int i;

        for (i = 0; i < totalConfigurations[0]; i++) {

            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_WIDTH, size);

            if (maxWidth < size[0]) maxWidth = size[0];

            egl.eglGetConfigAttrib(display, configurationsList[i], EGL10.EGL_MAX_PBUFFER_HEIGHT, size);

            if (maxHeight < size[0]) maxHeight = size[0];

        }

        egl.eglTerminate(display);

        return new int[]{maxWidth > 8192 ? 8192 : maxWidth, maxHeight > 8192 ? 8192 : maxHeight};

    }

    public int displayWidth() {
        return displayWidth(mContext);
    }

    public int displayHeight() {
        return displayHeight(mContext);
    }

    public int appWidth() {
        return appWidth(mContext);
    }

    public int appHeight() {
        return appHeight(mContext);
    }

    public int statusBarHeight() {
        return statusBarHeight(mContext);
    }

    public int navigationBarHeight() {
        return navigationBarHeight(mContext);
    }

    public int dip2px(int dip) {
        return dip2px(dip, mContext);
    }

}
