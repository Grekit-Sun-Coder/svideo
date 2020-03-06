package cn.weli.svideo.module.mine.presenter;

import java.util.List;

import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.module.mine.model.MsgModel;
import cn.weli.svideo.module.mine.model.bean.MsgBean;
import cn.weli.svideo.module.mine.model.bean.MsgResponseBean;
import cn.weli.svideo.module.mine.view.IMsgView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see cn.weli.svideo.module.mine.ui.MsgActivity
 * @since [1.0.0]
 */
public class MsgPresenter implements IPresenter {

    private IMsgView mView;

    private MsgModel mModel;

    private String mLastTime;

    private boolean hasMore = true;

    public MsgPresenter(IMsgView view) {
        mView = view;
        mModel = new MsgModel();
    }

    /**
     * 请求消息列表
     *
     * @param isFirst 是否首次
     * @param refresh 是否是刷新
     */
    public void getMsgList(boolean isFirst, boolean refresh) {
        if (refresh) {
            hasMore = true;
            mLastTime = String.valueOf(System.currentTimeMillis());
        }
        if (!hasMore) {
            return;
        }
        mModel.getMsgList(mLastTime, new HttpSubscriber<MsgResponseBean>() {
            @Override
            public void onPreExecute() {
                if (isFirst) {
                    mView.showLoadView();
                }
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
                if (isFirst) {
                    mView.showErrorView();
                }
            }

            @Override
            public void onResponseSuccess(MsgResponseBean bean) {
                if (bean != null) {
                    List<MsgBean> list = bean.data_list;
                    if (list == null || list.isEmpty()) {
                        if (isFirst) {
                            mView.showEmptyView();
                        }
                        mView.handleNoLoadMoreData();
                    } else {
                        mLastTime = bean.last_read_time;
                        hasMore = true;
                        if (refresh) {
                            mView.handleListRefresh(list);
                        } else {
                            mView.handleListAppend(list);
                        }
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
                if (isFirst) {
                    mView.finishLoadView();
                }
                mView.finishRefreshLoadMore();
            }
        });
    }

    @Override
    public void clear() {
        mModel.cancelMsgList();
    }
}
