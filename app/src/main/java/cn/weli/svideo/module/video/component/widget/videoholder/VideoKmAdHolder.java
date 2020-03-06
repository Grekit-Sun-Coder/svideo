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
import com.google.android.exoplayer2.Player;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.ttshell.sdk.api.TTFeedOb;
import com.ttshell.sdk.api.TTNativeOb;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.advert.MultiDrawAdHelper;
import cn.weli.svideo.advert.bean.BaseDrawVideoBean;
import cn.weli.svideo.advert.bean.BdDrawVideoBean;
import cn.weli.svideo.advert.bean.GdtDrawVideoBean;
import cn.weli.svideo.advert.bean.TtDrawVideoBean;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAd;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAdData;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAdListener;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.helper.WeCountDownTimer;
import cn.weli.svideo.baselib.helper.glide.ILoader;
import cn.weli.svideo.baselib.helper.glide.WeImageLoader;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.baselib.utils.ConstUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.ConfigConstants;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.main.model.AdvertModel;
import cn.weli.svideo.module.task.component.helper.DrawAdRewardHelper;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.task.model.bean.DrawAdTaskBean;
import cn.weli.svideo.module.video.component.adapter.VideoPlayAdapter;
import cn.weli.svideo.module.video.component.widget.VideoPlayView;
import cn.weli.svideo.module.video.component.widget.videoheart.HeartRelativeLayout;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import video.movieous.droid.player.core.video.scale.ScaleType;

/**
 * 小视频鲤跃draw广告holder
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-12
 * @see cn.weli.svideo.module.video.component.adapter.VideoPlayAdapter
 * @since [1.0.0]
 */
public class VideoKmAdHolder extends VideoBaseHolder {

    private static final int MAX_AD_SHOW_TIME = 5000;
    private static final int FLAG_EXPAND_DURATION = 500;
    private static final int MAX_AD_CHANGE_TIME = 1500;

    @BindView(R.id.video_view)
    public VideoPlayView mVideoView;
    @BindView(R.id.video_ad_Layout)
    public FrameLayout mVideoAdLayout;
    @BindView(R.id.video_gdt_ad_Layout)
    NativeAdContainer mVideoGdtAdLayout;
    @BindView(R.id.video_ad_click_layout)
    View mAdClickView;
    @BindView(R.id.video_heart_layout)
    HeartRelativeLayout mHeartLayout;
    @BindView(R.id.video_title_txt)
    TextView mTitleTxt;
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
    @BindView(R.id.video_complete_logo_img)
    ImageView mVideoCompleteLogoImg;
    @BindView(R.id.video_complete_title_txt)
    TextView mVideoCompleteTitleTxt;
    @BindView(R.id.video_complete_desc_txt)
    TextView mVideoCompleteDescTxt;
    @BindView(R.id.video_complete_action_txt)
    TextView mVideoCompleteActionTxt;
    @BindView(R.id.video_complete_play_txt)
    TextView mVideoCompletePlayTxt;
    @BindView(R.id.video_complete_layout)
    LinearLayout mVideoCompleteLayout;
    @BindView(R.id.video_ad_info_layout)
    LinearLayout mVideoAdInfoLayout;
    @BindView(R.id.video_ad_content_layout)
    LinearLayout mAdContentLayout;
    private FeedPortraitVideoView mFeedPortraitVideoView;
    private MediaView mGdtMediaView;
    private FrameLayout.LayoutParams mVideoLp;
    private VideoPlayAdapter mAdapter;
    private MultiDrawAdHelper mDrawAdHelper;
    private WeakHandler mWeakHandler;
    private boolean hasActionAnimEnd = true;
    private float mTransHeight;
    private WeCountDownTimer mCountDownTimer;

