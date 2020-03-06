package cn.weli.svideo.common.helper;

import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.util.NetworkUtil;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.ThirdSdkConstants;
import okhttp3.Response;

/**
 * 图片上传
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-26
 * @see [class/method]
 * @since [1.0.0]
 */
public class ImgUploadHelper {

    public interface OnUploadListener {
        /**
         * 开始
         */
        void onUploadStart();

        /**
         * 成功
         *
         * @param url 上传地址
         */
        void onUploadSuccess(String url);

        /**
         * 失败
         *
         * @param msg 失败原因
         */
        void onUploadError(String msg);

        /**
         * 结束
         */
        void onUploadEnd();
    }

    /**
     * 上传图片
     *
     * @param path     本地路径
     * @param listener 回调
     */
    public static void uploadPic(String path, OnUploadListener listener) {
        if (listener != null) {
            listener.onUploadStart();
        }
        if (StringUtil.isNull(path)) {
            if (listener != null) {
                listener.onUploadError(WlVideoAppInfo.sAppCtx.getString(R.string.upload_path_error));
                listener.onUploadEnd();
            }
            return;
        }
        if (!NetworkUtil.isConnected(WlVideoAppInfo.sAppCtx)) {
            if (listener != null) {
                listener.onUploadError(WlVideoAppInfo.sAppCtx.getString(R.string.common_str_network_unavailable));
                listener.onUploadEnd();
            }
            return;
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                if (listener != null) {
                    listener.onUploadError(WlVideoAppInfo.sAppCtx.getString(R.string.upload_file_error));
                    listener.onUploadEnd();
                }
                return;
            }
            Map<String, Object> paramsMap = new HashMap<>(16);
            paramsMap.put(Params.BUCKET, ThirdSdkConstants.Upy.BUCKET);
            paramsMap.put(Params.SAVE_KEY, ThirdSdkConstants.Upy.SAVE_KEY);
            paramsMap.put(Params.CONTENT_LENGTH, file.length());
            paramsMap.put(Params.RETURN_URL, ThirdSdkConstants.Upy.RETURN_URL);
            UploadEngine.getInstance().formUpload(file, paramsMap, ThirdSdkConstants.Upy.ACCOUNT, ThirdSdkConstants.Upy.PASSWORD, new UpCompleteListener() {
                @Override
                public void onComplete(boolean isSuccess, Response response, Exception error) {
                    try {
                        String result = null;
                        if (response != null) {
                            result = response.body().string();
                            Logger.json(result);
                            JSONObject object = new JSONObject(result);
                            String url = object.optString("url");
                            if (!StringUtil.isNull(url)) {
                                if (listener != null) {
                                    listener.onUploadSuccess(ThirdSdkConstants.Upy.HEADER + url);
                                }
                            } else {
                                if (listener != null) {
                                    listener.onUploadError(WlVideoAppInfo.sAppCtx.getString(R.string.upload_fail_error));
                                }
                            }
                        } else if (error != null) {
                            result = error.toString();
                            if (listener != null) {
                                listener.onUploadError(WlVideoAppInfo.sAppCtx.getString(R.string.upload_fail_error));
                            }
                            Logger.e("upload pic error is " + result);
                        }
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                        if (listener != null) {
                            listener.onUploadError(WlVideoAppInfo.sAppCtx.getString(R.string.upload_fail_error));
                        }
                    }
                    if (listener != null) {
                        listener.onUploadEnd();
                    }
                }
            }, null);
        } catch (Exception e) {
            Logger.e(e.getMessage());
            if (listener != null) {
                listener.onUploadError(WlVideoAppInfo.sAppCtx.getString(R.string.upload_fail_error));
                listener.onUploadEnd();
            }
        }
    }
}
