package cn.weli.svideo.module.task.model.api;

import java.util.ArrayList;
import java.util.HashMap;

import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.module.task.model.bean.CoinOpenBean;
import cn.weli.svideo.module.task.model.bean.CoinOpenStatusBean;
import cn.weli.svideo.module.task.model.bean.SignInBean;
import cn.weli.svideo.module.task.model.bean.TaskBean;
import cn.weli.svideo.module.task.model.bean.VideoTaskBean;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-21
 * @see [class/method]
 * @since [1.0.0]
 */
public interface TaskService {

    @GET("api/auth/task")
    Observable<HttpResponse<ArrayList<TaskBean>>> getTaskList(@QueryMap HashMap<String, String> map);

    @POST("api/auth/task/{taskKey}/obtain")
    Observable<HttpResponse<VideoTaskBean>> getVideoTask(@Path("taskKey") String taskKey, @QueryMap HashMap<String, String> map);

    @POST("api/auth/task/{taskKey}/submit")
    Observable<HttpResponse<VideoTaskBean>> submitVideoTask(@Path("taskKey") String taskKey, @QueryMap HashMap<String, String> map);

    @GET("api/auth/check_in/info")
    Observable<HttpResponse<SignInBean>> signIn(@QueryMap HashMap<String, String> map);

    @GET("api/auth/box/status")
    Observable<HttpResponse<CoinOpenStatusBean>> queryCoinOpenStatus(@QueryMap HashMap<String, String> map);

    @POST("api/auth/box/open")
    Observable<HttpResponse<CoinOpenBean>> openCoin(@QueryMap HashMap<String, String> map);
}
