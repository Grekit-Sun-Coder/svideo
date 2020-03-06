package cn.weli.svideo.module.main.model;

import java.util.HashMap;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.transfer.HttpRxFun;
import cn.etouch.retrofit.transfer.RxComposer;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.utils.GsonUtil;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.module.main.model.api.ConfigService;
import cn.weli.svideo.module.main.model.api.MainService;
import cn.weli.svideo.module.main.model.bean.ConfigDataBean;
import cn.weli.svideo.module.main.model.bean.VersionBean;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * 主页M层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see cn.weli.svideo.module.main.presenter.MainPresenter
 * @since [1.0.0]
 */
public class MainModel {

    private DisposableObserver mAppInitObserver;

    private DisposableObserver mAppConfigObserver;

    private DisposableObserver mVersionObserver;

    private MainService mMainService;

    private ConfigService mConfigService;

    public MainModel() {
        mMainService = RetrofitManager.getInstance().getDefaultRetrofit().create(MainService.class);
        mConfigService = RetrofitManager.getInstance().getSpecialUrlRetrofit(HttpConstant.HTTP_URL_CONFIG).create(ConfigService.class);
    }

    /**
     * 获取用户个人指引是否同意
     *
     * @return 是否同意
     */
    public boolean hasUserPrivacyAgree() {
        return SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_PRIVACY, false);
    }

    /**
     * 设置用户个人指引同意
     */
    public void setUserPrivacyAgree() {
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_PRIVACY, true);
    }

    /**
     * 获取channel
     *
     * @return channel
     */
    public String getAppChannel() {
        return SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_APP_CHANNEL, BusinessConstants.DEFAULT_CHANNEL);
    }
    /**
     * 获取引导渠道列表
     *
     * @return 渠道列表
     */
    public String getUserPrivacyList() {
        return PackageHelper.getMetaData(WlVideoAppInfo.sAppCtx, BusinessConstants.MetaData.USER_PRIVACY);
    }

    /**
     * 返回提醒是否提示过
     *
     * @return 提醒打开通知时间
     */
    public boolean hasShowNotifyPermission() {
        return SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_NO_PERMISSION, false);
    }

    /**
     * 设置是否提示过
     *
     * @param hasShow 是否提示过
     */
    public void setHasShowNotifyOPermission(boolean hasShow) {
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_NO_PERMISSION, hasShow);
    }

    /**
     * app打开次数
     *
     * @return app打开次数
     */
    public int getAppOpenTimes() {
        return SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_TIMES_OPPEN, 0);
    }

    /**
     * app打开次数
     *
     * @param time 次数
     */
    public void setHasShowNotifyOPermission(int time) {
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_TIMES_OPPEN, time);
    }

    /**
     * app打开
     *
     * @return app是否首次打开
     */
    public boolean isAppFirstStart() {
        return SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_APP_START, true);
    }

    /**
     * app打开
     */
    public void setHasAppFirstStart() {
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_APP_START, false);
    }

    /**
     * 初始化用户数据
     */
    public void initUserInfo() {
        try {
            String userStr = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_INFO, StringUtil.EMPTY_STR);
            if (!StringUtil.isNull(userStr)) {
                Logger.json("User info is " + userStr);
                UserInfoBean bean = (UserInfoBean) GsonUtil.fromJsonToObject(userStr, UserInfoBean.class);
                if (bean != null) {
                    WlVideoAppInfo.getInstance().setUserInfoBean(bean);
                    SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_ACCESS_TOKEN, bean.access_token);
                    SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_UID, bean.uid);
                    SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_SEX, bean.sex);
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 初始化接口
     *
     * @param cid        push cid
     * @param subscriber 回调
     */
    public void appInit(String cid, HttpSubscriber<Object> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("cid", cid);
        ApiHelper.addCommonParams(map);
        mAppInitObserver = mMainService
                .initAppInfo(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 版本更新
     *
     * @param subscriber 回调
     */
    public void checkAppVersion(HttpSubscriber<VersionBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mVersionObserver = mMainService
                .checkVersion(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 初始化统一配置接口
     *
     * @param map        数据
     * @param subscriber 回调
     */
    public void getAppConfig(HashMap<String, String> map, HttpSubscriber<ConfigDataBean> subscriber) {
        mAppConfigObserver = mConfigService
                .getAppConfig(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    public void cancelAppInit() {
        if (mAppInitObserver != null && !mAppInitObserver.isDisposed()) {
            mAppInitObserver.dispose();
        }
    }

    public void cancelGetAppVersion() {
        if (mVersionObserver != null && !mVersionObserver.isDisposed()) {
            mVersionObserver.dispose();
        }
    }

    public void cancelGetAppConfig() {
        if (mAppConfigObserver != null && !mAppConfigObserver.isDisposed()) {
            mAppConfigObserver.dispose();
        }
    }
}
