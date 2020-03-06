package cn.weli.svideo.module.video.component.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.video.component.widget.videoholder.VideoKmAdHolder;
import cn.weli.svideo.module.video.component.widget.videoholder.VideoPlayHolder;
import cn.weli.svideo.module.video.component.widget.videoholder.VideoTtAdHolder;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import cn.weli.svideo.module.video.ui.VideoPlayFragment;
import cn.weli.svideo.utils.NumberUtil;

/**
 * 视频播放adapter
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-15
 * @see VideoPlayFragment
 * @since [1.0.0]
 */
public class VideoPlayAdapter extends CommonRecyclerAdapter<VideoBean> {

    private static final long MAX_SIZE = 9999;

    private static final int TYPE_VIDEO = 0x100;
    private static final int TYPE_TT_AD = 0x101;
    private static final int TYPE_KM_AD = 0x102;

    private List<VideoPlayHolder> mVideoPlayHolderList = new ArrayList<>();
    private List<VideoKmAdHolder> mVideoKmAdHolderList = new ArrayList<>();

    private VideoPlayHolder mVideoPlayHolder;
    private VideoTtAdHolder mVideoTtAdHolder;
    private VideoKmAdHolder mVideoKmAdHolder;

    private OnVideoOperationListener mOperationListener;
    /**
     * 当前播放的位置
     */
    private int mCurrentPlayPos;

    public VideoPlayAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemViewType(int position) {
        VideoBean bean = getContentList().get(position);
        if (bean != null) {
            String actionType = bean.feed_item_type;
            String source = StringUtil.EMPTY_STR;
            if (bean.ad_config != null) {
                source = bean.ad_config.source;
            }
            if (StringUtil.equals(actionType, AdConfigBean.VIDEO_TYPE_POST)) {
                return TYPE_VIDEO;
            } else if (StringUtil.equals(source, AdConfigBean.VIDEO_AD_TYPE_TT)
                || StringUtil.equals(source, AdConfigBean.VIDEO_AD_TYPE_GDT)
                || StringUtil.equals(source, AdConfigBean.VIDEO_AD_TYPE_BD)) {
                return TYPE_TT_AD;
            } else if (StringUtil.equals(source, AdConfigBean.VIDEO_AD_TYPE_KM)) {
                return TYPE_KM_AD;
            }
        }
        return TYPE_VIDEO;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TT_AD) {
            mVideoTtAdHolder = new VideoTtAdHolder(this, mLayoutInflater.inflate(R.layout.item_video_tt_ad_view, parent, false), mItemClickListener);
            return mVideoTtAdHolder;
        } else if (viewType == TYPE_KM_AD) {
            mVideoKmAdHolder = new VideoKmAdHolder(this, mLayoutInflater.inflate(R.layout.item_video_km_ad_view, parent, false), mItemClickListener);
            return mVideoKmAdHolder;
        } else {
            mVideoPlayHolder = new VideoPlayHolder(this, mLayoutInflater.inflate(R.layout.item_video_play_view, parent, false), mItemClickListener);
            return mVideoPlayHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        int viewType = getItemViewType(position);
        if (!payloads.isEmpty()) {
            if (viewType == TYPE_TT_AD) {
                if (mVideoTtAdHolder != null) {
                    mVideoTtAdHolder.bindAdVideo((VideoTtAdHolder) holder, getContentList().get(position), position);
                }
            } else if (viewType == TYPE_KM_AD) {
                if (mVideoKmAdHolder != null) {
                    mVideoKmAdHolder.bindKmAdVideo((VideoKmAdHolder) holder, getContentList().get(position), position, (Integer) payloads.get(0));
                }
            } else {
                if (mVideoPlayHolder != null) {
                    mVideoPlayHolder.bindVideoPlay((VideoPlayHolder) holder, getContentList().get(position), position, (Integer) payloads.get(0));
                }
            }
        } else {
            if (viewType == TYPE_TT_AD) {
                if (mVideoTtAdHolder != null) {
                    mVideoTtAdHolder.bindAdVideo((VideoTtAdHolder) holder, getContentList().get(position), position);
                }
            } else if (viewType == TYPE_KM_AD) {
                if (mVideoKmAdHolder != null) {
                    mVideoKmAdHolder.bindKmAdVideo((VideoKmAdHolder) holder, getContentList().get(position), position);
                }
            } else {
                if (mVideoPlayHolder != null) {
                    mVideoPlayHolder.bindVideoPlay((VideoPlayHolder) holder, getContentList().get(position), position);
                }
            }
        }
    }

    /**
     * 释放视频资源
     */
    public void releaseVideo() {
        try {
            for (VideoPlayHolder holder : mVideoPlayHolderList) {
                holder.mVideoView.stopPlayback();
                holder.mVideoView.release();
            }
            for (VideoKmAdHolder holder : mVideoKmAdHolderList) {
                holder.mVideoView.stopPlayback();
                holder.mVideoView.release();
            }
            if (mVideoPlayHolder != null) {
                mVideoPlayHolder.cancelVideoPathRequest();
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public String getFormatSize(long click) {
        if (click > MAX_SIZE) {
            return mContext.getResources().getString(R.string.video_count_title, NumberUtil.formatVideoNumber(click));
        }
        return String.valueOf(click);
    }

    public void setCurrentPlayPos(int currentPlayPos) {
        mCurrentPlayPos = currentPlayPos;
    }

    public int getCurrentPlayPos() {
        return mCurrentPlayPos;
    }

    public void setOperationListener(OnVideoOperationListener operationListener) {
        mOperationListener = operationListener;
    }

    public OnVideoOperationListener getOperationListener() {
        return mOperationListener;
    }

    public List<VideoPlayHolder> getVideoPlayHolderList() {
        if (mVideoPlayHolderList == null) {
            mVideoPlayHolderList = new ArrayList<>();
        }
        return mVideoPlayHolderList;
    }

    public List<VideoKmAdHolder> getVideoKmAdHolderList() {
        if (mVideoKmAdHolderList == null) {
            mVideoKmAdHolderList = new ArrayList<>();
        }
        return mVideoKmAdHolderList;
    }

    /**
     * 视频操作
     */
    public interface OnVideoOperationListener {

        /**
         * 分享
         *
         * @param bean 视频
         */
        void onVideoShareClick(VideoBean bean);

        /**
         * 点赞
         *
         * @param bean 视频
         */
        void onVideoPraiseClick(VideoBean bean);

        /**
         * 双击点赞
         */
        void onVideoDoublePraiseClick(VideoBean bean);

        /**
         * 头条广告视频暂停
         */
        void onTtVideoAdPause();

        /**
         * 头条视频广告暂停后继续播放
         */
        void onTtVideoAdPlay();

        /**
         * 广告点击
         */
        void onVideoAdClick();

        /**
         * 视频开始播放
         *
         * @param position 位置
         */
        void onVideoEnter(int position);
    }
}
