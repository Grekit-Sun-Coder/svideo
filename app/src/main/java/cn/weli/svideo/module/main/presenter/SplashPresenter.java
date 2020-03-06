package cn.weli.svideo.module.main.presenter;

import java.util.List;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.module.main.model.MainModel;
import cn.weli.svideo.module.main.view.ISplashView;
import cn.weli.svideo.module.video.model.VideoModel;
import cn.weli.svideo.module.video.model.bean.VideoBean;

/**
 * 启动开屏页P层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see cn.weli.svideo.module.main.ui.SplashActivity
 * @since [1.0.0]
 */
public class SplashPresenter implements IPresenter {

    private ISplashView mView;

    private VideoModel mVideoModel;

    private MainModel mMainModel;

    private boolean isFirstStart;

    public SplashPresenter(ISplashView view) {
        mView = view;
        mVideoModel = new VideoModel();
        mMainModel = new MainModel();
    }

    /**
     * 检查用户个人信息指引保护
     */
    public void checkAppUserPrivacy() {
        String channel = mMainModel.getAppChannel();
        String privacyStr = mMainModel.getUserPrivacyList();
        Logger.d("App channel is [" + channel + "] privacy list is [" + privacyStr + "]");
        if (!StringUtil.isNull(channel) && !StringUtil.isNull(privacyStr) && privacyStr.contains(channel)) {
            if (mMainModel.hasUserPrivacyAgree()) {
                mView.startAppLaunchInit();
            } else {
                mView.showUserPrivacyDialog();
            }
        } else {
            mView.startAppLaunchInit();
        }
    }

    /**
     * 处理app启动
     */
    public void handleAppStart() {
        isFirstStart = mMainModel.isAppFirstStart();
        if (isFirstStart) {
            mView.startToMainDelay();
            mMainModel.setHasAppFirstStart();
        } else {
            mView.loadSplashAd();
        }
    }

    /**
     * 初始化用户信息
     */
    public void initUserInfo() {
        mMainModel.initUserInfo();
    }

    /**
     * 设置用户指引已经同意
     */
    public void saveUserPrivacyAgree() {
        mMainModel.setUserPrivacyAgree();
    }

    /**
     * 预加载首页小视频列表
     */
    public void preLoadVideoList(String videoId) {
        mVideoModel.requestVideoList(!StringUtil.isNull(videoId) ? videoId : StringUtil.EMPTY_STR,
                new HttpSubscriber<List<VideoBean>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onNetworkUnavailable() {
            }

            @Override
            public void onResponseSuccess(List<VideoBean> list) {
                if (list != null && !list.isEmpty()) {
                    WlVideoAppInfo.getInstance().setPreVideoList(list);
                    if (isFirstStart) {
                        mView.startToMainNow();
                    }
                }
            }

            @Override
            public void onResponseError(String s, String s1) {
            }

            @Override
            public void onNetworkError() {
            }

            @Override
            public void onPostExecute() {
            }
        });
    }

    @Override
    public void clear() {
        mVideoModel.cancelVideoListRequest();
    }
}
