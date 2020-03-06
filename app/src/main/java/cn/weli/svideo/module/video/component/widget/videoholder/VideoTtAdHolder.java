package cn.weli.svideo.module.video.component.widget.videoholder;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobads.component.FeedPortraitVideoView;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.ttshell.sdk.api.TTFeedOb;
import com.ttshell.sdk.api.TTNativeOb;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.advert.MultiDrawAdHelper;
import cn.weli.svideo.advert.bean.BaseDrawVideoBean;
import cn.weli.svideo.advert.bean.BdDrawVideoBean;
import cn.weli.svideo.advert.bean.GdtDrawVideoBean;
import cn.weli.svideo.advert.bean.TtDrawVideoBean;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.helper.WeCountDownTimer;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.baselib.utils.ConstUtil;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.video.component.adapter.VideoPlayAdapter;
import cn.weli.svideo.module.video.model.bean.VideoBean;

/**
 * 小视频头条draw广告holder
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-12
 * @see cn.weli.svideo.module.video.component.adapter.VideoPlayAdapter
 * @since [1.0.0]
 */
public class VideoTtAdHolder extends VideoBaseHolder {

    private static final int MAX_AD_SHOW_TIME = 5000;
    private static final int MAX_AD_CHANGE_TIME = 1500;
    private static final int FLAG_EXPAND_DURATION = 500;

    @BindView(R.id.video_title_txt)
    TextView mTitleTxt;
    @BindView(R.id.video_ad_Layout)
    public FrameLayout mVideoAdLayout;
    @BindView(R.id.video_gdt_ad_Layout)
    NativeAdContainer mVideoGdtAdLayout;
    @BindView(R.id.video_ad_click_layout)
    View mAdClickView;
    @BindView(R.id.video_ad_action_txt)
    TextView mAdActionTxt;
    @BindView(R.id.video_ad_title_txt)
    TextView mAdTitleTxt;
    @BindView(R.id.video_bottom_view)
    View mVideoBottomView;
    @BindView(R.id.video_ad_icon_img)
    ImageView mAdIconImg;
    @BindView(R.id.video_ad_tag_img)
    ImageView mAdTagImg;
    @BindView(R.id.video_ad_content_layout)
    LinearLayout mAdContentLayout;
    private FeedPortraitVideoView mFeedPortraitVideoView;
    private MediaView mGdtMediaView;
    private VideoPlayAdapter mAdapter;
    private MultiDrawAdHelper mDrawAdHelper;
    private WeakHandler mWeakHandler;
    private WeCountDownTimer mCountDownTimer;
    private FrameLayout.LayoutParams mVideoLp;
    private boolean hasAnimEnd;
    private float mTransHeight;

