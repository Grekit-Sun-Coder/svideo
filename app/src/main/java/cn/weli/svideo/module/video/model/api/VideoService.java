package cn.weli.svideo.module.video.model.api;

import java.util.HashMap;
import java.util.List;

import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.common.http.bean.RequestWrapperBean;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import cn.weli.svideo.module.video.model.bean.VideoPathBean;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * 视频相关请求接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see cn.weli.svideo.module.video.model.VideoModel
 * @since [1.0.0]
 */
public interface VideoService {

    @POST("api/feed")
    Observable<HttpResponse<List<VideoBean>>> getVideoList(@QueryMap HashMap<String, String> map, @Body RequestWrapperBean bean);

    @GET("api/play_url")
    Observable<HttpResponse<VideoPathBean>> getVideoPath(@QueryMap HashMap<String, String> map);

    @POST("api/auth/praise/cancel/{itemId}")
    Observable<HttpResponse<EmptyResponseBean>> videoUnPraise(@Path("itemId") String itemId, @QueryMap HashMap<String, String> map);

    @POST("api/auth/praise/confirm/{itemId}")
    Observable<HttpResponse<EmptyResponseBean>> videoPraise(@Path("itemId") String itemId, @QueryMap HashMap<String, String> map);

    @GET("api/auth/my_praise")
    Observable<HttpResponse<List<VideoBean>>> getVideoPraiseList(@QueryMap HashMap<String, String> map);

    @POST("api/report/{itemId}")
    Observable<HttpResponse<EmptyResponseBean>> videoReport(@Path("itemId") String itemId, @QueryMap HashMap<String, String> map);

    @GET("api/auth/share/callback")
    Observable<HttpResponse<EmptyResponseBean>> videoShare(@QueryMap HashMap<String, String> map);
}
