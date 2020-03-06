package cn.weli.svideo.module.video.component.widget.videoholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.exoplayer2.Player;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.video.component.adapter.VideoPlayAdapter;
import cn.weli.svideo.module.video.component.widget.VideoPlayView;
import cn.weli.svideo.module.video.component.widget.videoheart.HeartRelativeLayout;
import cn.weli.svideo.module.video.model.VideoModel;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import cn.weli.svideo.module.video.model.bean.VideoPathBean;
import video.movieous.droid.player.core.video.scale.ScaleType;

/**
 * 小视频播放holder
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-12
 * @see cn.weli.svideo.module.video.component.adapter.VideoPlayAdapter
 * @since [1.0.0]
 */
public class VideoPlayHolder extends VideoBaseHolder implements VideoPlayView.OnVideoPlayErrorListener {

    /**
     * 点赞
     */
    public static final int FLAG_PRAISE_STATUS = 0x111;
    /**
     * 分享
     */
    public static final int FLAG_SHARE_STATUS = 0x112;

    @BindView(R.id.video_heart_layout)
    HeartRelativeLayout mHeartLayout;
    @BindView(R.id.video_view)
    public VideoPlayView mVideoView;
    @BindView(R.id.video_title_txt)
    TextView mVideoTitleTxt;
    @BindView(R.id.video_praise_img)
    public ImageView mPraiseImg;
    @BindView(R.id.video_praise_layout)
    RelativeLayout mVideoPraiseLayout;
    @BindView(R.id.video_praise_txt)
    TextView mPraiseTxt;
    @BindView(R.id.video_praise_anim_view)
    public LottieAnimationView mPraiseAnimView;
    @BindView(R.id.video_share_txt)
    TextView mVideoShareTxt;

    private boolean hasRequestVideoPath;

    private VideoPlayAdapter mAdapter;

    private VideoModel mVideoModel;

    public VideoPlayHolder(VideoPlayAdapter adapter, View itemView, CommonRecyclerAdapter.OnItemClickListener listener) {
        super(itemView, listener);
        ButterKnife.bind(this, itemView);
        mVideoModel = new VideoModel();
        mAdapter = adapter;
        mVideoView.setPlayErrorListener(this);
        hasRequestVideoPath = false;
    }

    /**
     * item刷新
     */
    public void bindVideoPlay(VideoPlayHolder holder, VideoBean bean, int position) {
        if (holder != null && bean != null && mAdapter != null) {
            holder.mVideoView.setHideProgress(false);
            holder.mVideoView.setRepeatMode(Player.REPEAT_MODE_ALL);
            holder.setHasRequestVideoPath(false);
            holder.mHeartLayout.setEnableDoubleClick(true);
            if (StringUtil.isNull(bean.play_url)) {
                holder.requestVideoPath(bean);
            } else {
                holder.mVideoView.setVideoPath(bean.play_url);
            }
            if (StringUtil.equals(bean.direction, VideoBean.VIDEO_DIRECTION_V)) {
                holder.mVideoView.setScaleType(ScaleType.CENTER_CROP);
                holder.mVideoView.setImageView(bean.img_url, ImageView.ScaleType.CENTER_CROP, true);
            } else {
                holder.mVideoView.setScaleType(ScaleType.FIT_CENTER);
                holder.mVideoView.setImageView(bean.img_url, ImageView.ScaleType.FIT_CENTER, true);
            }
            if (bean.stats != null) {
                if (bean.stats.praise > 0) {
                    holder.mPraiseTxt.setText(mAdapter.getFormatSize(bean.stats.praise));
                } else {
                    holder.mPraiseTxt.setText(mContext.getResources().getString(R.string.video_like_title));
                }
                holder.mPraiseImg.setImageResource(bean.stats.hasPraised() ?
                        R.drawable.home_icon_dianzan_selected : R.drawable.home_icon_dianzan_normal);
                if (bean.stats.share > 0) {
                    holder.mVideoShareTxt.setText(mAdapter.getFormatSize(bean.stats.share));
                } else {
                    holder.mVideoShareTxt.setText(R.string.share_title);
                }
            } else {
                holder.mPraiseTxt.setText(mContext.getResources().getString(R.string.video_like_title));
                holder.mVideoShareTxt.setText(R.string.share_title);
            }
            holder.mVideoTitleTxt.setText(bean.title);
            holder.mVideoShareTxt.setVisibility(StringUtil.isNull(bean.share_link) ? View.INVISIBLE : View.VISIBLE);
            mAdapter.getVideoPlayHolderList().add(holder);

            holder.mVideoShareTxt.setOnClickListener(new OnVideoShareClick(bean));
            holder.mVideoPraiseLayout.setOnClickListener(new OnVideoPraiseClick(bean));
            holder.mHeartLayout.setTouchListener(new OnVideoSingleDoubleClick(bean, holder));
        }
    }

