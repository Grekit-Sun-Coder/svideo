package cn.weli.svideo.module.main.presenter;

import cn.etouch.logger.Logger;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.helper.FragmentFactory;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.main.component.widget.MainTabLayout;
import cn.weli.svideo.module.main.model.MainModel;
import cn.weli.svideo.module.main.model.bean.VersionBean;
import cn.weli.svideo.module.main.view.IMainView;

/**
 * 主页P层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see cn.weli.svideo.module.main.ui.MainActivity
 * @since [1.0.0]
 */
public class MainPresenter implements IPresenter {

    private static final int FLAG_QUIT_DURATION = 1000;

    private IMainView mView;

    private MainModel mModel;

    private long mLastClickMillis = 0;

    private String mCurrentFragment = FragmentFactory.FLAG_FRAGMENT_MINE;

    public MainPresenter(IMainView view) {
        mView = view;
        mModel = new MainModel();
    }

    /**
     * 处理两次点击关闭应用
     */
    public void handleOnBackPressed(long clickMillis) {
        if (mLastClickMillis != 0 && clickMillis - mLastClickMillis < FLAG_QUIT_DURATION) {
            mView.finishActivity();
            WlVideoAppInfo.getInstance().setAppRunning(false);
        } else {
            mLastClickMillis = clickMillis;
            mView.showQuitAppToast();
        }
    }

    /**
     * 处理tab选中
     *
     * @param tab 选中tab
     */
    public void handleTabSelected(int tab) {
        switch (tab) {
            case MainTabLayout.TAB_VIDEO:
                if (!FragmentFactory.FLAG_FRAGMENT_VIDEO.equals(mCurrentFragment)) {
                    changeCurrentFragment(FragmentFactory.FLAG_FRAGMENT_VIDEO);
                }
                break;
            case MainTabLayout.TAB_MINE:
                if (!FragmentFactory.FLAG_FRAGMENT_MINE.equals(mCurrentFragment)) {
                    changeCurrentFragment(FragmentFactory.FLAG_FRAGMENT_MINE);
                }
                break;
            case MainTabLayout.TAB_TASK:
                if (!FragmentFactory.FLAG_FRAGMENT_TASK.equals(mCurrentFragment)) {
                    changeCurrentFragment(FragmentFactory.FLAG_FRAGMENT_TASK);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 检查应用Fragment状态，以防错乱.
     *
     * @param isInstanceNull saveInstance是否为空
     */
    public void checkFragment(boolean isInstanceNull) {
        if (!isInstanceNull) {
            mView.hideAllFragment();
        }
    }

    /**
     * 切换Fragment
     *
     * @param fragment 页面标识
     */
    private void changeCurrentFragment(String fragment) {
        mView.switchContent(mCurrentFragment, fragment);
        mCurrentFragment = fragment;
    }

    /**
     * 处理退出登录，如果在我的页面，则切换到视频页面
     */
    public void handleLogout() {
        if (StringUtil.equals(mCurrentFragment, FragmentFactory.FLAG_FRAGMENT_MINE)) {
            mView.changeTabToVideoPage();
        }
    }

    /**
     * 开始检查版本更新
     *
     * @param currentVersion 当前版本
     */
    public void startVersionRequestTask(int currentVersion) {
        mModel.checkAppVersion(new SimpleHttpSubscriber<VersionBean>() {
            @Override
            public void onResponseSuccess(VersionBean versionBean) {
                if (versionBean == null) {
                    return;
                }
                Logger.d("Current version code is [" + currentVersion + "] remote version code is [" + versionBean.version_code + "]");
                if (versionBean.version_code > currentVersion && versionBean.needUpdate()) {
                    handleVersionUpdate(versionBean);
                }
            }
        });
    }

    /**
     * 处理版本更新
     *
     * @param bean 更新数据
     */
    private void handleVersionUpdate(VersionBean bean) {
        if (bean == null || StringUtil.isNull(bean.version_link) || StringUtil.isNull(bean.version_desc)
                || StringUtil.isNull(bean.version_name) || !bean.needUpdate()) {
            return;
        }
        mView.showVersionUpdateDialog(bean.version_desc, bean.forceUpdate(), bean.version_link,
                bean.version_name);
    }

    /**
     * 处理通知权限
     *
     * @param notificationEnable 权限是否开启
     */
    public void handleNotificationPermission(boolean notificationEnable) {
        if (notificationEnable) {
            Logger.d("Notification permission is enable");
            return;
        }
        int times = mModel.getAppOpenTimes();
        if (times == 0) {
            // 首次打开
            mModel.setHasShowNotifyOPermission(1);
        } else {
            // 只显示一次
            if (!mModel.hasShowNotifyPermission()) {
                mView.showNotifyPermissionTip();
                mModel.setHasShowNotifyOPermission(true);
            }
        }
    }

    @Override
    public void clear() {
        mModel.cancelGetAppVersion();
        mModel.cancelGetAppConfig();
    }
}
