package cn.weli.svideo.module.main.model;

import java.util.ArrayList;
import java.util.HashMap;

import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.transfer.HttpRxFun;
import cn.etouch.retrofit.transfer.RxComposer;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.module.main.model.api.LocationService;
import cn.weli.svideo.module.main.model.bean.CityBean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * 定位相关model
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see [class/method]
 * @since [1.0.0]
 */
public class LocationModel {

    public static void getCityList(String keyword, String adCode, double lat, double lon, boolean foreign, HttpSubscriber<ArrayList<CityBean>> subscriber) {
        HashMap<String, String> map = new HashMap<>(16);
        map.put("type", HttpConstant.Params.GPS);
        map.put("keyword", StringUtil.isNull(keyword) ? StringUtil.EMPTY_STR : keyword);
        map.put("lat", StringUtil.isNull(String.valueOf(lat)) ? StringUtil.EMPTY_STR : String.valueOf(lat));
        map.put("lon", StringUtil.isNull(String.valueOf(lon)) ? StringUtil.EMPTY_STR : String.valueOf(lon));
        map.put("foreign", String.valueOf(foreign));
        if (!StringUtil.isNull(adCode)) {
            map.put("adcode", adCode);
        }
        //使用高德定位则必须传入此参数
        map.put("gpstype", "gd");
        map.put("t", String.valueOf(System.currentTimeMillis()));
        LocationService service = RetrofitManager.getInstance().getSpecialUrlRetrofit(HttpConstant.HTTP_URL_CITY).create(LocationService.class);
        DisposableObserver observer = service.getCityList(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }
}
