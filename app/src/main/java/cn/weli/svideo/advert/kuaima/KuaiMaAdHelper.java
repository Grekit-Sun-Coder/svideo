package cn.weli.svideo.advert.kuaima;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.concurrent.Executors;

import cn.weli.svideo.R;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAdData;

/**
 * 鲤跃视频广告帮助类
 *
 * @author chenbixin
 * @version [v_1.0.5]
 * @date 2020-02-12
 * @see [class/method]
 * @since [v_1.0.5]
 */
public class KuaiMaAdHelper {

    /**
     * 获取下载类广告的包名
     *
     * @param context        上下文
     * @param etKuaiMaAdData 广告对象
     **/
    public static String getBtnStr(Context context, ETKuaiMaAdData etKuaiMaAdData) {
        String btnStr = "";
        if (etKuaiMaAdData == null) {
            return btnStr;
        }
        try {
            String packageName = etKuaiMaAdData.kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.package_name;
            if (!TextUtils.isEmpty(packageName) && checkAppInstalled(context, packageName)) {
                btnStr = context.getString(R.string.ad_install_success_title);
                return btnStr;
            }
        } catch (Exception e) {

        }
        return btnStr;
    }

    /**
     * 判断程序中是否安装此应用
     * @param context 上下文
     * @param pkgName 包名
     * **/
    private static boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;//true为安装了，false为未安装
        }
    }


}
