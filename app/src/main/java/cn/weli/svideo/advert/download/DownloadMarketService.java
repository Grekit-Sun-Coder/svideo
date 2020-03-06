package cn.weli.svideo.advert.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;

import java.io.File;

import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAdDownloadData;
import cn.weli.svideo.baselib.component.widget.toast.WeToast;
import cn.weli.svideo.baselib.helper.BitmapHelper;
import cn.weli.svideo.baselib.helper.glide.config.GlideApp;
import cn.weli.svideo.baselib.utils.MD5Util;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.ThreadPoolUtil;
import cn.weli.svideo.module.task.component.helper.DrawAdRewardHelper;
import cn.weli.svideo.utils.NumberUtil;


public class DownloadMarketService extends Service {
    public int nowPosition = 0;// 当前正在下载歌曲在歌曲列表中的位�?

    private NotificationRefreshHandler notificationRefreshHandler;
    private final String ACTION_NOTIFICATION_CLICK = "action_notification_click";
    private final String ACTION_NOTIFICATION_CLEAR = "action_notification_clear";
    public static final String ACTION_NOTIFICATION_STATUS_CLICK = "action_notification_status_click";
    private NotificationReceiver notificationReceiver;
    /**
     * 下载完成时的回调用于下载完成时通知页面
     */
    private DownLoadMarketReceiver downLoadMarketReceiver;

    /**
     * 最原始的下载文件的方法入口
     *
     * @param ctx
     * @param name
     * @param isZipIsNeedExtend
     * @param extendDir
     * @param netUrl
     * @param flag
     */
    public static void DownloadTheFile(Context ctx, String name,
                                       boolean isZipIsNeedExtend, String extendDir, String netUrl,
                                       String flag) {
        DownloadTheFile(ctx, name, isZipIsNeedExtend, extendDir, netUrl, flag, false);
    }

