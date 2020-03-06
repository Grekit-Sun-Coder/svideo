package cn.weli.svideo.module.task.model.api;

import java.util.HashMap;
import java.util.List;

import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.module.task.model.bean.FlowDetailBean;
import cn.weli.svideo.module.task.model.bean.ProfitBean;
import cn.weli.svideo.module.task.model.bean.WithdrawAccountBean;
import cn.weli.svideo.module.task.model.bean.WithdrawProBean;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public interface WithdrawService {

    @GET("api/auth/coin/brief")
    Observable<HttpResponse<ProfitBean>> getUserProfit(@QueryMap HashMap<String, String> map);

    @GET("api/auth/product")
    Observable<HttpResponse<List<WithdrawProBean>>> getWithdrawProList(@QueryMap HashMap<String, String> map);

    @GET("api/auth/coin/flows")
    Observable<HttpResponse<List<FlowDetailBean>>> getFlowList(@QueryMap HashMap<String, String> map);

    @GET("api/auth/withdraw/account/info")
    Observable<HttpResponse<WithdrawAccountBean>> getBindInfo(@QueryMap HashMap<String, String> map);

    @POST("api/auth/withdraw/account/bind")
    Observable<HttpResponse<EmptyResponseBean>> bindWithdrawAccount(@QueryMap HashMap<String, String> map);

    @GET("api/auth/withdraw/apply")
    Observable<HttpResponse<EmptyResponseBean>> withdrawApply(@QueryMap HashMap<String, String> map);
}
