package cn.weli.svideo.module.mine.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.helper.PermissionRequestHelper;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.presenter.CommonPresenter;
import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.common.ui.AppBaseActivity;

/**
 * 隐私权限页面
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-01-15
 * @see SettingActivity
 * @since [1.0.0]
 */
public class PermissionSettingActivity extends AppBaseActivity<CommonPresenter, IBaseView> implements IBaseView {

    @BindView(R.id.permission_phone_txt)
    TextView mPermissionPhoneTxt;
    @BindView(R.id.permission_storage_txt)
    TextView mPermissionStorageTxt;
    @BindView(R.id.permission_location_txt)
    TextView mPermissionLocationTxt;
    @BindView(R.id.permission_camera_txt)
    TextView mPermissionCameraTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_setting);
        ButterKnife.bind(this);
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(R.string.setting_permission_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncPermission();
    }

    /**
     * 刷新权限
     */
    private void syncPermission() {
        mPermissionPhoneTxt.setText(PermissionRequestHelper.hasGrantedPermission(this,
                Manifest.permission.READ_PHONE_STATE) ?
                R.string.withdraw_has_set_info_title : R.string.withdraw_set_info_title);
        mPermissionStorageTxt.setText((PermissionRequestHelper.hasGrantedPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                PermissionRequestHelper.hasGrantedPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) ?
                R.string.withdraw_has_set_info_title : R.string.withdraw_set_info_title);
        mPermissionLocationTxt.setText((PermissionRequestHelper.hasGrantedPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) && PermissionRequestHelper.hasGrantedPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) ?
                R.string.withdraw_has_set_info_title : R.string.withdraw_set_info_title);
        mPermissionCameraTxt.setText(PermissionRequestHelper.hasGrantedPermission(this,
                Manifest.permission.CAMERA) ?
                R.string.withdraw_has_set_info_title : R.string.withdraw_set_info_title);
    }

    @OnClick({R.id.permission_phone_layout, R.id.permission_storage_layout,
            R.id.permission_location_layout, R.id.permission_camera_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.permission_phone_layout:
            case R.id.permission_storage_layout:
            case R.id.permission_location_layout:
            case R.id.permission_camera_layout:
            default:
                PermissionRequestHelper.startPermissionSetting(this);
                break;
        }
    }

    @Override
    protected Class<CommonPresenter> getPresenterClass() {
        return CommonPresenter.class;
    }

    @Override
    protected Class<IBaseView> getViewClass() {
        return IBaseView.class;
    }
}