    public VideoKmAdHolder(VideoPlayAdapter adapter, View itemView, CommonRecyclerAdapter.OnItemClickListener listener) {
        super(itemView, listener);
        ButterKnife.bind(this, itemView);
        mDrawAdHelper = new MultiDrawAdHelper(mContext);
        mWeakHandler = new WeakHandler();
        mAdapter = adapter;
        mVideoView.setRepeatMode(Player.REPEAT_MODE_OFF);
        mTransHeight = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_124px);
        mVideoAdInfoLayout.setTranslationY(mTransHeight);
    }

    /**
     * item内部空间局部刷新，不影响整体播放
     * 只针对下载类的广告
     */
    public void bindKmAdVideo(VideoKmAdHolder holder, VideoBean bean, int position, int payload) {
        if (holder != null && bean != null && mAdapter != null) {
            if (payload == DrawAdRewardHelper.STATUS_DOWNLOAD_SUCCESS) {
                holder.mAdActionTxt.setText(mContext.getResources().getString(R.string.ad_open_txt));
                holder.mVideoCompleteActionTxt.setText(mContext.getResources().getString(R.string.ad_open_txt));
            } else if (payload == DrawAdRewardHelper.STATUS_DOWNLOADING) {
                holder.mAdActionTxt.setText(mContext.getResources().getString(R.string.ad_downloading_txt));
                holder.mVideoCompleteActionTxt.setText(mContext.getResources().getString(R.string.ad_downloading_txt));
            } else if (payload == DrawAdRewardHelper.STATUS_DOWNLOAD_START) {
                if (bean.ad_down_task != null) {
                    holder.mAdActionTxt.setText(mContext.getString(R.string.watch_get_coin_txt) + bean.ad_down_task.reward + mContext.getString(R.string.coin));
                }
            }
        }
    }

    /**
     * 绑定draw视频
     *
     * @param holder   当前holder
     * @param bean     视频数据
     * @param position 位置
     */
    public void bindKmAdVideo(VideoKmAdHolder holder, VideoBean bean, int position) {
        if (holder != null && bean != null && bean.ad_config != null) {
            holder.mHeartLayout.setEnableDoubleClick(false);
            holder.mHeartLayout.setTouchListener(new OnKmVideoSingleDoubleClick(bean, holder));
            holder.mVideoView.setHideProgress(true);
            if (StringUtil.equals(bean.ad_config.source, AdConfigBean.VIDEO_AD_TYPE_KM)) {
                // 麦田广告
                if (bean.mETKuaiMaAdData != null) {
                    displayKmAdVideo(holder, bean);
                } else {
                    if (!StringUtil.equals(bean.ad_config.source, AdConfigBean.VIDEO_AD_TYPE_KM)) {
                        bean.ad_config.source = AdConfigBean.VIDEO_AD_TYPE_KM;
                        bean.ad_config.ad_id = ConfigConstants.LI_YUE_VIDEO_ID;
                    }
                    if (StringUtil.isNull(bean.ad_config.ad_id)) {
                        bean.ad_config.ad_id = ConfigConstants.LI_YUE_VIDEO_ID;
                    }
                    ETKuaiMaAd maAd = new ETKuaiMaAd((AppBaseActivity) mContext, bean.ad_config.source,
                            bean.ad_config.ad_id, new ETKuaiMaAdListener() {
                        @Override
                        public void onADLoaded(List<ETKuaiMaAdData> data) {
                            if (data != null && !data.isEmpty()) {
                                bean.mETKuaiMaAdData = data.get(0);
                                displayKmAdVideo(holder, bean);
                            }
                        }

                        @Override
                        public void onNoAD() {
                            Logger.d("There is no kuai ma ad, so use tou tiao ad now!");
                            dealOtherAdGet(holder, bean, position);
                        }
                    });
                    maAd.loadAD();
                }
            }
        }
    }

    /**
     * 展示kuaima draw广告
     *
     * @param holder    当前holder
     * @param videoBean 视频数据
     */
    private void displayKmAdVideo(VideoKmAdHolder holder, VideoBean videoBean) {
        try {
            ETKuaiMaAdData bean = videoBean.mETKuaiMaAdData;
            holder.mAdTitleTxt.setText(bean.kuaiMaVideoData.ads.get(0).inline.title);
            holder.mTitleTxt.setText(bean.kuaiMaVideoData.ads.get(0).inline.desc);
            holder.mVideoView.setVideoPath(bean.kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.media_files.get(0).media.url);
            if (StringUtil.isNull(bean.source_icon)) {
                holder.mAdIconImg.setVisibility(View.GONE);
            } else {
                holder.mAdIconImg.setVisibility(View.VISIBLE);
                WeImageLoader.getInstance().load(mContext, holder.mAdIconImg, bean.source_icon, new ILoader.Options(-1, -1));
            }
            int actionType = bean.kuaiMaVideoData.ads.get(0).inline.creatives.get(0).action_type;
            String actionStr = StringUtil.isNull(bean.kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.click_btn_content) ?
                    mContext.getResources().getString(R.string.today_detail_txt) :
                    bean.kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.click_btn_content;
            if (bean.isWxAd()) {
                // 微信按钮
                holder.mAdActionTxt.setText(StringUtil.isNull(bean.getWxBtn()) ?
                        mContext.getResources().getString(R.string.today_detail_txt) : bean.getWxBtn());
                holder.mVideoCompleteActionTxt.setText(StringUtil.isNull(bean.getWxBtn()) ?
                        mContext.getResources().getString(R.string.today_detail_txt) : bean.getWxBtn());
            } else {
                if (actionType == 2) {
                    if (videoBean.ad_down_task != null && !StringUtil.isNull(videoBean.ad_down_task.reward)) {
                        actionStr = mContext.getString(R.string.download_get_coin_txt) + videoBean.ad_down_task.reward + mContext.getString(R.string.coin);
                        DrawAdRewardHelper.setIsADEnable(true);
                    } else {
                        DrawAdRewardHelper.setIsADEnable(false);
                    }
                } else {
                    if (videoBean.ad_jump_task != null && !StringUtil.isNull(videoBean.ad_jump_task.reward)) {
                        actionStr = mContext.getString(R.string.watch_get_coin_txt) + videoBean.ad_jump_task.reward + mContext.getString(R.string.coin);
                        DrawAdRewardHelper.setIsADEnable(true);
                    } else {
                        DrawAdRewardHelper.setIsADEnable(false);
                    }
                }
                holder.mAdActionTxt.setText(actionStr);
                holder.mVideoCompleteActionTxt.setText(actionStr);
            }
            // 播放完成后的设置
            holder.mVideoCompleteLayout.setVisibility(View.GONE);
            holder.mAdContentLayout.setVisibility(View.VISIBLE);
            holder.mAdActionTxt.setVisibility(View.VISIBLE);
            holder.mVideoCompleteTitleTxt.setText(bean.kuaiMaVideoData.ads.get(0).inline.title);
            holder.mVideoCompleteDescTxt.setText(bean.kuaiMaVideoData.ads.get(0).inline.desc);
            if (StringUtil.isNull(bean.iconurl)) {
                holder.mVideoCompleteLogoImg.setVisibility(View.GONE);
            } else {
                holder.mVideoCompleteLogoImg.setVisibility(View.VISIBLE);
                WeImageLoader.getInstance().load(mContext, holder.mVideoCompleteLogoImg, bean.iconurl, new ILoader.Options(-1, -1));
            }
            if (bean.video_type == 2) {
                holder.mVideoView.setScaleType(ScaleType.CENTER_CROP);
                holder.mVideoView.setImageView(bean.kuaiMaVideoData.ads.get(0).inline.cover, ImageView.ScaleType.CENTER_CROP, true);
            } else {
                holder.mVideoView.setScaleType(ScaleType.FIT_CENTER);
                holder.mVideoView.setImageView(bean.kuaiMaVideoData.ads.get(0).inline.cover, ImageView.ScaleType.FIT_CENTER, true);
            }
            holder.mAdActionTxt.setVisibility(View.VISIBLE);
            holder.mAdTitleTxt.setVisibility(View.VISIBLE);
            holder.mVideoAdLayout.setVisibility(View.GONE);
            holder.mVideoGdtAdLayout.setVisibility(View.GONE);
            holder.mVideoBottomView.setVisibility(View.VISIBLE);
            holder.mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            holder.hasActionAnimEnd = true;

            mAdapter.getVideoKmAdHolderList().add(holder);
            DrawAdTaskBean drawAdTaskBean = new DrawAdTaskBean();
            drawAdTaskBean.position = getAdapterPosition();
            drawAdTaskBean.type = actionType;
            if (actionType == 2) {
                drawAdTaskBean.taskKey = videoBean.ad_down_task != null ? videoBean.ad_down_task.key : "";
                drawAdTaskBean.token = bean.kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.package_name;
            } else if (actionType == 1) {
                drawAdTaskBean.taskKey = videoBean.ad_jump_task != null ? videoBean.ad_jump_task.key : "";
                drawAdTaskBean.token = bean.kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.click_through;
            }

            holder.mAdClickView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        bean.onClicked(false);
                        DrawAdRewardHelper.getInstance().addTaskItem(drawAdTaskBean);
                        AdvertModel.reportAdEvent(videoBean.ad_config.space, AdConfigBean.ACTION_TYPE_CLICK);
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }
            });
            holder.mAdActionTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (bean.isWxAd()) {
                            bean.onClickWx();
                        } else {
                            bean.onClicked(false);
                            DrawAdRewardHelper.getInstance().addTaskItem(drawAdTaskBean);
                        }
                        AdvertModel.reportAdEvent(videoBean.ad_config.space, AdConfigBean.ACTION_TYPE_CLICK);
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }
            });
            holder.mVideoCompleteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        bean.onClicked(false);
                        DrawAdRewardHelper.getInstance().addTaskItem(drawAdTaskBean);
                        AdvertModel.reportAdEvent(videoBean.ad_config.space, AdConfigBean.ACTION_TYPE_CLICK);
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }
            });
            holder.mVideoCompleteActionTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (bean.isWxAd()) {
                            bean.onClickWx();
                        } else {
                            bean.onClicked(false);
                            DrawAdRewardHelper.getInstance().addTaskItem(drawAdTaskBean);
                        }
                        AdvertModel.reportAdEvent(videoBean.ad_config.space, AdConfigBean.ACTION_TYPE_CLICK);
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }
            });
            holder.mVideoCompletePlayTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.mVideoCompleteLayout.setVisibility(View.GONE);
                    holder.mAdContentLayout.setVisibility(View.VISIBLE);
                    holder.mAdActionTxt.setVisibility(View.VISIBLE);
                    holder.mVideoView.restart();
                }
            });
            holder.mVideoView.setVideoPlayListener(new VideoPlayView.OnVideoPlayListener() {
                @Override
                public void onVideoStart() {
                    try {
                        bean.onTongjiTrack(System.currentTimeMillis(), 200, 0, 0);
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }

                @Override
                public void onVideoPause() {
                    try {
                        float offset = 1.0f * holder.mVideoView.getCurrentPosition() / holder.mVideoView.getDuration();
                        bean.onTongjiTrack(System.currentTimeMillis(), 201, offset, (int) (holder.mVideoView.getDuration() / ConstUtil.SEC));
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }

                @Override
                public void onVideoStop() {
                    try {
                        float offset = 1.0f * holder.mVideoView.getCurrentPosition() / holder.mVideoView.getDuration();
                        bean.onTongjiTrack(System.currentTimeMillis(), 201, offset, (int) (holder.mVideoView.getDuration() / ConstUtil.SEC));
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }

                @Override
                public void onVideoComplete() {
                    try {
                        holder.mVideoCompleteLayout.setVisibility(View.VISIBLE);
                        holder.mAdContentLayout.setVisibility(View.GONE);
                        holder.mAdActionTxt.setVisibility(View.GONE);
                        bean.onTongjiTrack(System.currentTimeMillis(), 205, 1, (int) (holder.mVideoView.getDuration() / ConstUtil.SEC));
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 使用第三方sdk填充
     *  @param holder 当前holder
     * @param bean   视频数据
     * @param position
     */
    private void dealOtherAdGet(VideoKmAdHolder holder, VideoBean bean, int position) {
        if (holder != null && bean != null) {
            holder.mHeartLayout.setEnableDoubleClick(false);
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
                mDrawAdHelper.loadAd(StringUtil.EMPTY_STR, StringUtil.EMPTY_STR,
                        bean.ad_config.backup_source, bean.ad_config.backup_ad_id);
            }
            holder.mAdActionTxt.setVisibility(View.VISIBLE);
            holder.mAdTitleTxt.setVisibility(View.VISIBLE);
            holder.mVideoBottomView.setVisibility(View.VISIBLE);
            holder.mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
        }
    }

    /**
     * 绑定draw视频
     *
     * @param drawVideoBean draw视频数据
     * @param position
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
     * 开始kuaima广告按钮展示
     */
    public void startKuaiMaBtnShow() {
        if (!hasActionAnimEnd) {
            return;
        }
        hasActionAnimEnd = false;
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
        mCountDownTimer.start();
    }

    /**
     * 设置头条draw视频数据
     *
     * @param adBean 视频
     */
    public void setTtDrawVideoAd(TtDrawVideoBean adBean) {
        if (adBean != null && mAdapter != null && adBean.getAdBean() != null) {
            adBean.getAdBean().setActivityForDownloadApp((AppBaseActivity) mContext);
            adBean.getAdBean().setCanInterruptVideoPlay(true);
            adBean.getAdBean().setPauseIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.video_icon_play), 60);
            mVideoGdtAdLayout.setVisibility(View.GONE);
            mVideoAdLayout.setVisibility(View.VISIBLE);
            mVideoAdLayout.removeAllViews();
            mVideoAdLayout.addView(adBean.getAdBean().getObView());
            mAdActionTxt.setText(adBean.getAdBean().getButtonText());
            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            mAdIconImg.setImageResource(R.drawable.img_jinritoutiao);
            mAdIconImg.setVisibility(View.VISIBLE);
            mAdTagImg.setVisibility(View.VISIBLE);
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
                    if (mCountDownTimer != null) {
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
            mVideoGdtAdLayout.setVisibility(View.VISIBLE);
            mVideoAdLayout.setVisibility(View.GONE);
            mVideoGdtAdLayout.removeAllViews();
            mVideoGdtAdLayout.addView(mGdtMediaView, mVideoLp);
            mAdActionTxt.setText(adBean.getBtnDesc());
            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            mTitleTxt.setText(adBean.getDesc());
            mAdTitleTxt.setText(adBean.getTitle());
            mAdIconImg.setVisibility(View.VISIBLE);
            mAdIconImg.setImageResource(R.drawable.gdt_logo);
            mAdTagImg.setVisibility(View.VISIBLE);
            hasActionAnimEnd = false;
            adBean.getAdBean().setNativeAdEventListener(new NativeADEventListener() {
                @Override
                public void onADExposed() {
                }

                @Override
                public void onADClicked() {
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onVideoAdClick();
                    }
                }

                @Override
                public void onADError(AdError adError) {
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
        mVideoAdLayout.setVisibility(View.VISIBLE);
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
        hasActionAnimEnd = false;
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
                mVideoAdInfoLayout.setTranslationY(mTransHeight * (1.0f - cVal));
                if (cVal == 1) {
                    mWeakHandler.postDelayed(() -> {
                        hasActionAnimEnd = true;
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

    private void cancelKmBtnShow() {
        try {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            hasActionAnimEnd = true;
            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            mVideoCompleteLayout.setVisibility(View.GONE);
            mAdContentLayout.setVisibility(View.VISIBLE);
            mAdActionTxt.setVisibility(View.VISIBLE);
            mVideoAdInfoLayout.setTranslationY(mTransHeight);
            mWeakHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 开始计时按钮状态
     */
    private void startTimer() {
        if (mCountDownTimer != null && !hasActionAnimEnd) {
            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
            mCountDownTimer.start();
        }
    }

    public void startKmVideo(VideoKmAdHolder holder, VideoBean bean) {
        if (holder.mVideoView.getVideoUri() != null) {
            if (holder.mVideoView.getTag() == null
                    || !holder.mVideoView.getTag().equals(holder.mVideoView.getVideoUri().toString())) {
                holder.mVideoView.start();
            } else if (!holder.mVideoView.restart()) {
                holder.mVideoView.start();
            }
            holder.startKuaiMaBtnShow();
        }
    }

    /**
     * 开始播放
     *
     * @param holder 当前holder
     * @param bean   视频数据
     */
    public void startTtVideo(VideoKmAdHolder holder, VideoBean bean) {
        if (holder.mVideoAdLayout != null && holder.mVideoAdLayout.getVisibility() == View.VISIBLE) {
            if (bean.mDrawVideoAd != null) {
                if (bean.mDrawVideoAd instanceof TtDrawVideoBean) {
                    if (holder.mVideoAdLayout.getChildAt(0) == null) {
                        setTtDrawVideoAd((TtDrawVideoBean) bean.mDrawVideoAd);
                    }
                } else if (bean.mDrawVideoAd instanceof GdtDrawVideoBean) {
                    if (holder.mGdtMediaView != null) {
                        setGdtDrawVideoAd((GdtDrawVideoBean) bean.mDrawVideoAd);
                        if (mCountDownTimer != null && !hasActionAnimEnd) {
                            mAdActionTxt.setBackgroundResource(R.drawable.shape_video_ad_btn_bg);
                            mCountDownTimer.start();
                        }
                    }
                } else if (bean.mDrawVideoAd instanceof BdDrawVideoBean) {
                    setBdDrawVideoAd((BdDrawVideoBean) bean.mDrawVideoAd, true);
                }
            }
        }
    }

    public void resumeVideo(VideoKmAdHolder holder, VideoBean bean) {
        if (holder.mVideoAdLayout != null && bean.mDrawVideoAd != null) {
            if (bean.mDrawVideoAd instanceof BdDrawVideoBean) {
                if (mFeedPortraitVideoView != null) {
                    mFeedPortraitVideoView.resume();
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onTtVideoAdPlay();
                    }
                }
            }
        } else if (holder.mVideoView != null) {
            holder.mVideoView.start();
        }
    }

    public void pauseVideo(VideoKmAdHolder holder, VideoBean bean) {
        if (holder.mVideoAdLayout != null && bean.mDrawVideoAd != null) {
            if (bean.mDrawVideoAd instanceof BdDrawVideoBean) {
                if (mFeedPortraitVideoView != null) {
                    mFeedPortraitVideoView.pause();
                    if (mAdapter.getOperationListener() != null) {
                        mAdapter.getOperationListener().onTtVideoAdPause();
                    }
                }
            }
        } else if (holder.mVideoView != null && holder.mVideoView.isPlaying()) {
            holder.mVideoView.pause();
        }
    }

    /**
     * 停止播放
     *
     * @param holder 当前holder
     * @param bean   视频数据
     */
    public void stopVideo(VideoKmAdHolder holder, VideoBean bean) {
        if (holder.mVideoView != null) {
            holder.mVideoView.setStateListener(null);
            if (holder.mVideoView.getVideoUri() != null) {
                holder.mVideoView.setTag(holder.mVideoView.getVideoUri().toString());
                holder.mVideoView.stopPlayback();
            }
            holder.cancelKmBtnShow();
        } else if (holder.mVideoAdLayout != null && bean.mDrawVideoAd != null) {
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
                }
            }
            holder.cancelKmBtnShow();
        }
    }

    class OnKmVideoSingleDoubleClick implements HeartRelativeLayout.OnHeartTouchListener {

        private VideoBean mBean;

        private VideoKmAdHolder mHolder;

        public OnKmVideoSingleDoubleClick(VideoBean bean, VideoKmAdHolder holder) {
            mBean = bean;
            mHolder = holder;
        }

        @Override
        public void onSingleClick() {
            if (mHolder != null && mHolder.mVideoView != null) {
                mHolder.mVideoView.onSingleClick();
            }
        }

        @Override
        public void onDoubleClick() {
        }
    }
}
