package cn.weli.svideo.advert.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cn.weli.svideo.advert.kuaima.DownloadMarketDBManager;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.ThreadPoolUtil;
import cn.weli.svideo.module.task.component.helper.DrawAdRewardHelper;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-01-16
 * @see [class/method]
 * @since [1.0.0]
 */
public class DownLoadMarketReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (StringUtil.equals(Intent.ACTION_PACKAGE_ADDED, action) && intent.getData() != null) {
            String pkg = intent.getData().getSchemeSpecificPart();
            DrawAdRewardHelper.getInstance().upDateTaskStatus(pkg, DrawAdRewardHelper.STATUS_DOWNLOAD_SUCCESS);
            DrawAdRewardHelper.getInstance().doTask(pkg, context);
            //安装成功启动apk
            Intent start = context.getPackageManager().getLaunchIntentForPackage(pkg);
            if (start != null) {
                try {
                    start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(start);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //安装成功回调
                String trackUrls = SharePrefUtil.getInfoFromPref("install_success_track_" + pkg,
                        StringUtil.EMPTY_STR);
                if (!StringUtil.isNull(trackUrls)) {
                    try {
                        JSONArray array = new JSONArray(trackUrls);
                        if (array.length() > 0) {
                            ArrayList<String> install_success_track_urls = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                install_success_track_urls.add(array.optString(i));
                            }
                            DownloadStatusAsyncTask asyncTask = new DownloadStatusAsyncTask();
                            asyncTask.setTrackUrls(install_success_track_urls);
                            asyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