    /**
     * item内部空间局部刷新，不影响整体播放
     */
    public void bindVideoPlay(VideoPlayHolder holder, VideoBean bean, int position, int payload) {
        if (holder != null && bean != null && mAdapter != null) {
            if (payload == FLAG_PRAISE_STATUS) {
                if (bean.stats != null) {
                    holder.mPraiseImg.setImageResource(bean.stats.hasPraised() ?
                            R.drawable.home_icon_dianzan_selected : R.drawable.home_icon_dianzan_normal);
                    if (bean.stats.praise > 0) {
                        holder.mPraiseTxt.setText(mAdapter.getFormatSize(bean.stats.praise));
                    } else {
                        holder.mPraiseTxt.setText(mContext.getResources().getString(R.string.video_like_title));
                    }
                }
            } else if (payload == FLAG_SHARE_STATUS) {
                if (bean.stats != null) {
                    if (bean.stats.share > 0) {
                        holder.mVideoShareTxt.setText(mAdapter.getFormatSize(bean.stats.share));
                    } else {
                        holder.mVideoShareTxt.setText(R.string.share_title);
                    }
                }
            }
        }
    }

    /**
     * 获取视频播放地址
     *
     * @param bean 数据
     */
    public void requestVideoPath(VideoBean bean) {
        Logger.d("Current video path is empty, so request new path now!");
        mVideoModel.requestVideoPath(bean.item_id, new SimpleHttpSubscriber<VideoPathBean>() {

            @Override
            public void onResponseSuccess(VideoPathBean pathBean) {
                if (pathBean != null && !StringUtil.isNull(pathBean.url)) {
                    bean.play_url = pathBean.url;
                    mVideoView.setVideoPath(pathBean.url);
                    if (mAdapter != null && mAdapter.getCurrentPlayPos() == getAdapterPosition()) {
                        Logger.d("Current video is new video path now, so play this video!");
                        if (mAdapter.getOperationListener() != null) {
                            mAdapter.getOperationListener().onVideoEnter(mAdapter.getCurrentPlayPos());
                        }
                    }
                }
            }
        });
    }

    public void setHasRequestVideoPath(boolean hasRequestVideoPath) {
        this.hasRequestVideoPath = hasRequestVideoPath;
    }

    @Override
    public void onVideoPathOverdue() {
        if (!hasRequestVideoPath && mAdapter != null) {
            hasRequestVideoPath = true;
            if (mAdapter.getContentList() != null && !mAdapter.getContentList().isEmpty()
                    && mAdapter.getCurrentPlayPos() >= 0
                    && mAdapter.getCurrentPlayPos() < mAdapter.getContentList().size()) {
                requestVideoPath(mAdapter.getContentList().get(mAdapter.getCurrentPlayPos()));
            }
        }
    }

    public void startVideo(VideoPlayHolder holder) {
        if (holder.mVideoView.getVideoUri() != null) {
            if (holder.mVideoView.getTag() == null
                    || !holder.mVideoView.getTag().equals(holder.mVideoView.getVideoUri().toString())) {
                holder.mVideoView.start();
            } else if (!holder.mVideoView.restart()) {
                holder.mVideoView.start();
            }
        }
    }

    public void stopVideo(VideoPlayHolder holder) {
        if (holder.mVideoView != null) {
            holder.mVideoView.setStateListener(null);
            if (holder.mVideoView.getVideoUri() != null) {
                holder.mVideoView.setTag(holder.mVideoView.getVideoUri().toString());
                holder.mVideoView.stopPlayback();
            }
        }
    }

    public void resumeVideo(VideoPlayHolder holder) {
        if (holder.mVideoView != null) {
            startVideo(holder);
        }
    }

    public void pauseVideo(VideoPlayHolder holder) {
        if (holder.mVideoView != null && holder.mVideoView.isPlaying()) {
            holder.mVideoView.pause();
        }
    }

    public void cancelVideoPathRequest() {
        if (mVideoModel != null) {
            mVideoModel.cancelVideoPathRequest();
        }
    }

    class OnVideoShareClick implements View.OnClickListener {

        private VideoBean mBean;

        public OnVideoShareClick(VideoBean bean) {
            mBean = bean;
        }

        @Override
        public void onClick(View v) {
            if (mAdapter != null && mAdapter.getOperationListener() != null) {
                mAdapter.getOperationListener().onVideoShareClick(mBean);
            }
        }
    }

    class OnVideoPraiseClick implements View.OnClickListener {

        private VideoBean mBean;

        public OnVideoPraiseClick(VideoBean bean) {
            mBean = bean;
        }

        @Override
        public void onClick(View v) {
            if (mAdapter != null && mAdapter.getOperationListener() != null) {
                mAdapter.getOperationListener().onVideoPraiseClick(mBean);
            }
        }
    }

    class OnVideoSingleDoubleClick implements HeartRelativeLayout.OnHeartTouchListener {

        private VideoBean mBean;

        private VideoPlayHolder mHolder;

        public OnVideoSingleDoubleClick(VideoBean bean, VideoPlayHolder holder) {
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
            if (mHolder != null && mHolder.mPraiseAnimView != null) {
                mHolder.mPraiseAnimView.playAnimation();
                if (mAdapter != null && mAdapter.getOperationListener() != null) {
                    mAdapter.getOperationListener().onVideoDoublePraiseClick(mBean);
                }
            }
        }
    }
}
