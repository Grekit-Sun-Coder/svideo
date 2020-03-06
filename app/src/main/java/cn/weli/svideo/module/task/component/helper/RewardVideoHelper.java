package cn.weli.svideo.module.task.component.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * 激励视频回调监听帮助类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-03
 * @see cn.weli.svideo.module.task.ui.RewardVideoActivity
 * @since [1.0.0]
 */
public class RewardVideoHelper {

    private static RewardVideoHelper sInstance;

    private List<OnRewardVideoListener> mListenerList = new ArrayList<>();

    public static RewardVideoHelper getInstance() {
        if (sInstance == null) {
            synchronized (RewardVideoHelper.class) {
                if (sInstance == null) {
                    sInstance = new RewardVideoHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 添加一个激励视频监听
     *
     * @param syncListener 监听
     */
    public void addRewardVideoListener(OnRewardVideoListener syncListener) {
        if (mListenerList == null) {
            mListenerList = new ArrayList<>();
        }
        mListenerList.add(syncListener);
    }

    /**
     * 移除一个激励视频监听
     *
     * @param syncListener 监听
     */
    public void removeRewardVideoListener(OnRewardVideoListener syncListener) {
        if (mListenerList == null || mListenerList.isEmpty()) {
            return;
        }
        mListenerList.remove(syncListener);
    }

    /**
     * 激励视频开始
     *
     * @param from     来源
     * @param taskType 任务类型
     */
    public void onRewardVideoStart(String from, String taskType) {
        if (mListenerList == null || mListenerList.isEmpty()) {
            return;
        }
        for (OnRewardVideoListener listener : mListenerList) {
            if (listener != null) {
                listener.onRewardVideoStart(from, taskType);
            }
        }
    }

    /**
     * 激励视频结束
     *
     * @param from     来源
     * @param taskType 任务类型
     */
    public void onRewardVideoComplete(String from, String taskType) {
        if (mListenerList == null || mListenerList.isEmpty()) {
            return;
        }
        for (OnRewardVideoListener listener : mListenerList) {
            if (listener != null) {
                listener.onRewardVideoComplete(from, taskType);
            }
        }
    }

    /**
     * 移除所有监听
     */
    public void removeAllListener() {
        if (mListenerList == null || mListenerList.isEmpty()) {
            return;
        }
        mListenerList.clear();
        mListenerList = null;
    }

    /**
     * 激励视频播放回调
     */
    public interface OnRewardVideoListener {

        /**
         * 激励视频播放开始回调
         *
         * @param from    来源
         * @param taskKey 任务key
         */
        void onRewardVideoStart(String from, String taskKey);

        /**
         * 激励视频播放完成回调
         *
         * @param from    来源
         * @param taskKey 任务key
         */
        void onRewardVideoComplete(String from, String taskKey);
    }
}
