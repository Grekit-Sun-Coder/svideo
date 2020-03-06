package cn.weli.svideo.module.mine.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.scwang.smartrefresh.layout.api.RefreshHeader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonFragmentAdapter;
import cn.weli.svideo.baselib.component.helper.DialogHelper;
import cn.weli.svideo.baselib.component.widget.dialog.CommonToastDialog;
import cn.weli.svideo.baselib.component.widget.roundimageview.RoundedImageView;
import cn.weli.svideo.baselib.component.widget.smartrefresh.WeRefreshLayout;
import cn.weli.svideo.baselib.helper.glide.ILoader;
import cn.weli.svideo.baselib.helper.glide.WeImageLoader;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.TimeUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.ui.AppBaseFragment;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.mine.component.event.LoginSuccessEvent;
import cn.weli.svideo.module.mine.component.event.LogoutEvent;
import cn.weli.svideo.module.mine.component.event.UserInfoChangeEvent;
import cn.weli.svideo.module.mine.component.widget.OnMultiPurposeListenerAdapter;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.mine.presenter.MinePresenter;
import cn.weli.svideo.module.mine.view.IMineView;

/**
 * 我的页面
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see MainActivity
 * @since [1.0.0]
 */
public class MinePageFragment extends AppBaseFragment<MinePresenter, IMineView> implements IMineView, AppBarLayout.OnOffsetChangedListener {

