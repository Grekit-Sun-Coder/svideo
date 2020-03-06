package cn.weli.svideo.common.helper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.RetrofitManager;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.advert.toutiao.TtAdManagerHolder;
import cn.weli.svideo.baselib.utils.GsonUtil;
import cn.weli.svideo.baselib.utils.MD5Util;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.constant.ConfigConstants;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.main.model.MainModel;
import cn.weli.svideo.module.main.model.bean.ConfigBean;
import cn.weli.svideo.module.main.model.bean.ConfigDataBean;

/**
 * 客户端统一配置
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-28
 * @see cn.weli.svideo.module.main.ui.MainActivity
 * @since [1.0.0]
 */
public class ClientConfigHelper {

    private static ClientConfigHelper sHelper;

    public static ClientConfigHelper getInstance() {
        if (sHelper == null) {
            synchronized (ClientConfigHelper.class) {
                if (sHelper == null) {
                    sHelper = new ClientConfigHelper();
                }
            }
        }
        return sHelper;
    }

    /**
     * 检查客户端统一配置项
     *
     */
    public void checkLocalClientConfig() {
        try {
            String cache = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_APP_CONFIG, StringUtil.EMPTY_STR);
            if (!StringUtil.isNull(cache)) {
                ConfigDataBean bean = (ConfigDataBean) GsonUtil.fromJsonToObject(cache, ConfigDataBean.class);
                if (bean != null) {
                    Logger.d("Launch app check local has cache client config, so deal client config");
                    dealClientConfig(bean);
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 检查服务器客户端统一配置项
     */
    public void checkRemoteClientConfig() {
        MainModel mainModel = new MainModel();
        HashMap<String, String> map = new HashMap<>();
        map.put("app_key", BusinessConstants.Config.CONFIG_APP_ID);
        map.put("envir", BusinessConstants.Config.CONFIG_EN);
        map.put("sign", getTheAppSign());
        mainModel.getAppConfig(map, new SimpleHttpSubscriber<ConfigDataBean>() {
            @Override
            public void onResponseSuccess(ConfigDataBean configBean) {
                if (configBean != null) {
                    dealRemoteClientConfig(configBean);
                }
            }
        });
    }

    /**
     * 处理服务器客户端统一配置数据
     *
     * @param bean 服务器客户端统一配置数据
     */
    private void dealRemoteClientConfig(ConfigDataBean bean) {
        try {
            String cache = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_APP_CONFIG, StringUtil.EMPTY_STR);
            if (!StringUtil.isNull(cache)) {
                ConfigDataBean cacheBean = (ConfigDataBean) GsonUtil.fromJsonToObject(cache, ConfigDataBean.class);
                if (cacheBean != null && cacheBean.updateTime != bean.updateTime) {
                    Logger.d("Current config is different from local config, so need to update cache!");
                    SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_APP_CONFIG, GsonUtil.toJson(bean));
                    dealClientConfig(bean);
                    return;
                }
                Logger.d("Current config is same as local config, so not need to update cache!");
            } else {
                Logger.d("Local config is empty, so need to save cache!");
                SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_APP_CONFIG, GsonUtil.toJson(bean));
                dealClientConfig(bean);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 处理覆盖本地缓存的客户端配置
     */
    private void dealClientConfig(ConfigDataBean configDataBean) {
        try {
            JSONObject object = new JSONObject(configDataBean.config);
            Iterator<String> iterator = object.keys();
            String configData = StringUtil.EMPTY_STR;
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (StringUtil.equals(key, ConfigConstants.ConfigKey.APP_CONFIG)) {
                    configData = object.get(key).toString();
                }
            }
            if (StringUtil.isNull(configData)) {
                Logger.d("config data is null");
                return;
            }
            Logger.d("config data is\n" + configData);
            ConfigBean bean = (ConfigBean) GsonUtil.fromJsonToObject(configData, ConfigBean.class);
            if (bean != null) {
                if (!StringUtil.isNull(bean.videoBaseUrl)) {
                    HttpConstant.HTTP_URL_VIDEO = bean.videoBaseUrl;
                }
                if (!StringUtil.isNull(bean.liYueBaseUrl)) {
                    HttpConstant.HTTP_URL_LI_YUE_AD = bean.liYueBaseUrl;
                }
                if (!StringUtil.isNull(bean.rewardVideoId)) {
                    ConfigConstants.REWARD_VIDEO_ID = bean.rewardVideoId;
                }
                if (!StringUtil.isNull(bean.ttAdAppId)) {
                    ConfigConstants.TT_AD_APP_ID = bean.ttAdAppId;
                }
                if (!StringUtil.isNull(bean.drawVideoId)) {
                    ConfigConstants.TT_DRAW_VIDEO_ID = bean.drawVideoId;
                }
                if (!StringUtil.isNull(bean.liYueVideoId)) {
                    ConfigConstants.LI_YUE_VIDEO_ID = bean.liYueVideoId;
                }
                if (bean.document != null) {
                    ConfigConstants.PROFIT_DESC = StringUtil.isNull(bean.document.profitDesc)
                            ? WlVideoAppInfo.sAppCtx.getString(R.string.profit_detail_title) : bean.document.profitDesc;
                    ConfigConstants.WITHDRAW_DESC = StringUtil.isNull(bean.document.withdrawDesc)
                            ? WlVideoAppInfo.sAppCtx.getString(R.string.withdraw_rule_detail_title) : bean.document.withdrawDesc;
                    ConfigConstants.ACCOUNT_DESC = StringUtil.isNull(bean.document.accountDesc)
                            ? WlVideoAppInfo.sAppCtx.getString(R.string.ali_bind_info_title) : bean.document.accountDesc;
                }
            }
            RetrofitManager.init(WlVideoAppInfo.sAppCtx, HttpConstant.HTTP_URL_VIDEO, HttpConstant.RESPONSE_OK);
            TtAdManagerHolder.reInit(WlVideoAppInfo.sAppCtx);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 生成签名
     */
    private static String getTheAppSign() {
        String sb = BusinessConstants.Config.CONFIG_APP_ID + BusinessConstants.Config.CONFIG_SECRET_KEY;
        return MD5Util.getMD5(sb.getBytes());
    }
}
