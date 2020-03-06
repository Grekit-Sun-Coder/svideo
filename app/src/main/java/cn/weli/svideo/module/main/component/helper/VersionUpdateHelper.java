package cn.weli.svideo.module.main.component.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.download.ProgressHelper;
import cn.etouch.retrofit.download.ProgressReponseUIListener;
import cn.weli.svideo.BuildConfig;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.baselib.utils.StorageUtils;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.utils.AppUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-29
 * @see [class/method]
 * @since [1.0.0]
 */
public class VersionUpdateHelper {

    private static final String APK_NAME = "wlvideo_android";

    private static final int HANDLE_UPDATE_SUCCESS = 0x100;

    private Context mContext;

    private String mNewApkName;

    private String mNewApkNameTmp;

    private String mDownloadUrl;

    private Call mDownloadCall;

    private WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case HANDLE_UPDATE_SUCCESS:
                    deleteFile(mNewApkName);
                    renameFile(mNewApkNameTmp, mNewApkName);
                    deleteFile(mNewApkNameTmp);
                    if (AppUtils.checkPackageName(mContext, new File(getDownloadDir(), mNewApkName).getAbsolutePath())) {
                        setupApk(mNewApkName);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public VersionUpdateHelper(Context context) {
        mContext = context;
    }

    public void setDownloadUrl(String url) {
        mDownloadUrl = url;
    }

    public void startDownload() {
        Logger.d("Start download apk url is [" + mDownloadUrl + "]");
        if (StringUtil.isNull(mDownloadUrl)) {
            return;
        }
        if (mNewApkName == null) {
            mNewApkName = rename(APK_NAME);
            mNewApkNameTmp = APK_NAME + ".tmp";
        }

        deleteFile(mNewApkName);
        deleteFile(mNewApkNameTmp);

        OkHttpClient originalClient = RetrofitManager.getInstance().getOkHttpClient();
        OkHttpClient mClient = ProgressHelper.addProgressResponseListener(originalClient,
                new ProgressReponseUIListener() {
                    @Override
                    public void onUIResponseProgress(long bytesRead, long contentLength,
                                                     boolean done) {
                    }
                });
        Request downloadRequest = new Request.Builder().url(mDownloadUrl).build();
        mDownloadCall = mClient.newCall(downloadRequest);
        mDownloadCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Logger.e("onFailure Download apk failed");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws
                    IOException {
                if (response.body() == null) {
                    Logger.e("onResponse Download apk failed");
                    return;
                }
                byte[] buf = new byte[2048];
                int len;
                File file = new File(getDownloadDir(), mNewApkNameTmp);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                } catch (IOException e) {
                    Logger.e("down load new apk fail, throw an error = [" + e.getMessage() + "]");
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            Logger.e("close is after write file, throw an error = ["
                                    + e.getMessage() + "]");
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            Logger.e("close fos after write file, throw an error = ["
                                    + e.getMessage() + "]");
                        }
                    }
                }
                mHandler.sendEmptyMessageDelayed(HANDLE_UPDATE_SUCCESS, 1000);
            }
        });
    }

    private void setupApk(String name) {
        File file = new File(getDownloadDir(), name);
        if (!file.exists()) {
            Logger.d("the apk file is not exist!");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Logger.d("the device system version is over android 7.0 , so try to install by "
                    + "FileProvider !");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
//            //兼容8.0
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                boolean hasInstallPermission = mContext.getPackageManager()
// .canRequestPackageInstalls();
//                if (!hasInstallPermission) {
//                    startInstallPermissionSettingActivity();
//                    return;
//                }
//            }
        } else {
            Logger.d("the device system version is below android 7.0 , so try to install by old "
                    + "api !");
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
        //关闭旧版本的应用程序的进程
        //没有这句,安装完新软件,会出现闪退效果,需要手动找到图标重启才能进入新安装的软件
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void onDownloadCancel() {
        if (mDownloadCall != null) {
            mDownloadCall.cancel();
        }
        deleteFile(mNewApkNameTmp);
        deleteFile(mNewApkName);
    }

    private void deleteFile(String fileName) {
        if (StringUtil.isNull(fileName)) {
            return;
        }
        File apkFile = new File(getDownloadDir(), fileName);
        apkFile.delete();
    }

    private void renameFile(String oldName, String newName) {
        if (StringUtil.isNull(oldName) || StringUtil.isNull(newName)) {
            return;
        }
        File file = new File(getDownloadDir(), oldName);
        file.renameTo(new File(getDownloadDir(), newName));
    }

    private String rename(String versionName) {
        return versionName + ".apk";
    }

    /**
     * 获取下载的目录
     */
    private String getDownloadDir() {
        return StorageUtils.getExternalDirectory(mContext).getAbsolutePath();
    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
