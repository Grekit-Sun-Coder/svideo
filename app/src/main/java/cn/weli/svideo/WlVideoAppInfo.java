package cn.weli.svideo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.util.List;

import cn.etouch.logger.Logger;
import cn.weli.svideo.advert.toutiao.TtAdManagerHolder;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.common.helper.ClientConfigHelper;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.video.model.bean.VideoBean;

/**
 * Application全局信息
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-05
 * @see WlVideoApplication
 * @since [1.0.0]
 */
public class WlVideoAppInfo {

    public static Context sAppCtx;

    private static WlVideoAppInfo sInstance = null;

    /**
     * APP是否在前台
     */
    private boolean isRunningForeground = false;
    /**
     * activity在前台个数
     */
    private int mActivityCount = 0;

    private boolean isAppRunning;

    private UserInfoBean mUserInfoBean;

    private List<VideoBean> mPreVideoList;


    public static WlVideoAppInfo getInstance() {
        if (sInstance == null) {
            synchronized (WlVideoAppInfo.class) {
                if (sInstance == null) {
                    sInstance = new WlVideoAppInfo();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     * url初始化
     */
    public void init(WlVideoApplication application, Context applicationContext) {
        sAppCtx = applicationContext;
        application.registerActivityLifecycleCallbacks(new SwitchBackgroundCallbacks());
        ApiHelper.setHasInitApi(false);
        initUrls();
    }



    /**
     * 初始化url
     */
    private void initUrls() {
        HttpConstant.initUrls(sAppCtx);
        ClientConfigHelper.getInstance().checkLocalClientConfig();
        TtAdManagerHolder.init(sAppCtx);
    }

    public UserInfoBean getUserInfoBean() {
        if (mUserInfoBean == null) {
            mUserInfoBean = new UserInfoBean();
        }
        return mUserInfoBean;
    }

    public void setUserInfoBean(UserInfoBean userInfoBean) {
        mUserInfoBean = userInfoBean;
    }

    /**
     * 判断当前用户是否登录
     *
     * @return 用户是否登录
     */
    public boolean hasLogin() {
        if (mUserInfoBean != null && !StringUtil.isNull(mUserInfoBean.access_token)) {
            return true;
        }
        return false;
    }

    /**
     * 获取预加载的视频列表
     */
    public List<VideoBean> getPreVideoList() {
        return mPreVideoList;
    }

    /**
     * 设置预加载的视频列表
     */
    public void setPreVideoList(List<VideoBean> preVideoList) {
        mPreVideoList = preVideoList;
    }

    public boolean isAppRunning() {
        return isAppRunning;
    }

    public void setAppRunning(boolean appRunning) {
        isAppRunning = appRunning;
    }

    /**
     * 判断APP是否在前台
     *
     * @return isRunningForeground
     */
    public boolean isRunningForeground() {
        return isRunningForeground;
    }

    private class SwitchBackgroundCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (mActivityCount == 0) {
                Logger.d("SwitchBackgroundCallbacks, >>>>>>>>>>App切换到前台");
                isRunningForeground = true;
            }
            mActivityCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            mActivityCount--;
            if (mActivityCount == 0) {
                Logger.d("SwitchBackgroundCallbacks, >>>>>>>>>>App切换到后台");
                isRunningForeground = false;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
