package cn.weli.svideo.common.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.etouch.logger.Logger;
import cn.weli.svideo.BuildConfig;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.helper.FileHelper;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.helper.RootHelper;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.baselib.utils.DeviceUtils;
import cn.weli.svideo.baselib.utils.EncryptUtil;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.http.NetManager;
import cn.weli.svideo.common.http.bean.RequestWrapperBean;

/**
 * 网络请求公共参数拼接
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see ApiHelper
 * @since [1.0.0]
 */
public class ApiHelper {

    private static String sAppKey;
    private static String sAppSecret;
    private static String sVerCode;
    private static String sVerName;
    private static String sChannel;
    private static String sOsVer;
    private static String sRoot;
    private static String sSimCount;
    private static String sDevMode;
    private static String sDeviceId;
    private static String sOperator;
    private static String sOperatorValue;
    private static String sImei;
    private static String sImsi;
    private static String sMac;
    private static String sCid;
    private static String sPackageName;
    private static boolean hasInitApi = false;

    /**
     * 接口公共参数
     * 以后接口调用统一需要加参数然后再添加app_sign
     *
     * @param table 已有参数
     */
    public static void addCommonParams(Map<String, String> table) {
        if (table != null) {
            if (!hasInitApi) {
                initAppInfo();
            }
            if (!table.containsKey(HttpConstant.Params.APP_KEY)) {
                table.put(HttpConstant.Params.APP_KEY, sAppKey);
            }
            table.put(HttpConstant.Params.AA_ID, getUserAaid());
            table.put(HttpConstant.Params.OA_ID, getUserOaid());
            if (!table.containsKey(HttpConstant.Params.SIM_COUNT)) {
                table.put(HttpConstant.Params.SIM_COUNT, sSimCount);
            }
            if (!table.containsKey(HttpConstant.Params.DEV_MODE)) {
                table.put(HttpConstant.Params.DEV_MODE, sDevMode);
            }
            if (!table.containsKey(HttpConstant.Params.ROOT)) {
                table.put(HttpConstant.Params.ROOT, sRoot);
            }
            if (!table.containsKey(HttpConstant.Params.APP_TS)) {
                table.put(HttpConstant.Params.APP_TS, String.valueOf(1));
            }
            if (!table.containsKey(HttpConstant.Params.UID)) {
                table.put(HttpConstant.Params.UID, SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_UID, StringUtil.EMPTY_STR));
            }
            if (!table.containsKey(HttpConstant.Params.ACCESS_TOKEN)) {
                table.put(HttpConstant.Params.ACCESS_TOKEN, SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_ACCESS_TOKEN, StringUtil.EMPTY_STR));
            }
            if (!table.containsKey(HttpConstant.Params.VER_CODE)) {
                table.put(HttpConstant.Params.VER_CODE, sVerCode);
            }
            if (!table.containsKey(HttpConstant.Params.VER_NAME)) {
                table.put(HttpConstant.Params.VER_NAME, sVerName);
            }
            if (!table.containsKey(HttpConstant.Params.CHANNEL)) {
                table.put(HttpConstant.Params.CHANNEL, sChannel);
            }

