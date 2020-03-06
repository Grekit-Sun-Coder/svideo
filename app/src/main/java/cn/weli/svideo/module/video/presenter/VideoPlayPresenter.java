package cn.weli.svideo.module.video.presenter;

import com.hwangjr.rxbus.RxBus;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.http.HttpUtils;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.task.model.TaskModel;
import cn.weli.svideo.module.task.model.bean.VideoTaskBean;
import cn.weli.svideo.module.video.VideoInfo;
import cn.weli.svideo.module.video.component.event.VideoCancelListEvent;
import cn.weli.svideo.module.video.component.event.VideoPraiseChangeEvent;
import cn.weli.svideo.module.video.model.VideoModel;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import cn.weli.svideo.module.video.ui.VideoPlayFragment;
import cn.weli.svideo.module.video.view.IVideoPlayView;
import cn.weli.svideo.utils.NumberUtil;

/**
 * 视频首页 P层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see VideoPlayFragment
 * @since [1.0.0]
 */
public class VideoPlayPresenter implements IPresenter {

    /**
     * 默认第一页
     */
    private static final int FLAG_FIRST_PAGE = 1;
    /**
     * 默认需要提前预加载的数量
     */
    private static final int FLAG_NEED_REQUEST_SIZE = 4;
    /**
     * 分享人数的随机数最小值
     */
    private static final int FLAG_MIN_PEO = 100;
    /**
     * 分享人数的随机数最小值
     */
    private static final int FLAG_MAX_PEO = 2000;

    private IVideoPlayView mView;

    private VideoModel mModel;

    private TaskModel mTaskModel;
    /**
     * 当前页码
     */
    private int mCurrentPage = FLAG_FIRST_PAGE;
    /**
     * 是否还有更多视频数据
     */
    private boolean hasMore = true;
    /**
     * 视频列表类型
     */
    private int mVideoType;
    /**
     * 视频推荐id
     */
    private String mVideoItemId;
    /**
     * 取消收藏的列表
     */
    private List<VideoBean> mCancelCollectList = new ArrayList<>();
    /**
     * 视频任务
     */
    private VideoTaskBean mTaskBean;

    public VideoPlayPresenter(IVideoPlayView view) {
        mView = view;
        mModel = new VideoModel();
        mTaskModel = new TaskModel();
    }

    /**
     * 初始化视频
     */
    public void initVideo(int type, int pos, int page, String itemId) {
        mVideoType = type;
        mVideoItemId = itemId;
        Logger.d("VideoPraiseChangeEvent play init : type=[" + mVideoType + "] pos=[" + pos
                + "] page=[" + page + "]");
        if (IVideoPlayView.TYPE_VIDEO_HOME == mVideoType) {
            mView.showBottomCoverImg();
            List<VideoBean> list = WlVideoAppInfo.getInstance().getPreVideoList();
            if (list != null && !list.isEmpty()) {
                mCurrentPage++;
                hasMore = true;
                mView.handleVideoListRefresh(list);
            } else {
                requestVideoList(true, false);
            }
            checkVideoTask();
        } else if (IVideoPlayView.TYPE_VIDEO_COLLECT == mVideoType) {
            List<VideoBean> list = VideoInfo.getInstance().getCacheVideoList();
            if (list != null && !list.isEmpty()) {
                mCurrentPage = page;
                mView.showReadyVideoList(list, pos);
            }
        }
    }

    /**
     * 处理视频任务
     */
    public void checkVideoTask() {
        if (WlVideoAppInfo.getInstance().hasLogin()) {
            requestVideoTask();
        } else {
            mView.setVideoTaskTipLogin();
        }
    }

    /**
     * 处理fragment页面展示
     *
     * @param hide 是否对用户可见
     */
    public void handleUserVisible(boolean hide) {
        if (hide) {
            mView.handleFragmentHide();
        } else {
            mView.handleFragmentShow();
        }
    }

