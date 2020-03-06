package cn.weli.svideo.module.main.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.yanzhenjie.permission.Action;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.jsbridge.BridgeWebView;
import cn.weli.svideo.baselib.component.jsbridge.BridgeWebViewClient;
import cn.weli.svideo.baselib.component.jsbridge.DefaultHandler;
import cn.weli.svideo.baselib.component.widget.WeWebProgressBar;
import cn.weli.svideo.baselib.helper.PermissionRequestHelper;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.helper.SharePasteHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.common.widget.webview.WeWebView;
import cn.weli.svideo.module.main.component.helper.WebRegisterHelper;
import cn.weli.svideo.module.main.component.widget.ShareSelectedDialog;
import cn.weli.svideo.module.main.model.bean.ShareInfoBean;
import cn.weli.svideo.module.main.model.bean.WebMenuBean;
import cn.weli.svideo.module.main.presenter.WebPresenter;
import cn.weli.svideo.module.main.view.IWebView;
import cn.weli.svideo.module.task.component.event.RefreshCoinEvent;
import cn.weli.svideo.module.task.component.event.TaskRefreshEvent;
import cn.weli.svideo.module.task.component.helper.DrawAdRewardHelper;
import cn.weli.svideo.module.task.component.widget.RewardCoinDialog;
import cn.weli.svideo.module.task.model.bean.RewardBean;

/**
 * 通用WebView页面
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see WebViewActivity
 * @since [1.0.0]
 */
