package cn.weli.svideo.module.mine.presenter;

import java.util.List;

import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.module.mine.view.ICashDetailView;
import cn.weli.svideo.module.task.model.WithdrawModel;
import cn.weli.svideo.module.task.model.bean.FlowDetailBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class CashDetailPresenter implements IPresenter {

    /**
     * 默认第一页
     */
    private static final int FLAG_FIRST_PAGE = 1;
    /**
     * 一页数量
     */
    private static final int FLAG_PAGE_SIZE = 20;
    /**
     * 流水类型
     */
    private static final String FLAG_TYPE = "MONEY";

    private ICashDetailView mView;

    private WithdrawModel mModel;
    /**
     * 当前页
     */
    private int mCurrentPage;
    /**
     * 是否还有更多
     */
    private boolean hasMore = true;

    public CashDetailPresenter(ICashDetailView view) {
        mView = view;
        mModel = new WithdrawModel();
    }

    /**
     * 请求流水
     *
     * @param isFirst   是否第一次加载
     * @param isRefresh 是否是刷新
     */
    public void requestFlowList(boolean isFirst, boolean isRefresh) {
        if (isRefresh) {
            hasMore = true;
            mCurrentPage = FLAG_FIRST_PAGE;
        }
        if (!hasMore) {
            return;
        }
        mModel.getFlowDetailList(FLAG_TYPE, mCurrentPage, FLAG_PAGE_SIZE, new HttpSubscriber<List<FlowDetailBean>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onNetworkUnavailable() {
                if (isFirst) {
                    mView.showErrorView();
                }
            }

            @Override
            public void onResponseSuccess(List<FlowDetailBean> list) {
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
                        mView.handleListRefresh(list);
                    } else {
                        mView.handleListAppend(list);
                    }
                }
            }

            @Override
            public void onResponseError(String desc, String code) {
                mView.showToast(desc);
                if (isFirst) {
                    mView.showErrorView();
                }
            }

            @Override
            public void onNetworkError() {
                mView.showNetworkError();
                if (isFirst) {
                    mView.showErrorView();
                }
            }

            @Override
            public void onPostExecute() {
                mView.finishRefreshLoadMore();
            }
        });
    }

    @Override
    public void clear() {
        mModel.cancelFlowRequest();
    }
}
