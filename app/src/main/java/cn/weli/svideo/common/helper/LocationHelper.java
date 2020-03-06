package cn.weli.svideo.common.helper;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.etouch.logger.Logger;
import cn.weli.analytics.AnalyticsDataAPI;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.main.model.LocationModel;
import cn.weli.svideo.module.main.model.bean.CityBean;
import cn.weli.svideo.module.main.model.bean.LocationBean;

 /**
 * 定位工具类.
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see [class/method]
 * @since [1.0.0]
 */
public class LocationHelper {

    private static LocationHelper sInstance = null;

    public static LocationHelper getInstance() {
        if (sInstance == null) {
            synchronized (LocationHelper.class) {
                if (sInstance == null) {
                    sInstance = new LocationHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 声明AMapLocationClient类对象
     */
    private AMapLocationClient mLocationClient;
    /**
     * 自定义返回定位信息回调
     */
    private LocationListener mLocationListener;

    public LocationHelper() {
    }

    /**
     * 构造函数
     *
     * @param context  上下文
     * @param listener 默认回调，可自行处理返回结果
     */
    public LocationHelper(Context context, AMapLocationListener listener) {
        // 初始化定位
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        // 设置定位回调监听
        mLocationClient.setLocationListener(listener);
    }

    /**
     * 构造函数
     *
     * @param context  上下文
     * @param listener 自定义回调，返回处理后的bean信息
     */
    public LocationHelper(Context context, LocationListener listener) {
        // 初始化定位
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        mLocationListener = listener;
        // 设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (null == aMapLocation) {
                    return;
                }
                if (aMapLocation.getErrorCode() == 0) {
                    // 定位成功
                    if (null != mLocationListener) {
                        mLocationListener.onLocationChanged(handleLocationBean(aMapLocation));
                    }
                    Logger.d("location Success，" + aMapLocation.toString());
                } else {
                    // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Logger.e("location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        });
    }

    public void startLocate(Context context, LocationListener listener) {
        // 初始化定位
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        mLocationListener = listener;
        // 设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (null == aMapLocation) {
                    return;
                }
                if (aMapLocation.getErrorCode() == 0) {
                    // 定位成功
                    LocationBean bean = handleLocationBean(aMapLocation);
                    if (null != mLocationListener) {
                        mLocationListener.onLocationChanged(bean);
                    }
                    syncCityInfo(bean);
                    Logger.d("location Success，" + aMapLocation.toString());
                } else {
                    // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Logger.e("location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        });
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        startLocation(mLocationOption);
    }

    private LocationBean handleLocationBean(AMapLocation aMapLocation) {
        LocationBean bean = new LocationBean();
        // 解析aMapLocation获取相应内容
        //获取纬度
        bean.setLatitude(aMapLocation.getLatitude());
        //获取经度
        bean.setLongitude(aMapLocation.getLongitude());
        //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
        bean.setAddress(aMapLocation.getAddress());
        //国家信息
        bean.setCountry(aMapLocation.getCountry());
        //省信息
        bean.setProvince(aMapLocation.getProvince());
        //城市信息
        bean.setCity(aMapLocation.getCity());
        //城区信息
        bean.setDistrict(aMapLocation.getDistrict());
        //街道信息
        bean.setStreet(aMapLocation.getStreet());
        //街道门牌号信息
        bean.setStreetNum(aMapLocation.getStreetNum());
        //城市编码
        bean.setCityCode(aMapLocation.getCityCode());
        //地区编码
        bean.setAdCode(aMapLocation.getAdCode());
        //获取当前定位点的AOI信息
        bean.setAoiName(aMapLocation.getAoiName());
        //获取当前定位点的POI信息
        bean.setPoiName(aMapLocation.getPoiName());
        return bean;
    }

    private void syncCityInfo(LocationBean bean) {
        //获取服务器数据
        LocationModel.getCityList(bean.getDistrict(), bean.getAdCode(), bean.getLatitude(),
                bean.getLongitude(), true, new SimpleHttpSubscriber<ArrayList<CityBean>>() {
                    @Override
                    public void onResponseSuccess(ArrayList<CityBean> cityBeans) {
                        if (cityBeans != null && !cityBeans.isEmpty()) {
                            String cityKey2 = cityBeans.get(0).cityid;
                            String cityKey1 = cityBeans.get(0).city_level_id;
                            String city2 = cityBeans.get(0).name;
                            String city1 = bean.getCity();
                            if (StringUtil.isNull(bean.getCity())) {
                                city1 = city2;
                            }
                            if (StringUtil.isNull(city2)) {
                                city2 = city1;
                            }
                            if (StringUtil.isNull(cityKey1)) {
                                cityKey1 = cityKey2;
                            }
                            if (StringUtil.isNull(cityKey2)) {
                                cityKey2 = cityKey1;
                            }
                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("city1", city1);
                                obj.put("cityKey1", cityKey1);
                                obj.put("city2", city2);
                                obj.put("cityKey2", cityKey2);
                                obj.put("city", bean.getCity());
                                obj.put("province", bean.getProvince());
                                obj.put("district", bean.getDistrict());
                                obj.put("ad_code", bean.getAdCode());
                                obj.put("lat", bean.getLatitude());
                                obj.put("lon", bean.getLongitude());
                                obj.put("address", bean.getAddress());
                                SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_LOCATION, obj.toString());
                                AnalyticsDataAPI.sharedInstance(WlVideoAppInfo.sAppCtx)
                                        .setGPSLocation(cityKey1, String.valueOf(bean.getLatitude()), String.valueOf(bean.getLongitude()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void startLocation(AMapLocationClientOption option) {
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(option);
            // 设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    /**
     * 一次定位
     */
    public void locationOnce() {
        // 初始化AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setOnceLocation(true);
        startLocation(mLocationOption);
    }

    /**
     * 停止定位(本地定位服务并不会被销毁)
     */
    public void stopLocation() {
        mLocationClient.stopLocation();
    }

    /**
     * 销毁定位客户端(同时销毁本地定位服务)
     */
    public void destroyLocation() {
        mLocationClient.onDestroy();
    }

    public interface LocationListener {
        /**
         * 定位结果
         *
         * @param bean 定位信息
         */
        void onLocationChanged(LocationBean bean);
    }

}