    /**
     * 请求视频列表
     *
     * @param refresh   是否是刷新
     * @param isSilence 是否是静默
     */
    public void requestVideoList(boolean refresh, boolean isSilence) {
        if (refresh) {
            mCurrentPage = FLAG_FIRST_PAGE;
            hasMore = true;
        }
        if (!hasMore && !isSilence) {
            Logger.d("VideoPraiseChangeEvent load more end and not silence");
            mView.handleNoLoadMoreData();
            mView.finishLoadMore();
            return;
        } else if (!hasMore) {
            Logger.d("VideoPraiseChangeEvent load more end and is silence, so not load more");
            return;
        }
        mModel.requestVideoList(getVideoItemId(), new HttpSubscriber<List<VideoBean>>() {
            @Override
            public void onPreExecute() {
                if (!isSilence) {
                    mView.showLoadView();
                }
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
                if (refresh) {
                    mView.showErrorView();
                }
            }

            @Override
            public void onResponseSuccess(List<VideoBean> list) {
                if (list != null) {
                    if (list.isEmpty()) {
                        if (mCurrentPage == FLAG_FIRST_PAGE) {
                            mView.showEmptyView();
                        }
                        hasMore = false;
                        mView.handleNoLoadMoreData();
                    } else {
                        hasMore = true;
                        mCurrentPage++;
                        if (refresh) {
                            mView.handleVideoListRefresh(list);
                        } else {
                            mView.handleVideoListAppend(list);
                        }
                        // 列表同步
                        handleVideoListAppend(mVideoType, list);
                    }
                }
            }

            @Override
            public void onResponseError(String desc, String code) {
                mView.showToast(desc);
                if (refresh) {
                    mView.showEmptyView();
                }
            }

            @Override
            public void onNetworkError() {
                mView.showNetworkError();
                if (refresh) {
                    mView.showEmptyView();
                }
            }

            @Override
            public void onPostExecute() {
                if (!isSilence) {
                    mView.finishLoadView();
                }
                if (!refresh) {
                    mView.finishLoadMore();
                }
            }
        });
    }

    /**
     * 请求点赞列表
     */
    private void requestPraiseList() {
        if (!hasMore) {
            return;
        }
        mModel.requestVideoPraiseList(mCurrentPage, new SimpleHttpSubscriber<List<VideoBean>>() {

            @Override
            public void onResponseSuccess(List<VideoBean> list) {
                hasMore = (list == null || list.isEmpty());
                if (list != null) {
                    if (list.isEmpty()) {
                        if (mCurrentPage == FLAG_FIRST_PAGE) {
                            mView.showEmptyView();
                        }
                        hasMore = false;
                        mView.handleNoLoadMoreData();
                    } else {
                        mCurrentPage++;
                        mView.handleVideoListAppend(list);
                        // 列表同步
                        handleVideoListAppend(mVideoType, list);
                    }
                }
            }
        });
    }

    /**
     * 获取任务
     */
    public void requestVideoTask() {
        mTaskModel.getVideoTask(TaskModel.KEY_WATCH_VIDEO,
                new SimpleHttpSubscriber<VideoTaskBean>() {

                    @Override
                    public void onResponseSuccess(VideoTaskBean bean) {
                        if (bean != null) {
                            mTaskBean = bean;
                            boolean hasMoreTask = !StringUtil.isNull(bean.task_id);
                            boolean isLastTask = false;
                            if (hasMoreTask) {
                                isLastTask = (bean.cycle_current == bean.cycle_total - 1);
                            }
                            mView.initVideoTaskInfo(bean, hasMoreTask, isLastTask);
                        }
                    }
                });
    }

