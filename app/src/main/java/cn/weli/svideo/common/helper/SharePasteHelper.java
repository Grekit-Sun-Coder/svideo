package cn.weli.svideo.common.helper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import org.json.JSONObject;

import java.io.File;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.component.widget.toast.WeToast;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.module.main.model.bean.ShareInfoBean;

/**
 * 粘贴分享Helper
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see cn.weli.svideo.module.main.ui.WebViewActivity
 * @since [1.0.0]
 */
public class SharePasteHelper {

    public static void sharePaste(Activity activity, String platform, String title) {
        if (StringUtil.equals(platform, ShareInfoBean.PLATFORM_WEIXIN)) {
            shareToWeiXinFriend(activity, null, title);
            try {
                JSONObject object = new JSONObject();
                object.put("share_to", "weixin");
                StatisticsAgent.share(activity, StatisticsUtils.CID.CID_301, StatisticsUtils.MD.MD_2, "", object.toString());
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        } else {
            shareToQQ(activity, title);
            try {
                JSONObject object = new JSONObject();
                object.put("share_to", "QQ");
                StatisticsAgent.share(activity, StatisticsUtils.CID.CID_301, StatisticsUtils.MD.MD_2, "", object.toString());
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    /**
     * 微信分享到好友(单张图片及描述)
     */
    public static void shareToWeiXinFriend(Activity context, File file, String description) {
        try {
            if (!isAppInstalled(context, "com.tencent.mm")) {
                WeToast.getInstance().showToast(context, R.string.share_wx_not_installed);
                return;
            }
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, description);
            if (file != null) {
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, getCompatUri(file));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // 给目标应用一个临时授权
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            } else {
                intent.setType("text/*");
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shareToQQ(Activity activity, String title) {
        try {
            if (!isAppInstalled(activity, "com.tencent.mobileqq")) {
                WeToast.getInstance().showToast(activity, R.string.share_qq_not_installed);
                return;
            }
            String packageName = "com.tencent.mobileqq";
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, "com.tencent.mobileqq.activity.JumpActivity"));
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, title);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过文件获取uri，各版本兼容
     *
     * @param file
     * @return
     */
    public static Uri getCompatUri(File file) {
        if (null == file) {
            return null;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return FileProvider.getUriForFile(WlVideoAppInfo.sAppCtx, WlVideoAppInfo.sAppCtx.getPackageName() + ".fileprovider", file);
            } else {
                return Uri.fromFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAppInstalled(Context context, String pkg) {
        final PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(pkg, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    public static boolean isWxInstalled(Context context) {
        return isAppInstalled(context, "com.tencent.mm");
    }
}
