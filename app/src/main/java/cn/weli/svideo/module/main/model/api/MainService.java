package cn.weli.svideo.module.main.model.api;

import java.util.HashMap;

import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.module.main.model.bean.VersionBean;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see [class/method]
 * @since [1.0.0]
 */
public interface MainService {

    @GET("api/init")
    Observable<HttpResponse<Object>> initAppInfo(@QueryMap HashMap<String, String> map);

    @GET("api/app_version/check")
    Observable<HttpResponse<VersionBean>> checkVersion(@QueryMap HashMap<String, String> map);
}
