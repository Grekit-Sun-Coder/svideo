package cn.weli.svideo.module.video.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.baselib.utils.ScreenUtil;
import cn.weli.svideo.common.helper.FragmentFactory;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.video.presenter.VideoDetailPresenter;
import cn.weli.svideo.module.video.view.IVideoDetailView;

/**
 * 视频播放详情
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see cn.weli.svideo.module.mine.ui.MinePraiseVideoFragment
 * @since [1.0.0]
 */
public class VideoDetailActivity extends AppBaseActivity<VideoDetailPresenter, IVideoDetailView> implements IVideoDetailView {

    @BindView(R.id.video_close_img)
    ImageView mImageView;
    private VideoPlayFragment mVideoPlayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail);
        setContentLayoutID(R.id.video_content_layout);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onBackPressed() {
        if (mVideoPlayFragment == null) {
            mVideoPlayFragment = (VideoPlayFragment) getSupportFragmentManager().findFragmentByTag(FragmentFactory.FLAG_FRAGMENT_VIDEO);
        }
        if (mVideoPlayFragment != null) {
            mVideoPlayFragment.handleVideoClose();
        }
        super.onBackPressed();
    }

    @OnClick(R.id.video_close_img)
    public void onVideoCloseImg() {
        onBackPressed();
    }

    /**
     * 初始化
     */
    private void initView() {
        ScreenUtil.setTranslucentStatus(this);
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            lp.topMargin = DensityUtil.getInstance().getStatusBarHeight();
            mImageView.setLayoutParams(lp);
        }
        openFragment(FragmentFactory.FLAG_FRAGMENT_VIDEO);
    }

    @Override
    protected Class<VideoDetailPresenter> getPresenterClass() {
        return VideoDetailPresenter.class;
    }

    @Override
    protected Class<IVideoDetailView> getViewClass() {
        return IVideoDetailView.class;
    }
}
