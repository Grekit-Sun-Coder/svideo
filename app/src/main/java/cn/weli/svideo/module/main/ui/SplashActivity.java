package cn.weli.svideo.module.main.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.yanzhenjie.permission.Action;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.helper.PermissionRequestHelper;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.ThreadPoolUtil;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ClientConfigHelper;
import cn.weli.svideo.common.helper.DeviceHelper;
import cn.weli.svideo.common.helper.LocationHelper;
import cn.weli.svideo.common.helper.MiitDeviceHelper;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.main.component.widget.UserPrivacyDialog;
import cn.weli.svideo.module.main.model.AdvertModel;
import cn.weli.svideo.module.main.presenter.SplashPresenter;
import cn.weli.svideo.module.main.view.IMainView;
import cn.weli.svideo.module.main.view.ISplashView;
import cn.weli.svideo.module.mine.model.UserModel;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.video.view.IVideoPlayView;

/**
 * 启动开屏页V层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see SplashActivity
 * @since [1.0.0]
 */
public class SplashActivity extends AppBaseActivity<SplashPresenter, ISplashView> implements
        ISplashView {

    @BindView(R.id.splash_container_layout)
    FrameLayout mSplashContainerLayout;
    @BindView(R.id.splash_logo_layout)
    RelativeLayout mSplashLogoLayout;
    /**
     * 视频id
     */
    private String mVideoId;

    /**
     * 跳转主页的Runnable
     */
    private Runnable mStartMainRunnable = this::startToMain;

    private boolean canJump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setNeedSetOrientation(false);
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        WlVideoAppInfo.getInstance().setAppRunning(true);
        mPresenter.checkAppUserPrivacy();
    }

    @Override
    public void showUserPrivacyDialog() {
        UserPrivacyDialog dialog = new UserPrivacyDialog(this);
        dialog.setPrivacyListener(new UserPrivacyDialog.OnUserPrivacyListener() {
            @Override
            public void onAgreeUserPrivacy() {
                mPresenter.saveUserPrivacyAgree();
                startAppLaunchInit();
            }

            @Override
            public void onDisAgreeUserPrivacy() {
                finish();
            }
        });
        dialog.show();
    }

    @Override
    public void startAppLaunchInit() {
        requestPermission();
        initLauncherEvent();
    }

    /**
     * 请求必须权限
     */
    private void requestPermission() {
        PermissionRequestHelper.requestMultiPermission(this, new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        init();
                    }
                }, new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        init();
                    }
                }, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    /**
     * 跳转到主页
     */
    private void init() {
        if (ActivityCompat.checkSelfPermission(WlVideoAppInfo.sAppCtx,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            DeviceHelper.initDeviceInfo(WlVideoAppInfo.sAppCtx);
        }
        LocationHelper.getInstance().startLocate(getApplicationContext(), null);
        MiitDeviceHelper.getInstance(getApplicationContext()).initDevice();
        mPresenter.initUserInfo();
        mPresenter.preLoadVideoList(getProtocolVideoId());
        mPresenter.handleAppStart();
    }

    /**
     * 加载开屏
     */
    @Override
    public void loadSplashAd() {
        SplashAD splashAd = new SplashAD(this, PackageHelper.getMetaData(this,
                BusinessConstants.MetaData.GDT_APP_ID), GDT_SPLASH_ID, new SplashADListener() {
            @Override
            public void onADDismissed() {
                Logger.d("onADDismissed");
                startToMainNow();
            }

            @Override
            public void onNoAD(AdError adError) {
                Logger.d("onNoAD " + adError.getErrorMsg());
                startToMainNow();
            }

            @Override
            public void onADPresent() {
                Logger.d("onADPresent");
                AdvertModel.reportAdEvent("SCREEN", AdConfigBean.ACTION_TYPE_PV);
            }

            @Override
            public void onADClicked() {
                Logger.d("onADClicked");
                AdvertModel.reportAdEvent("SCREEN", AdConfigBean.ACTION_TYPE_CLICK);
            }

            @Override
            public void onADTick(long millisUntilFinished) {
            }

            @Override
            public void onADExposure() {
                Logger.d("onADExposure");
            }
        }, FLAG_MAIN_DELAY);
        splashAd.fetchAndShowIn(mSplashContainerLayout);
    }

    @Override
    public void startToMainDelay() {
        handleEventDelay(mStartMainRunnable, FLAG_MAIN_DELAY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startToMainNow();
    }

    @Override
    public void startToMainNow() {
        if (canJump) {
            removeHandlerCallbacks(mStartMainRunnable);
            startToMain();
        } else {
            canJump = true;
        }
    }

    /**
     * 初始化启动处理的service
     */
    private void initLauncherEvent() {
        ThreadPoolUtil.getInstance().execute(() -> {
            ClientConfigHelper.getInstance().checkRemoteClientConfig();
            queryUserInfo();
        });
    }

    /**
     * 跳转主页
     */
    private void startToMain() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        String protocol = getIntent().getStringExtra(IMainView.EXTRA_PROTOCOL);
        if (!StringUtil.isNull(protocol)) {
            intent.putExtra(IMainView.EXTRA_PROTOCOL, protocol);
        }
        if (!StringUtil.isNull(mVideoId)) {
            intent.putExtra(IVideoPlayView.EXTRA_VIDEO_ID, mVideoId);
        }
        intent.setData(getIntent().getData());
        startActivity(intent);
        finishActivity();
        overridePendingTransition(R.anim.alpha_show, R.anim.alpha_gone);
    }

    /**
     * 获取视频id
     *
     * @return 视频id
     */
    private String getProtocolVideoId() {
        if (getIntent() != null) {
            try {
                String protocol = getIntent().getStringExtra(IMainView.EXTRA_PROTOCOL);
                if (StringUtil.isNull(protocol) && getIntent().getData() != null) {
                    protocol = getIntent().getData().toString();
                }
                if (!StringUtil.isNull(protocol) && protocol.contains(StringUtil.QUESTION_STR)
                        && protocol.contains(StringUtil.EQUALS_STR)) {
                    Uri uri = Uri.parse(protocol);
                    mVideoId = uri.getQueryParameter(IVideoPlayView.EXTRA_VIDEO_ID);
                }
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
        return mVideoId;
    }

    /**
     * 查询用户信息
     */
    private void queryUserInfo() {
        if (StringUtil.isNull(
                SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_ACCESS_TOKEN,
                        StringUtil.EMPTY_STR))) {
            return;
        }
        UserModel model = new UserModel();
        model.getUserInfoData(new SimpleHttpSubscriber<UserInfoBean>() {
            @Override
            public void onResponseSuccess(UserInfoBean bean) {
                model.updateLocalUserInfo(bean);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected Class<SplashPresenter> getPresenterClass() {
        return SplashPresenter.class;
    }

    @Override
    protected Class<ISplashView> getViewClass() {
        return ISplashView.class;
    }
}
