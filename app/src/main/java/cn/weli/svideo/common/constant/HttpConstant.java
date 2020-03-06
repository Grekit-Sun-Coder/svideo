package cn.weli.svideo.common.constant;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.RetrofitManager;
import cn.weli.svideo.BuildConfig;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.helper.FileHelper;

/**
 * 网络请求相关参数
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see HttpConstant
 * @since [1.0.0]
 */
public class HttpConstant {

    /**
     * 统计地址
     */
    public static final String HTTP_URL_ANALYTICS = "https://log-lss.weli010.cn/collect/event/v3";

    public static final String HTTP_URL_CITY = "https://weather.weilitoutiao.net/";
    /**
     * url统一管理文件id
     */
    public static final int URL_FILE_RAW = R.raw.url;
    /**
     * 请求成功的code
     */
    public static final String RESPONSE_OK = "1000";
    /**
     * 测试URL名称
     */
    public static final String HTTP_URL_VIDEO_TEST = "VIDEO_URL_TEST";
    public static final String HTTP_URL_LI_YUE_AD_TEST = "LI_YUE_AD_URL_TEST";
    public static final String HTTP_URL_CONFIG_TEST = "CONFIG_URL_TEST";
    /**
     * 测试H5名称
     */
    public static final String H5_URL_USER_AGREEMENT_TEST = "USER_AGREEMENT_URL_TEST";
    public static final String H5_URL_PRIVACY_POLICY_TEST = "PRIVACY_POLICY_URL_TEST";
    public static final String H5_URL_CANCEL_ACCOUNT_TEST = "CANCEL_ACCOUNT_URL_TEST";
    public static final String H5_URL_WITHDRAW_PROGRESS_TEST = "WITHDRAW_PROGRESS_URL_TEST";
    public static final String H5_URL_BIND_INVITE_TEST = "BIND_INVITE_URL_TEST";

    /**
     * 生产URL名称
     */
    public static final String HTTP_URL_VIDEO_PRODUCT = "VIDEO_URL_PRODUCT";
    public static final String HTTP_URL_LI_YUE_AD_PRODUCT = "LI_YUE_AD_URL_PRODUCT";
    public static final String HTTP_URL_CONFIG_PRODUCT = "CONFIG_URL_PRODUCT";

    /**
     * 生产H5名称
     */
    public static final String H5_URL_USER_AGREEMENT_PRODUCT = "USER_AGREEMENT_URL_PRODUCT";
    public static final String H5_URL_PRIVACY_POLICY_PRODUCT = "PRIVACY_POLICY_URL_PRODUCT";
    public static final String H5_URL_CANCEL_ACCOUNT_PRODUCT = "CANCEL_ACCOUNT_URL_PRODUCT";
    public static final String H5_URL_WITHDRAW_PROGRESS_PRODUCT = "WITHDRAW_PROGRESS_URL_PRODUCT";
    public static final String H5_URL_BIND_INVITE_PRODUCT = "BIND_INVITE_URL_PRODUCT";

    /**
     * Http请求URL前缀
     */
    public static String HTTP_URL_VIDEO = "";
    public static String HTTP_URL_LI_YUE_AD = "";
    public static String HTTP_URL_CONFIG = "";

    /**
     * H5地址
     */
    public static String H5_URL_USER_AGREEMENT = "";
    public static String H5_URL_PRIVACY_POLICY = "";
    public static String H5_URL_CANCEL_ACCOUNT = "";
    public static String H5_URL_WITHDRAW_PROGRESS = "";
    public static String H5_URL_BIND_INVITE = "";

    /**
     * 初始化url
     *
     * @param context app context
     */
    public static void initUrls(Context context) {
        Properties props = new Properties();
        try {
            BufferedInputStream bi = new BufferedInputStream(
                    context.getResources().openRawResource(HttpConstant.URL_FILE_RAW));
            props.load(bi);

            if (BuildConfig.URL_DEBUG) {
                HttpConstant.HTTP_URL_VIDEO = FileHelper.getUrls(props,
                        HttpConstant.HTTP_URL_VIDEO_TEST);
                HttpConstant.HTTP_URL_LI_YUE_AD = FileHelper.getUrls(props,
                        HttpConstant.HTTP_URL_LI_YUE_AD_TEST);
                HttpConstant.HTTP_URL_CONFIG = FileHelper.getUrls(props,
                        HttpConstant.HTTP_URL_CONFIG_TEST);

                HttpConstant.H5_URL_USER_AGREEMENT = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_USER_AGREEMENT_TEST);
                HttpConstant.H5_URL_PRIVACY_POLICY = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_PRIVACY_POLICY_TEST);
                HttpConstant.H5_URL_CANCEL_ACCOUNT = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_CANCEL_ACCOUNT_TEST);
                HttpConstant.H5_URL_WITHDRAW_PROGRESS = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_WITHDRAW_PROGRESS_TEST);
                HttpConstant.H5_URL_BIND_INVITE = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_BIND_INVITE_TEST);
            } else {
                HttpConstant.HTTP_URL_VIDEO = FileHelper.getUrls(props,
                        HttpConstant.HTTP_URL_VIDEO_PRODUCT);
                HttpConstant.HTTP_URL_LI_YUE_AD = FileHelper.getUrls(props,
                        HttpConstant.HTTP_URL_LI_YUE_AD_PRODUCT);
                HttpConstant.HTTP_URL_CONFIG = FileHelper.getUrls(props,
                        HttpConstant.HTTP_URL_CONFIG_PRODUCT);

                HttpConstant.H5_URL_USER_AGREEMENT = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_USER_AGREEMENT_PRODUCT);
                HttpConstant.H5_URL_PRIVACY_POLICY = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_PRIVACY_POLICY_PRODUCT);
                HttpConstant.H5_URL_CANCEL_ACCOUNT = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_CANCEL_ACCOUNT_PRODUCT);
                HttpConstant.H5_URL_WITHDRAW_PROGRESS = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_WITHDRAW_PROGRESS_PRODUCT);
                HttpConstant.H5_URL_BIND_INVITE = FileHelper.getUrls(props,
                        HttpConstant.H5_URL_BIND_INVITE_PRODUCT);
            }
            RetrofitManager.init(context, HttpConstant.HTTP_URL_VIDEO, HttpConstant.RESPONSE_OK);
            RetrofitManager.getInstance().createHeaders();
        } catch (IOException e) {
            Logger.e("Init parse url.properties file failed !");
        }
    }

    /**
     * 请求公共参数名称
     */
    public interface Params {

        String UP_TYPE = "ANDROID";

        String APP_KEY = "app_key";
        String AA_ID = "aaid";
        String OA_ID = "oaid";
        String SIM_COUNT = "sim_count";
        String DEV_MODE = "dev_mode";
        String ROOT = "root";
        String APP_TS = "app_ts";
        String UID = "uid";
        String ACCESS_TOKEN = "access_token";
        String VER_CODE = "ver_code";
        String VER_NAME = "ver_name";
        String CHANNEL = "channel";
        String CITY_KEY = "city_key";
        String CITY_KEY_1 = "cityKey1";
        String LAT = "lat";
        String LON = "lon";
        String GPS = "gps";
        String OS_VERSION = "os_version";
        String DEVICE_ID = "device_id";
        String IMEI = "imei_idfa";
        String UP = "up";
        String OPERATOR = "operator";
        String SEX = "sex";
        String APP_SIGN = "app_sign";
    }
}
