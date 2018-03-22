package gorden.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * SharedPreferences 存储工具类
 * PreferencesUtil.getInstance().
 * Created by Gordn on 2017/2/23.
 */

public class PreferencesUtil {
    private static PreferencesUtil preferencesUtil = null;
    private static String FILE_NAME = "shared_data";
    private SharedPreferences mPreferences = null;
    private SharedPreferences.Editor editor = null;

    public static void init(String fileName) {
        FILE_NAME = fileName;
    }

    private PreferencesUtil(Context context) {
        mPreferences = context.getSharedPreferences(FILE_NAME, Activity.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public static PreferencesUtil getInstance(Context context) {
        if (preferencesUtil == null) {
            synchronized (PreferencesUtil.class) {
                if (preferencesUtil == null) {
                    preferencesUtil = new PreferencesUtil(context);
                }
            }
        }
        return preferencesUtil;
    }

    public String getString(String key) {
        return mPreferences.getString(key, "");
    }

    public String getString(String key, String def) {
        return mPreferences.getString(key, def);
    }

    public boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        return mPreferences.getBoolean(key, def);
    }

    public float getFloat(String key) {
        return mPreferences.getFloat(key, 0f);
    }

    public float getFload(String key, float def) {
        return mPreferences.getFloat(key, def);
    }

    public int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }

    public int getInt(String key, int def) {
        return mPreferences.getInt(key, def);
    }

    public long getLong(String key) {
        return mPreferences.getLong(key, 0l);
    }

    public long getLong(String key, long def) {
        return mPreferences.getLong(key, def);
    }

    public Set<String> getStringSet(String key) {
        return mPreferences.getStringSet(key, new HashSet<String>());
    }

    public Set<String> getStringSet(String key, Set<String> def) {
        return mPreferences.getStringSet(key, def);
    }

    public void putString(String key, String value) {
        editor.putString(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public void putFloat(String key, float value) {
        editor.putFloat(key, value).apply();
    }

    public void putLong(String key, long value) {
        editor.putLong(key, value).apply();
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public void putStringSet(String key, Set<String> stringSet) {
        editor.putStringSet(key, stringSet).apply();
    }

    /**
     * 移除某个key
     */
    public void remove(String key) {
        editor.remove(key).apply();
    }

    /**
     * 清楚所有数据
     */
    public void clear() {
        editor.clear().apply();
    }

    public boolean contains(String key) {
        return mPreferences.contains(key);
    }
}
