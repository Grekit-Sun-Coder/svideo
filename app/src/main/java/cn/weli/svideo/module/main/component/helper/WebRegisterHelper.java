package cn.weli.svideo.module.main.component.helper;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.component.jsbridge.BridgeHandler;
import cn.weli.svideo.baselib.component.jsbridge.CallBackFunction;
import cn.weli.svideo.baselib.utils.GsonUtil;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.common.widget.webview.WeWebView;
import cn.weli.svideo.module.main.model.bean.ShareInfoBean;
import cn.weli.svideo.module.main.model.bean.ToastInfoBean;
import cn.weli.svideo.module.main.model.bean.WebMenuBean;
import cn.weli.svideo.module.task.model.bean.RewardBean;

/**
 * Web注册一些方法
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see cn.weli.svideo.module.main.ui.WebViewActivity
 * @since [1.0.0]
 */
public class WebRegisterHelper {

    private static final String WEB_SHARE = "share";

    private static final String WEB_TOAST = "toast";

    private static final String WEB_SHOW_LOADING = "showLoading";

    private static final String WEB_FINISH_LOADING = "finishLoading";

    private static final String WEB_BIND_CODE = "inviteCodeBind";

    private static final String WEB_SHOW_MENU = "showMenu";

    private static final String WEB_REFRESH_COIN = "refreshCoin";

    private AppBaseActivity mActivity;

    private WeWebView mWebView;

    private WebActionListener mActionListener;

    public interface WebActionListener {

        /**
         * 处理分享
         *
         * @param bean 分享信息
         */
        void handleShareInfo(ShareInfoBean bean);

        /**
         * 绑定邀请码成功
         */
        void handleBindInviteCode(RewardBean bean);

        /**
         * 展示menu按钮
         *
         * @param bean 数据
         */
        void handleShowMenu(WebMenuBean bean);

        /**
         * 刷新任务金币数
         */
        void refreshCoin();
    }

    public WebRegisterHelper(AppBaseActivity activity, WeWebView webView) {
        mActivity = activity;
        mWebView = webView;
    }

    /**
     * 初始化注册js方法
     */
    public void initRegister() {
        if (mActivity == null || mWebView == null) {
            return;
        }
        // 分享
        mWebView.registerHandler(WEB_SHARE, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mActivity == null || mWebView == null) {
                    return;
                }
                try {
                    ShareInfoBean shareInfoBean = (ShareInfoBean) GsonUtil.fromJsonToObject(data,
                            ShareInfoBean.class);
                    if (shareInfoBean != null && mActionListener != null) {
                        mActionListener.handleShareInfo(shareInfoBean);
                    }
                } catch (Exception e) {
                    Logger.w(e.getMessage());
                }
            }
        });
        // 提示
        mWebView.registerHandler(WEB_TOAST, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mActivity == null || mWebView == null) {
                    return;
                }
                Logger.json(data);
                try {
                    ToastInfoBean toastInfoBean = (ToastInfoBean) GsonUtil.fromJsonToObject(data,
                            ToastInfoBean.class);
                    if (toastInfoBean != null) {
                        mActivity.showToast(toastInfoBean.toast);
                    }
                } catch (Exception e) {
                    Logger.w(e.getMessage());
                }
            }
        });
        // finish loading
        mWebView.registerHandler(WEB_FINISH_LOADING, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mActivity == null || mWebView == null) {
                    return;
                }
                try {
                    mActivity.finishLoadView();
                } catch (Exception e) {
                    Logger.w(e.getMessage());
                }
            }
        });
        // show loading
        mWebView.registerHandler(WEB_SHOW_LOADING, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mActivity == null || mWebView == null) {
                    return;
                }
                try {
                    mActivity.showLoadView();
                } catch (Exception e) {
                    Logger.w(e.getMessage());
                }
            }
        });
        // 任务完成
        mWebView.registerHandler(WEB_BIND_CODE, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mActivity == null || mWebView == null) {
                    return;
                }
                try {
                    RewardBean bean = (RewardBean) GsonUtil.fromJsonToObject(data, RewardBean.class);
                    if (bean != null && mActionListener != null) {
                        mActionListener.handleBindInviteCode(bean);
                    }
                } catch (Exception e) {
                    Logger.w(e.getMessage());
                }
            }
        });
        // 展示menu按钮
        mWebView.registerHandler(WEB_SHOW_MENU, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mActivity == null || mWebView == null) {
                    return;
                }
                try {
                    WebMenuBean bean = (WebMenuBean) GsonUtil.fromJsonToObject(data, WebMenuBean.class);
                    if (bean != null && mActionListener != null) {
                        mActionListener.handleShowMenu(bean);
                    }
                } catch (Exception e) {
                    Logger.w(e.getMessage());
                }
            }
        });
        // 通知更新任务金币数
        mWebView.registerHandler(WEB_REFRESH_COIN, new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                if (mActivity == null || mWebView == null) {
                    return;
                }
                try {
                    if (mActionListener != null) {
                        mActionListener.refreshCoin();
                    }
                } catch (Exception e) {
                    Logger.w(e.getMessage());
                }
            }
        });
    }

    public void setActionListener(WebActionListener actionListener) {
        mActionListener = actionListener;
    }

    /**
     * 释放资源
     */
    public void release() {
        mActivity = null;
        mWebView = null;
    }
}
