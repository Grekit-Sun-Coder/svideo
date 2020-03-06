package cn.weli.svideo.wxapi.model;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.response.HttpResponse;
import cn.weli.svideo.baselib.helper.ExecutorDelivery;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.wxapi.model.bean.WxBindBean;
import cn.weli.svideo.wxapi.model.bean.WxTokenBean;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class WxModel {

    private ExecutorDelivery mDelivery;

    public WxModel() {
        mDelivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()));
    }

    public void getWxInfo(String access_token, String openid, final HttpSubscriber<WxBindBean> listener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid).build();
        if (listener != null) {
            listener.onPreExecute();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mDelivery != null) {
                    mDelivery.post(() -> {
                        if (listener != null) {
                            listener.onNetworkError();
                        }
                        if (listener != null) {
                            listener.onPostExecute();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (mDelivery != null) {
                    mDelivery.post(() -> {
                        if (response.body() == null) {
                            return;
                        }
                        try {
                            String responseStr = response.body().string();
                            Logger.json(responseStr);
                            JSONObject json;
                            json = new JSONObject(responseStr);
                            HttpResponse<WxBindBean> commonResponse = new HttpResponse<>();
                            commonResponse.setStatus(json.optString("errcode"));
                            if (StringUtil.isNull(commonResponse.getStatus())) {
                                commonResponse.setData(handleWxInfoResult(responseStr));
                                if (listener != null) {
                                    listener.onResponseSuccess(commonResponse.getData());
                                }
                            } else {
                                commonResponse.setData(handleWxInfoErrorMsgResult(responseStr));
                                if (listener != null) {
                                    listener.onResponseError(commonResponse.getData().getMessage(),
                                            commonResponse.getStatus());
                                }
                            }
                        } catch (Exception e) {
                            Logger.e(e.getMessage());
                        }
                        if (listener != null) {
                            listener.onPostExecute();
                        }
                    });
                }
            }
        });
    }

    public void getWxAccessToken(String appid, String secret, String code,
                                 final HttpSubscriber<WxTokenBean> listener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + appid + "&secret="
                        + secret + "&code=" + code + "&grant_type=authorization_code")
                .build();
        if (listener != null) {
            listener.onPreExecute();
        }
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (mDelivery != null) {
                    mDelivery.post(() -> {
                        if (listener != null) {
                            listener.onNetworkError();
                        }
                        if (listener != null) {
                            listener.onPostExecute();
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (mDelivery != null) {
                    mDelivery.post(() -> {
                        if (response.body() == null) {
                            return;
                        }
                        try {
                            String responseStr = response.body().string();
                            Logger.json(responseStr);
                            JSONObject json;
                            json = new JSONObject(responseStr);
                            HttpResponse<WxTokenBean> commonResponse = new HttpResponse<>();
                            commonResponse.setStatus(json.optString("errcode"));
                            if (StringUtil.isNull(commonResponse.getStatus())) {
                                commonResponse.setData(handleWxTokenResult(responseStr));
                                if (listener != null) {
                                    listener.onResponseSuccess(commonResponse.getData());
                                }
                            } else {
                                commonResponse.setData(handleWxErrorMsgResult(responseStr));
                                if (listener != null) {
                                    listener.onResponseError(commonResponse.getData().message,
                                            commonResponse.getStatus());
                                }
                            }
                        } catch (Exception e) {
                            Logger.e(e.getMessage());
                        }
                        if (listener != null) {
                            listener.onPostExecute();
                        }
                    });
                }
            }
        });
    }

    private WxTokenBean handleWxErrorMsgResult(String responseStr) throws JSONException {
        JSONObject response = new JSONObject(responseStr);
        WxTokenBean bean = new WxTokenBean();
        bean.setMessage(response.optString("errmsg"));
        return bean;
    }

    private WxTokenBean handleWxTokenResult(String responseStr) throws JSONException {
        JSONObject response = new JSONObject(responseStr);
        WxTokenBean bean = new WxTokenBean();
        bean.setAccess_token(response.optString("access_token"));
        bean.setExpires_in(response.optLong("expires_in"));
        bean.setRefresh_token(response.optString("refresh_token"));
        bean.setOpenid(response.optString("openid"));
        bean.setScope(response.optString("scope"));
        bean.setUnionid(response.optString("unionid"));
        return bean;
    }

    private WxBindBean handleWxInfoErrorMsgResult(String responseStr) throws JSONException {
        JSONObject response = new JSONObject(responseStr);
        WxBindBean bean = new WxBindBean();
        bean.setMessage(response.optString("errmsg"));
        return bean;
    }

    private WxBindBean handleWxInfoResult(String responseStr) throws JSONException {
        JSONObject response = new JSONObject(responseStr);
        WxBindBean bean = new WxBindBean();
        bean.setOpenid(response.optString("openid"));
        bean.setNickname(response.optString("nickname"));
        bean.setSex(response.optInt("sex"));
        bean.setProvince(response.optString("province"));
        bean.setCity(response.optString("city"));
        bean.setCountry(response.optString("country"));
        bean.setHeadimgurl(response.optString("headimgurl"));
        bean.setPrivilege(response.optString("privilege"));
        bean.setUnionid(response.optString("unionid"));
        return bean;
    }
}
