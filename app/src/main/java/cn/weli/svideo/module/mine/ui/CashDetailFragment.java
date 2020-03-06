package cn.weli.svideo.module.mine.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.widget.smartrefresh.WeRefreshRecyclerView;
import cn.weli.svideo.common.ui.AppBaseFragment;
import cn.weli.svideo.module.mine.component.adapter.FlowDetailAdapter;
import cn.weli.svideo.module.mine.presenter.CashDetailPresenter;
import cn.weli.svideo.module.mine.view.ICashDetailView;
import cn.weli.svideo.module.task.model.bean.FlowDetailBean;

/**
 * 现金明细
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class CashDetailFragment extends AppBaseFragment<CashDetailPresenter, ICashDetailView> implements
        ICashDetailView, OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.cash_recycler_view)
    WeRefreshRecyclerView mRefreshRecyclerView;
    private View mFragmentView;
    private FlowDetailAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_cash_detail, container, false);
            ButterKnife.bind(this, mFragmentView);
            initView();
        } else {
            if (mFragmentView.getParent() != null) {
                ((ViewGroup) mFragmentView.getParent()).removeView(mFragmentView);
            }
        }
        return mFragmentView;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (isAdded()) {
            mPresenter.requestFlowList(false, true);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (isAdded()) {
            mRefreshRecyclerView.getRecyclerView().stopScroll();
            mPresenter.requestFlowList(false, false);
        }
    }

    @Override
    public void showErrorView() {
        if (isAdded()) {
            mRefreshRecyclerView.setErrorView(R.dimen.common_len_200px);
        }
    }

    @Override
    public void showEmptyView() {
        if (isAdded()) {
            mRefreshRecyclerView.setEmptyViewWithMargin(getString(R.string.profit_empty_title), R.dimen.common_len_200px);
        }
    }

    @Override
    public void handleNoLoadMoreData() {
        if (isAdded()) {
            mRefreshRecyclerView.setLoadMoreEnd();
        }
    }

    @Override
    public void handleListRefresh(List<FlowDetailBean> list) {
        if (isAdded()) {
            mRefreshRecyclerView.setContentShow();
            mAdapter.addItems(list);
        }
    }

    @Override
    public void handleListAppend(List<FlowDetailBean> list) {
        if (isAdded()) {
            mAdapter.appendItems(list);
        }
    }

    @Override
    public void finishRefreshLoadMore() {
        if (isAdded()) {
            mRefreshRecyclerView.finishRefresh();
            mRefreshRecyclerView.finishLoadMore();
        }
    }

    @Override
    public void onLazyLoad() {
        mPresenter.requestFlowList(true, true);
    }

    /**
     * 初始化
     */
    private void initView() {
        mRefreshRecyclerView.setEnableRefresh(false);
        mRefreshRecyclerView.setEnableLoadMore(true);
        mRefreshRecyclerView.setOnRefreshListener(this);
        mRefreshRecyclerView.setOnLoadMoreListener(this);
        mRefreshRecyclerView.setEnableAutoLoadMore(true);
        mRefreshRecyclerView.setEnableOverScrollDrag(false);
        mRefreshRecyclerView.setEnableLoadMoreWhenContentNotFull(false);
        mRefreshRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FlowDetailAdapter(getActivity());
        mRefreshRecyclerView.getRecyclerView().setAdapter(mAdapter);
    }

    @Override
    protected Class<CashDetailPresenter> getPresenterClass() {
        return CashDetailPresenter.class;
    }

    @Override
    protected Class<ICashDetailView> getViewClass() {
        return ICashDetailView.class;
    }
}
