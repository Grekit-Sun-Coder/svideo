package cn.weli.svideo.module.video.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.component.widget.smartrefresh.WeRefreshRecyclerView;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ShareHelper;
import cn.weli.svideo.common.ui.AppBaseFragment;
import cn.weli.svideo.module.main.component.widget.ShareDialog;
import cn.weli.svideo.module.main.model.AdvertModel;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.mine.component.event.LoginSuccessEvent;
import cn.weli.svideo.module.mine.component.event.LogoutEvent;
import cn.weli.svideo.module.mine.ui.LoginActivity;
import cn.weli.svideo.module.mine.ui.MineProfitActivity;
import cn.weli.svideo.module.task.component.event.RefreshCoinEvent;
import cn.weli.svideo.module.task.component.helper.DrawAdRewardHelper;
import cn.weli.svideo.module.task.component.helper.listener.DrawAdRewardListener;
import cn.weli.svideo.module.task.component.widget.progress.ProgressView;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.task.model.bean.VideoTaskBean;
import cn.weli.svideo.module.video.component.adapter.VideoPlayAdapter;
import cn.weli.svideo.module.video.component.helper.GravityPagerSnapHelper;
import cn.weli.svideo.module.video.component.helper.GravitySnapHelper;
import cn.weli.svideo.module.video.component.widget.GuideCoinDialog;
import cn.weli.svideo.module.video.component.widget.RotateImageView;
import cn.weli.svideo.module.video.component.widget.VideoPlayView;
import cn.weli.svideo.module.video.component.widget.videoholder.VideoBaseHolder;
import cn.weli.svideo.module.video.component.widget.videoholder.VideoKmAdHolder;
import cn.weli.svideo.module.video.component.widget.videoholder.VideoPlayHolder;
import cn.weli.svideo.module.video.component.widget.videoholder.VideoTtAdHolder;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import cn.weli.svideo.module.video.presenter.VideoPlayPresenter;
import cn.weli.svideo.module.video.view.IVideoPlayView;

/**
 * 视频首页 V层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see MainActivity
 * @since [1.0.0]
 */
