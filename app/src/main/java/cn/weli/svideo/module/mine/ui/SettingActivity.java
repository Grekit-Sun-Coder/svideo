package cn.weli.svideo.module.mine.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.hwangjr.rxbus.RxBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.helper.DialogHelper;
import cn.weli.svideo.baselib.component.widget.dialog.CommonDialog;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.main.ui.WebViewActivity;
import cn.weli.svideo.module.main.view.IWebView;
import cn.weli.svideo.module.mine.component.event.LogoutEvent;
import cn.weli.svideo.module.mine.presenter.SettingPresenter;
import cn.weli.svideo.module.mine.view.ISettingView;

/**
 * 设置页码
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-18
 * @see MinePageFragment
 * @since [1.0.0]
 */
public class SettingActivity extends AppBaseActivity<SettingPresenter, ISettingView> implements ISettingView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void handleLogoutSuccess() {
        RxBus.get().post(new LogoutEvent());
        showToast(R.string.setting_logout_success_tip);
        finishActivity();
    }

    @OnClick({R.id.setting_argue_layout, R.id.setting_policy_layout, R.id.setting_about_layout,
            R.id.setting_account_layout, R.id.setting_logout_layout, R.id.setting_permission_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_argue_layout:
                Intent agreeIntent = new Intent(this, WebViewActivity.class);
                agreeIntent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, HttpConstant.H5_URL_USER_AGREEMENT);
                startActivity(agreeIntent);
                break;
            case R.id.setting_policy_layout:
                Intent policyIntent = new Intent(this, WebViewActivity.class);
                policyIntent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, HttpConstant.H5_URL_PRIVACY_POLICY);
                startActivity(policyIntent);
                break;
            case R.id.setting_about_layout:
                startActivity(new Intent(this, AboutAppActivity.class));
                break;
            case R.id.setting_permission_layout:
                startActivity(new Intent(this, PermissionSettingActivity.class));
                break;
            case R.id.setting_account_layout:
                Intent accountIntent = new Intent(this, WebViewActivity.class);
                accountIntent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, HttpConstant.H5_URL_CANCEL_ACCOUNT);
                startActivity(accountIntent);
                break;
            case R.id.setting_logout_layout:
                showLogoutDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 退出登陆提示弹窗
     */
    private void showLogoutDialog() {
        CommonDialog dialog = DialogHelper.commonDialog(this).content(R.string.setting_logout_tip)
                .callback(new CommonDialog.ButtonCallback() {
                    @Override
                    public void onPositive(Dialog dialog) {
                        super.onPositive(dialog);
                        mPresenter.handleLogout();
                    }
                }).build();
        dialog.show();
    }

    /**
     * 初始化
     */
    private void initView() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(R.string.setting_title);
    }

    @Override
    protected Class<SettingPresenter> getPresenterClass() {
        return SettingPresenter.class;
    }

    @Override
    protected Class<ISettingView> getViewClass() {
        return ISettingView.class;
    }
}
