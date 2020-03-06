package cn.weli.svideo.module.mine.model.api;

import java.util.HashMap;

import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.module.mine.model.UserModel;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.mine.model.bean.VerifyCodeBean;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * 登录接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-18
 * @see UserModel
 * @since [1.0.0]
 */
public interface UserService {

    @POST("api/verify_code")
    Observable<HttpResponse<VerifyCodeBean>> getPhoneCode(@QueryMap HashMap<String, String> map);

    @POST("api/login/phone")
    Observable<HttpResponse<UserInfoBean>> loginByPhone(@QueryMap HashMap<String, String> map);

    @GET("api/auth/user/info")
    Observable<HttpResponse<UserInfoBean>> getUserInfo(@QueryMap HashMap<String, String> map);

    @POST("api/auth/logout")
    Observable<HttpResponse<EmptyResponseBean>> logout(@QueryMap HashMap<String, String> map);

    @POST("api/auth/user/info")
    Observable<HttpResponse<UserInfoBean>> updateUserInfo(@QueryMap HashMap<String, String> map);
}
