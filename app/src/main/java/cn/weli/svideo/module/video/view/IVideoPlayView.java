package cn.weli.svideo.module.video.view;

import java.util.List;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.task.model.bean.VideoTaskBean;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import cn.weli.svideo.module.video.ui.VideoPlayFragment;

/**
 * 视频首页 V层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see VideoPlayFragment
 * @since [1.0.0]
 */
public interface IVideoPlayView extends IBaseView {

    String EXTRA_VIDEO_POSITION = "video_position";

    String EXTRA_VIDEO_PAGE = "video_page";

    String EXTRA_VIDEO_TYPE = "video_type";

    String EXTRA_VIDEO_ID = "videoId";

    String RED_PKG_JSON = "redpkg.json";
    String COM_RIPPLE_JSON = "ripple.json";

    int PLAY_DELAY = 300;
    int TIP_GONE_DELAY = 5000;
    int TIP_SLIDE_DELAY = 6000;
    int TIP_REWARD_DELAY = 12000;

    int DEFAULT_AD_TIME = 30000;

    /**
     * 视频详情的位置-首页、收藏
     */
    int TYPE_VIDEO_HOME = 1;
    int TYPE_VIDEO_COLLECT = 2;

    /**
     * 没有更多数据
     */
    void handleNoLoadMoreData();

    /**
     * 视频列表数据
     *
     * @param list 数据
     */
    void handleVideoListRefresh(List<VideoBean> list);

    /**
     * 视频列表数据
     *
     * @param list 数据
     */
    void handleVideoListAppend(List<VideoBean> list);

    /**
     * 展示空页面
     */
    void showEmptyView();

    /**
     * 展示无网络
     */
    void showErrorView();

    /**
     * 结束加载更多
     */
    void finishLoadMore();

    /**
     * 展示
     */
    void handleFragmentShow();

    /**
     * 隐藏
     */
    void handleFragmentHide();

    /**
     * 取消点赞
     */
    void handleVideoUnPraise();

    /**
     * 点赞
     */
    void handleVideoPraise();

    /**
     * 刷新列表数据
     */
    void notifyCurrentVideoPraise();

    /**
     * 刷新列表数据
     */
    void notifyCurrentVideoShare();

    /**
     * 展示现成的列表
     *
     * @param list 列表数据
     * @param pos  当前位置
     */
    void showReadyVideoList(List<VideoBean> list, int pos);

    /**
     * 设置底部圆角覆盖展示
     */
    void showBottomCoverImg();

    /**
     * 初始化视频任务信息
     */
    void initVideoTaskInfo(VideoTaskBean bean, boolean hasMoreTask, boolean isLastTask);

    /**
     * 重置视频进度
     *
     * @param coin        获得的金币
     * @param isBigCoin   是否是锦鲤大将
     * @param hasMoreTask 是否还有下一个任务
     */
    void resetVideoProgress(int coin, boolean isBigCoin, boolean hasMoreTask, boolean isLastTask);

    /**
     * 设置视频任务信息
     *
     * @param bean 视频任务信息
     */
    void setVideoTaskInfo(VideoTaskBean bean);

    /**
     * 设置视频任务要登陆
     */
    void setVideoTaskTipLogin();

    /**
     * 展示金币到账提示
     */
    void showTaskCoinTip();

    /**
     * 分享
     *
     * @param url      分享地址
     * @param title    分享标题
     * @param random   随机数
     * @param imgUrl   封面
     * @param platform 平台
     */
    void handleShare(String url, String title, String random, String imgUrl, String platform);

    /**
     * 提示举报成功
     */
    void handleReportSuccess();

    /**
     * 移除当前item
     */
    void handleDeleteCurrentVideo();
}
