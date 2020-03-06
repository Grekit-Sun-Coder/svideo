package cn.weli.svideo.module.mine.model;

import java.util.HashMap;

import cn.etouch.cache.CacheManager;
import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.etouch.retrofit.transfer.HttpRxFun;
import cn.etouch.retrofit.transfer.RxComposer;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.utils.GsonUtil;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.CacheConstant;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.module.mine.model.api.UserService;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.mine.model.bean.VerifyCodeBean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * 登陆注册相关M层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see cn.weli.svideo.module.mine.presenter.LoginPresenter
 * @since [1.0.0]
 */
public class UserModel {

    private DisposableObserver mLoginObserver;

    private DisposableObserver mCodeObserver;

    private DisposableObserver mLogoutObserver;

    private DisposableObserver mUserInfoObserver;

    private UserService mUserService;

    public UserModel() {
        mUserService = RetrofitManager.getInstance().getDefaultRetrofit().create(UserService.class);
    }

    /**
     * 登录
     *
     * @param phone      手机号
     * @param code       验证码
     * @param subscriber 回调
     */
    public void login(String phone, String code, HttpSubscriber<UserInfoBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("ticket", code);
        ApiHelper.addCommonParams(map);
        mLoginObserver = mUserService
                .loginByPhone(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 获取验证码
     *
     * @param phone      手机号
     * @param subscriber 回调
     */
    public void getVerifyCode(String phone, HttpSubscriber<VerifyCodeBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        ApiHelper.addCommonParams(map);
        mCodeObserver = mUserService
                .getPhoneCode(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 获取用户信息
     *
     * @param subscriber 回调
     */
    public void getUserInfoData(HttpSubscriber<UserInfoBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mCodeObserver = mUserService
                .getUserInfo(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 退出登陆
     *
     * @param subscriber 回调
     */
    public void logout(HttpSubscriber<EmptyResponseBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mLogoutObserver = mUserService
                .logout(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 更新用户信息
     *
     * @param avatar     头像
     * @param nickname   昵称
     * @param sex        性别
     * @param subscriber 回调
     */
    public void updateUserInfo(String avatar, String nickname, String sex, HttpSubscriber<UserInfoBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        if (!StringUtil.isNull(avatar)) {
            map.put("avatar", avatar);
        }
        if (!StringUtil.isNull(nickname)) {
            map.put("nick_name", nickname);
        }
        if (!StringUtil.isNull(sex)) {
            map.put("sex", sex);
        }
        ApiHelper.addCommonParams(map);
        mUserInfoObserver = mUserService
                .updateUserInfo(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 保存用户token
     *
     * @param accessToken token
     */
    public void saveUserAccessToken(String accessToken) {
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_ACCESS_TOKEN, accessToken);
    }

    /**
     * 保存用户uid
     *
     * @param infoBean 用户
     */
    public void saveUserInfo(UserInfoBean infoBean) {
        if (infoBean == null) {
            return;
        }
        WlVideoAppInfo.getInstance().setUserInfoBean(infoBean);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_UID, infoBean.uid);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_OPEN_ID, infoBean.open_uid);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_SEX, infoBean.sex);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_ACCESS_TOKEN, infoBean.access_token);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_INFO, GsonUtil.toJson(infoBean));
    }

    /**
     * 更新用户信息
     *
     * @param bean 用户信息
     */
    public void updateLocalUserInfo(UserInfoBean bean) {
        if (bean != null) {
            bean.access_token = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_ACCESS_TOKEN, StringUtil.EMPTY_STR);
            bean.open_uid = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_OPEN_ID, StringUtil.EMPTY_STR);
            saveUserInfo(bean);
        }
    }

    /**
     * 清除用户信息
     */
    public void clearUserInfo() {
        WlVideoAppInfo.getInstance().setUserInfoBean(new UserInfoBean());
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_UID, StringUtil.EMPTY_STR);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_OPEN_ID, StringUtil.EMPTY_STR);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_SEX, StringUtil.EMPTY_STR);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_INFO, StringUtil.EMPTY_STR);
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_ACCESS_TOKEN, StringUtil.EMPTY_STR);
        CacheManager.getInstance().delete(CacheConstant.CACHE_PROFIT_INFO);
        CacheManager.getInstance().delete(CacheConstant.CACHE_PRAISE_LIST);
    }

    public void cancelLogin() {
        if (mLoginObserver != null && !mLoginObserver.isDisposed()) {
            mLoginObserver.dispose();
        }
    }

    public void cancelPhoneCode() {
        if (mCodeObserver != null && !mCodeObserver.isDisposed()) {
            mCodeObserver.dispose();
        }
    }

    public void cancelLogout() {
        if (mLogoutObserver != null && !mLogoutObserver.isDisposed()) {
            mLogoutObserver.dispose();
        }
    }

    public void cancelUpdateUser() {
        if (mUserInfoObserver != null && !mUserInfoObserver.isDisposed()) {
            mUserInfoObserver.dispose();
        }
    }
}
