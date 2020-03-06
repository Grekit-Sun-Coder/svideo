package cn.weli.svideo.baselib.helper;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;

import cn.etouch.statusbar.StatusBarCompat;
import cn.weli.svideo.baselib.R;

/**
 * 状态栏工具类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @see StatusBarHelper
 * @since [1.0.0]
 */
public class StatusBarHelper {

    /**
     * 6.0 以上支持状态栏文字图标改色 + 状态栏颜色
     * 5.0 以上只支持状态栏颜色，因为想多个页面顶部是白色，与白色状态栏文字冲突，这里统一设置成黑色
     * 4.4 以上支持沉浸式，带阴影，所以当前万年历项目不需要适配
     */
    public static void setStatusBarColor(Activity activity, @ColorInt int color, boolean lightStatusBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(activity, color, lightStatusBar);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarCompat.setStatusBarColor(activity, ContextCompat.getColor(activity, R.color.color_50_333333), lightStatusBar);
        }
    }
}
