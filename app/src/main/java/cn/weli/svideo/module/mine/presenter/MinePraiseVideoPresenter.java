package cn.weli.svideo.module.mine.presenter;

import java.util.ArrayList;
import java.util.List;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.module.mine.view.IMinePraiseVideoView;
import cn.weli.svideo.module.video.model.VideoModel;
import cn.weli.svideo.module.video.model.bean.VideoBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class MinePraiseVideoPresenter implements IPresenter {

    /**
     * 默认第一页
     */
    private static final int FLAG_FIRST_PAGE = 1;

    private IMinePraiseVideoView mView;

    private VideoModel mModel;

    private int mCurrentPage;
    /**
     * 是否还有更多
     */
    private boolean hasMore = true;

    /**
     * 是否点赞变更
     */
    private boolean isNeedRefresh;

    public MinePraiseVideoPresenter(IMinePraiseVideoView view) {
        mView = view;
        mModel = new VideoModel();
    }

    /**
     * 初始化
     */
    public void initPraiseList() {
        List<VideoBean> list = mModel.getCachePraiseList();
        if (list != null && !list.isEmpty()) {
            mView.handleVideoListRefresh(list);
        }
    }

    /**
     * 请求收藏列表
     *
     * @param isFirst   是否第一次
     * @param isRefresh 是否是刷新
     */
    public void requestCollectList(boolean isFirst, boolean isRefresh) {
        if (isRefresh) {
            hasMore = true;
            mCurrentPage = FLAG_FIRST_PAGE;
        }
        if (!hasMore) {
            return;
        }
        mModel.requestVideoPraiseList(mCurrentPage, new HttpSubscriber<List<VideoBean>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
                if (isFirst) {
                    mView.showEmptyView();
                }
            }

            @Override
            public void onResponseSuccess(List<VideoBean> list) {
                if (list == null || list.isEmpty()) {
                    if (isFirst) {
                        mView.showEmptyView();
                    }
                    hasMore = false;
                    mView.handleNoLoadMoreData();
                } else {
                    mCurrentPage++;
                    hasMore = true;
                    if (isRefresh) {
                        mView.handleVideoListRefresh(list);
                        mModel.saveCachePraiseList((ArrayList<VideoBean>) list);
                    } else {
                        mView.handleVideoListAppend(list);
                    }
                }
            }

            @Override
            public void onResponseError(String desc, String code) {
                mView.showToast(desc);
                if (isFirst) {
                    mView.showEmptyView();
                }
            }

            @Override
            public void onNetworkError() {
                mView.showNetworkError();
                if (isFirst) {
                    mView.showEmptyView();
                }
            }

            @Override
            public void onPostExecute() {
                mView.finishLoadMore();
            }
        });
    }

    /**
     * 处理取消的视频列表
     *
     * @param cancelList 取消的列表
     */
    public void handleVideoCancelList(List<VideoBean> cancelList, List<VideoBean> currentList) {
        if (cancelList != null && !cancelList.isEmpty()) {
            List<VideoBean> removeList = new ArrayList<>();
            for (VideoBean cancelBean : cancelList) {
                for (VideoBean videoBean : currentList) {
                    if (cancelBean.item_id == videoBean.item_id) {
                        removeList.add(videoBean);
                        break;
                    }
                }
            }
            if (!removeList.isEmpty()) {
                currentList.removeAll(removeList);
                mView.notifyCurrentList();
            }
        }
    }

    /**
     * 处理页面展示
     *
     * @param contentList 当前列表
     */
    public void handlePageShow(List<VideoBean> contentList) {
        if (contentList == null || contentList.isEmpty() || isNeedRefresh) {
            if (WlVideoAppInfo.getInstance().hasLogin()) {
                requestCollectList(true, true);
                isNeedRefresh = false;
            }
        }
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int page) {
        mCurrentPage = page;
    }

    public void setNeedRefresh(boolean needRefresh) {
        Logger.d("Video praise status changed, so need refresh");
        isNeedRefresh = needRefresh;
    }

    @Override
    public void clear() {
        mModel.cancelVideoPraiseListRequest();
    }
}
