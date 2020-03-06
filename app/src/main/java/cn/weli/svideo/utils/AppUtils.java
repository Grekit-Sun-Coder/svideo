package cn.weli.svideo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-29
 * @see [class/method]
 * @since [1.0.0]
 */
public class AppUtils {

    /**
     * 判断当前包名是否和下载的Apk提取的包名一致
     *
     * @param context 上下文
     * @param apkPath apk包路径
     * @return 返回
     */
    public static boolean checkPackageName(Context context, String apkPath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (info != null && !StringUtil.isNull(info.packageName)) {
                if (info.packageName.equals(context.getPackageName())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            Logger.e("[versionName failed]:" + e.getMessage());
        }
        return true;
    }

    /**
     * 获取安装包大小以MB为单位取整
     *
     * @param apkPath 安装包路径
     * @return 返回值
     */
    public static long getApkSize(String apkPath) {
        long fileSize = -1;
        try {
            File file = new File(apkPath);
            if (file.exists() && file.isFile()) {
                fileSize = (long) (file.length() * 1.0 / (1024 * 1024));
            }
        } catch (Exception e) {

        }
        return fileSize;
    }

    /**
     * 判断接口传回的APk大小是否和下载的apk路径相同
     *
     * @param versionSize 接口传回的版本大小
     * @param apkPath     apk路径
     * @return 返回
     */
    public static boolean isSameApkSize(String versionSize, String apkPath) {
        if (StringUtil.isNull(versionSize) && !versionSize.endsWith("MB")) {
            return true;
        }
        try {
            int lastIndexOf = versionSize.lastIndexOf("MB");
            String substring = versionSize.substring(0, lastIndexOf);
            if (StringUtil.isNull(substring)) {
                return true;
            }
            long verSize = Double.valueOf(substring).longValue();
            long apkSize = getApkSize(apkPath);
            if (apkSize != -1) {
                if (verSize == apkSize) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            Logger.e("[versionSize Compare failed]:" + e.getMessage());
        }
        return true;
    }

    public static boolean isApkValid(Context context, String versionSize, String apkPath) {
        return isSameApkSize(versionSize, apkPath) && checkPackageName(context, apkPath);
    }
}