    public VideoTtAdHolder(VideoPlayAdapter adapter, View itemView, CommonRecyclerAdapter.OnItemClickListener listener) {
        super(itemView, listener);
        ButterKnife.bind(this, itemView);
        mAdapter = adapter;
        mDrawAdHelper = new MultiDrawAdHelper(mContext);
        mWeakHandler = new WeakHandler();
        mTransHeight = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_124px);
        mAdContentLayout.setTranslationY(mTransHeight);
    }

    /**
     * 绑定draw视频
     *
     * @param drawVideoBean draw视频数据
     */
    public void bindDrawVideoAd(BaseDrawVideoBean drawVideoBean, int position) {
        if (drawVideoBean instanceof TtDrawVideoBean) {
            setTtDrawVideoAd((TtDrawVideoBean) drawVideoBean);
        } else if (drawVideoBean instanceof GdtDrawVideoBean) {
            setGdtDrawVideoAd((GdtDrawVideoBean) drawVideoBean);
        } else if (drawVideoBean instanceof BdDrawVideoBean) {
            setBdDrawVideoAd((BdDrawVideoBean) drawVideoBean, position == mAdapter.getCurrentPlayPos());
        }
    }

    /**
     * 绑定draw视频
     *
     * @param holder   当前holder
     * @param bean     视频数据
     * @param position 位置
     */
    public void bindAdVideo(VideoTtAdHolder holder, VideoBean bean, int position) {
        if (holder != null && bean != null && bean.ad_config != null) {
            if (bean.mDrawVideoAd == null) {
                mDrawAdHelper.setAdListener(new MultiDrawAdHelper.OnMultiDrawAdListener() {
                    @Override
                    public void onGetDrawAdSuccess(BaseDrawVideoBean drawVideoBean) {
                        if (drawVideoBean != null) {
                            bean.mDrawVideoAd = drawVideoBean;
                            Logger.d("Get draw ad success result is [" + bean.mDrawVideoAd + "]");
                            bindDrawVideoAd(drawVideoBean, position);
                        }
                    }

                    @Override
                    public void onGeDrawAdFailed(String code, String msg) {
                        Logger.d("Get draw ad failed is [" + code + " " + msg + "]");
                    }
                });
                mDrawAdHelper.loadAd(bean.ad_config.source, bean.ad_config.ad_id,
                        bean.ad_config.backup_source, bean.ad_config.backup_ad_id);
            }
            holder.mAdActionTxt.setVisibility(View.VISIBLE);
            holder.mAdTitleTxt.setVisibility(View.VISIBLE);
            holder.mVideoBottomView.setVisibility(View.VISIBLE);
            holder.mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
        }
    }

    /**
     * 设置广点通draw视频数据
     *
     * @param adBean 视频
     */
    private void setGdtDrawVideoAd(GdtDrawVideoBean adBean) {
        if (adBean == null || adBean.getAdBean() == null) {
            return;
        }
        adBean.getAdBean().bindAdToView(mContext, mVideoGdtAdLayout, null, null);
        if (adBean.getAdBean().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            VideoOption.Builder builder = new VideoOption.Builder();
            builder.setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS);
            builder.setAutoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO);
            builder.setEnableDetailPage(false);
            builder.setNeedProgressBar(true);
            builder.setAutoPlayMuted(false);
            if (mGdtMediaView == null) {
                mGdtMediaView = new MediaView(mContext);
            }
            if (mVideoLp == null) {
                mVideoLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            }
            mVideoGdtAdLayout.removeAllViews();
            mVideoGdtAdLayout.addView(mGdtMediaView, mVideoLp);
            mVideoGdtAdLayout.setVisibility(View.VISIBLE);
            mAdActionTxt.setText(adBean.getBtnDesc());
            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            mTitleTxt.setText(adBean.getDesc());
            mAdTitleTxt.setText(adBean.getTitle());
            mAdIconImg.setVisibility(View.VISIBLE);
            mAdIconImg.setImageResource(R.drawable.gdt_logo);
            mAdTagImg.setVisibility(View.VISIBLE);
            hasAnimEnd = false;
            adBean.getAdBean().setNativeAdEventListener(new NativeADEventListener() {
                @Override
                public void onADExposed() {
                    Logger.d("gdt draw video exposed");
                }

                @Override
                public void onADClicked() {
                    Logger.d("gdt draw video click");
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onVideoAdClick();
                    }
                }

                @Override
                public void onADError(AdError adError) {
                    Logger.d("gdt draw video adError " + adError.getErrorMsg());
                }

                @Override
                public void onADStatusChanged() {
                }
            });
            adBean.getAdBean().bindMediaView(mGdtMediaView, builder.build(),
                    new NativeADMediaListener() {
                        @Override
                        public void onVideoInit() {
                        }

                        @Override
                        public void onVideoLoading() {
                        }

                        @Override
                        public void onVideoReady() {
                        }

                        @Override
                        public void onVideoLoaded(int videoDuration) {
                        }

                        @Override
                        public void onVideoStart() {
                            mVideoAdLayout.setVisibility(View.VISIBLE);
                            if (mAdapter.getOperationListener() != null) {
                                mAdapter.getOperationListener().onTtVideoAdPlay();
                            }
                        }

                        @Override
                        public void onVideoPause() {
                            if (mAdapter.getOperationListener() != null) {
                                mAdapter.getOperationListener().onTtVideoAdPause();
                            }
                        }

                        @Override
                        public void onVideoResume() {
                            if (mAdapter.getOperationListener() != null) {
                                mAdapter.getOperationListener().onTtVideoAdPlay();
                            }
                        }

                        @Override
                        public void onVideoCompleted() {
                        }

                        @Override
                        public void onVideoError(AdError error) {
                            Logger.d("gdt onVideoError error " + error.getErrorMsg());
                        }

                        @Override
                        public void onVideoStop() {
                        }

                        @Override
                        public void onVideoClicked() {
                            if (mAdapter.getOperationListener() != null) {
                                mAdapter.getOperationListener().onVideoAdClick();
                            }
                        }
                    });
            mAdClickView.setVisibility(View.GONE);
            initTimer();
        }
    }

    /**
     * 设置头条draw视频数据
     *
     * @param adBean 视频
     */
    private void setTtDrawVideoAd(TtDrawVideoBean adBean) {
        if (adBean != null && mAdapter != null && adBean.getAdBean() != null) {
            adBean.getAdBean().setActivityForDownloadApp((AppBaseActivity) mContext);
            adBean.getAdBean().setCanInterruptVideoPlay(true);
            adBean.getAdBean().setPauseIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.video_icon_play), 60);
            mVideoGdtAdLayout.setVisibility(View.GONE);
            mVideoAdLayout.removeAllViews();
            mVideoAdLayout.addView(adBean.getAdBean().getObView());
            mAdActionTxt.setText(adBean.getBtnDesc());
            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            mAdIconImg.setImageResource(R.drawable.img_jinritoutiao);
            mAdIconImg.setVisibility(View.VISIBLE);
            mAdTagImg.setVisibility(View.VISIBLE);
            hasAnimEnd = false;
            mAdClickView.setVisibility(View.VISIBLE);
            adBean.getAdBean().registerViewForInteraction(mVideoAdLayout, mAdClickView, new TTNativeOb.ObInteractionListener() {
                @Override
                public void onObClicked(View view, TTNativeOb ttNativeOb) {

                }

                @Override
                public void onObCreativeClick(View view, TTNativeOb ttNativeOb) {
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onVideoAdClick();
                    }
                }

                @Override
                public void onObShow(TTNativeOb ttNativeOb) {
                    if (mCountDownTimer != null && !hasAnimEnd) {
                        mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
                        mCountDownTimer.start();
                    }
                }
            });
            adBean.getAdBean().setVideoObListener(new TTFeedOb.VideoObListener() {
                @Override
                public void onVideoLoad(TTFeedOb ttFeedOb) {
                }

                @Override
                public void onVideoError(int i, int i1) {
                }

                @Override
                public void onVideoObStartPlay(TTFeedOb ttFeedOb) {
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onTtVideoAdPlay();
                    }
                }

                @Override
                public void onVideoObPaused(TTFeedOb ttFeedOb) {
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onTtVideoAdPause();
                    }
                }

                @Override
                public void onVideoObContinuePlay(TTFeedOb ttFeedOb) {
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onTtVideoAdPlay();
                    }
                }

                @Override
                public void onProgressUpdate(long l, long l1) {
                }

                @Override
                public void onVideoObComplete(TTFeedOb ttFeedOb) {
                }
            });
            mTitleTxt.setText(adBean.getDesc());
            mAdTitleTxt.setText(adBean.getTitle());
            initTimer();
        }
    }

    /**
     * 设置百度draw视频数据
     *
     * @param adBean 视频
     */
    private void setBdDrawVideoAd(BdDrawVideoBean adBean, boolean needPlay) {
        if (adBean == null || adBean.getAdBean() == null) {
            return;
        }
        if (mFeedPortraitVideoView == null) {
            mFeedPortraitVideoView = new FeedPortraitVideoView(mContext);
        }
        if (mVideoLp == null) {
            mVideoLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        }
        mVideoGdtAdLayout.setVisibility(View.GONE);
        mVideoAdLayout.removeAllViews();
        mVideoAdLayout.addView(mFeedPortraitVideoView, mVideoLp);
        mFeedPortraitVideoView.setAdData(adBean.getAdBean());
        mAdActionTxt.setText(adBean.getBtnDesc());
        mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
        mAdIconImg.setImageResource(R.drawable.baidu_logo);
        mAdIconImg.setVisibility(View.VISIBLE);
        mAdTagImg.setVisibility(View.VISIBLE);
        mTitleTxt.setText(adBean.getDesc());
        mAdTitleTxt.setText(adBean.getTitle());
        hasAnimEnd = false;
        initTimer();
        mAdClickView.setVisibility(View.VISIBLE);
        mAdClickView.setOnClickListener(view -> {
            if (adBean.getAdBean() != null) {
                adBean.getAdBean().handleClick(view);
                if (mAdapter.getOperationListener() != null) {
                    mAdapter.getOperationListener().onVideoAdClick();
                }
            }
        });
        if (needPlay) {
            mFeedPortraitVideoView.play();
            if (mAdapter.getOperationListener() != null) {
                mAdapter.getOperationListener().onTtVideoAdPlay();
            }
            startTimer();
        }
    }

    /**
     * 初始化倒计时
     */
    private void initTimer() {
        mCountDownTimer = new WeCountDownTimer(MAX_AD_SHOW_TIME, ConstUtil.SEC);
        mCountDownTimer.setCountDownCallBack(new WeCountDownTimer.CountDownCallBack() {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (mAdActionTxt != null) {
                    showActionTxt();
                }
            }
        });
    }

    /**
     * 展示按钮
     */
    private void showActionTxt() {
        try {
            ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration(FLAG_EXPAND_DURATION);
            anim.addUpdateListener(valueAnimator -> {
                float cVal = (Float) valueAnimator.getAnimatedValue();
                mAdContentLayout.setTranslationY(mTransHeight * (1.0f - cVal));
                if (cVal == 1) {
                    mWeakHandler.postDelayed(() -> {
                        hasAnimEnd = true;
                        mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg_selected);
                        mAdActionTxt.setAlpha(0.3f);
                        mAdActionTxt.animate().alpha(1).setDuration(500).start();
                    }, MAX_AD_CHANGE_TIME);
                }
            });
            anim.start();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 取消倒计时
     */
    private void cancelCountTimer() {
        try {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            mAdContentLayout.setTranslationY(mTransHeight);
            mWeakHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 开始计时按钮状态
     */
    private void startTimer() {
        if (mCountDownTimer != null && !hasAnimEnd) {
            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            mCountDownTimer.start();
        }
    }

    /**
     * 开始播放
     *
     * @param holder 当前holder
     * @param bean   视频数据
     */
    public void startVideo(VideoTtAdHolder holder, VideoBean bean) {
        if (holder.mVideoAdLayout != null && holder.mVideoAdLayout.getVisibility() == View.VISIBLE) {
            if (bean.mDrawVideoAd != null) {
                if (bean.mDrawVideoAd instanceof TtDrawVideoBean) {
                    if (holder.mVideoAdLayout.getChildAt(0) == null) {
                        setTtDrawVideoAd((TtDrawVideoBean) bean.mDrawVideoAd);
                    }
                } else if (bean.mDrawVideoAd instanceof GdtDrawVideoBean) {
                    if (holder.mGdtMediaView != null) {
                        setGdtDrawVideoAd((GdtDrawVideoBean) bean.mDrawVideoAd);
                        startTimer();
                    }
                } else if (bean.mDrawVideoAd instanceof BdDrawVideoBean) {
                    setBdDrawVideoAd((BdDrawVideoBean) bean.mDrawVideoAd, true);
                }
            }
        }
    }

    /**
     * 停止播放
     *
     * @param holder 当前holder
     * @param bean   视频数据
     */
    public void stopVideo(VideoTtAdHolder holder, VideoBean bean) {
        if (holder.mVideoAdLayout != null && bean.mDrawVideoAd != null) {
            if (bean.mDrawVideoAd instanceof TtDrawVideoBean) {
                holder.mVideoAdLayout.removeAllViews();
                TtDrawVideoBean adBean = (TtDrawVideoBean) bean.mDrawVideoAd;
                if (adBean != null && adBean.getAdBean() != null) {
                    adBean.getAdBean().setVideoObListener(null);
                }
            } else if (bean.mDrawVideoAd instanceof GdtDrawVideoBean) {
                if (holder.mGdtMediaView != null) {
                    GdtDrawVideoBean adBean = (GdtDrawVideoBean) bean.mDrawVideoAd;
                    adBean.getAdBean().stopVideo();
                }
            } else if (bean.mDrawVideoAd instanceof BdDrawVideoBean) {
                if (mFeedPortraitVideoView != null) {
                    mFeedPortraitVideoView.stop();
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onTtVideoAdPause();
                    }
                }
            }
            holder.cancelCountTimer();
        }
    }

    public void resumeVideo(VideoTtAdHolder holder, VideoBean bean) {
        if (holder.mVideoAdLayout != null && bean.mDrawVideoAd != null) {
            if (bean.mDrawVideoAd instanceof BdDrawVideoBean) {
                if (mFeedPortraitVideoView != null) {
                    mFeedPortraitVideoView.resume();
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onTtVideoAdPlay();
                    }
                }
            }
        }
    }

    public void pauseVideo(VideoTtAdHolder holder, VideoBean bean) {
        if (holder.mVideoAdLayout != null && bean.mDrawVideoAd != null) {
            if (bean.mDrawVideoAd instanceof BdDrawVideoBean) {
                if (mFeedPortraitVideoView != null) {
                    mFeedPortraitVideoView.pause();
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onTtVideoAdPause();
                    }
                }
            }
        }
    }
}