    //相比上面加了一个是否是静默加载
    public static void DownloadTheFile(Context ctx, String name,
                                       boolean isZipIsNeedExtend, String extendDir, String netUrl,
                                       String flag, boolean isSilentDown) {
        DownloadBean db = new DownloadBean();
        db.doorflag = 1;
        db.taskId = DownloadManager.getTheNextTaskId();
        db.name = name;
        db.downloadUrl = netUrl;
        db.isZipIsNeedExtend = isZipIsNeedExtend;
        db.extendDir = extendDir;
        db.flag = flag;
        db.isSilentDownApp = isSilentDown;//是否静默下载不显示通知
        DownloadManager.addItem(db);

        try {
            Intent in = new Intent(ctx, DownloadMarketService.class);
            ctx.startService(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadTheApk(Context ctx, long apkid, String ad_RTP,
                                      String name, String netUrl, String iconUrl, ETKuaiMaAdDownloadData downloadData) {
        DownloadBean db = new DownloadBean();
        db.doorflag = 2;
        db.taskId = DownloadManager.getTheNextTaskId();
        db.name = StringUtil.isNull(name) ? "正在下载" : name;
        db.downloadUrl = netUrl;
        db.isZipIsNeedExtend = false;
        db.extendDir = "";
        db.flag = "";
        db.apkid = apkid;
        db.ad_rtp = ad_RTP;
        db.iconUrl = iconUrl;
        db.etKuaiMaAdDownloadData = downloadData;
        DownloadManager.addItem(db);
        try {
            Intent in = new Intent(ctx, DownloadMarketService.class);
            ctx.startService(in);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void DownloadTheApk(final Context ctx, final long apkid, final String ad_RTP,
                                      final String name, final String netUrl, String iconUrl, final ETKuaiMaAdDownloadData downloadData) {
        if (ctx == null) {
            return;
        }
        if (null != downloadData && !TextUtils.isEmpty(downloadData.package_name)) {//已安装就打开
            try {
                Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(downloadData.package_name);
                if (null != intent) {
                    ctx.startActivity(intent);
                    return;
                } else {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> WeToast.getInstance().showToast(WlVideoAppInfo.sAppCtx, "开始下载"));
                }
            } catch (Exception e) {

            }
        }
        downloadTheApk(ctx, apkid, ad_RTP, name, netUrl, iconUrl, downloadData);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationRefreshHandler = new NotificationRefreshHandler();
        notificationReceiver = new NotificationReceiver();
        downLoadMarketReceiver = new DownLoadMarketReceiver();

        IntentFilter packageIntentFilter = new IntentFilter();
        packageIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        packageIntentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        packageIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        packageIntentFilter.addDataScheme("package");
        this.registerReceiver(downLoadMarketReceiver, packageIntentFilter);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        downloadAllFile(this);
        super.onStart(intent, startId);
    }

    // 循环下载歌曲
    public void downloadAllFile(final Context context) {
        nowPosition = 0;
        while (nowPosition < DownloadManager.downloadList.size()) {
            DownloadBean mb = DownloadManager.downloadList.get(nowPosition);
            if (mb != null && mb.mDownload != null) {
                if (mb.isDownloading == 1) {
                    Message msg2 = new Message();// 刷新Notification
                    msg2.arg1 = 81;
                    handler.sendMessage(msg2);
                } else {
                    mb.mDownload.start();
                }
            } else {
                if (mb.downloadSize < mb.totalSize
                        && DownloadManager.downloadList.get(nowPosition).isDownloading == 0
                        && DownloadManager.DownloadToken > 0) {
                    Download mDown = new Download(context, mb.taskId,
                            mb.isZipIsNeedExtend, mb.extendDir,
                            mb.downloadUrl, new OnDownloadChangeListener() {

                        @Override
                        public void onDownloadStart(String netUrl) {
                            Message msg = new Message();
                            msg.arg1 = 81;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onDownloadPause(String netUrl) {
                            Message msg = new Message();
                            msg.arg1 = 84;
                            Bundle d = new Bundle();
                            d.putString("netUrl", netUrl);
                            msg.setData(d);
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onStartExtend(String netUrl) {

                        }

                        @Override
                        public void onDownloadSuccess(String localPath, String netUrl) {
                            Message msg = new Message();
                            msg.arg1 = 82;
                            Bundle d = new Bundle();
                            d.putString("netUrl", netUrl);
                            d.putString("localPath", localPath);
                            msg.setData(d);
                            handler.sendMessage(msg);
                            downloadAllFile(DownloadMarketService.this);
                        }

                        @Override
                        public void onDownloadError(String netUrl) {
                            Message msg = new Message();
                            msg.arg1 = 84;
                            Bundle d = new Bundle();
                            d.putString("netUrl", netUrl);
                            msg.setData(d);
                            handler.sendMessage(msg);
                            downloadAllFile(DownloadMarketService.this);
                        }
                    });
                    mb.mDownload = mDown;
                    mDown.start();
                    //下载开始回调
                    if (mb.etKuaiMaAdDownloadData != null && mb.etKuaiMaAdDownloadData.download_start_track_urls != null
                            && mb.etKuaiMaAdDownloadData.download_start_track_urls.size() > 0) {
                        DownloadStatusAsyncTask asyncTask = new DownloadStatusAsyncTask();
                        asyncTask.setTrackUrls(mb.etKuaiMaAdDownloadData.download_start_track_urls);
                        asyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
                    }
                }

            }
            nowPosition++;
        }// while
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != downLoadMarketReceiver) {
            this.unregisterReceiver(downLoadMarketReceiver);
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (DownloadManager.downloadList == null) {
            return;
        }
        for (int i = DownloadManager.downloadList.size() - 1; i >= 0; i--) {
            DownloadBean db = DownloadManager.downloadList.get(i);
            if (db != null && db.mDownload != null) {
                db.mDownload.stop();
                DownloadManager.removeItem(db.downloadUrl);
                hideOneNotification(db.taskId);
            }
        }
        if (null != notificationRefreshHandler) {
            notificationRefreshHandler.removeCallbacksAndMessages(null);
        }
    }


    class NotificationRefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Message msg2 = new Message();// 刷新Notification
            msg2.arg1 = 81;
            handler.sendMessage(msg2);
        }

        public void sleep(long delayMillis) {
            this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    }


    /**
     * 显示一个Notification
     */
    private synchronized void showOneNotification(DownloadBean downbean) {
        if (downbean.isSilentDownApp || !downbean.showNotification) {//如果是静默下载不显示通知栏
            return;
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }
        if (downbean.totalSize == 1) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_NOTIFICATION_CLEAR);
        intentFilter.addAction(ACTION_NOTIFICATION_CLICK);
        intentFilter.addAction(ACTION_NOTIFICATION_STATUS_CLICK);
        registerReceiver(notificationReceiver, intentFilter);
        if (downbean.notification == null) {
            String channelId = "wlsq_down_apk";
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // IMPORTANCE_DEFAULT 表示此渠道的重要级别: 默认default;
                NotificationChannel chanl = new NotificationChannel(channelId, "wlvideo", NotificationManager.IMPORTANCE_LOW);
                chanl.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                chanl.enableLights(false);
                chanl.enableVibration(false);
                builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
                manager.createNotificationChannel(chanl);
            } else {
                builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            }
            builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
            builder.setTicker("开始下载");
            downbean.notification = builder.build();
            Intent intent = new Intent(ACTION_NOTIFICATION_CLICK);
            intent.putExtra("netUrl", downbean.downloadUrl);
            downbean.notification.contentIntent = PendingIntent.getBroadcast(
                    DownloadMarketService.this, downbean.taskId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            downbean.notification.deleteIntent = PendingIntent.getBroadcast(
                    DownloadMarketService.this, downbean.taskId, new Intent(
                            ACTION_NOTIFICATION_CLEAR),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            downbean.notification.contentView = new RemoteViews(
                    getPackageName(), R.layout.layout_market_notification);
            Intent Intent_pre = new Intent(ACTION_NOTIFICATION_STATUS_CLICK);
            Intent_pre.putExtra("netUrl", downbean.downloadUrl);
            //得到PendingIntent
            PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, downbean.taskId, Intent_pre, PendingIntent.FLAG_UPDATE_CURRENT);
            //设置监听
            downbean.notification.contentView.setOnClickPendingIntent(R.id.tv_download_status, pendIntent_click);
        }
        /**
         * 如果更换了url,则重新注册一遍
         * **/
        if (downbean.notifyNeedUpdate) {
            downbean.notifyNeedUpdate = false;
            Intent intent = new Intent(ACTION_NOTIFICATION_CLICK);
            intent.putExtra("netUrl", downbean.downloadUrl);
            downbean.notification.contentIntent = PendingIntent.getBroadcast(
                    DownloadMarketService.this, downbean.taskId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            downbean.notification.deleteIntent = PendingIntent.getBroadcast(
                    DownloadMarketService.this, downbean.taskId, new Intent(
                            ACTION_NOTIFICATION_CLEAR),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            downbean.notification.contentView = new RemoteViews(
                    getPackageName(), R.layout.layout_market_notification);
            Intent Intent_pre = new Intent(ACTION_NOTIFICATION_STATUS_CLICK);
            Intent_pre.putExtra("netUrl", downbean.downloadUrl);
            //得到PendingIntent
            PendingIntent pendIntent_click = PendingIntent.getBroadcast(this, downbean.taskId, Intent_pre, PendingIntent.FLAG_UPDATE_CURRENT);
            //设置监听
            downbean.notification.contentView.setOnClickPendingIntent(R.id.tv_download_status, pendIntent_click);
        }
        long totalSize = downbean.totalSize;
        long downloadSize = downbean.downloadSize;
        int progress = (int) (downloadSize * 1f / totalSize * 100);
        downbean.notification.contentView.setProgressBar(R.id.progressbar_notification,
                100, progress, false);
        downbean.notification.contentView.setTextViewText(
                R.id.tv_app_name, downbean.name + "");
        if (downbean.mBitmap == null && !StringUtil.isNull(downbean.iconUrl)) {
            GlideApp.with(this).asBitmap().load(downbean.iconUrl).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    downbean.mBitmap = BitmapHelper.centerSquareScaleBitmap(resource, 120);
                }
            });
        }
        if (downbean.mBitmap != null) {
            downbean.notification.contentView.setImageViewBitmap(R.id.img_app_icon, downbean.mBitmap);
        }
        if (downbean.mDownload != null) {
            if (downbean.mDownload.isDown) {
                downbean.notification.contentView.setTextViewText(
                        R.id.tv_download_status, "暂停");
            } else {
                downbean.notification.contentView.setTextViewText(
                        R.id.tv_download_status, "继续下载");
            }
        }
        String downLoadSizeStr = NumberUtil.FormetFileSize(downloadSize);
        String totalSizeStr = NumberUtil.FormetFileSize(totalSize);
        downbean.notification.contentView.setTextViewText(
                R.id.tv_download_size, downLoadSizeStr);
        downbean.notification.contentView.setTextViewText(
                R.id.tv_total_size, totalSizeStr);

        manager.notify(downbean.taskId, downbean.notification);
    }// end showOneNotification

    /**
     * 取消显示一个Notification
     */
    private void hideOneNotification(int taskId) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(taskId);
        }
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 81:// 显示Notification
                    boolean showOneNotification = false;
                    for (DownloadBean db : DownloadManager.downloadList) {
                        if ((DownloadManager.downloadList.size() > 0)
                                && db.isDownloading == 1) {
                            showOneNotification = true;
                            showOneNotification(db);
                        }
                    }
                    if (showOneNotification) {
                        notificationRefreshHandler.sleep(500);
                    }
                    break;
                case 82: // 下载成功

                    Bundle d = msg.getData();
                    final String netUrl2 = d.getString("netUrl");
                    final DownloadBean db2 = DownloadManager.getItem(netUrl2);
                    DrawAdRewardHelper.getInstance().upDateTaskStatus(db2.etKuaiMaAdDownloadData.package_name, DrawAdRewardHelper.STATUS_DOWNLOAD_SUCCESS);
                    hideOneNotification(db2.taskId);
                    DownloadManager.removeItem(netUrl2);
                    String filePath = d.getString("localPath");
                    //下载成功回调
                    if (db2.etKuaiMaAdDownloadData != null && db2.etKuaiMaAdDownloadData.download_success_track_urls != null
                            && db2.etKuaiMaAdDownloadData.download_success_track_urls.size() > 0) {
                        DownloadStatusAsyncTask asyncTask = new DownloadStatusAsyncTask();
                        asyncTask.setTrackUrls(db2.etKuaiMaAdDownloadData.download_success_track_urls);
                        asyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
                    }
                    if (!TextUtils.isEmpty(filePath) && filePath.toLowerCase().endsWith(".apk")) {
                        if (TextUtils.isEmpty(db2.apkMd5) || (db2.isNeedCheckMd5 && MD5Util.isMatchApkMd5(filePath, db2.apkMd5))) {
                            if (!db2.isSilentDownApp) {//如果不是静默下载的话就弹出安装
                                SystemUtil.installApk(getApplicationContext(), filePath);
                                //安装开始回调
                                if (db2.etKuaiMaAdDownloadData != null && db2.etKuaiMaAdDownloadData.install_start_track_urls != null
                                        && db2.etKuaiMaAdDownloadData.install_start_track_urls.size() > 0) {
                                    DownloadStatusAsyncTask asyncTask = new DownloadStatusAsyncTask();
                                    asyncTask.setTrackUrls(db2.etKuaiMaAdDownloadData.install_start_track_urls);
                                    asyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
                                }
                                //安装成功链接设置
                                if (db2.etKuaiMaAdDownloadData != null && db2.etKuaiMaAdDownloadData.install_success_track_urls != null
                                        && db2.etKuaiMaAdDownloadData.install_success_track_urls.size() > 0) {
                                    JSONArray array = new JSONArray();
                                    for (int i = 0; i < db2.etKuaiMaAdDownloadData.install_success_track_urls.size(); i++) {
                                        array.put(db2.etKuaiMaAdDownloadData.install_success_track_urls.get(i));
                                    }
                                    SharePrefUtil.saveInfoToPref("install_success_track_" + db2.etKuaiMaAdDownloadData.package_name, array + "");
                                }
                            }
                        } else {
                            if (!db2.isSilentDownApp) {
                                WeToast.getInstance().showToast(WlVideoAppInfo.sAppCtx, "安装包错误！");
                            }
                            File file = new File(filePath);
                            boolean delete = file.delete();
                        }
                    }// end 以apk结束

                    break;
                case 84: // 下载状态该变
                    Bundle d4 = msg.getData();
                    String netUrl4 = d4.getString("netUrl");
                    DownloadBean db4 = DownloadManager.getItem(netUrl4);
                    showOneNotification(db4);

                    break;
                case 90:
                    Intent intent = new Intent(DownloadMarketService.this, DialogActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    public interface OnDownloadChangeListener {
        void onDownloadStart(String netUrl);

        void onDownloadPause(String netUrl);

        /**
         * 下载完成开始解压
         */
        void onStartExtend(String netUrl);

        void onDownloadSuccess(String localPath, String netUrl);


        void onDownloadError(String netUrl);
    }


    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.equals(intent.getAction(), ACTION_NOTIFICATION_CLICK)) {
                String netUrl = intent.getStringExtra("netUrl");
                Message msg = new Message();
                msg.arg1 = 90;
                msg.obj = netUrl;
                if (null != handler) {
                    handler.sendMessage(msg);
                }
            } else if (TextUtils.equals(intent.getAction(), ACTION_NOTIFICATION_CLEAR)) {
                if (DownloadManager.downloadList == null) {
                    return;
                }
                if (null != notificationRefreshHandler) {
                    notificationRefreshHandler.removeCallbacksAndMessages(null);
                }
                if (handler != null) {
                    handler.removeCallbacksAndMessages(null);
                }
                for (int i = DownloadManager.downloadList.size() - 1; i >= 0; i--) {
                    DownloadBean db = DownloadManager.downloadList.get(i);
                    if (db != null && db.mDownload != null) {
                        db.mDownload.stop();
                        DownloadManager.removeItem(db.downloadUrl);
                        DrawAdRewardHelper.getInstance().upDateTaskStatus(db.etKuaiMaAdDownloadData.package_name, DrawAdRewardHelper.STATUS_DOWNLOAD_START);
                        hideOneNotification(db.taskId);
                    }
                }
            } else if (TextUtils.equals(intent.getAction(), ACTION_NOTIFICATION_STATUS_CLICK)) {
                String netUrl = intent.getStringExtra("netUrl");
                if (!StringUtil.isNull(netUrl)) {
                    DownloadBean db2 = DownloadManager.getItem(netUrl);
                    if (db2 != null && db2.mDownload != null) {
                        if (db2.mDownload.isDown) {
                            db2.mDownload.stop();
                        } else {
                            db2.mDownload.start();
                        }
                    }
                }
            }
        }
    }

}