    /**
     * 提交任务
     */
    public void requestSubmitVideoTask() {
        if (mTaskBean == null) {
            return;
        }
        if (StringUtil.isNull(mTaskBean.task_id)) {
            Logger.d("video task id is null, so not submit");
            return;
        }
        mTaskModel.submitVideoTask(TaskModel.KEY_WATCH_VIDEO, mTaskBean.task_id,
                mTaskBean.task_timestamp,
                new SimpleHttpSubscriber<VideoTaskBean>() {

                    @Override
                    public void onResponseSuccess(VideoTaskBean bean) {
                        if (bean != null) {
                            mTaskBean = bean;
                            boolean hasMoreTask = !StringUtil.isNull(bean.task_id);
                            if (hasMoreTask) {
                                mView.setVideoTaskInfo(bean);
                            }
                            boolean isLastTask = false;
                            if (hasMoreTask) {
                                isLastTask = (bean.cycle_current == bean.cycle_total - 1);
                            }
                            mView.resetVideoProgress(mTaskBean.coin,
                                    bean.cycle_current == 0 && bean.cycle_total != 0,
                                    !StringUtil.isNull(bean.task_id), isLastTask);
                        }
                    }

                    @Override
                    public void onResponseError(String s, String s1) {
                        if (mTaskBean != null) {
                            mView.resetVideoProgress(mTaskBean.coin, false, false, false);
                        }
                    }

                    @Override
                    public void onNetworkError() {
                        if (mTaskBean != null) {
                            mView.resetVideoProgress(mTaskBean.coin, false, false, false);
                        }
                    }
                });
    }

    /**
     * 检查任务完成金币到账提示状态
     */
    public void checkTaskCoinTipStatus(boolean hasShow) {
        if (!hasShow) {
            mView.showTaskCoinTip();
        }
    }

    /**
     * 检查当前播放位置
     *
     * @param itemCount       总个数
     * @param currentPosition 当前位置
     */
    public void checkCurrentPlayPosition(int itemCount, int currentPosition) {
        if (itemCount - currentPosition < FLAG_NEED_REQUEST_SIZE) {
            Logger.d("Check position, need to load more video list");
            if (IVideoPlayView.TYPE_VIDEO_COLLECT == mVideoType) {
                requestPraiseList();
            } else if (IVideoPlayView.TYPE_VIDEO_HOME == mVideoType) {
                requestVideoList(false, true);
            }
        }
    }

    /**
     * 处理当前位置更新
     *
     * @param currentPosition 位置
     */
    public void handleCurrentPositionSync(int currentPosition) {
        if (mVideoType == IVideoPlayView.TYPE_VIDEO_COLLECT) {
            VideoInfo.getInstance().onVideoPositionChange(mVideoType, currentPosition);
            if (!mCancelCollectList.isEmpty()) {
                Logger.d("collect video list cancel is not empty, so send event!");
                RxBus.get().post(new VideoCancelListEvent(mCancelCollectList));
            }
        }
    }

    /**
     * 处理视频列表添加，只有视频列表跳转过来的需要同步
     *
     * @param videoType 视频类型
     * @param list      视频列表
     */
    private void handleVideoListAppend(int videoType, List<VideoBean> list) {
        if (IVideoPlayView.TYPE_VIDEO_COLLECT == mVideoType) {
            VideoInfo.getInstance().onVideoListAppend(videoType, list, mCurrentPage);
        }
    }

    /**
     * 处理视频分享
     *
     * @param bean     视频
     * @param platform 平台
     */
    public void handleVideoShare(VideoBean bean, String platform) {
        Logger.d("share title is [" + bean.title + "], platform is [" + platform + "]");
        if (StringUtil.equals(platform, BusinessConstants.ShareType.REPORT)) {
            // 举报
            mModel.requestVideoReport(String.valueOf(bean.item_id));
            mView.handleReportSuccess();
        } else if (StringUtil.equals(platform, BusinessConstants.ShareType.NOT_INTERESTED)) {
            // 不感兴趣
            mView.handleDeleteCurrentVideo();
        } else if (!StringUtil.isNull(bean.share_link)) {
            String count = NumberUtil.getRandom(FLAG_MIN_PEO, FLAG_MAX_PEO);
            mView.handleShare(handleVideoShareUrl(bean.share_link, count), bean.title, count, bean.img_url, platform);
            mModel.requestVideoShare(String.valueOf(bean.item_id));
            if (bean.stats != null) {
                bean.stats.share++;
                mView.notifyCurrentVideoShare();
            }
        }
    }

