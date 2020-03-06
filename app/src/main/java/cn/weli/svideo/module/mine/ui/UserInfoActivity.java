package cn.weli.svideo.module.mine.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.widget.roundimageview.RoundedImageView;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.helper.glide.ILoader;
import cn.weli.svideo.baselib.helper.glide.ImageLoadCallback;
import cn.weli.svideo.baselib.helper.glide.WeImageLoader;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.helper.ImgUploadHelper;
import cn.weli.svideo.common.helper.picture.PictureHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.common.widget.SeparatorPhoneEditView;
import cn.weli.svideo.module.mine.component.widget.UserNicknameDialog;
import cn.weli.svideo.module.mine.component.widget.UserSexDialog;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.mine.presenter.UserInfoPresenter;
import cn.weli.svideo.module.mine.view.IUserInfoView;

/**
 * 个人中心
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-23
 * @see MinePageFragment
 * @since [1.0.0]
 */
public class UserInfoActivity extends AppBaseActivity<UserInfoPresenter, IUserInfoView> implements
        IUserInfoView, UserSexDialog.OnUserSexListener, UserNicknameDialog.OnUserNickChangeListener {

    @BindView(R.id.user_info_head_img)
    RoundedImageView mUserInfoHeadImg;
    @BindView(R.id.user_info_nick_txt)
    TextView mUserInfoNickTxt;
    @BindView(R.id.user_info_sex_txt)
    TextView mUserInfoSexTxt;
    @BindView(R.id.user_info_phone_txt)
    SeparatorPhoneEditView mUserInfoPhoneTxt;

    private UserSexDialog mUserSexDialog;
    private UserNicknameDialog mUserNicknameDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST) {
            List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
            if (list != null && !list.isEmpty()) {
                LocalMedia media = list.get(0);
                if (media != null && media.isCompressed() && !StringUtil.isNull(media.getCompressPath())) {
                    uploadHeadImg(media.getCompressPath());
                }
            }
        }
    }

    @Override
    public void showUserInfoBean(UserInfoBean bean) {
        WeImageLoader.getInstance().load(this, mUserInfoHeadImg, bean.avatar,
                new ILoader.Options(R.drawable.img_touxiang, R.drawable.img_touxiang, ImageView.ScaleType.CENTER_CROP));
        mUserInfoNickTxt.setText(bean.nick_name);
        mUserInfoSexTxt.setText(getUserSex(bean.sex));
        mUserInfoPhoneTxt.setText(bean.phone_no);
    }

    @Override
    public void showUserSexSelectDialog(String sex) {
        if (mUserSexDialog == null) {
            mUserSexDialog = new UserSexDialog(this);
            mUserSexDialog.setSexListener(this);
        }
        mUserSexDialog.setCurrentSex(sex);
        mUserSexDialog.show();
    }

    @Override
    public void onUserSexSelect(String sex) {
        mPresenter.handleUserSexChange(sex);
    }

    @Override
    public void handleUserSexChangeSuccess(String sex) {
        mUserInfoSexTxt.setText(getUserSex(sex));
        if (mUserSexDialog != null && mUserSexDialog.isShowing()) {
            mUserSexDialog.dismiss();
        }
    }

    @Override
    public void handleUserHeadImgUploadSuccess(String url) {
        WeImageLoader.getInstance().load(UserInfoActivity.this, url, ILoader.Options.defaultOptions(),
                new ImageLoadCallback() {
            @Override
            public void onLoadReady(Drawable drawable) {
                if (drawable != null) {
                    mUserInfoHeadImg.setImageDrawable(drawable);
                }
            }
        });
    }

    @Override
    public void showUserNickDialog(String nickname) {
        if (mUserNicknameDialog == null) {
            mUserNicknameDialog = new UserNicknameDialog(this);
            mUserNicknameDialog.setChangeListener(this);
        }
        mUserNicknameDialog.setCurrentNickname(nickname);
        mUserNicknameDialog.show();
    }

    @Override
    public void handleUserNickChangeSuccess(String nickname) {
        mUserInfoNickTxt.setText(nickname);
        if (mUserNicknameDialog != null && mUserNicknameDialog.isShowing()) {
            mUserNicknameDialog.dismiss();
        }
    }

    @Override
    public void onUserNickChanged(String nickname) {
        mPresenter.handleUserNickChange(nickname);
    }

    @OnClick({R.id.user_info_head_layout, R.id.user_info_nick_layout, R.id.user_info_sex_layout, R.id.user_info_phone_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_info_head_layout:
                PictureHelper.startSingleSelect(this);
                StatisticsAgent.click(this, StatisticsUtils.CID.CID_402, StatisticsUtils.MD.MD_5);
                break;
            case R.id.user_info_nick_layout:
                mPresenter.handleUserNickEdit();
                StatisticsAgent.click(this, StatisticsUtils.CID.CID_403, StatisticsUtils.MD.MD_5);
                break;
            case R.id.user_info_sex_layout:
                mPresenter.handleUserSexSelect();
                StatisticsAgent.click(this, StatisticsUtils.CID.CID_404, StatisticsUtils.MD.MD_5);
                break;
            case R.id.user_info_phone_layout:
                break;
            default:
                break;
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(R.string.user_info_title);
        mPresenter.initUserInfo();
    }

    /**
     * 上传头像
     *
     * @param path 头像本地路径
     */
    private void uploadHeadImg(String path) {
        ImgUploadHelper.uploadPic(path, new ImgUploadHelper.OnUploadListener() {
            @Override
            public void onUploadStart() {
                showLoadView(0);
            }

            @Override
            public void onUploadSuccess(String url) {
                mPresenter.handleUploadHeadImg(url);
            }

            @Override
            public void onUploadError(String msg) {
                showToast(msg);
            }

            @Override
            public void onUploadEnd() {
                finishLoadView();
            }
        });
    }

    /**
     * 获取性别
     *
     * @param sex 性别标签
     * @return 性别
     */
    private String getUserSex(String sex) {
        if (StringUtil.equals(sex, UserInfoBean.SEX_MAN)) {
            return getString(R.string.user_info_sex_man);
        } else if (StringUtil.equals(sex, UserInfoBean.SEX_WOMAN)) {
            return getString(R.string.user_info_sex_woman);
        } else {
            return getString(R.string.user_info_sex_none);
        }
    }

    @Override
    public JSONObject getTrackProperties() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(StatisticsUtils.field.content_id, StatisticsUtils.CID.CID_401);
        json.put(StatisticsUtils.field.module, StatisticsUtils.MD.MD_5);
        return json;
    }

    @Override
    protected Class<UserInfoPresenter> getPresenterClass() {
        return UserInfoPresenter.class;
    }

    @Override
    protected Class<IUserInfoView> getViewClass() {
        return IUserInfoView.class;
    }
}
