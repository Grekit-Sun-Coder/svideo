package cn.weli.svideo.baselib.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharePreference 工具类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @since [1.0.0]
 */
public class SharePrefUtil {

    private static Context sContext;

    /**
     * 注意在Application初始化的时候赋值，否则可能会导致内存泄漏
     */
    public static void init(Context context) {
        sContext = context;
    }

    public static void saveInfoToPref(String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getInfoFromPref(String key, String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        return prefs.getString(key, defaultValue);
    }

    public static int getInfoFromPref(String key, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        return prefs.getInt(key, defaultValue);
    }

    public static long getInfoFromPref(String key, long defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        return prefs.getLong(key, defaultValue);
    }

    public static boolean getInfoFromPref(String key, boolean defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        return prefs.getBoolean(key, defaultValue);
    }

    public static void saveInfoToPref(String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveInfoToPref(String key, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void saveInfoToPref(String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void removeInfoToPref(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(sContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
