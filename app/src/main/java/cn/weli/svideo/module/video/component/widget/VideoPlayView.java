package cn.weli.svideo.module.video.component.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.analytics.AnalyticsListener;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.helper.glide.ILoader;
import cn.weli.svideo.baselib.helper.glide.WeImageLoader;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.video.ui.VideoPlayFragment;
import video.movieous.droid.player.listener.OnCompletionListener;
import video.movieous.droid.player.listener.OnPreparedListener;
import video.movieous.droid.player.ui.widget.VideoView;

/**
 * 视频播放器，自定义了一些UI属性
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see VideoPlayFragment
 * @since [1.0.0]
 */
public class VideoPlayView extends VideoView implements OnPreparedListener, OnCompletionListener,
        AnalyticsListener {

    private static final int STATE_BUFFERING = 2;
    private static final String VIDEO_ERROR_403 = "403";
    private static final long EFFECT_PLAY_TIME = 3000;

    private ImageView mBottomView;
    private ImageView mImageView;
    private ProgressBar mPlayProgressBar;
    private VideoLoadingView mLoadingView;
    private ImageView mPlayBtnImg;
    private WeakHandler mWeakHandler;
    private Runnable mProRunnable;
    private Animation mScaleAnim;

    private Context mContext;

    /**
     * 是否隐藏进度条控件
     */
    private boolean hideProgress;

    /**
     * 是否是无UI模式，只有视频播放显示
     */
    private boolean isClearMode;
    /**
     * 当前播放进度时长
     */
    private int mCurrentPosition;
    /**
     * 是否是记录了有效播放
     */
    private boolean hasRecordEffectPlay;

    private OnVideoPlayErrorListener mPlayErrorListener;
    private OnVideoPreparedListener mPreparedListener;
    private OnVideoPlayListener mVideoPlayListener;
    private OnVideoStateListener mStateListener;

    public VideoPlayView(Context context) {
        this(context, null);
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        setOnPreparedListener(this);
    }

    public void setClearMode(boolean clearMode) {
        isClearMode = clearMode;
        if (isClearMode) {
            mBottomView.setVisibility(View.GONE);
            mPlayProgressBar.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
            mPlayBtnImg.setVisibility(View.GONE);
        }
    }

    private void init(Context context) {
        mContext = context;
        setAnalyticsListener(this);
        setReleaseOnDetachFromWindow(false);
        mWeakHandler = new WeakHandler();
        mImageView = new ImageView(context);
        addView(mImageView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mBottomView = new ImageView(context);
        mBottomView.setBackgroundResource(R.drawable.home_img_mask_bottom);
        LayoutParams bgLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        bgLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mBottomView, bgLp);

        mPlayProgressBar = (ProgressBar) LayoutInflater.from(mContext).inflate(
                R.layout.layout_video_progress_bar, null);

        LayoutParams proLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.common_len_2px));
        proLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mPlayProgressBar, proLp);

        mLoadingView = new VideoLoadingView(mContext);
        LayoutParams loadLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.common_len_2px));
        loadLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mLoadingView, loadLp);
        mLoadingView.setVisibility(View.GONE);

        mPlayBtnImg = new ImageView(mContext);
        mPlayBtnImg.setImageResource(R.drawable.video_icon_play);
        mPlayBtnImg.setVisibility(View.GONE);
        LayoutParams playLp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        playLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mPlayBtnImg, playLp);
        mScaleAnim = AnimationUtils.loadAnimation(WlVideoAppInfo.sAppCtx, R.anim.scale_center_set);
    }

    @Override
    public void setRepeatMode(int repeatMode) {
        super.setRepeatMode(repeatMode);
        if (repeatMode == Player.REPEAT_MODE_OFF) {
            setOnCompletionListener(this);
        } else {
            setOnCompletionListener(null);
        }
    }

    private void startCountTime() {
        if (isClearMode) {
            return;
        }
        if (mProRunnable == null) {
            mProRunnable = this::updateProgress;
        }
        mWeakHandler.postDelayed(mProRunnable, 50);
    }

    private void updateProgress() {
        if (isClearMode) {
            return;
        }
        mPlayProgressBar.setMax((int) getDuration());
        int position = (int) getCurrentPosition();
        // 当当前时长小于上次记录时长位置，则是重播了
        if (position < mCurrentPosition) {
            hasRecordEffectPlay = false;
            if (mStateListener != null) {
                mStateListener.onVideoCompletePlay();
            }
        }
        mCurrentPosition = position;
        mPlayProgressBar.setProgress(mCurrentPosition);
        mPlayProgressBar.setSecondaryProgress(
                (int) (getBufferPercentage() * 0.01f * getDuration()));
        startCountTime();
        if (mCurrentPosition > EFFECT_PLAY_TIME && !hasRecordEffectPlay) {
            if (mStateListener != null) {
                mStateListener.onVideoEffectPlay();
            }
            hasRecordEffectPlay = true;
        }
    }

    public void setImageView(String url, ImageView.ScaleType scaleType, boolean black) {
        WeImageLoader.getInstance().load(mContext, mImageView, url,
                new ILoader.Options(black ? R.drawable.shape_black_bg : R.drawable.shape_black_bg,
                        black ? R.drawable.img_moren_black : R.drawable.shape_grey_bg, scaleType));
        mImageView.setVisibility(View.VISIBLE);
    }

    public void onSingleClick() {
        if (isClearMode) {
            return;
        }
        if (mPlayBtnImg.getVisibility() == View.VISIBLE) {
            // 暂停→播放
            mPlayBtnImg.setVisibility(View.GONE);
            start();
        } else {
            // 播放→暂停
            pause();
            mPlayBtnImg.setVisibility(View.VISIBLE);
            try {
                mPlayBtnImg.startAnimation(mScaleAnim);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (mStateListener != null) {
                mStateListener.onVideoPause(true);
            }
        }
    }

    public void setHideProgress(boolean hideProgress) {
        this.hideProgress = hideProgress;
    }

    @Override
    public void onPrepared() {
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
            if (mPreparedListener != null) {
                mPreparedListener.onVideoPrepared();
            }
        });
    }

    @Override
    public boolean restart() {
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
            if (mVideoPlayListener != null) {
                mVideoPlayListener.onVideoStart();
            }
            if (isClearMode) {
                return;
            }
            mPlayBtnImg.setVisibility(View.GONE);
            mWeakHandler.post(this::startCountTime);
        });
        return super.restart();
    }

    @Override
    public void start() {
        super.start();
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
            if (mVideoPlayListener != null) {
                mVideoPlayListener.onVideoStart();
            }
            if (isClearMode) {
                return;
            }
            mPlayBtnImg.setVisibility(View.GONE);
            mWeakHandler.removeCallbacksAndMessages(null);
            mWeakHandler.post(this::startCountTime);
        });
    }

    @Override
    public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady,
            int playbackState) {
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
            if (isClearMode) {
                return;
            }
            if (playbackState == STATE_BUFFERING) {
                showLoading();
                if (mStateListener != null) {
                    mStateListener.onVideoPause(false);
                }
            } else {
                hideLoading();
                if (isPlaying()) {
                    if (mStateListener != null && getDuration() > 0) {
                        mStateListener.onVideoStart(getDuration());
                    }
                }
            }
        });
    }

    @Override
    public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
            if (error != null) {
                if (error.getMessage().contains(VIDEO_ERROR_403)) {
                    Logger.w("Current video url is 403 now");
                    if (mPlayErrorListener != null) {
                        mPlayErrorListener.onVideoPathOverdue();
                    }
                }
            }
        });
    }

    @Override
    public void stopPlayback() {
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
            resetProgress();
            mWeakHandler.removeCallbacksAndMessages(null);
            mImageView.setVisibility(View.VISIBLE);
            if (mVideoPlayListener != null) {
                mVideoPlayListener.onVideoStop();
            }
        });
        super.stopPlayback();
    }

    @Override
    public void release() {
        super.release();
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void pause() {
        super.pause();
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
            if (mVideoPlayListener != null) {
                mVideoPlayListener.onVideoPause();
            }
        });
    }

    @Override
    public void onCompletion() {
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
            if (mVideoPlayListener != null) {
                mVideoPlayListener.onVideoComplete();
            }
        });
    }

    @Override
    public void onRenderedFirstFrame(EventTime eventTime, Surface surface) {
        ((AppBaseActivity) mContext).runOnUiThread(() -> {
//            mImageView.setVisibility(View.GONE);
            mImageView.animate().alpha(0).setDuration(500).start();
        });
    }

    private void showLoading() {
        if (isClearMode) {
            return;
        }
        if (hideProgress) {
            mLoadingView.setVisibility(View.GONE);
            mPlayProgressBar.setVisibility(View.GONE);
            return;
        }
        mLoadingView.setVisibility(View.VISIBLE);
        mPlayProgressBar.setVisibility(View.GONE);
        mLoadingView.setCanLoading(true);
    }

    private void hideLoading() {
        if (isClearMode) {
            return;
        }
        if (hideProgress) {
            mLoadingView.setVisibility(View.GONE);
            mPlayProgressBar.setVisibility(View.GONE);
            return;
        }
        mLoadingView.setVisibility(View.GONE);
        mPlayProgressBar.setVisibility(View.VISIBLE);
        mLoadingView.setCanLoading(false);
    }

    private void resetProgress() {
        if (isClearMode) {
            return;
        }
        if (hideProgress) {
            mPlayProgressBar.setVisibility(View.GONE);
            return;
        }
        mPlayProgressBar.setMax((int) getDuration());
        mPlayProgressBar.setProgress(0);
        mPlayProgressBar.setSecondaryProgress(0);
        mCurrentPosition = 0;
        hasRecordEffectPlay = false;
    }

    public void setPlayErrorListener(OnVideoPlayErrorListener playErrorListener) {
        mPlayErrorListener = playErrorListener;
    }

    public void setPreparedListener(OnVideoPreparedListener preparedListener) {
        mPreparedListener = preparedListener;
    }

    public void setVideoPlayListener(OnVideoPlayListener videoPlayListener) {
        mVideoPlayListener = videoPlayListener;
    }

    public void setStateListener(OnVideoStateListener stateListener) {
        mStateListener = stateListener;
    }

    public interface OnVideoPlayErrorListener {

        /**
         * 视频播放地址过期
         */
        void onVideoPathOverdue();
    }

    public interface OnVideoPreparedListener {

        void onVideoPrepared();
    }

    public interface OnVideoPlayListener {

        /**
         * 开始播放
         */
        void onVideoStart();

        /**
         * 暂停播放
         */
        void onVideoPause();

        /**
         * 停止播放
         */
        void onVideoStop();

        /**
         * 播放完成
         */
        void onVideoComplete();
    }

    public interface OnVideoStateListener {
        /**
         * 播放
         *
         * @param duration 视频总时长
         */
        void onVideoStart(long duration);

        /**
         * 暂停
         *
         * @param click 是否是点击的暂停
         */
        void onVideoPause(boolean click);

        /**
         * 视频有效播放，这里是3秒以后算一次有效播放
         */
        void onVideoEffectPlay();

        /**
         * 视频完整播放一次
         */
        void onVideoCompletePlay();
    }
}
