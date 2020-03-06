package cn.weli.svideo.module.main.presenter;

import java.net.URLDecoder;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.ProtocolConstants;
import cn.weli.svideo.common.constant.ThirdSdkConstants;
import cn.weli.svideo.module.main.model.bean.ShareInfoBean;
import cn.weli.svideo.module.main.view.IWebView;

/**
 * WebView P层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see [class/method]
 * @since [1.0.0]
 */
public class WebPresenter implements IPresenter {

    private static final String EMPTY_PAGE = "about:blank";

    private IWebView mView;

    private String mOriginalUrl;

    private ShareInfoBean mShareInfoBean;

    public WebPresenter(IWebView view) {
        mView = view;
    }

    /**
     * 初始化web页面元素
     *
     * @param title        标题
     * @param url          地址
     * @param defaultTitle 默认标题
     */
    public void initWebInfo(String title, String url, String defaultTitle) {
        if (StringUtil.isNull(url)) {
            mView.finishActivity();
            return;
        }
        if (StringUtil.isNull(title)) {
            title = defaultTitle;
        }
        mOriginalUrl = url;
        mView.initWebViewInfo(title, mOriginalUrl);
    }

    /**
     * 处理返回事件
     *
     * @param canGoBack web是否能返回
     * @param url       当前web页面地址
     */
    public void handleBackEvent(boolean canGoBack, String url) {
        if (canGoBack && !url.equals(EMPTY_PAGE) && !url.equals(mOriginalUrl)) {
            mView.webGoBack();
        } else {
            mView.superBackPressed();
        }
    }

    /**
     * 当传递过来的title值为空，并且根据接受的title不为空，则设置toolbar的标题
     *
     * @param title 标题
     */
    public void handleReceivedTitle(String title) {
        if (!StringUtil.isNull(title) && !title.contains("http")) {
            mView.setWebTitle(title);
        }
    }

    /**
     * 处理url跳转
     *
     * @param urlSrc url
     */
    public void handleLoadingUrl(String urlSrc) {
        if (StringUtil.isNull(urlSrc)) {
            return;
        }
        Logger.d("Web view load url is [" + urlSrc + "]");
        if (!isLoadingUrlProtocol(urlSrc)) {
            mView.loadUrl(urlSrc);
        }
    }

    /**
     * 处理跳转url
     */
    private boolean isLoadingUrlProtocol(String url) {
        boolean result = true;
        if (url.startsWith(ThirdSdkConstants.Scheme.WEIXIN)
                || url.startsWith(ThirdSdkConstants.Scheme.ALI)
                || url.startsWith(ThirdSdkConstants.Scheme.TABBAO)) {
            mView.startThirdUrl(url);
            result = true;
        }
        if (url.startsWith(ProtocolConstants.HTTP_SCHEME_HEADER)) {
            // Http开头的网页
            result = false;
        } else if (url.startsWith(ProtocolConstants.APP_SCHEME_HEADER)) {
            url = URLDecoder.decode(url);
            mView.startProtocol(url);
        }
        return result;
    }

    /**
     * 保存分享信息
     *
     * @param bean 分享信息
     */
    public void setCurrentShareInfo(ShareInfoBean bean) {
        mShareInfoBean = bean;
    }

    /**
     * 设置分享平台
     *
     * @param platform 分享平台
     */
    public void setSharePlatform(String platform) {
        if (mShareInfoBean != null) {
            mShareInfoBean.platform = platform;
            if (mShareInfoBean.shareType == ShareInfoBean.TYPE_PASTE) {
                // 粘贴类型
                mView.startPasteToShare(mShareInfoBean.platform, mShareInfoBean.title);
            }
        }
    }

    @Override
    public void clear() {
    }
}
