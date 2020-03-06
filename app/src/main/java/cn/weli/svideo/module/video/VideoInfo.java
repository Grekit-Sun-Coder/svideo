package cn.weli.svideo.module.video;

import java.util.ArrayList;
import java.util.List;

import cn.weli.svideo.module.video.model.bean.VideoBean;

/**
 * 视频信息类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see cn.weli.svideo.module.video.ui.VideoDetailActivity
 * @since [1.0.0]
 */
public class VideoInfo {

    public static VideoInfo sInstance;

    private List<VideoBean> mCacheVideoList = new ArrayList<>();

    public static VideoInfo getInstance() {
        if (sInstance == null) {
            synchronized (VideoInfo.class) {
                if (sInstance == null) {
                    sInstance = new VideoInfo();
                }
            }
        }
        return sInstance;
    }

    public List<VideoBean> getCacheVideoList() {
        return mCacheVideoList;
    }

    public void setCacheVideoList(List<VideoBean> cacheVideoList) {
        if (cacheVideoList != null) {
            mCacheVideoList.clear();
            if (!cacheVideoList.isEmpty()) {
                mCacheVideoList.addAll(cacheVideoList);
            }
        }
    }

    private List<OnVideoSyncListener> mSyncListeners = new ArrayList<>();

    public void addSyncListener(OnVideoSyncListener syncListener) {
        if (mSyncListeners == null) {
            mSyncListeners = new ArrayList<>();
        }
        mSyncListeners.add(syncListener);
    }

    public void removeSyncListener(OnVideoSyncListener syncListener) {
        if (mSyncListeners == null || mSyncListeners.isEmpty()) {
            return;
        }
        mSyncListeners.remove(syncListener);
    }

    public void onVideoListAppend(int videoType, List<VideoBean> list, int currentPage) {
        if (mSyncListeners == null || mSyncListeners.isEmpty()) {
            return;
        }
        for (OnVideoSyncListener listener : mSyncListeners) {
            if (listener != null) {
                listener.onVideoListAppend(videoType, list, currentPage);
            }
        }
    }

    public void onVideoPositionChange(int videoType, int position) {
        if (mSyncListeners == null || mSyncListeners.isEmpty()) {
            return;
        }
        for (OnVideoSyncListener listener : mSyncListeners) {
            if (listener != null) {
                listener.onVideoPositionChange(videoType, position);
            }
        }
    }

    public void onVideoPraiseChange(int videoType, int postId, int hasPraise, long praise) {
        if (mSyncListeners == null || mSyncListeners.isEmpty()) {
            return;
        }
        for (OnVideoSyncListener listener : mSyncListeners) {
            if (listener != null) {
                listener.onVideoPraiseChange(videoType, postId, hasPraise, praise);
            }
        }
    }

    public interface OnVideoSyncListener {

        void onVideoListAppend(int videoType, List<VideoBean> list, int currentPage);

        void onVideoPositionChange(int videoType, int position);

        void onVideoPraiseChange(int videoType, int postId, int hasPraise, long praise);
    }
}
