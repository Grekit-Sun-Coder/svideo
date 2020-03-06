package cn.weli.svideo.common.widget.webview;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.tencent.smtt.sdk.WebSettings;

import java.io.File;
import java.util.Map;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.util.NetworkUtil;
import cn.weli.svideo.baselib.component.jsbridge.BridgeWebView;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StorageUtils;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ApiHelper;

/**
 * 通用webview
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see [class/method]
 * @since [1.0.0]
 */
public class WeWebView extends BridgeWebView {

    private static final int MAX_CACHE_SIZE = 1024 * 1024 * 4;

    private String mSourceUserAgent = "";

    private boolean mTouchByUser;

    public WeWebView(Context context) {
        this(context.getApplicationContext(), null);
    }

    public WeWebView(Context context, AttributeSet attrs) {
        this(context.getApplicationContext(), attrs, 0);
    }

    public WeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context.getApplicationContext(), attrs, defStyleAttr);
        initSetting(context.getApplicationContext());
    }

    private void initSetting(Context context) {
        removeJavascriptInterface("searchBoxJavaBridge_");
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
        WebSettings settings = getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setSaveFormData(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setSavePassword(false);
        settings.setAllowUniversalAccessFromFileURLs(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (getX5WebViewExtension() != null) {
            getX5WebViewExtension().setScrollBarFadingEnabled(false);
        }
        settings.setDisplayZoomControls(false);
        settings.setBuiltInZoomControls(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheMaxSize(MAX_CACHE_SIZE);
        //设置缩放密度
        settings.setUseWideViewPort(true);
        //设置WebView是否支持多窗口,
        settings.setSupportMultipleWindows(false);
        settings.setAppCachePath(getExternalCacheDir(context));
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // 若由网络，则实时网上获取，如无网络，显示本地缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //启用数据库
        settings.setDatabaseEnabled(true);
        String dir = context.getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        settings.setGeolocationEnabled(true);
        //设置定位的数据库路径
        settings.setGeolocationDatabasePath(dir);
        mSourceUserAgent = settings.getUserAgentString();
        setUserAgent();
    }

    /**
     * 处理重定向 begin
     */
    @Override
    public final void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
        resetAllStateInternal(url);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        resetAllStateInternal(url);
    }

    @Override
    public final void postUrl(String url, byte[] postData) {
        super.postUrl(url, postData);
        resetAllStateInternal(url);
    }

    @Override
    public final void loadData(String data, String mimeType, String encoding) {
        super.loadData(data, mimeType, encoding);
        resetAllStateInternal(getUrl());
    }

    @Override
    public final void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String
            encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
        resetAllStateInternal(getUrl());
    }

    @Override
    public void reload() {
        super.reload();
        resetAllStateInternal(getUrl());
    }

    private void resetAllStateInternal(String url) {
        if (!TextUtils.isEmpty(url) && url.startsWith("javascript:")) {
            return;
        }
        resetAllState();
    }

    /**
     * 加载url时重置touch状态
     */
    protected void resetAllState() {
        mTouchByUser = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //用户按下到下一个链接加载之前，置为true
                mTouchByUser = true;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置userAgent
     */
    public void setUserAgent() {
        String root = ApiHelper.getRoot();
        String simCount = ApiHelper.getSimCount();
        String devMode = ApiHelper.getDevMode();
        String ua = " root/" + root + " sim_count/" + simCount + " dev_mode/" + devMode;
        getSettings().setUserAgentString(mSourceUserAgent + equipUserAgent() + ua);
    }

    private String equipUserAgent() {
        StringBuilder sb = new StringBuilder();
        sb.append(" app/wlvideo");
        sb.append(" v_name/").append(ApiHelper.getVerName());
        sb.append(" channel/").append(ApiHelper.getChannel());
        if (NetworkUtil.isWifiConnected(getContext())) {
            sb.append(" net/wifi");
        }
        String openId = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_OPEN_ID, StringUtil.EMPTY_STR);
        if (!StringUtil.isNull(openId)) {
            sb.append(" open_id/").append(openId);
        }
        String uid = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_UID, StringUtil.EMPTY_STR);
        if (!StringUtil.isNull(uid)) {
            sb.append(" uid/").append(uid);
        }
        sb.append(" device_id/");
        sb.append(ApiHelper.getDeviceId());
        sb.append(" app_key/");
        if (!TextUtils.isEmpty(ApiHelper.getAppKey())) {
            sb.append(ApiHelper.getAppKey());
        }
        return sb.toString();
    }

    /**
     * 释放webView.
     */
    public void releaseWebView() {
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
        stopLoading();
        getSettings().setJavaScriptEnabled(false);
        clearHistory();
        clearView();
        removeAllViews();
        try {
            destroy();
        } catch (Throwable ex) {
            Logger.e("Destroy web view error [" + ex.getMessage() + "]");
        }
        setWebViewClient(null);
        setWebChromeClient(null);
        setDownloadListener(null);
    }


    private String getExternalCacheDir(Context context) {
        File dataDir = new File(StorageUtils.getCacheDirectory(context).getAbsolutePath(), "web");
        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                return context.getCacheDir().getAbsolutePath();
            }
        }
        return dataDir.getAbsolutePath();
    }
}

