package cn.weli.svideo.module.main.model.api;

import java.util.HashMap;

import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.module.main.model.bean.ConfigDataBean;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-28
 * @see [class/method]
 * @since [1.0.0]
 */
public interface ConfigService {

    @GET("api/app/config")
    Observable<HttpResponse<ConfigDataBean>> getAppConfig(@QueryMap HashMap<String, String> map);
}
