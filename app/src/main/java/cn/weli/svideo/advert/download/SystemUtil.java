package cn.weli.svideo.advert.download;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import java.io.File;

import cn.etouch.logger.Logger;
import cn.weli.svideo.BuildConfig;

/**
 * 获取系统相关属性的工具类
 *
 * @author JXH
 * @date 18/4/13
 */
public class SystemUtil {
    private static boolean sLowMemInit = false;
    private static boolean sLowMemoryDevice = true;

    /**
     * 获取手机厂商,转为大写
     */
    public static String getMobileType() {
        String result = "";

        if (Build.MANUFACTURER != null) {
            result = Build.MANUFACTURER.toUpperCase().trim();
        }
        return result;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        try {
            int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = ctx.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 安装apk
     *
     * @param filePath apk路径
     */
    public static void installApk(Context context, String filePath) {
        try {
            if (context == null || TextUtils.isEmpty(filePath)) {
                return;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(filePath));
            } else {
                uri = Uri.fromFile(file);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            // 设置Uri和类型
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            // 执行安装
            context.startActivity(intent);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public static boolean isLowMemoryDevice(Context context) {
        // Explicitly check with an if statement, on some devices both parts of boolean expressions
        // can be evaluated even if we'd normally expect a short circuit.
        //noinspection SimplifiableIfStatement
        if (sLowMemInit) {
            return sLowMemoryDevice;
        }
        sLowMemInit = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                sLowMemoryDevice = activityManager.isLowRamDevice();
            }
        }
        return sLowMemoryDevice;
    }
}
