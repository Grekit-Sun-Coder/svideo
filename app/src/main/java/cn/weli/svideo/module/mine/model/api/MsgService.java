package cn.weli.svideo.module.mine.model.api;

import java.util.HashMap;

import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.module.mine.model.bean.MsgResponseBean;
import cn.weli.svideo.module.mine.model.bean.RedPointBean;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
public interface MsgService {

    @POST("api/auth/user_message")
    Observable<HttpResponse<MsgResponseBean>> getMsgList(@QueryMap HashMap<String, String> map);

    @GET("api/auth/red_point")
    Observable<HttpResponse<RedPointBean>> queryRedPoint(@QueryMap HashMap<String, String> map);
}
