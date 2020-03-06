package cn.weli.svideo.module.main.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;

import com.baidu.mobads.production.BaiduXAdSDKContext;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.igexin.sdk.PushManager;
import com.microquation.linkedme.android.LinkedME;
import com.tencent.smtt.sdk.QbSdk;

import java.net.URLDecoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.component.helper.DialogHelper;
import cn.weli.svideo.baselib.component.widget.dialog.CommonDialog;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.utils.ScreenUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.ThreadPoolUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.helper.FragmentFactory;
import cn.weli.svideo.common.helper.picture.PictureHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.main.component.event.NotificationPerChangeEvent;
import cn.weli.svideo.module.main.component.helper.VersionUpdateHelper;
import cn.weli.svideo.module.main.component.widget.MainTabLayout;
import cn.weli.svideo.module.main.component.widget.UpdateDialog;
import cn.weli.svideo.module.main.model.AdvertModel;
import cn.weli.svideo.module.main.presenter.MainPresenter;
import cn.weli.svideo.module.main.view.IMainView;
import cn.weli.svideo.module.mine.component.event.LoginSuccessEvent;
import cn.weli.svideo.module.mine.component.event.LogoutEvent;
import cn.weli.svideo.module.task.component.event.TaskCoinStatusEvent;
import cn.weli.svideo.module.task.component.helper.RewardVideoHelper;
import cn.weli.svideo.module.task.component.helper.TaskCoinOpenHelper;
import cn.weli.svideo.module.video.view.IVideoPlayView;
import cn.weli.svideo.push.NotificationUtils;
import cn.weli.svideo.push.WlPushManager;

/**
 * 主页V层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see MainActivity
 * @since [1.0.0]
 */
public class MainActivity extends AppBaseActivity<MainPresenter, IMainView> implements IMainView, MainTabLayout.OnMainTabChangeListener {

    @BindView(R.id.main_tab_layout)
    MainTabLayout mMainTabLayout;

    private VersionUpdateHelper mUpdateHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentLayoutID(R.id.main_content_layout);
        ButterKnife.bind(this);
        RxBus.get().register(this);
        initView(savedInstanceState);
        handleProtocol(null);
        handleOtherAction();
        checkNotificationPermission();
        initX5Browse();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinkedME.getInstance().setImmediate(true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleSelectTab(intent);
        handleProtocol(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
        PictureHelper.clearPicCache(this);
        AdvertModel.clear();
        RewardVideoHelper.getInstance().removeAllListener();
        BaiduXAdSDKContext.exit();
        if (mUpdateHelper != null) {
            mUpdateHelper.onDownloadCancel();
        }
    }

    @Override
    public void onBackPressed() {
        mPresenter.handleOnBackPressed(SystemClock.uptimeMillis());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_NOTIFICATION == requestCode) {
            RxBus.get().post(new NotificationPerChangeEvent(NotificationUtils.isNotificationEnable(this)));
        }
    }

    @Subscribe
    public void onLoginSuccess(LoginSuccessEvent event) {
        TaskCoinOpenHelper.getInstance().queryTaskCoinOpenStatus();
    }

    @Subscribe
    public void onLoginout(LogoutEvent event) {
        mPresenter.handleLogout();
        mMainTabLayout.hideOpenCoin();
    }

    @Subscribe
    public void onTaskOpenCoinEvent(TaskCoinStatusEvent event) {
        mMainTabLayout.setOpenCoinStatus(event.status);
    }

    @Override
    public void switchContent(String from, String to) {
        switchFragment(from, to);
    }

    @Override
    public void changeTabToVideoPage() {
        mMainTabLayout.setCurrentTab(MainTabLayout.TAB_VIDEO);
    }

