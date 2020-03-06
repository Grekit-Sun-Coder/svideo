package cn.weli.svideo;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.bun.miitmdid.core.JLibrary;
import com.igexin.sdk.PushManager;
import com.luck.picture.lib.app.IApp;
import com.luck.picture.lib.app.PictureAppMaster;
import com.meituan.android.walle.WalleChannelReader;
import com.microquation.linkedme.android.LinkedME;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.commonsdk.UMConfigure;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.etouch.cache.CacheConfiguration;
import cn.etouch.cache.CacheManager;
import cn.etouch.logger.Logger;
import cn.weli.analytics.AnalyticsDataAPI;
import cn.weli.svideo.baselib.helper.FileHelper;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.WatchDogKillerUtil;
import cn.weli.svideo.common.Statistics.FloatViewManager;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.constant.CacheConstant;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.module.linkedme.MiddleActivity;
import cn.weli.svideo.push.WlVideoIntentService;
import cn.weli.svideo.push.WlVideoPushService;
import video.movieous.droid.player.MovieousPlayer;
import video.movieous.droid.player.MovieousPlayerEnv;
import video.movieous.droid.player.strategy.ULoadControl;

/**
 * Application类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see WlVideoApplication
 * @since [1.0.0]
 */
public class WlVideoApplication extends MultiDexApplication implements IApp {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!isMainProcess()) {
            return;
        }
        initCommon();
        initStrictMode();
        initLogger();
        initLeakCanary();
        initUmSdk();
        initGetUi();
        initMoviePlayer();
        initDensity();
        initCache();
        initLinkedMe();
        WlVideoAppInfo.getInstance().init(this, getApplicationContext());
    }

    /**
     * 初始化友盟sdk及V3埋点sdk
     */
    private void initUmSdk() {
        JLibrary.InitEntry(getApplicationContext());
        String channel = WalleChannelReader.getChannel(getApplicationContext());
        channel = StringUtil.isNull(channel) ? BusinessConstants.DEFAULT_CHANNEL : channel;
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_APP_CHANNEL, channel);
        UMConfigure.init(getApplicationContext(), PackageHelper.getMetaData(this, BusinessConstants.MetaData.UMENG_APPKEY),
                channel, UMConfigure.DEVICE_TYPE_PHONE, null);
        initAnalyticsData(channel);
    }

    /**
     * 初始化个推
     */
    private void initGetUi() {
        PushManager.getInstance().initialize(getApplicationContext(), WlVideoPushService.class);
        PushManager.getInstance().registerPushIntentService(getApplicationContext(), WlVideoIntentService.class);
    }

    /**
     * 初始化LeakCanary检测内存泄漏
     */
    private void initLeakCanary() {
        if (BuildConfig.LOG_DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                return;
            }
            LeakCanary.install(this);
        }
    }

    /**
     * 初始化视频控件
     */
    private void initMoviePlayer() {
        // 视频播放器sdk初始化
        MovieousPlayerEnv.init(this, PackageHelper.getMetaData(this, BusinessConstants.MetaData.UCLOUD_KEY));
        // 最小缓冲3s,最大缓冲10s,首次缓冲开始播放时间500ms，再次缓冲播放时间1s,后两者都需小雨最小缓冲时间;
        MovieousPlayer.setLoadControl(new ULoadControl(3 * 1000, 15 * 1000, 500, 1000));
    }

    /**
     * 设置严格模式.
     */
    private void initStrictMode() {
        if (BuildConfig.LOG_DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyFlashScreen()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
    }

    /**
     * 初始化日志
     */
    private void initLogger() {
        Logger.setIsDebug(BuildConfig.LOG_DEBUG);
        Logger.setTAG(BusinessConstants.FLAG_LOG_TAG);
    }

    /**
     * 初始化缓存.
     */
    private void initCache() {
        CacheConfiguration configuration = new CacheConfiguration.Builder(getApplicationContext())
                .maxDiskTime(Integer.MAX_VALUE)
                .maxMemoryTime(CacheConstant.FLAG_MAX_MEMORY_TIME)
                .maxMemoryCount(CacheConstant.FLAG_MAX_MEMORY_COUNT)
                .diskCacheFileCount(CacheConstant.FLAG_DISK_FILE_COUNT)
                .diskCacheSize(CacheConstant.FLAG_DISK_FILE_SIZE)
                .build();
        CacheManager.getInstance().init(configuration);
    }

    /**
     * 初始化项目里需要的一些东西
     */
    private void initCommon() {
        SharePrefUtil.init(getApplicationContext());
        PictureAppMaster.getInstance().setApp(this);
        WatchDogKillerUtil.stopWatchDog();
    }

    /**
     * 初始化 Analytics SDK
     */
    private void initAnalyticsData(String channel) {
        AnalyticsDataAPI.setChannel(channel);
        AnalyticsDataAPI.sharedInstance(this, HttpConstant.HTTP_URL_ANALYTICS, AnalyticsDataAPI.DebugMode.DEBUG_OFF);
        AnalyticsDataAPI.sharedInstance().enableLog(BuildConfig.STATISTICS_DEBUG);
        List<AnalyticsDataAPI.AutoTrackEventType> eventTypeList = new ArrayList<>();
        eventTypeList.add(AnalyticsDataAPI.AutoTrackEventType.APP_START);
        eventTypeList.add(AnalyticsDataAPI.AutoTrackEventType.APP_END);
        eventTypeList.add(AnalyticsDataAPI.AutoTrackEventType.APP_VIEW_SCREEN);
        AnalyticsDataAPI.sharedInstance(this).enableAutoTrack(eventTypeList);

        String uid = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_UID, StringUtil.EMPTY_STR);
        if (!StringUtil.isNull(uid)) {
            AnalyticsDataAPI.sharedInstance(this).login(uid);
        } else {
            AnalyticsDataAPI.sharedInstance(this).logout();
        }
        String location = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_LOCATION, StringUtil.EMPTY_STR);
        String cityKey = StringUtil.EMPTY_STR, lat = StringUtil.EMPTY_STR, lon = StringUtil.EMPTY_STR;
        try {
            if (!StringUtil.isNull(location)) {
                JSONObject jsonObject = new JSONObject(location);
                cityKey = jsonObject.optString(HttpConstant.Params.CITY_KEY_1, StringUtil.EMPTY_STR);
                lat = jsonObject.optString(HttpConstant.Params.LAT);
                lon = jsonObject.optString(HttpConstant.Params.LON);
            }
        } catch (JSONException e) {
            Logger.w("Get location info error is [" + e.getMessage() + "]");
        }
        AnalyticsDataAPI.sharedInstance(this).setGPSLocation(cityKey, lat, lon);
        try {
            AnalyticsDataAPI.sharedInstance(this).setCommonData(new JSONObject(FileHelper.getDeviceData4Q("device")));
            if (BuildConfig.STATISTICS_DEBUG) {
                FloatViewManager.getInstance(this).addWindow();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 屏幕适配初始化
     */
    private void initDensity() {
        DensityUtil.getInstance().initDensity(this);
    }

    /**
     * 获取是否是主进程
     *
     * @return 是否是主进程
     */
    private boolean isMainProcess() {
        return StringUtil.equals(getProcessName(), getPackageName());
    }

    /**
     * 获取当前进程的名称.
     */
    private String getProcessName() {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> rps;
        String name = "";
        if (am != null) {
            rps = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo rp : rps) {
                if (rp.pid == pid) {
                    name = rp.processName;
                    break;
                }
            }
        }
        return name;
    }

    /**
     * linkedme 初始化
     */
    private void initLinkedMe(){
        // 初始化SDK，为了提高初始化效率，linkedme key不再在AndroidManifest.xml文件中配置
        LinkedME.getInstance(this, PackageHelper.getMetaData(this, BusinessConstants.MetaData.LINKEDME_KEY));
        if (BuildConfig.DEBUG) {
            //设置debug模式下打印LinkedME日志
            LinkedME.getInstance().setDebug();
        }
        //初始时请设置为false
        LinkedME.getInstance().setImmediate(false);
        //设置处理跳转逻辑的中转页，MiddleActivity详见后续配置
        LinkedME.getInstance().setHandleActivity(MiddleActivity.class.getName());
    }

    /**
     * 在主线程中执行代码块。
     */
    public static void runOnUiThread(Runnable action) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            action.run();
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }
}
