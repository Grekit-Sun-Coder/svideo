package cn.weli.svideo.module.main.view;

import cn.weli.svideo.baselib.view.IBaseView;

/**
 * WebView V层接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see [class/method]
 * @since [1.0.0]
 */
public interface IWebView extends IBaseView {

    String EXTRA_WEB_VIEW_TITLE = "title";

    String EXTRA_WEB_VIEW_URL = "url";
    String EXTRA_WEB_VIEW_TOKEN = "token";
    String EXTRA_WEB_VIEW_FROM_AD = "from_ad";

    /**
     * 展示web页面信息
     *
     * @param title       标题
     * @param originalUrl 地址
     */
    void initWebViewInfo(String title, String originalUrl);

    /**
     * 设置标题
     *
     * @param title 标题
     */
    void setWebTitle(String title);

    /**
     * web回退
     */
    void webGoBack();

    /**
     * 跟随系统返回事件
     */
    void superBackPressed();

    /**
     * 加载url
     *
     * @param urlSrc 地址
     */
    void loadUrl(String urlSrc);

    /**
     * 跳转微信
     *
     * @param url 地址
     */
    void startThirdUrl(String url);

    /**
     * 去粘贴
     *
     * @param platform 平台
     * @param title    标题
     */
    void startPasteToShare(String platform, String title);
}