public class WebViewActivity extends AppBaseActivity<WebPresenter, IWebView> implements IWebView,
        WebRegisterHelper.WebActionListener, ShareSelectedDialog.OnSharePlatformSelectListener {

    @BindView(R.id.web_title_txt)
    TextView mWebTitleTxt;
    @BindView(R.id.web_view_layout)
    FrameLayout mWebViewLayout;
    @BindView(R.id.web_close_img)
    ImageView mWebCloseImg;
    @BindView(R.id.web_menu_txt)
    TextView mWebMenuTxt;

    private WeWebView mWebView;

    private WebRegisterHelper mRegisterHelper;

    private WeWebProgressBar mWebProgressBar;

    private ShareSelectedDialog mShareSelectedDialog;
    private boolean mIsFromAd;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //避免网页中的视频进入屏幕的时候，可能出现闪烁的情况
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        initWebView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        reloadWebView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.releaseWebView();
        }
        if (mRegisterHelper != null) {
            mRegisterHelper.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (mShareSelectedDialog != null && mShareSelectedDialog.isShowing()) {
            mShareSelectedDialog.dismiss();
            return;
        }
        mPresenter.handleBackEvent(mWebView.canGoBack(), mWebView.getUrl());
    }

    @Override
    public void initWebViewInfo(String title, String originalUrl) {
        setWebTitle(title);
        mWebViewLayout.removeAllViews();
        mWebView = new WeWebView(this);
        mRegisterHelper = new WebRegisterHelper(this, mWebView);
        mRegisterHelper.setActionListener(this);
        mWebView.setDefaultHandler(new DefaultHandler());
        mWebView.setWebViewClient(new MyWebViewClient(mWebView));
        mWebView.setWebChromeClient(new MyWebChromeViewClient());
        mWebView.setDownloadListener(new MyDownLoadListener(this));
        mWebViewLayout.addView(mWebView);
        initProgressBar();
        mWebViewLayout.addView(mWebProgressBar);
        mWebView.loadUrl(originalUrl);
        mRegisterHelper.initRegister();
    }

    @Override
    public void webGoBack() {
        mWebView.goBack();
    }

    @Override
    public void superBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void loadUrl(String urlSrc) {
        if (StringUtil.isNull(urlSrc)) {
            return;
        }
        mWebCloseImg.setVisibility(View.VISIBLE);
        mWebView.loadUrl(urlSrc);
    }

    @Override
    public void startThirdUrl(String url) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Logger.w(e.getMessage());
        }
    }

    @Override
    public void startPasteToShare(String platform, String title) {
        Logger.d("share title is [" + title + "]");
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData clipData = ClipData.newPlainText("share", title);
            cm.setPrimaryClip(clipData);
        }
        SharePasteHelper.sharePaste(this, platform, title);
    }

    @Override
    public void setWebTitle(String title) {
        mWebTitleTxt.setText(title);
    }

    @Override
    public void handleShareInfo(ShareInfoBean bean) {
        mPresenter.setCurrentShareInfo(bean);
        if (mShareSelectedDialog == null) {
            mShareSelectedDialog = new ShareSelectedDialog(this);
            mShareSelectedDialog.setPlatformSelectListener(this);
        }
        mShareSelectedDialog.show();
    }

    @Override
    public void onPlatformSelected(String platform) {
        mPresenter.setSharePlatform(platform);
    }

    @Override
    public void handleBindInviteCode(RewardBean bean) {
        RxBus.get().post(new TaskRefreshEvent());
        RewardCoinDialog dialog = new RewardCoinDialog(this);
        dialog.setMoreListener(this::finishActivity);
        dialog.setCoinTitleTxt(getString(R.string.task_bind_title));
        dialog.setCoinTxt(bean.reward);
        dialog.show();
        StatisticsAgent.view(this, StatisticsUtils.CID.CID_5012, StatisticsUtils.MD.MD_2);
    }

    @Override
    public void handleShowMenu(WebMenuBean bean) {
        if (bean != null && !StringUtil.isNull(bean.title) && !StringUtil.isNull(bean.menuScheme)) {
            mWebMenuTxt.setVisibility(View.VISIBLE);
            mWebMenuTxt.setText(bean.title);
            mWebMenuTxt.setTag(bean.menuScheme);
        } else {
            mWebMenuTxt.setVisibility(View.GONE);
        }
    }

    @Override
    public void refreshCoin() {
        RxBus.get().post(new RefreshCoinEvent(-1));
    }

    @OnClick({R.id.web_back_img, R.id.web_close_img, R.id.web_menu_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.web_back_img:
                onBackPressed();
                break;
            case R.id.web_close_img:
                finishActivity();
                break;
            case R.id.web_menu_txt:
                try {
                    startProtocol((String) mWebMenuTxt.getTag());
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化
     */
    private void initWebView() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(EXTRA_WEB_VIEW_TITLE);
            String url = intent.getStringExtra(EXTRA_WEB_VIEW_URL);
            mIsFromAd = intent.getBooleanExtra(EXTRA_WEB_VIEW_FROM_AD, false);
            token = intent.getStringExtra(EXTRA_WEB_VIEW_TOKEN);
            Logger.d("Web page url is [" + url + "]");
            mPresenter.initWebInfo(title, url, getString(R.string.app_name));
        }
    }

    /**
     * 进入页面加载
     */
    private void reloadWebView() {
        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(EXTRA_WEB_VIEW_URL);
            Logger.d("Web page url is [" + url + "]");
            loadUrl(url);
        }
    }

    private class MyWebViewClient extends BridgeWebViewClient {

        public MyWebViewClient(BridgeWebView webView) {
            super(webView);
        }

        @Override
        protected boolean onCustomShouldOverrideUrlLoading(String url) {
            mPresenter.handleLoadingUrl(url);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mWebProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mWebProgressBar.setVisibility(View.GONE);
            if (mIsFromAd && !StringUtil.isNull(token)) {
                DrawAdRewardHelper.getInstance().doTask(token, WebViewActivity.this);
                mIsFromAd = false;
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                                    String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }

    private class MyWebChromeViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (mWebProgressBar == null) {
                return;
            }
            mWebProgressBar.setCurrentProgress(mWebProgressBar.getProgress());
            if (newProgress >= WeWebProgressBar.MAX_PROGRESS && !mWebProgressBar.isAnimStart()) {
                mWebProgressBar.setProgress(newProgress);
                mWebProgressBar.startDismissAnimation(mWebProgressBar.getProgress());
            } else {
                mWebProgressBar.startProgressAnimation(newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mPresenter.handleReceivedTitle(title);
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback callback) {
            requestChoicePermission(origin, callback);
            super.onGeolocationPermissionsShowPrompt(origin, callback);
        }

        @Override
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String
                capture) {
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            return true;
        }
    }

    /**
     * WebView 下载监听
     */
    public class MyDownLoadListener implements DownloadListener {
        private Context context;

        public MyDownLoadListener(Context context) {
            this.context = context;
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                    String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }

    /**
     * WebView 请求位置权限
     */
    private void requestChoicePermission(final String origin, final GeolocationPermissionsCallback callback) {
        PermissionRequestHelper.requestMultiPermission(this, new Action() {
            @Override
            public void onAction(List<String> permissions) {
                Logger.d("request location permission success");
                callback.invoke(origin, true, false);
            }
        }, new Action() {
            @Override
            public void onAction(List<String> permissions) {
                Logger.d("request location permission fail");
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    /**
     * 初始化进度条
     */
    private void initProgressBar() {
        mWebProgressBar = new WeWebProgressBar(this, null,
                android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.common_len_4px));
        mWebProgressBar.setLayoutParams(layoutParams);
        Drawable drawable = getResources().getDrawable(R.drawable.shape_web_progress_bar);
        mWebProgressBar.setProgressDrawable(drawable);
    }

    @Override
    protected Class<WebPresenter> getPresenterClass() {
        return WebPresenter.class;
    }

    @Override
    protected Class<IWebView> getViewClass() {
        return IWebView.class;
    }
}
