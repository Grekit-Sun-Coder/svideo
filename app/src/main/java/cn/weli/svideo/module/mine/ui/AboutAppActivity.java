package cn.weli.svideo.module.mine.ui;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.helper.DialogHelper;
import cn.weli.svideo.baselib.component.widget.dialog.CommonToastDialog;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.mine.presenter.AboutAppPresenter;
import cn.weli.svideo.module.mine.view.IAboutAppView;

/**
 * 关于我们
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-23
 * @see SettingActivity
 * @since [1.0.0]
 */
public class AboutAppActivity extends AppBaseActivity<AboutAppPresenter, IAboutAppView> implements IAboutAppView {

    @BindView(R.id.app_version)
    TextView mAppVersion;
    /**
     * 彩蛋点击次数
     */
    private int mColorAgeTimes;

    private long mSavedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick(R.id.app_logo_img)
    public void onAppLogoClick() {
        long time = System.currentTimeMillis();
        if (time - mSavedTime > FLAG_TIME_LIMIT) {
            mSavedTime = time;
            mColorAgeTimes = 0;
        } else {
            mColorAgeTimes++;
        }
        if (mColorAgeTimes == FLAG_TIMES_COUNT) {
            showAppInfoDialog();
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(R.string.about_title);
        mAppVersion.setText(getString(R.string.about_version_title, ApiHelper.getVerName()));
    }

    /**
     * 展示app信息
     */
    private void showAppInfoDialog() {
        try {
            String builder = "pkg：" + getPackageName() + "\nchannel：" +
                    ApiHelper.getChannel() + "\nversionName：" +
                    ApiHelper.getVerName() + "\nversionCode:" +
                    ApiHelper.getVerCode() + "\nOS_version:" +
                    ApiHelper.getOsVer() + "\nmodel:" +
                    ApiHelper.getModel() + "\nimei:" +
                    ApiHelper.getIMEI() + "\nclient_id:" +
                    ApiHelper.getCid() + "\ndevice_id:" +
                    ApiHelper.getDeviceId() + "\nuid:" +
                    SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_USER_UID, StringUtil.EMPTY_STR);
            CommonToastDialog dialog = DialogHelper.commonToastDialog(this)
                    .okStr(R.string.common_str_ok)
                    .content(builder)
                    .build();
            dialog.showDialog(this);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    protected Class<AboutAppPresenter> getPresenterClass() {
        return AboutAppPresenter.class;
    }

    @Override
    protected Class<IAboutAppView> getViewClass() {
        return IAboutAppView.class;
    }
}