public class VideoPlayFragment extends AppBaseFragment<VideoPlayPresenter, IVideoPlayView> implements
        IVideoPlayView, OnLoadMoreListener, WeRefreshRecyclerView.OnNetErrorRefreshListener,
        VideoPlayAdapter.OnVideoOperationListener, OnRefreshListener,
        ProgressView.OnLoadingFinishListener, ShareHelper.OnShareResultListener, DrawAdRewardListener {

    @BindView(R.id.video_play_recycler_view)
    WeRefreshRecyclerView mRefreshRecyclerView;
    @BindView(R.id.video_bottom_cover_img)
    ImageView mBottomCoverImg;
    @BindView(R.id.video_progress_view)
    ProgressView mProgressView;
    @BindView(R.id.video_task_layout)
    RelativeLayout mVideoTaskLayout;
    @BindView(R.id.video_task_bg_img)
    RotateImageView mVideoTaskBgImg;
    @BindView(R.id.ripple_anim_view)
    LottieAnimationView mRippleAnimView;
    @BindView(R.id.coin_img)
    ImageView mCoinImg;
    @BindView(R.id.coin_flay_img)
    ImageView mCoinFlyView;
    @BindView(R.id.video_task_num_txt)
    TextView mTaskNumTxt;
    @BindView(R.id.video_task_tip_layout)
    LinearLayout mTaskTipLayout;
    @BindView(R.id.video_task_tip_txt)
    TextView mTaskTipTxt;
    @BindView(R.id.video_coin_layout)
    LinearLayout mCoinLayout;
    @BindView(R.id.video_coin_tip_txt)
    TextView mCoinTipTxt;
    private RecyclerView mRecyclerView;
    private View mFragmentView;
    private LinearLayoutManager mLayoutManager;
    private VideoPlayAdapter mAdapter;
    private AnimationDrawable mCoinAnimDrawable;
    private ShareDialog mShareDialog;
    /**
     * 当前pos位置
     */
    private int mCurrentPosition = -1;
    /**
     * 当前播放的视频Holder
     */
    private VideoBaseHolder mCurrentHolder;
    /**
     * 是否是刷新
     */
    private boolean isRefresh = true;
    /**
     * fragment是否在前台
     */
    private boolean isFront;
    /**
     * 是否是暂停状态
     */
    private boolean isPause;
    /**
     * fragment是否是隐藏状态
     */
    private boolean isHidden;
    /**
     * 是否需要恢复时播放视频
     */
    private boolean isNeedResumePlay;
    /**
     * 是否处理过引导
     */
    private boolean hasHandledGuide;
    /**
     * 是否有任务
     */
    private boolean isHasTask;
    /**
     * 任务进度
     */
    private String mTaskNumStr;
    /**
     * 统计时长
     */
    private long mUseTime;
    /**
     * 动画监听，防止多次监听
     */
    private OnAnimViewAnimatorListener mAnimViewAnimatorListener;

    private Runnable mTaskTipGoneRunnable = new Runnable() {
        @Override
        public void run() {
            mTaskTipLayout.setVisibility(View.GONE);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_video_page, container, false);
            ButterKnife.bind(this, mFragmentView);
            RxBus.get().register(this);
            initView();
            initData();
        } else {
            if (mFragmentView.getParent() != null) {
                ((ViewGroup) mFragmentView.getParent()).removeView(mFragmentView);
            }
        }
        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded() && getActivity() != null) {
            isFront = true;
            if (isPause && !isHidden) {
                isPause = false;
                resumeVideo();
            } else if (isNeedResumePlay) {
                startPlay(false);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isAdded() && getActivity() != null) {
            isFront = false;
            if (!isPause && !isHidden) {
                pauseVideo();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        mPresenter.handleUserVisible(hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
        DrawAdRewardHelper.getInstance().clear();
        if (mAdapter != null) {
            mAdapter.releaseVideo();
        }
        if (mProgressView != null && mVideoTaskLayout.getVisibility() == View.VISIBLE) {
            mProgressView.stop();
        }
    }

    @Override
    public void handleFragmentShow() {
        if (isAdded() && getActivity() != null) {
            if (isPause && !isHidden && isFront) {
                isPause = false;
                resumeVideo();
            }
        }
    }

    @Override
    public void handleFragmentHide() {
        if (isAdded() && getActivity() != null) {
            pauseVideo();
        }
    }

    @Subscribe
    public void onLoginSuccess(LoginSuccessEvent event) {
        if (isAdded()) {
            mPresenter.checkVideoTask();
        }
    }

    @Subscribe
    public void onLoginout(LogoutEvent event) {
        if (isAdded()) {
            mProgressView.stop();
            mPresenter.checkVideoTask();
        }
    }

    @Override
    public void handleVideoListRefresh(List<VideoBean> list) {
        if (isAdded() && getActivity() != null) {
            mRefreshRecyclerView.setContentShow();
            mAdapter.addItems(list);
            if (isRefresh) {
                // 是手动刷新的需要触发播放
                isRefresh = false;
                mRefreshRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startPlay(false);
                    }
                }, PLAY_DELAY);
            }
        }
    }

    @Override
    public void handleVideoListAppend(List<VideoBean> list) {
        if (isAdded() && getActivity() != null) {
            mAdapter.appendItems(list);
        }
    }

    @Override
    public void handleNoLoadMoreData() {
        if (isAdded() && getActivity() != null) {
            mRefreshRecyclerView.setLoadMoreEnd();
        }
    }

    @Override
    public void showEmptyView() {
        if (isAdded() && getActivity() != null) {
            mRefreshRecyclerView.setEmptyView(getString(R.string.video_empty_title));
        }
    }

    @Override
    public void showErrorView() {
        if (isAdded() && getActivity() != null) {
            mRefreshRecyclerView.setEmptyView(getString(R.string.video_empty_title));
        }
    }

    @Override
    public void finishLoadMore() {
        if (isAdded() && getActivity() != null) {
            mRefreshRecyclerView.finishLoadMore();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (isAdded() && getActivity() != null) {
            isRefresh = true;
            mCurrentPosition = -1;
            mAdapter.setCurrentPlayPos(mCurrentPosition);
            mPresenter.requestVideoList(true, false);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (isAdded()) {
            mPresenter.requestVideoList(false, true);
        }
    }

    @Override
    public void onNetErrorRefresh() {
        if (isAdded() && getActivity() != null) {
            isRefresh = true;
            mRefreshRecyclerView.setContentShow();
            mPresenter.requestVideoList(true, false);
        }
    }

    @Override
    public void onVideoShareClick(VideoBean bean) {
        if (isAdded() && getActivity() != null) {
            if (mShareDialog == null) {
                mShareDialog = new ShareDialog(getActivity());
            }
            mShareDialog.setPlatformSelectListener(
                    platform -> {
                        mPresenter.handleVideoShare(bean, platform);
                        StatisticsAgent.share(getActivity(), bean.item_id, StatisticsUtils.MD.MD_1, "", "", bean.content_model);
                    });
            mShareDialog.show();
        }
    }

    @Override
    public void handleShare(String url, String title, String random, String imgUrl, String platform) {
        if (isAdded() && getActivity() != null) {
            ShareHelper.share(getActivity(), platform, url, title, imgUrl, getString(R.string.share_video_sub_title, random), this);
        }
    }

    @Override
    public void handleReportSuccess() {
        if (isAdded() && getActivity() != null) {
            showToast(R.string.share_report_success_title);
        }
    }

    @Override
    public void handleDeleteCurrentVideo() {
        if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
            mAdapter.getContentList().remove(mCurrentPosition);
            mAdapter.notifyItemRemoved(mCurrentPosition);
            mAdapter.notifyItemRangeChanged(mCurrentPosition, mAdapter.getContentList().size() - mCurrentPosition);
            showToast(R.string.share_delete_success_title);
            mRefreshRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    playVideo(mCurrentPosition, true);
                }
            }, PLAY_DELAY);
        }
    }

    @Override
    public void onVideoPraiseClick(VideoBean bean) {
        mPresenter.handleVideoPraise(bean);
        StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_101, StatisticsUtils.MD.MD_1);
    }

    @Override
    public void onVideoDoublePraiseClick(VideoBean bean) {
        mPresenter.handleVideoDoublePraise(bean);
    }

    @Override
    public void onTtVideoAdPause() {
        stopVideoTask();
    }

    @Override
    public void onTtVideoAdPlay() {
        if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
            if (mCurrentHolder != null) {
                VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                startVideoTask(videoBean, false, DEFAULT_AD_TIME);
            }
        }
    }

    @Override
    public void onVideoAdClick() {
        try {
            if (mCurrentPosition < mAdapter.getContentList().size()) {
                VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                JSONObject object = new JSONObject();
                object.put("AD_type", videoBean.ad_config.source);
                StatisticsAgent.click(getActivity(), videoBean.item_id, StatisticsUtils.MD.MD_1, "", object.toString(), videoBean.content_model);

                AdvertModel.reportAdEvent(videoBean.ad_config.space, AdConfigBean.ACTION_TYPE_CLICK);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoEnter(int position) {
        try {
            playVideo(position, true);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void handleVideoUnPraise() {
        // Do Nothing，这里暂时不需要动画
    }

    @Override
    public void handleVideoPraise() {
        if (isAdded() && getActivity() != null && mCurrentHolder != null
                && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
            try {
                VideoPlayHolder holder = (VideoPlayHolder) mCurrentHolder;
                if (holder.mPraiseAnimView != null && holder.mPraiseImg != null) {
                    holder.mPraiseAnimView.setVisibility(View.VISIBLE);
                    holder.mPraiseImg.setVisibility(View.GONE);
                    holder.mPraiseAnimView.addAnimatorListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            holder.mPraiseAnimView.setVisibility(View.GONE);
                            holder.mPraiseImg.setVisibility(View.VISIBLE);
                        }
                    });
                    holder.mPraiseAnimView.playAnimation();
                }
                VideoBean bean = mAdapter.getContentList().get(mCurrentPosition);
                JSONObject object = new JSONObject();
                object.put("source", bean.source);
                StatisticsAgent.like(getActivity(), bean.item_id, StatisticsUtils.MD.MD_1, "", object.toString(), bean.content_model);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    @Override
    public void notifyCurrentVideoPraise() {
        if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
            mAdapter.notifyItemChanged(mCurrentPosition, VideoPlayHolder.FLAG_PRAISE_STATUS);
        }
    }

    @Override
    public void notifyCurrentVideoShare() {
        if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
            mAdapter.notifyItemChanged(mCurrentPosition, VideoPlayHolder.FLAG_SHARE_STATUS);
        }
    }

    @Override
    public void showReadyVideoList(List<VideoBean> list, int pos) {
        if (isAdded()) {
            mVideoTaskLayout.setVisibility(View.GONE);
            mRefreshRecyclerView.setEnableRefresh(false);
            mAdapter.addItems(list);
            mRecyclerView.post(() -> {
                mLayoutManager.scrollToPositionWithOffset(pos, 0);
                mRecyclerView.post(() -> playVideo(pos, false));
            });
        }
    }

    @Override
    public void showBottomCoverImg() {
        if (isAdded()) {
            mBottomCoverImg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initVideoTaskInfo(VideoTaskBean bean, boolean hasMoreTask, boolean isLastTask) {
        if (isAdded() && getActivity() != null && hasMoreTask) {
            isHasTask = true;
            mVideoTaskLayout.setVisibility(View.VISIBLE);
            mTaskTipLayout.setVisibility(View.INVISIBLE);
            mProgressView.setTotalAngleTime(bean.cycle_seconds);
            mTaskNumStr = getString(R.string.video_task_count_title, String.valueOf(bean.cycle_current), String.valueOf(bean.cycle_total - 1));
            mTaskNumTxt.setText(mTaskNumStr);
            mTaskNumTxt.setVisibility(View.VISIBLE);
            mCoinImg.setBackgroundResource(isLastTask ? R.drawable.home_coin_hongbao : R.drawable.jinbi_00);
            if (mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
                if (mCurrentHolder != null && mCurrentHolder instanceof VideoPlayHolder) {
                    VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                    VideoPlayHolder holder = (VideoPlayHolder) mCurrentHolder;
                    holder.mVideoView.setStateListener(new OnVideoStateChangeListener());
                    if (holder.mVideoView.isPlaying()) {
                        startVideoTask(videoBean, true, holder.mVideoView.getDuration());
                    }
                }
            }
        }
    }

    @Override
    public void setVideoTaskInfo(VideoTaskBean bean) {
        if (isAdded()) {
            mTaskNumStr = getString(R.string.video_task_count_title, String.valueOf(bean.cycle_current), String.valueOf(bean.cycle_total - 1));
            mTaskNumTxt.setText(mTaskNumStr);
            mProgressView.setTotalAngleTime(bean.cycle_seconds);
        }
    }

    @Override
    public void setVideoTaskTipLogin() {
        if (isAdded()) {
            isHasTask = false;
            mVideoTaskLayout.setVisibility(View.VISIBLE);
            mTaskNumTxt.setVisibility(View.INVISIBLE);
            mProgressView.resetProgress(-1);
            setTaskTipShow(getString(R.string.video_get_coin_title), false, false);
        }
    }

    @Override
    public void showTaskCoinTip() {
        if (isAdded()) {
            setTaskTipShow(getString(R.string.video_own_coin_title), true, true);
            SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_VIDEO_TASK_COIN, true);
        }
    }

    @Override
    public void onProgressFinish() {
        if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
            VideoBean bean = mAdapter.getContentList().get(mCurrentPosition);
            mPresenter.requestSubmitVideoTask();
        }
    }

    @Override
    public void resetVideoProgress(int coin, boolean bigCoin, boolean hasMoreTask, boolean isLastTask) {
        if (isAdded() && isHasTask && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
            try {
                RxBus.get().post(new RefreshCoinEvent(coin));
                mVideoTaskBgImg.stopAnimation();
                showVideoCoinTip(coin);
                mRippleAnimView.setAnimation(bigCoin ? RED_PKG_JSON : COM_RIPPLE_JSON);
                mRippleAnimView.setVisibility(View.VISIBLE);
                mProgressView.resetProgress(mAdapter.getContentList().get(mCurrentPosition).item_id);
                mCoinImg.setVisibility(View.GONE);
                mCoinImg.setBackgroundResource(isLastTask ? R.drawable.home_coin_hongbao : R.drawable.jinbi_00);
                mCoinFlyView.setVisibility(bigCoin ? View.GONE : View.VISIBLE);
                mRippleAnimView.cancelAnimation();
                mRippleAnimView.removeAnimatorListener(mAnimViewAnimatorListener);
                mAnimViewAnimatorListener = new OnAnimViewAnimatorListener(hasMoreTask, isLastTask);
                mRippleAnimView.addAnimatorListener(mAnimViewAnimatorListener);
                mRippleAnimView.playAnimation();
                playCoinFlyAnim();
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    @OnClick(R.id.video_task_layout)
    public void onVideoTaskClick() {
        if (WlVideoAppInfo.getInstance().hasLogin()) {
            Intent intent = new Intent(getActivity(), MineProfitActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_102, StatisticsUtils.MD.MD_1);
    }

    /**
     * 设置任务提示tip显示
     *
     * @param content 内容
     */
    private void setTaskTipShow(String content, boolean showSlide, boolean dismiss) {
        mTaskTipLayout.setVisibility(View.VISIBLE);
        mTaskTipTxt.setText(content);
        if (dismiss) {
            removeHandlerCallbacks(mTaskTipGoneRunnable);
            handleEventDelay(mTaskTipGoneRunnable, TIP_GONE_DELAY);
        }
        if (showSlide) {
            handleEventDelay(() -> setTaskTipShow(getString(R.string.video_rotate_title), false, true), TIP_SLIDE_DELAY);
            handleEventDelay(() -> setTaskTipShow(getString(R.string.video_big_reward_title), false, true), TIP_REWARD_DELAY);
        }
    }

    /**
     * 提示获得的金币
     */
    private void showVideoCoinTip(int coin) {
        if (coin > 0) {
            mCoinTipTxt.setText(getString(R.string.profit_add_plus_title, String.valueOf(coin)));
            mCoinLayout.setVisibility(View.VISIBLE);
            handleEventDelay(() -> mCoinLayout.setVisibility(View.GONE), TIP_GONE_DELAY);
        }
    }

    /**
     * 处理视频页面关闭
     */
    public void handleVideoClose() {
        if (isAdded() && getActivity() != null) {
            mPresenter.handleCurrentPositionSync(mCurrentPosition);
        }
    }

    /**
     * 播放当前的
     */
    public void startPlay(boolean force) {
        if (isAdded()) {
            if (mAdapter.getItemCount() == 0) {
                showToast(R.string.video_empty_title);
                return;
            }
        }
        int position = mCurrentPosition;
        if (position == -1) {
            position = 0;
        }
        if (!hasHandledGuide) {
            initCoinGuide();
        }
        playVideo(position, force);
    }

    /**
     * 播放视频
     *
     * @param position 位置
     */
    private void playVideo(int position, boolean force) {
        if (isAdded() && getActivity() != null && position >= 0 && position < mAdapter.getContentList().size()) {
            if (!isFront) {
                isNeedResumePlay = true;
                Logger.d("Current video page is not front, so set tag to resume to play video");
                return;
            }
            if (mCurrentPosition == position && !force) {
                Logger.d("Play position is same to last one, so return");
                return;
            }
            stopVideo();
            mCurrentPosition = position;
            VideoBean bean = mAdapter.getContentList().get(mCurrentPosition);
            mAdapter.setCurrentPlayPos(mCurrentPosition);
            VideoBaseHolder currentHolder = (VideoBaseHolder) mRecyclerView
                    .findViewHolderForAdapterPosition(position);
            if (currentHolder != null) {
                mCurrentHolder = currentHolder;
                if (mCurrentHolder instanceof VideoPlayHolder) {
                    VideoPlayHolder holder = (VideoPlayHolder) mCurrentHolder;
                    if (holder.mVideoView != null) {
                        holder.mVideoView.setStateListener(new OnVideoStateChangeListener());
                        holder.startVideo(holder);
                    }
                } else if (mCurrentHolder instanceof VideoTtAdHolder) {
                    VideoTtAdHolder holder = (VideoTtAdHolder) mCurrentHolder;
                    holder.startVideo(holder, bean);
                } else if (mCurrentHolder instanceof VideoKmAdHolder) {
                    VideoKmAdHolder holder = (VideoKmAdHolder) mCurrentHolder;
                    if (bean.mETKuaiMaAdData != null) {
                        if (holder.mVideoView != null) {
                            holder.mVideoView.setStateListener(new OnVideoStateChangeListener());
                            holder.startKmVideo(holder, bean);
                        }
                        // 曝光上报
                        bean.mETKuaiMaAdData.onExposured();
                    } else if (bean.mDrawVideoAd != null) {
                        holder.startTtVideo(holder, bean);
                    }
                }
            }
            enterVideo();
            mPresenter.checkCurrentPlayPosition(mAdapter.getItemCount(), mCurrentPosition);
        }
    }

    /**
     * 停止当前播放的视频
     */
    private void stopVideo() {
        if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
            VideoBean bean = mAdapter.getContentList().get(mCurrentPosition);
            if (mCurrentHolder instanceof VideoPlayHolder) {
                VideoPlayHolder holder = (VideoPlayHolder) mCurrentHolder;
                holder.stopVideo(holder);
                stopVideoTask();
            } else if (mCurrentHolder instanceof VideoTtAdHolder) {
                VideoTtAdHolder holder = (VideoTtAdHolder) mCurrentHolder;
                holder.stopVideo(holder, bean);
                stopVideoTask();
            } else if (mCurrentHolder instanceof VideoKmAdHolder) {
                VideoKmAdHolder holder = (VideoKmAdHolder) mCurrentHolder;
                holder.stopVideo(holder, bean);
                stopVideoTask();
            }
            exitVideo();
        }
    }

    /**
     * onResume时恢复视频
     */
    private void resumeVideo() {
        try {
            if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
                VideoBean bean = mAdapter.getContentList().get(mCurrentPosition);
                if (mCurrentHolder != null) {
                    if (mCurrentHolder instanceof VideoPlayHolder) {
                        VideoPlayHolder holder = (VideoPlayHolder) mCurrentHolder;
                        holder.resumeVideo(holder);
                    } else if (mCurrentHolder instanceof VideoKmAdHolder) {
                        VideoKmAdHolder holder = (VideoKmAdHolder) mCurrentHolder;
                        holder.resumeVideo(holder, bean);
                    } else if (mCurrentHolder instanceof VideoTtAdHolder) {
                        VideoTtAdHolder holder = (VideoTtAdHolder) mCurrentHolder;
                        holder.resumeVideo(holder, bean);
                    }
                    enterVideo();
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * onPause时暂停视频
     */
    private void pauseVideo() {
        try {
            if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
                VideoBean bean = mAdapter.getContentList().get(mCurrentPosition);
                if (mCurrentHolder != null) {
                    if (mCurrentHolder instanceof VideoPlayHolder) {
                        isPause = true;
                        VideoPlayHolder holder = (VideoPlayHolder) mCurrentHolder;
                        holder.pauseVideo(holder);
                        stopVideoTask();
                    } else if (mCurrentHolder instanceof VideoKmAdHolder) {
                        isPause = true;
                        VideoKmAdHolder holder = (VideoKmAdHolder) mCurrentHolder;
                        holder.pauseVideo(holder, bean);
                        stopVideoTask();
                    } else if (mCurrentHolder instanceof VideoTtAdHolder) {
                        isPause = true;
                        VideoTtAdHolder holder = (VideoTtAdHolder) mCurrentHolder;
                        holder.pauseVideo(holder, bean);
                        stopVideoTask();
                    }
                    exitVideo();
                    pauseStatistics();
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 开始视频任务进度
     *
     * @param bean 视频数据
     */
    private void startVideoTask(VideoBean bean, boolean showTaskNum, long duration) {
        if (isHasTask) {
            mProgressView.start(bean.item_id, duration);
            mVideoTaskBgImg.startAnimation();
            if (showTaskNum) {
                mTaskNumTxt.setText(mTaskNumStr);
                mTaskNumTxt.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 停止视频任务进度
     */
    private void stopVideoTask() {
        if (isHasTask) {
            mProgressView.stop();
            mVideoTaskBgImg.stopAnimation();
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        if (getActivity() == null) {
            return;
        }
        mProgressView.setFinishListener(this);
        mRefreshRecyclerView.setEnableRefresh(true);
        mRefreshRecyclerView.setEnableLoadMore(true);
        mRefreshRecyclerView.setEnableOverScrollDrag(false);
        mRefreshRecyclerView.setOnLoadMoreListener(this);
        mRefreshRecyclerView.setOnRefreshListener(this);
        mRefreshRecyclerView.setErrorRefreshListener(this);
        mRecyclerView = mRefreshRecyclerView.getRecyclerView();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new VideoPlayAdapter(getActivity());
        mAdapter.setOperationListener(this);
        mRecyclerView.setAdapter(mAdapter);
        DrawAdRewardHelper.getInstance().setDrawAdRewardListener(this);
        new GravityPagerSnapHelper(Gravity.TOP, true,
                new RecyclerViewSnapListener()).attachToRecyclerView(mRecyclerView);
        mCoinAnimDrawable = (AnimationDrawable) mCoinFlyView.getBackground();
        if (mCoinAnimDrawable != null) {
            mCoinAnimDrawable.setOneShot(true);
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (getActivity() == null || getActivity().getIntent() == null) {
            return;
        }
        Intent intent = getActivity().getIntent();
        int type = intent.getIntExtra(IVideoPlayView.EXTRA_VIDEO_TYPE, IVideoPlayView.TYPE_VIDEO_HOME);
        int pos = intent.getIntExtra(IVideoPlayView.EXTRA_VIDEO_POSITION, 0);
        int page = intent.getIntExtra(IVideoPlayView.EXTRA_VIDEO_PAGE, 1);
        String videoId = intent.getStringExtra(IVideoPlayView.EXTRA_VIDEO_ID);
        mPresenter.initVideo(type, pos, page, videoId);
    }

    /**
     * 初始化新人红包
     */
    private void initCoinGuide() {
        hasHandledGuide = true;
        if (WlVideoAppInfo.getInstance().hasLogin()) {
            return;
        }
        boolean hasCoinGuide = SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_VIDEO_GUIDE_COIN, false);
        if (!hasCoinGuide && getActivity() != null && isAdded()) {
            GuideCoinDialog dialog = new GuideCoinDialog(getActivity());
            dialog.setCoinListener(new GuideCoinDialog.OnGuideCoinListener() {
                @Override
                public void onGuideCoinClick() {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_VIDEO_GUIDE_COIN, true);
                    StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_103, StatisticsUtils.MD.MD_1);
                }

                @Override
                public void onGuideCancel() {
                    SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_VIDEO_GUIDE_COIN, true);
                }
            });
            dialog.show();
            StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_103, StatisticsUtils.MD.MD_1);
        }
    }

    /**
     * 进入当前视频
     */
    private void enterVideo() {
        try {
            if (mCurrentPosition < mAdapter.getContentList().size()) {
                VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                mUseTime = SystemClock.elapsedRealtime();
                JSONObject object = new JSONObject();
                object.put("from", String.valueOf(mPresenter.getVideoType()));
                if (mCurrentHolder instanceof VideoPlayHolder) {
                    object.put("type", videoBean.source);
                    object.put("source", videoBean.source);
                    object.put("content_type", "video");
                } else if (mCurrentHolder instanceof VideoTtAdHolder) {
                    object.put("type", videoBean.ad_config.source);
                    object.put("source", videoBean.ad_config.source);
                    object.put("content_type", "ad_video");
                    AdvertModel.reportAdEvent(videoBean.ad_config.space, AdConfigBean.ACTION_TYPE_PV);
                } else if (mCurrentHolder instanceof VideoKmAdHolder) {
                    object.put("type", videoBean.ad_config.source);
                    object.put("source", videoBean.ad_config.source);
                    object.put("content_type", "ad_video");
                    AdvertModel.reportAdEvent(videoBean.ad_config.space, AdConfigBean.ACTION_TYPE_PV);
                }
                StatisticsAgent.enter(getActivity(), videoBean.item_id, StatisticsUtils.MD.MD_1, "", object.toString(), videoBean.content_model);
                StatisticsAgent.view(getActivity(), videoBean.item_id, StatisticsUtils.MD.MD_1, "", object.toString(), videoBean.content_model);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 离开当前视频
     */
    private void exitVideo() {
        try {
            if (!isFront) {
                return;
            }
            if (mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
                VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                JSONObject object = new JSONObject();
                if (mCurrentHolder instanceof VideoPlayHolder) {
                    VideoPlayHolder holder = (VideoPlayHolder) mCurrentHolder;
                    object.put("use_time", SystemClock.elapsedRealtime() - mUseTime);
                    object.put("total_time", holder.mVideoView.getDuration());
                    object.put("end_time", holder.mVideoView.getCurrentPosition());
                    object.put("type", videoBean.source);
                } else if (mCurrentHolder instanceof VideoTtAdHolder) {
                    object.put("use_time", SystemClock.elapsedRealtime() - mUseTime);
                    object.put("type", videoBean.ad_config.source);
                } else if (mCurrentHolder instanceof VideoKmAdHolder) {
                    VideoKmAdHolder holder = (VideoKmAdHolder) mCurrentHolder;
                    object.put("use_time", SystemClock.elapsedRealtime() - mUseTime);
                    object.put("total_time", holder.mVideoView.getDuration());
                    object.put("end_time", holder.mVideoView.getCurrentPosition());
                    object.put("type", videoBean.ad_config.source);
                }
                StatisticsAgent.exit(getActivity(), videoBean.item_id, StatisticsUtils.MD.MD_1, "", object.toString(), videoBean.content_model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShareSuccess() {
        showToast(R.string.share_success_title);
    }

    @Override
    public void onShareFailed(String msg) {
        showToast(R.string.share_failed_title);
    }

    @Override
    public void onShareCancel() {
        showToast(R.string.share_cancel_title);
    }

    @Override
    public void onAdDownLoadStatusChange(int status, int position) {
        if (isAdded() && getActivity() != null && position >= 0 && position < mAdapter.getContentList().size()) {
            mAdapter.notifyItemChanged(position, status);
        }
    }

    @Override
    public void onAdReward(int reward) {
        showToast(getResources().getString(R.string.reward_success_txt) + reward + getResources().getString(R.string.coin));
    }

    @Override
    public void onAdRewardFail() {
        showToast(getResources().getString(R.string.reward_fail_txt));
    }

    @Override
    public void onAdRewardEd() {
        showToast(getResources().getString(R.string.rewarded_txt));
    }

    /**
     * 视频手动播放暂停
     */
    public class OnVideoStateChangeListener implements VideoPlayView.OnVideoStateListener {

        @Override
        public void onVideoStart(long duration) {
            if (isAdded() && getActivity() != null && mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
                if (mCurrentHolder != null) {
                    VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                    if (mCurrentHolder instanceof VideoPlayHolder) {
                        startVideoTask(videoBean, true, duration);
                    } else if (mCurrentHolder instanceof VideoKmAdHolder) {
                        startVideoTask(videoBean, true, duration);
                    }
                }
            }
        }

        @Override
        public void onVideoPause(boolean click) {
            if (isAdded() && getActivity() != null) {
                stopVideoTask();
                if (click) {
                    pauseStatistics();
                }
            }
        }

        @Override
        public void onVideoEffectPlay() {
            effectPlayStatistics();
        }

        @Override
        public void onVideoCompletePlay() {
            completePlayStatistics();
        }
    }

    /**
     * 完整播放统计
     */
    private void completePlayStatistics() {
        try {
            if (mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
                VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                JSONObject object = new JSONObject();
                object.put("source", videoBean.source);
                object.put("content_type", "video");
                StatisticsAgent.watching(getActivity(), videoBean.item_id, StatisticsUtils.MD.MD_1, "", object.toString(), videoBean.content_model);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 有效播放统计
     */
    private void effectPlayStatistics() {
        try {
            if (mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
                VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                JSONObject object = new JSONObject();
                object.put("source", videoBean.source);
                object.put("content_type", "video");
                StatisticsAgent.play(getActivity(), videoBean.item_id, StatisticsUtils.MD.MD_1, "", object.toString(), videoBean.content_model);
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 暂停统计
     */
    private void pauseStatistics() {
        try {
            if (mCurrentPosition >= 0 && mCurrentPosition < mAdapter.getContentList().size()) {
                if (mCurrentHolder != null) {
                    JSONObject object = new JSONObject();
                    VideoBean videoBean = mAdapter.getContentList().get(mCurrentPosition);
                    object.put("source", videoBean.source);
                    if (mCurrentHolder instanceof VideoPlayHolder) {
                        object.put("content_type", "video");
                    } else if (mCurrentHolder instanceof VideoKmAdHolder
                            || mCurrentHolder instanceof VideoTtAdHolder) {
                        object.put("content_type", "ad_video");
                    }
                    StatisticsAgent.pause(getActivity(), videoBean.item_id, StatisticsUtils.MD.MD_1, "", object.toString(), videoBean.content_model);
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 视频滑动监听
     */
    class RecyclerViewSnapListener implements GravitySnapHelper.SnapListener {

        @Override
        public void onSnap(int position) {
            playVideo(position, false);
        }
    }

    /**
     * 金币动画监听
     */
    private class OnAnimViewAnimatorListener extends AnimatorListenerAdapter {

        private boolean hasMoreTask;
        private boolean isLastTask;

        public OnAnimViewAnimatorListener(boolean hasMoreTask, boolean isLastTask) {
            this.hasMoreTask = hasMoreTask;
            this.isLastTask = isLastTask;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mRippleAnimView.setVisibility(View.GONE);
            mCoinImg.setVisibility(View.VISIBLE);
            stopCoinFlyAnim();
            mCoinFlyView.setVisibility(View.GONE);
            mPresenter.checkTaskCoinTipStatus(SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_VIDEO_TASK_COIN, false));
            // 当前所有赚钱任务已经完成，则不展示
            if (!hasMoreTask) {
                isHasTask = false;
                mTaskNumTxt.setVisibility(View.INVISIBLE);
                mVideoTaskLayout.setVisibility(View.INVISIBLE);
                mTaskTipLayout.setVisibility(View.INVISIBLE);
                mCoinLayout.setVisibility(View.INVISIBLE);
                mProgressView.resetProgress(-1);
            }
        }
    }

    /**
     * 播放金币动画
     */
    private void playCoinFlyAnim() {
        if (mCoinAnimDrawable != null) {
            mCoinAnimDrawable.start();
        }
    }

    /**
     * 停止金币动画
     */
    private void stopCoinFlyAnim() {
        if (mCoinAnimDrawable != null) {
            mCoinAnimDrawable.stop();
        }
    }

    @Override
    protected Class<VideoPlayPresenter> getPresenterClass() {
        return VideoPlayPresenter.class;
    }

    @Override
    protected Class<IVideoPlayView> getViewClass() {
        return IVideoPlayView.class;
    }
}