    @Override
    public void showVersionUpdateDialog(String content, boolean forceUpdate, String link,
            String version_name) {
        UpdateDialog dialog = new UpdateDialog(this, version_name, content);
        dialog.setOnClickCallback(new UpdateDialog.OnClickCallback() {
            @Override
            public void onClickUpdate() {
                startVersionUpdate(link);
                showToast(getString(R.string.main_update_remind));
            }

            @Override
            public void onClickClose() {
                if (forceUpdate) {
                    finishActivity();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void showNotifyPermissionTip() {
        CommonDialog dialog = DialogHelper.commonDialog(this)
                .title(R.string.common_dialog_title)
                .content(R.string.main_no_permission_str)
                .positiveText(R.string.withdraw_set_info_title)
                .negativeText(R.string.common_str_cancel)
                .callback(new CommonDialog.ButtonCallback() {
                    @Override
                    public void onPositive(Dialog dialog) {
                        super.onPositive(dialog);
                        NotificationUtils.setNotification(MainActivity.this, REQUEST_CODE_NOTIFICATION);
                        StatisticsAgent.click(MainActivity.this, StatisticsUtils.CID.CID_201, StatisticsUtils.MD.MD_2);
                    }

                    @Override
                    public void onNegative(Dialog dialog) {
                        super.onNegative(dialog);
                    }
                }).build();
        dialog.show();
        StatisticsAgent.view(this, StatisticsUtils.CID.CID_2, StatisticsUtils.MD.MD_2);
    }

    @Override
    public void hideAllFragment() {
        hideFragment(FragmentFactory.FLAG_FRAGMENT_VIDEO);
        hideFragment(FragmentFactory.FLAG_FRAGMENT_MINE);
    }

    @Override
    public void showQuitAppToast() {
        showToast(R.string.main_quit_str);
    }

    @Override
    public void onMainTabChanged(int tab) {
        mPresenter.handleTabSelected(tab);
    }

    /**
     * 初始化
     */
    private void initView(Bundle savedInstanceState) {
        ScreenUtil.setTranslucentStatus(this);
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        // 设置首页视频类型
        getIntent().putExtra(IVideoPlayView.EXTRA_VIDEO_TYPE, IVideoPlayView.TYPE_VIDEO_HOME);
        mMainTabLayout.setChangeListener(this);
        mPresenter.checkFragment(savedInstanceState == null);
        PackageHelper packageHelper = new PackageHelper(this);
        mPresenter.startVersionRequestTask(packageHelper.getLocalVersionCode());
        handleSelectTab(null);
    }

    /**
     * 处理私有协议
     *
     * @param intent 协议
     */
    private void handleProtocol(Intent intent) {
        String protocol;
        boolean fromPush;
        if (intent == null) {
            intent = getIntent();
        }
        protocol = intent.getStringExtra(IMainView.EXTRA_PROTOCOL);
        fromPush = intent.getBooleanExtra(IMainView.EXTRA_FROM_PUSH, false);
        if (StringUtil.isNull(protocol) && intent.getData() != null) {
            protocol = intent.getData().toString();
            protocol = URLDecoder.decode(protocol);
        }
        Logger.d("Main receiver protocol is [" + protocol + "]，from push is [" + fromPush + "]");
        if (!StringUtil.isNull(protocol)) {
            startProtocol(protocol);
        }
        // 若是通知消息来的，则统计上报
        if (fromPush) {
            intent.putExtra(IMainView.EXTRA_FROM_PUSH, false);
            String taskId = intent.getStringExtra(IMainView.EXTRA_PUSH_TASK_ID);
            String msgId = intent.getStringExtra(IMainView.EXTRA_PUSH_MSG_ID);
            if (!StringUtil.isNull(taskId) && !StringUtil.isNull(msgId)) {
                PushManager.getInstance().sendFeedbackMessage(WlVideoAppInfo.sAppCtx,
                        taskId, msgId, WlPushManager.PUSH_ACTION_ID_CLICK);
                StatisticsAgent.push(this, StatisticsUtils.EventName.PUSH_MSG_CLICK,
                        StatisticsUtils.CID.CID_1, StatisticsUtils.MD.MD_9);
            }
        }
    }

    /**
     * 处理其他事件
     */
    private void handleOtherAction() {
        // 开宝箱状态
        TaskCoinOpenHelper.getInstance().queryTaskCoinOpenStatus();
    }

    /**
     * 处理tab选择
     */
    private void handleSelectTab(Intent intent) {
        String flag;
        int tabPosition;
        if (intent == null) {
            intent = getIntent();
        }
        setIntent(intent);
        flag = intent.getStringExtra(IMainView.EXTRA_TAB_FLAG_VALUE);
        tabPosition = getCurrentTab(flag);
        mMainTabLayout.setCurrentTab(tabPosition);
    }

    /**
     * 获取当前tab位置
     *
     * @param tabName 名称
     * @return tab位置
     */
    private int getCurrentTab(String tabName) {
        if (StringUtil.equals(tabName, TAB_VIDEO)) {
            return MainTabLayout.TAB_VIDEO;
        } else if (StringUtil.equals(tabName, TAB_MINE)) {
            return MainTabLayout.TAB_MINE;
        } else if (StringUtil.equals(tabName, TAB_TASK)) {
            return MainTabLayout.TAB_TASK;
        } else {
            return MainTabLayout.TAB_VIDEO;
        }
    }

    /**
     * 开始下载更新包
     */
    private void startVersionUpdate(String url) {
        if (mUpdateHelper == null) {
            mUpdateHelper = new VersionUpdateHelper(this);
        }
        mUpdateHelper.onDownloadCancel();
        mUpdateHelper.setDownloadUrl(url);
        mUpdateHelper.startDownload();
    }

    /**
     * 检查通知权限
     */
    private void checkNotificationPermission() {
        mPresenter.handleNotificationPermission(NotificationUtils.isNotificationEnable(this));
    }

    /**
     * 初始化腾讯X5内核
     */
    private void initX5Browse() {
        ThreadPoolUtil.getInstance().execute(() -> {
            //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
            QbSdk.PreInitCallback callback = new QbSdk.PreInitCallback() {
                @Override
                public void onViewInitFinished(boolean arg) {
                    //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                    Logger.d("X5 onViewInitFinished is = [" + arg + "]");
                }

                @Override
                public void onCoreInitFinished() {
                }
            };
            //x5内核初始化接口
            QbSdk.initX5Environment(getApplicationContext(), callback);
        });
    }

    @Override
    protected Class<MainPresenter> getPresenterClass() {
        return MainPresenter.class;
    }

    @Override
    protected Class<IMainView> getViewClass() {
        return IMainView.class;
    }
}