    /**
     * 处理视频点赞
     *
     * @param bean 视频
     */
    public void handleVideoPraise(VideoBean bean) {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            mView.startToLogin();
            return;
        }
        if (bean != null && bean.stats != null) {
            if (bean.stats.hasPraised()) {
                // 点过赞了，则去掉点赞
                bean.stats.has_praise = VideoBean.Stats.HAS_NOT_PRAISE;
                bean.stats.praise--;
                mModel.requestVideoUnPraise(bean.item_id, new SimpleHttpSubscriber<>());
                mView.handleVideoUnPraise();
                handleCollectVideoStatus(bean, true);
            } else {
                // 没有点赞，则去点赞
                bean.stats.has_praise = VideoBean.Stats.HAS_PRAISE;
                bean.stats.praise++;
                mModel.requestVideoPraise(bean.item_id, new SimpleHttpSubscriber<>());
                mView.handleVideoPraise();
                handleCollectVideoStatus(bean, false);
            }
            if (bean.stats.praise < 0) {
                bean.stats.praise = 0;
            }
            mView.notifyCurrentVideoPraise();
        }
    }

    /**
     * 处理视频双击点赞，没有点赞的触发点赞，点赞过的，不处理
     *
     * @param bean 视频
     */
    public void handleVideoDoublePraise(VideoBean bean) {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            return;
        }
        if (bean != null && bean.stats != null) {
            if (!bean.stats.hasPraised()) {
                handleVideoPraise(bean);
            }
        }
    }

    /**
     * 处理收藏的视频状态
     *
     * @param bean   当前视频
     * @param cancel 是否是取消收藏了
     */
    private void handleCollectVideoStatus(VideoBean bean, boolean cancel) {
        if (mVideoType == IVideoPlayView.TYPE_VIDEO_COLLECT) {
            if (cancel) {
                Logger.d("handle collect video status, add a video collect");
                mCancelCollectList.add(bean);
            } else {
                VideoBean removeBean = null;
                for (VideoBean videoBean : mCancelCollectList) {
                    if (bean.item_id == videoBean.item_id) {
                        removeBean = videoBean;
                    }
                }
                if (removeBean != null) {
                    Logger.d("handle collect video status, remove a video collect");
                    mCancelCollectList.remove(removeBean);
                }
            }
        } else if (mVideoType == IVideoPlayView.TYPE_VIDEO_HOME) {
            RxBus.get().post(new VideoPraiseChangeEvent());
        }
    }

    /**
     * 获取视频类型
     *
     * @return 视频类型
     */
    public int getVideoType() {
        return mVideoType;
    }

    /**
     * 处理视频分享
     */
    private String handleVideoShareUrl(String url, String count) {
        Map<String, List<Object>> params = new HashMap<>();
        if (!StringUtil.isNull(url)) {
            List<Object> countList = new ArrayList<>();
            countList.add(count);
            params.put("view_count", countList);
            return URLDecoder.decode(HttpUtils.createUrlFromParams(url, params));
        } else {
            return url;
        }
    }

    /**
     * 获取视频id
     *
     * @return 视频id
     */
    private String getVideoItemId() {
        String itemId = StringUtil.EMPTY_STR;
        if (!StringUtil.isNull(mVideoItemId)) {
            itemId = mVideoItemId;
            mVideoItemId = StringUtil.EMPTY_STR;
        }
        return itemId;
    }

    @Override
    public void clear() {
        mModel.cancelVideoListRequest();
        mModel.cancelVideoPathRequest();
        mModel.cancelVideoPraiseRequest();
        mModel.cancelVideoReportRequest();
        mTaskModel.cancelObtainRequest();
        mTaskModel.cancelSubmitRequest();
    }
}
