package cn.weli.svideo.module.main.model.api;

import java.util.HashMap;

import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.etouch.retrofit.response.HttpResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-01-11
 * @see [class/method]
 * @since [1.0.0]
 */
public interface AdvertService {

    @GET("api/auth/stats_ad/report")
    Observable<HttpResponse<EmptyResponseBean>> reportAdEvent(@QueryMap HashMap<String, String> map);
}
