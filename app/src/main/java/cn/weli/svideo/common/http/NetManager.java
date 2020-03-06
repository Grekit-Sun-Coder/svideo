package cn.weli.svideo.common.http;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import cn.weli.analytics.utils.ChannelUtil;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.http.bean.NetParams;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static okhttp3.MultipartBody.FORM;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-27
 * @see [class/method]
 * @since [1.0.0]
 */
public class NetManager {

    private static NetManager instance = null;
    private String userAgentStr = "";

    /**
     * Context 无需再传20120601，传入null即可
     */
    static public NetManager getInstance() {
        if (instance == null) {
            instance = new NetManager();
        }
        return instance;
    }

    private NetManager() {
        userAgentStr = equipUserAgent(WlVideoAppInfo.sAppCtx);
        try {
            client = new OkHttpClient();
        } catch (Error error) {
            isOKHttpCaseException = true;
        } catch (Exception e) {//OKHttp只支持2.3版本以上的系统！
            isOKHttpCaseException = true;
            e.printStackTrace();
        }
    }

    private boolean isOKHttpCaseException = false;
    private OkHttpClient client = null;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public String doPostAsString(String url, Hashtable<String, String> tableData) {
        return doPostAsString(url, tableData, "");
    }

    /**
     * 执行post返回String
     * 参数一定不要编码(方法内有编码处理)
     */
    public String doPostAsString(String url, Hashtable<String, String> tableData, String body) {
        StringBuffer sb = new StringBuffer();
        try {
            if (isOKHttpCaseException || client == null) {
//                if (standbyNetManager==null){
//                    standbyNetManager=new StandbyNetManager();
//                }
//                sb.append(standbyNetManager.doPostAsString(url,tableData,body));
            } else {
                NetParams params = new NetParams();
                if (tableData != null) {
                    Enumeration<String> enu = tableData.keys();
                    String key = "";
                    while (enu.hasMoreElements()) {
                        key = enu.nextElement();
                        String val = tableData.get(key);
                        params.addParam(key, val);
                    }
                }
                sb.append(post(url, params.getParamsAsString(), body));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return sb.toString();
        }
    }

    /**
     * 执行post,获取httpCode,只在trace系统上传数据时使用
     */
    public int doPostAsCode(String url, Hashtable<String, String> tableData) {
        int code = -1;
        try {
            if (isOKHttpCaseException || client == null) {
//                if (standbyNetManager==null){
//                    standbyNetManager=new StandbyNetManager();
//                }
//                code = standbyNetManager.doPostAsCode(url, tableData);
            } else {
                NetParams params = new NetParams();
                if (tableData != null) {
                    Enumeration<String> enu = tableData.keys();
                    String key = "";
                    while (enu.hasMoreElements()) {
                        key = enu.nextElement();
                        String val = tableData.get(key);
                        params.addParam(key, val);
                    }
                }
                RequestBody body = RequestBody.create(FORM, params.getParamsAsString());
                if (TextUtils.isEmpty(userAgentStr)) {
                    userAgentStr = getUserAgent(WlVideoAppInfo.sAppCtx) + equipUserAgent(WlVideoAppInfo.sAppCtx);
                }
                Request request = new Request.Builder()
                        .url(url)
                        .post(body).addHeader("User-Agent",
                                getUserAgent())//addHeader("User-Agent", userAgentStr)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.code() != 200) {
                    response.body().close();
                }

                code = response.code();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public String doGetAsString(String url) {
        NetParams params = null;
        return doGetAsString(url, params);
    }


    /**
     * get方法获取结果，并将结果转化为字符串
     * 参数一定不要编码(方法内有编码处理)
     */
    public String doGetAsString(String url, Hashtable<String, String> table) {
        NetParams params = new NetParams();
        if (table != null) {
            Enumeration<String> enu = table.keys();
            String key = "";
            while (enu.hasMoreElements()) {
                key = enu.nextElement();
                String val = table.get(key);
                params.addParam(key, val);
            }
        }
        return doGetAsString(url, params);
    }

    /**
     * get方法获取结果，并将结果转化为字符串,该方法可以保证参数顺序不变
     * 参数一定不要编码(方法内有编码处理)
     */
    public String doGetAsString(String url, NetParams params) {
        StringBuffer sb = new StringBuffer();
        try {
            String getUrl = url;
            if (params != null && !TextUtils.isEmpty(params.getParamsAsString())) {
                if (url.contains("?")) {
                    getUrl = url + "&" + params.getParamsAsString();
                } else {
                    getUrl = url + "?" + params.getParamsAsString();
                }
            }
            if (isOKHttpCaseException || client == null) {
//                if (standbyNetManager==null){
//                    standbyNetManager=new StandbyNetManager();
//                }
//                sb.append(standbyNetManager.doGetAsString(getUrl));
            } else {
                sb.append(get(getUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return sb.toString();
        }
    }

    /**
     * get方法获取返回的httpCode，只在 ETNativeAdData 中调用
     * 参数一定不要编码(方法内有编码处理)
     */
    public int doGetAsCode(String url) {
        int result = -1;
        try {
            if (isOKHttpCaseException || client == null) {
//                if (standbyNetManager==null){
//                    standbyNetManager=new StandbyNetManager();
//                }
//                result = standbyNetManager.doGetAsCode(url);
            } else {
                result = getAsCode(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String post(String url, String form, String json) throws IOException {
//        MLog.d("url=" + url + "\ndata=" + form);
//        MLog.d("body=" + json);
        RequestBody body;
        if (TextUtils.isEmpty(json)) {
            body = RequestBody.create(FORM, form);
        } else {
            if (!TextUtils.isEmpty(form)) {
                if (url.contains("?")) {
                    url = url + "&" + form;
                } else {
                    url = url + "?" + form;
                }
            }
            body = RequestBody.create(JSON, json);
        }
        if (TextUtils.isEmpty(userAgentStr)) {
            userAgentStr = getUserAgent(WlVideoAppInfo.sAppCtx) + equipUserAgent(WlVideoAppInfo.sAppCtx);
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body).addHeader("User-Agent",
                        getUserAgent())//addHeader("User-Agent", userAgentStr)
                .build();

        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            response.body().close();
            return "";
        } else {
            return response.body().string();
        }
    }

    private String get(String url) throws IOException {
        if (TextUtils.isEmpty(userAgentStr)) {
            userAgentStr = getUserAgent(WlVideoAppInfo.sAppCtx) + equipUserAgent(WlVideoAppInfo.sAppCtx);
        }
        Request request = new Request.Builder().addHeader("User-Agent", userAgentStr)
                .url(url).build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            response.body().close();
        }
        return response.body().string();
    }

    private int getAsCode(String url) throws IOException {
        if (TextUtils.isEmpty(userAgentStr)) {
            userAgentStr = getUserAgent(WlVideoAppInfo.sAppCtx) + equipUserAgent(WlVideoAppInfo.sAppCtx);
        }
        Request request = new Request.Builder().addHeader("User-Agent", userAgentStr)
                .url(url).build();
        Response response = client.newCall(request).execute();
        if (response.code() != 200) {
            response.body().close();
        } else {
            String res = response.body().string();
        }
        return response.code();
    }

    /**
     * 判断wifi是否可用
     */
    public static boolean isWiFiActive(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && "WIFI".equals(info.getTypeName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否有可用网络
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }// end isNetworkAvailable


    class MyX509TrustManager implements X509TrustManager {

        X509TrustManager sunJSSEX509TrustManager;

        public MyX509TrustManager() throws Exception {
            // create a "default" JSSE X509TrustManager.
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            TrustManager tms[] = tmf.getTrustManagers();
            /*
             * Iterate over the returned trustmanagers, look
             * for an instance of X509TrustManager.  If found,
             * use that as our "default" trust manager.
             */
            for (int i = 0; i < tms.length; i++) {
                if (tms[i] instanceof X509TrustManager) {
                    sunJSSEX509TrustManager = (X509TrustManager) tms[i];
                    return;
                }
            }
            /*
             * Find some other way to initialize, or else we have to fail the
             * constructor.
             */
            throw new Exception("Couldn't initialize");
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                if (sunJSSEX509TrustManager != null) {
                    sunJSSEX509TrustManager.checkClientTrusted(chain, authType);
                }
            } catch (CertificateException excep) {
                // do any special handling here, or rethrow exception.
            }
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            try {
                chain[0].checkValidity();
            } catch (Exception e) {
                try {
                    if (sunJSSEX509TrustManager != null) {
                        sunJSSEX509TrustManager.checkServerTrusted(chain, authType);
                    }
                } catch (CertificateException excep) {
                }
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 封装userAgent,软件中WebView userAgent在末尾添加上这一段
     */
    public static String equipUserAgent(Context ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ssy={Android;KuaiMaBrowser;");
        ApplicationInfo ai;
        try {
            ai = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            sb.append("V").append(info.versionName).append(";");
            sb.append(String.valueOf(ChannelUtil.getChannel(ctx))).append(";");
            ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo net_info = connectivity.getActiveNetworkInfo();
                if (net_info != null) {
                    sb.append(";").append(net_info.getTypeName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.append("}").toString();
    }

    /**
     * 获取 原始的 UserAgent
     */
    public static String getUserAgent(Context context) {
        String result = "";
        try {
            result = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_AGENT, StringUtil.EMPTY_STR);
            if (TextUtils.isEmpty(result)) {
                WebView webview = new WebView(context);
                WebSettings settings = webview.getSettings();
                result = settings.getUserAgentString();
                SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_USER_AGENT, result);
                NetManager.getInstance().updateUserAgent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 尚顶要求的 2xx, 301, 302 都是成功
     *
     * @param code
     */
    public static boolean isHttpCodeSuccess(int code) {
        return (code >= 200 && code < 300) || code == 301 || code == 302;
    }

    private static String getUserAgent() {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(WlVideoAppInfo.sAppCtx);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 更新UserAgent
     */
    public void updateUserAgent() {
        userAgentStr = getUserAgent(WlVideoAppInfo.sAppCtx) + equipUserAgent(WlVideoAppInfo.sAppCtx);
    }

    public static String getNetworkType(Context context) {
        String result = "UNKNOW";
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                result = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        result = "MOBILE_2G";//2G
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        result = "MOBILE_3G";//3G
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        result = "MOBILE_4G";//4G
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            result = "MOBILE_3G";//3G
                        }
                        break;
                }
            }
        }
        return result;
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    public static int getIntNetworkType(Context context) {
        int result = 0;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return result;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                result = 2;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        result = 4;//2G
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        result = 6;//3G
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        result = 4;//4G
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            result = 5;//3G
                        }
                        break;
                }
            }
        }
        return result;
    }
}