    @BindView(R.id.mine_refresh_layout)
    WeRefreshLayout mWeRefreshLayout;
    @BindView(R.id.mine_bg_img)
    ImageView mMineBgImg;
    @BindView(R.id.mine_head_img)
    RoundedImageView mMineHeadImg;
    @BindView(R.id.mine_nickname_txt)
    TextView mMineNicknameTxt;
    @BindView(R.id.mine_join_time_txt)
    TextView mMineJoinTimeTxt;
    @BindView(R.id.mine_view_pager)
    ViewPager mViewPager;
    @BindView(R.id.mine_appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.mine_top_layout)
    RelativeLayout mTopLayout;
    @BindView(R.id.mine_ms_point_img)
    View mMsgPointView;
    @BindView(R.id.mine_in_blacklist_txt)
    TextView mInBlacklistTxt;
    private View mFragmentView;
    private MinePraiseVideoFragment mPraiseVideoFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_mine_page, container, false);
            ButterKnife.bind(this, mFragmentView);
            RxBus.get().register(this);
            initView();
        } else {
            if (mFragmentView.getParent() != null) {
                ((ViewGroup) mFragmentView.getParent()).removeView(mFragmentView);
            }
        }
        return mFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            mPresenter.queryMsg();
            if (mPraiseVideoFragment != null) {
                mPraiseVideoFragment.onFragmentShow();
            }
            StatisticsAgent.enter(getActivity(), StatisticsUtils.CID.CID_1, StatisticsUtils.MD.MD_5);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isAdded()) {
            mPresenter.handleUserVisible(hidden);
        }
    }

    @Override
    public void handleFragmentShow() {
        if (isAdded()) {
            mPresenter.checkLoginStatus();
            mPresenter.queryMsg();
            if (mPraiseVideoFragment != null) {
                mPraiseVideoFragment.onFragmentShow();
            }
            StatisticsAgent.enter(getActivity(), StatisticsUtils.CID.CID_1, StatisticsUtils.MD.MD_5);
        }
    }

    @Override
    public void handleFragmentHide() {
    }

    @Subscribe
    public void onLoginSuccess(LoginSuccessEvent event) {
        if (isAdded()) {
            mPresenter.checkLoginStatus();
            if (mPraiseVideoFragment != null) {
                mPraiseVideoFragment.onLoginSuccess();
            }
        }
    }

    @Subscribe
    public void onLoginout(LogoutEvent event) {
        if (isAdded()) {
            mMineNicknameTxt.setText(StringUtil.EMPTY_STR);
            mMineJoinTimeTxt.setText(StringUtil.EMPTY_STR);
            setRedPointShow(false);
            WeImageLoader.getInstance().load(getActivity(), mMineHeadImg, StringUtil.EMPTY_STR);
            if (mPraiseVideoFragment != null) {
                mPraiseVideoFragment.onLogout();
            }
        }
    }

    @Subscribe
    public void onUserInfoChanged(UserInfoChangeEvent event) {
        if (isAdded() && event.bean != null) {
            showUserInfo(event.bean);
        }
    }

    @Override
    public void showUserInfo(UserInfoBean bean) {
        if (isAdded()) {
            mMineNicknameTxt.setText(bean.nick_name);
            mMineJoinTimeTxt.setText(getString(R.string.mine_join_time_title,
                    TimeUtil.milliseconds2String(bean.create_time, TimeUtil.TIME_FORMAT_DAY_POINT)));
            WeImageLoader.getInstance().load(getActivity(), mMineHeadImg, bean.avatar,
                    new ILoader.Options(R.drawable.img_touxiang, R.drawable.img_touxiang, ImageView.ScaleType.CENTER_CROP));
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        setTopBarTranslucent(-verticalOffset, appBarLayout.getTotalScrollRange());
    }

    @Override
    public void setRedPointShow(boolean show) {
        mMsgPointView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setBlacklistTxt(boolean show) {
        if (show) {
            mInBlacklistTxt.setVisibility(View.VISIBLE);
            StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_1,
                    StatisticsUtils.MD.MD_10);
        } else {
            mInBlacklistTxt.setVisibility(View.GONE);
        }
    }

    @Override
    public void setBlacklistTip(String tip, boolean isFromServer) {
        if (!isFromServer || StringUtil.isNull(tip)) {
            tip = getString(R.string.mine_in_blacklist);
        }
        CommonToastDialog dialog = DialogHelper.commonToastDialog(getActivity())
                .content(tip)
                .callback(new CommonToastDialog.OkCallback() {
                    @Override
                    public void onOkClick(Dialog dialog) {
                        super.onOkClick(dialog);
                        StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_2,
                                StatisticsUtils.MD.MD_10);
                        dialog.dismiss();
                    }
                })
                .build();
        dialog.showDialog(getActivity());
        StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_3,
                StatisticsUtils.MD.MD_10);
    }

    @OnClick({R.id.mine_setting_img, R.id.mine_ms_img, R.id.mine_head_img, R.id.mine_nickname_txt,
            R.id.mine_join_time_txt, R.id.mine_in_blacklist_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mine_setting_img:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_3, StatisticsUtils.MD.MD_5);
                break;
            case R.id.mine_ms_img:
                startActivity(new Intent(getActivity(), MsgActivity.class));
                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_2, StatisticsUtils.MD.MD_5);
                break;
            case R.id.mine_head_img:
            case R.id.mine_nickname_txt:
            case R.id.mine_join_time_txt:
                startActivity(new Intent(getActivity(), UserInfoActivity.class));
                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_4, StatisticsUtils.MD.MD_5);
                break;
            case R.id.mine_in_blacklist_txt:
                mPresenter.setTip();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        if (getActivity() == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTopLayout.setPadding(0, DensityUtil.getInstance().getStatusBarHeight(), 0, 0);
        }
        mTopLayout.setAlpha(0);
        mAppBarLayout.addOnOffsetChangedListener(this);
        mWeRefreshLayout.setOnMultiPurposeListener(new MultiPurposeListenerAdapter());
        mWeRefreshLayout.setFooterHeight(0);
        CommonFragmentAdapter adapter = new CommonFragmentAdapter(getActivity().getSupportFragmentManager());
        mPraiseVideoFragment = (MinePraiseVideoFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(makeFragmentTag(mViewPager.getId(), PAGE_PRAISE_VIDEO));
        if (mPraiseVideoFragment == null) {
            mPraiseVideoFragment = new MinePraiseVideoFragment();
        }
        adapter.addFragment(mPraiseVideoFragment);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(PAGE_PRAISE_VIDEO);
        mPresenter.checkLoginStatus();
        //设置黑名单显示与否
        mPresenter.checkUserProperty();
    }

    /**
     * 设置当前顶栏透明度
     *
     * @param scrollOffset 滑动距离
     */
    private void setTopBarTranslucent(int scrollOffset, int totalRange) {
        if (isAdded()) {
            if (scrollOffset < totalRange) {
                float size = (scrollOffset * 1.0f) / (totalRange * 1.0f);
                mTopLayout.setAlpha(size);
            } else {
                mTopLayout.setAlpha(1);
            }
        }
    }

    /**
     * 拖拽监听
     */
    class MultiPurposeListenerAdapter extends OnMultiPurposeListenerAdapter{

        @Override
        public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
            setMineBgImgSize(offset);
        }
    }

    /**
     * 设置我的页面的背景图片的缩放大小
     *
     * @param offset 下拉偏移量
     */
    private void setMineBgImgSize(int offset) {
        try {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mMineBgImg.getLayoutParams();
            lp.width = DensityUtil.getInstance().getScreenWidth() + offset;
            lp.height = (int) (getResources().getDimensionPixelSize(R.dimen.common_len_460px)
                    * (lp.width * 1.0f / DensityUtil.getInstance().getScreenWidth() * 1.0f));
            lp.setMargins(-(lp.width - DensityUtil.getInstance().getScreenWidth()) / 2, 0,
                    -(lp.width - DensityUtil.getInstance().getScreenWidth()) / 2, 0);
            mMineBgImg.setLayoutParams(lp);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    protected Class<MinePresenter> getPresenterClass() {
        return MinePresenter.class;
    }

    @Override
    protected Class<IMineView> getViewClass() {
        return IMineView.class;
    }
}