            if (!table.containsKey(HttpConstant.Params.CITY_KEY)) {
                String location = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_LOCATION, StringUtil.EMPTY_STR);
                JSONObject jsonObject;
                String cityKey = StringUtil.EMPTY_STR;
                if (!StringUtil.isNull(location)) {
                    try {
                        jsonObject = new JSONObject(location);
                        cityKey = jsonObject.optString(HttpConstant.Params.CITY_KEY_1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                table.put(HttpConstant.Params.CITY_KEY, cityKey);
            }
            if (!table.containsKey(HttpConstant.Params.OS_VERSION)) {
                table.put(HttpConstant.Params.OS_VERSION, sOsVer);
            }
            if (!table.containsKey(HttpConstant.Params.DEVICE_ID)) {
                table.put(HttpConstant.Params.DEVICE_ID, sDeviceId);
            }
            if (!table.containsKey(HttpConstant.Params.IMEI)) {
                table.put(HttpConstant.Params.IMEI, sImei);
            }
            if (!table.containsKey(HttpConstant.Params.UP)) {
                table.put(HttpConstant.Params.UP, HttpConstant.Params.UP_TYPE);
            }
            if (!table.containsKey(HttpConstant.Params.OPERATOR)) {
                table.put(HttpConstant.Params.OPERATOR, sOperator);
            }

            if (!table.containsKey(HttpConstant.Params.SEX)) {
                String sex = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_SEX, StringUtil.EMPTY_STR);
                table.put(HttpConstant.Params.SEX, String.valueOf(sex));
            }
            if (!table.containsKey(HttpConstant.Params.APP_SIGN)) {
                table.put(HttpConstant.Params.APP_SIGN, getTheAppSign(table, WlVideoAppInfo.sAppCtx));
            }
        }
    }

    /**
     * 蜂巢公共参数
     */
    public static RequestWrapperBean getRequestWrapper() {
        RequestWrapperBean bean = new RequestWrapperBean();
        try {
            if (!hasInitApi) {
                initAppInfo();
            }
            RequestWrapperBean.AppBean appBean = new RequestWrapperBean.AppBean();
            appBean.setApp_key(sAppKey);
            appBean.setApp_version(sVerName);
            appBean.setApp_version_code(sVerCode);
            appBean.setMarket_channel(sChannel);
            appBean.setPackage_name(sPackageName);
            bean.setApp(appBean);

            RequestWrapperBean.DeviceBean deviceBean = new RequestWrapperBean.DeviceBean();
            deviceBean.setAndroid_id(sDeviceId);
            deviceBean.setCarrier(sOperatorValue);
            deviceBean.setImei(sImei);
            deviceBean.setImsi(sImsi);
            deviceBean.setDensity(String.valueOf(DensityUtil.getInstance().getAppDisplayMetrics()));
            deviceBean.setMac(sMac);
            deviceBean.setLanguage(Locale.getDefault().getLanguage());
            deviceBean.setModel(android.os.Build.MODEL);
            deviceBean.setOs("Android");
            deviceBean.setOs_version(sOsVer);
            deviceBean.setResolution(new RequestWrapperBean.DeviceBean.ResolutionBean(
                    DensityUtil.getInstance().getAppDisplayMetrics().heightPixels,
                    DensityUtil.getInstance().getAppDisplayMetrics().widthPixels));
            deviceBean.setVendor(android.os.Build.MANUFACTURER);
            deviceBean.setNetwork(String.valueOf(NetManager.getIntNetworkType(WlVideoAppInfo.sAppCtx)));
            deviceBean.setOrientation("VERTICAL");
            bean.setDevice(deviceBean);

            RequestWrapperBean.GeoBean geoBean = new RequestWrapperBean.GeoBean();
            String location = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_LOCATION, StringUtil.EMPTY_STR);
            JSONObject jsonObject;
            String cityKey = StringUtil.EMPTY_STR;
            String lat = StringUtil.EMPTY_STR;
            String lon = StringUtil.EMPTY_STR;
            if (!StringUtil.isNull(location)) {
                try {
                    jsonObject = new JSONObject(location);
                    cityKey = jsonObject.optString(HttpConstant.Params.CITY_KEY_1);
                    lat = jsonObject.optString(HttpConstant.Params.LAT);
                    lon = jsonObject.optString(HttpConstant.Params.LON);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            geoBean.setCity_key(cityKey);
            geoBean.setLat(lat);
            geoBean.setLon(lon);
            geoBean.setZone("+008");
            bean.setGeo(geoBean);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return bean;
    }

    public static void setHasInitApi(boolean hasInitApi) {
        ApiHelper.hasInitApi = hasInitApi;
    }

    private static void initAppInfo() {
        try {
            PackageHelper packageHelper = new PackageHelper(WlVideoAppInfo.sAppCtx);
            sAppKey = PackageHelper.getMetaData(WlVideoAppInfo.sAppCtx, BusinessConstants.MetaData.APP_KAY);
            sAppSecret = PackageHelper.getMetaData(WlVideoAppInfo.sAppCtx, BusinessConstants.MetaData.APP_SECRET);
            sVerCode = String.valueOf(packageHelper.getLocalVersionCode());
            sVerName = packageHelper.getLocalVersionName();
            sChannel = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_APP_CHANNEL, BusinessConstants.DEFAULT_CHANNEL);
            sOsVer = String.valueOf(packageHelper.getOSVersion());
            sRoot = RootHelper.isRootSystem() ? "1" : "0";
            sSimCount = BuildConfig.URL_DEBUG ? "1" : String.valueOf(getSimCount(WlVideoAppInfo.sAppCtx));
            sDevMode = BuildConfig.URL_DEBUG ? "0" : String.valueOf(getDevDebugModel(WlVideoAppInfo.sAppCtx));
            sOperator = getSimOperator(WlVideoAppInfo.sAppCtx);
            sOperatorValue = String.valueOf(getSimOperatorValue(WlVideoAppInfo.sAppCtx));
            sImei = getImei();
            sImsi = getIMSI(WlVideoAppInfo.sAppCtx);
            sMac = getMac();
            sDeviceId = EncryptUtil.getMD5((sImei + sMac).getBytes());
            sPackageName = packageHelper.getPackageName();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public static String getSimOperator(Context context) {
        String operator = "";
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager != null) {
            operator = telManager.getSimOperator();
        }
        return operator;
    }

    /**
     * 获取对应运营商的值
     *
     * @param context
     * @return
     */
    public static int getSimOperatorValue(Context context) {
        int result = 0;
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telManager == null) {
            return 0;
        }
        String operatorName = telManager.getSimOperatorName();
        if (!TextUtils.isEmpty(operatorName)) {
            if (operatorName.equals("中国移动")) {
                //中国移动
                result = 1;
            } else if (operatorName.equals("中国联通")) {
                //中国联通
                result = 2;
            } else if (operatorName.equals("中国电信")) {
                //中国电信
                result = 3;
            }
        }
        return result;
    }

    /**
     * 将HashMap中的参数生成签名（参数排序后连接起来md5）
     */
    public static String getTheAppSign(Map<String, String> table, Context context) {
        TreeMap<String, Object> tree = new TreeMap<>();
        if (table != null) {
            Set<Map.Entry<String, String>> enu = table.entrySet();
            for (Map.Entry<String, String> item : enu) {
                tree.put(item.getKey(), item.getValue());
            }
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keys = tree.keySet();
        for (String key : keys) {
            sb.append(key).append(tree.get(key));
        }
        sb.append(sAppSecret);
        return EncryptUtil.getMD5(sb.toString().getBytes());
    }

    /**
     * 获取个推clientId
     **/
    public static String getCid() {
        if (!StringUtil.isNull(sCid)) {
            return sCid;
        } else {
            return "";
        }
    }

    /**
     * 设置个推cid
     **/
    public static void setCid(String cid) {
        sCid = cid;
    }

    /**
     * 获取sim卡数量
     *
     * @return
     */
    public static int getSimCount(Context context) {
        int result = 0;
        try {
            TelephonyManager telMgr = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            int simState = telMgr.getSimState();
            result = 1;
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    result = 0; // 没有SIM卡
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    result = 0;
                    break;
            }
            if (result == 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager mSubscriptionManager = SubscriptionManager.from(context);
                int simCardCount = getSimCardCount(context);
                int num = 0;
                if (simCardCount > 0) {
                    for (int i = 0; i < simCardCount; i++) {
                        @SuppressLint("MissingPermission") SubscriptionInfo sir = mSubscriptionManager
                                .getActiveSubscriptionInfoForSimSlotIndex(i);
                        if (sir != null) {
                            num++;
                        }
                    }
                }
                if (num > 0) {
                    result = num;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取手机sim卡数量
     *
     * @param context
     * @return
     */
    public static int getSimCardCount(Context context) {
        try {
            TelephonyManager mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            Class cls = mTelephonyManager.getClass();
            Method mMethod = cls.getMethod("getSimCount");
            mMethod.setAccessible(true);
            return (int) mMethod.invoke(mTelephonyManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 设备是否出于开发者模式
     *
     * @return
     */
    public static int getDevDebugModel(Context context) {
        try {
            boolean enableAdb = (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) > 0);
            if (enableAdb) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getUserOaid() {
        String oaid = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_DEVICE_OAID, StringUtil.EMPTY_STR);
        if (TextUtils.isEmpty(oaid)) {
            String device = FileHelper.getDeviceData4Q("device");
            if (!StringUtil.isNull(device)) {
                if (!StringUtil.isNull(device)) {
                    try {
                        JSONObject jsonObject = new JSONObject(device);
                        oaid = jsonObject.optString("oaid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (StringUtil.isNull(oaid)) {
                oaid = StringUtil.EMPTY_STR;
            }
        }
        return oaid;
    }

    public static String getUserAaid() {
        String aaid = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_DEVICE_AAID, StringUtil.EMPTY_STR);
        if (TextUtils.isEmpty(aaid)) {
            String device = FileHelper.getDeviceData4Q("device");
            if (!StringUtil.isNull(device)) {
                if (!StringUtil.isNull(device)) {
                    try {
                        JSONObject jsonObject = new JSONObject(device);
                        aaid = jsonObject.optString("aaid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (StringUtil.isNull(aaid)) {
                aaid = StringUtil.EMPTY_STR;
            }
        }
        return aaid;
    }

    /**
     * 获取imei，先从缓存获取，未获取到从sdcard中获取，再未获取到则原始获取
     */
    public static String getImei() {
        String imei = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_APP_IMEI, StringUtil.EMPTY_STR);
        if (TextUtils.isEmpty(imei)) {
            imei = DeviceHelper.getDeviceValue(DeviceHelper.IMEI);
            if (TextUtils.isEmpty(imei)) {
                TelephonyManager tm = (TelephonyManager) WlVideoAppInfo.sAppCtx
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    if (ActivityCompat.checkSelfPermission(WlVideoAppInfo.sAppCtx,
                            Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        return StringUtil.EMPTY_STR;
                    }
                    if (Build.VERSION.SDK_INT >= 29) {
                        imei = Settings.System.getString(WlVideoAppInfo.sAppCtx.getContentResolver(), Settings.System.ANDROID_ID);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        imei = tm.getImei();
                    } else {
                        imei = tm.getDeviceId();
                    }
                    if (!TextUtils.isEmpty(imei)) {
                        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_APP_IMEI, imei);
                        DeviceHelper.writeValue(DeviceHelper.IMEI, imei);
                    }
                }
            }
        }
        return imei;
    }

    /**
     * 获取 国际移动用户识别码是区别移动用户的标志 在sim卡中可用于区别移动用户的有效信息。其总长度不超过15位，同样使用0~9的数字。
     * 需要 android.permission.READ_PHONE_STATE 权限
     */
    public static String getIMSI(Context context) {
        if (ActivityCompat.checkSelfPermission(WlVideoAppInfo.sAppCtx,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return StringUtil.EMPTY_STR;
        }
        String imsi = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imsi = telephonyManager.getSubscriberId();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imsi;
    }

    public static String getMac() {
        String mac = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_APP_MAC, StringUtil.EMPTY_STR);
        if (TextUtils.isEmpty(mac) || !DeviceUtils.isAddressValid(mac)) {
            mac = DeviceHelper.getDeviceValue(DeviceHelper.MAC);
            if (TextUtils.isEmpty(mac) || !DeviceUtils.isAddressValid(mac)) {
                mac = DeviceUtils.getMacAddress(WlVideoAppInfo.sAppCtx);
                if (!TextUtils.isEmpty(mac) && DeviceUtils.isAddressValid(mac)) {
                    SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_APP_MAC, mac);
                    DeviceHelper.writeValue(DeviceHelper.MAC, mac);
                }
            }
        }
        return mac;
    }

    public static String getAppKey() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sAppKey;
    }

    public static String getVerName() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sVerName;
    }

    public static String getVerCode() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sVerCode;
    }

    public static String getChannel() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sChannel;
    }

    public static String getOsVer() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sOsVer;
    }

    public static String getModel() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return PackageHelper.getPhoneModel();
    }

    public static String getIMEI() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return StringUtil.isNull(sImei) ? StringUtil.EMPTY_STR : sImei;
    }

    public static String getDeviceId() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sDeviceId;
    }

    public static String getRoot() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sRoot;
    }

    public static String getSimCount() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sSimCount;
    }

    public static String getDevMode() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sDevMode;
    }

    public static String getIMSI() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return StringUtil.isNull(sImsi) ? StringUtil.EMPTY_STR : sImsi;
    }

    public static String getOaid() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sDevMode;
    }

    public static String getOperator() {
        if (!hasInitApi) {
            initAppInfo();
        }
        return sOperatorValue;
    }
}
