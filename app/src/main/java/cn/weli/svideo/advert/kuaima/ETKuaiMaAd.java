package cn.weli.svideo.advert.kuaima;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.etouch.logger.Logger;
import cn.weli.common.libs.AES;
import cn.weli.common.libs.WeliLib;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.ThreadPoolUtil;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.common.http.NetManager;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-27
 * @see [class/method]
 * @since [1.0.0]
 */
public class ETKuaiMaAd {
    /**
     * 5分钟
     */
    private static final long FLAG_FIVE_MIN = 5 * 60 * 1000;

    private Activity context;
    private String type;
    private int place;// 1 是开屏广告 2是信息流广告 3是信息流大图 4是测试位置 5是kuaima3的Id 6是kuaima4的Id 7信息流帖子详情 8鱼塘
    private ETKuaiMaAdListener listener;
    private String pid = "";
    private String hexKey = "";
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public ETKuaiMaAd(Activity context, String type, int place, ETKuaiMaAdListener listener) {
        this.context = context;
        this.type = type;
        this.place = place;
        if (place == 1) {
            pid = "2000000003";
        } else if (place == 2) {
            pid = "2000000005";
        } else if (place == 3) {
            pid = "2000000029";
        } else if (place == 4) {
            pid = "2000000031";
        } else if (place == 5) {
            pid = "2000000055";
        } else if (place == 6) {
            pid = "2000000057";
        } else if (place == 7) {
            pid = "2000000111";
        } else if (place == 8) {
            pid = "2000000107";
        }
        this.listener = listener;
    }

    public ETKuaiMaAd(Activity context, String type, String adId, ETKuaiMaAdListener listener) {
        this.context = context;
        this.type = type;
        this.listener = listener;
        this.pid = adId;
    }

    public void loadAD() {
        CustomAsyncTask customAsyncTask = new CustomAsyncTask();
        customAsyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
    }

    private class CustomAsyncTask extends AsyncTask<Void, Integer, List<ETKuaiMaAdData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<ETKuaiMaAdData> doInBackground(Void... params) {
            ArrayList<ETKuaiMaAdData> datas = null;
            try {
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                JSONObject body = new JSONObject();//原始数据
                body.put("pid", pid);
                body.put("debug", 0);
                body.put("ts", System.currentTimeMillis());
                body.put("version", ApiHelper.getVerName());

                String location = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_LOCATION, StringUtil.EMPTY_STR);
                JSONObject jsonObject;
                String cityKey = StringUtil.EMPTY_STR;
                String lat = StringUtil.EMPTY_STR;
                String lon = StringUtil.EMPTY_STR;
                String province = StringUtil.EMPTY_STR;
                String city = StringUtil.EMPTY_STR;
                String district = StringUtil.EMPTY_STR;
                String ad_code = StringUtil.EMPTY_STR;
                if (!StringUtil.isNull(location)) {
                    try {
                        jsonObject = new JSONObject(location);
                        cityKey = jsonObject.optString(HttpConstant.Params.CITY_KEY_1);
                        lat = jsonObject.optString(HttpConstant.Params.LAT);
                        lon = jsonObject.optString(HttpConstant.Params.LON);
                        province = jsonObject.optString("province");
                        city = jsonObject.optString("city");
                        district = jsonObject.optString("district");
                        ad_code = jsonObject.optString("ad_code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                body.put(HttpConstant.Params.CITY_KEY, cityKey);

                JSONObject app = new JSONObject();
                app.put("app_key", ApiHelper.getAppKey());
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                app.put("app_version", pi.versionName);
                app.put("app_version_code", pi.versionCode);
                app.put("bundle", context.getPackageName());
                app.put("channel", ApiHelper.getChannel());
                app.put("support_dpl", 1);
                body.put("app", app);

                JSONObject device = new JSONObject();
                device.put("os", "Android");
                device.put("osv", android.os.Build.VERSION.RELEASE);
                device.put("carrier", ApiHelper.getOperator());
                device.put("network", NetManager.getIntNetworkType(context));
                device.put("resolution", DensityUtil.getInstance().getScreenWidth() + "*" + DensityUtil.getInstance().getScreenHeight());
                device.put("density", dm.density + "");
                device.put("open_udid", "");
                device.put("aid", androidId);
                device.put("imei", ApiHelper.getIMEI());
                device.put("imsi", ApiHelper.getIMSI());
                device.put("idfa", "");
                device.put("idfv", "");
                device.put("mac", ApiHelper.getMac());
                device.put("aaid", "");
                device.put("oaid", ApiHelper.getOaid());
                device.put("duid", "");
                device.put("orientation", 0);
                device.put("vendor", android.os.Build.MANUFACTURER);
                device.put("model", android.os.Build.MODEL);
                String lan = Locale.getDefault().getLanguage();
                device.put("lan", lan);
                device.put("ssid", "");
                device.put("root", ApiHelper.getRoot());
                device.put("zone", "+008");
                String country = Locale.getDefault().getCountry();
                device.put("nation", country);
                device.put("sim_count", ApiHelper.getSimCount());
                device.put("dev_debug", 0);
                body.put("device", device);

                JSONObject geo = new JSONObject();
                geo.put("lat", lat);
                geo.put("lon", lon);
                geo.put("province", province);
                geo.put("city", city);
                geo.put("district", district);
                geo.put("ad_code", ad_code);

                body.put("geo", geo);
                Logger.json("kaima ad request:" + body);
                String iv = AES.genHexIv();
                String encryptBody = WeliLib.getInstance().doTheAESEncrypt(body.toString(), iv, 4);
                JSONObject json = new JSONObject();
                json.put("data", encryptBody);
                json.put("iv", iv);
                json.put("app_key", ApiHelper.getAppKey());

                String result = NetManager.getInstance().doPostAsString(HttpConstant.HTTP_URL_LI_YUE_AD, null, json.toString());
                Logger.json("kaima ad result:" + result);
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object = new JSONObject(result);
                    int status = object.optInt("status", 0);
                    if (status == 1000) {
                        datas = new ArrayList<>();
                        JSONArray data = object.optJSONArray("data");
                        int size = data == null ? 0 : data.length();
                        if (size > 0) {
                            for (int i = 0; i < size; i++) {
                                JSONObject oneObj = data.optJSONObject(i);
                                if (oneObj != null) {
                                    ETKuaiMaAdData adData = new ETKuaiMaAdData(context, type);
                                    adData.parseJson(oneObj);
                                    datas.add(adData);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
            return datas;
        }

        @Override
        protected void onPostExecute(List<ETKuaiMaAdData> datas) {
            if (datas != null && datas.size() > 0) {
                listener.onADLoaded(datas);
            } else {
                listener.onNoAD();
            }
        }
    }

}

