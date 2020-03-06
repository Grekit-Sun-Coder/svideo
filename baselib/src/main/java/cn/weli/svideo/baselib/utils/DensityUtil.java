package cn.weli.svideo.baselib.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

/**
 * 屏幕适配方案(头条方案).
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @since [1.0.0]
 */
public class DensityUtil {

    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

    private int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;

    private static DensityUtil mDensityUtil;

    private String mCurrentOrientation;

    private float mAppDensity;
    private float mAppScaledDensity;
    private DisplayMetrics mAppDisplayMetrics;
    private int mBarHeight;

    private float mWidthDensity;
    private float mWidthScaledDensity;
    private int mWidthDpi;

    private float mHeightDensity;
    private float mHeightScaledDensity;
    private int mHeightDpi;

    private DensityUtil() {
    }

    public static DensityUtil getInstance() {
        if (null == mDensityUtil) {
            synchronized (DensityUtil.class) {
                if (null == mDensityUtil) {
                    mDensityUtil = new DensityUtil();
                }
            }
        }
        return mDensityUtil;
    }

    public void initDensity(@NonNull final Application application) {
        //获取application的DisplayMetrics
        mAppDisplayMetrics = application.getResources().getDisplayMetrics();
        //获取状态栏高度
        mBarHeight = getStatusBarHeight(application);
        mScreenWidth = ScreenUtil.getScreenWidth(application);
        mScreenHeight = ScreenUtil.getScreenHeight(application);
        mStatusBarHeight = ScreenUtil.getStatusBarHeight(application);
        if (mAppDensity == 0) {
            //初始化的时候赋值
            mAppDensity = mAppDisplayMetrics.density;
            mAppScaledDensity = mAppDisplayMetrics.scaledDensity;

            //添加字体变化的监听
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体改变后,将mAppScaledDensity重新赋值
                    if (newConfig != null && newConfig.fontScale > 0) {
                        mAppScaledDensity =
                                application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
            initWidthDensity();
            initHeightDensity();
        }
    }

    public DisplayMetrics getAppDisplayMetrics() {
        return mAppDisplayMetrics;
    }

    /**
     * 获取状态栏高度
     *
     * @param context 上下文
     * @return 状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void initWidthDensity() {
        mWidthDensity = mAppDisplayMetrics.widthPixels / 375f;
        mWidthScaledDensity = mWidthDensity * (mAppScaledDensity / mAppDensity);
        mWidthDpi = (int) (160 * mWidthDensity);
    }

    private void initHeightDensity() {
        mHeightDensity = (mAppDisplayMetrics.heightPixels - mBarHeight) / 667f;
        mHeightScaledDensity = mHeightDensity * (mAppScaledDensity / mAppDensity);
        mHeightDpi = (int) (160 * mHeightDensity);
    }

    private void setDisplayMetrics(@Nullable Activity activity, float targetDensity,
                                   float targetScaledDensity, int targetDensityDpi) {
        if (null == activity) {
            return;
        }
        // 将修改过后的值赋给系统参数, 修改Activity的density值
        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

    /**
     * 设置Activity适配方向, 默认宽度适配
     *
     * @param activity activity
     */
    public void setDefault(Activity activity) {
        setOrientation(activity, WIDTH);
    }

    /**
     * 设置Activity适配方向
     *
     * @param activity    activity
     * @param orientation 方向 宽度适配="width", 高度适配="height"
     */
    public void setOrientation(Activity activity, String orientation) {
        if (HEIGHT.equals(orientation)) {
            mCurrentOrientation = HEIGHT;
            if (mHeightDensity <= 0 || mHeightScaledDensity <= 0 || mHeightDpi <= 0) {
                initHeightDensity();
            }
            setDisplayMetrics(activity, mHeightDensity, mHeightScaledDensity, mHeightDpi);
        } else {
            mCurrentOrientation = WIDTH;
            if (mWidthDensity <= 0 || mWidthScaledDensity <= 0 || mWidthDpi <= 0) {
                initWidthDensity();
            }
            setDisplayMetrics(activity, mWidthDensity, mWidthScaledDensity, mWidthDpi);
        }
    }

    /**
     * 返回当前页面适配方向
     * @return 当前页面适配方向
     */
    public String getCurrentOrientation() {
        return mCurrentOrientation;
    }

    public float getWidthDensity() {
        return mWidthDensity;
    }

    public float getHeightDensity() {
        return mHeightDensity;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        mScreenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        mScreenHeight = screenHeight;
    }

    public int getStatusBarHeight() {
        return mStatusBarHeight;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private static float scale = -1;

    public static int dp2px(Context context, float dpValue) {
        if (scale == -1) {
            scale = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dpValue * scale + 0.5f);
    }
}
