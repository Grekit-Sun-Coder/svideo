package cn.weli.svideo.module.main.view;

import cn.weli.svideo.baselib.view.IBaseView;

/**
 * 启动开屏页V层接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see cn.weli.svideo.module.main.ui.SplashActivity
 * @since [1.0.0]
 */
public interface ISplashView extends IBaseView {

    String GDT_SPLASH_ID = "4090497368906993";

    /**
     * 跳转主页的最大延迟
     */
    int FLAG_MAIN_DELAY = 2000;

    /**
     * 即时跳转主页
     */
    void startToMainNow();

    /**
     * 启动app初始化
     */
    void startAppLaunchInit();

    /**
     * 弹窗用户指引
     */
    void showUserPrivacyDialog();

    /**
     * 加载开屏广告
     */
    void loadSplashAd();

    /**
     * 延迟跳转main
     */
    void startToMainDelay();
}
