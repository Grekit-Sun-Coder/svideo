package cn.weli.svideo.module.main.model.api;

import java.util.ArrayList;
import java.util.HashMap;

import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.module.main.model.bean.CityBean;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * 定位相关接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see cn.weli.svideo.module.main.model.LocationModel
 * @since [1.0.0]
 */
public interface LocationService {

    /**
     * 获取定位城市
     *
     * @param map 请求参数
     * @return 城市列表
     */
    @GET("Ecalender/api/city")
    Observable<HttpResponse<ArrayList<CityBean>>> getCityList(@QueryMap HashMap<String, String> map);
}
