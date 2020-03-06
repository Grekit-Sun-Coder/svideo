package cn.weli.svideo.module.mine.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.widget.smartrefresh.WeRefreshRecyclerView;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.mine.component.adapter.MsgListAdapter;
import cn.weli.svideo.module.mine.model.bean.MsgBean;
import cn.weli.svideo.module.mine.presenter.MsgPresenter;
import cn.weli.svideo.module.mine.view.IMsgView;

/**
 * 消息列表
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see MinePageFragment
 * @since [1.0.0]
 */
public class MsgActivity extends AppBaseActivity<MsgPresenter, IMsgView> implements IMsgView, WeRefreshRecyclerView.OnNetErrorRefreshListener, OnRefreshListener, OnLoadMoreListener, CommonRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.msg_recycler_view)
    WeRefreshRecyclerView mMsgRecyclerView;

    private MsgListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mPresenter.getMsgList(false, true);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mPresenter.getMsgList(false, false);
    }

    @Override
    public void showErrorView() {
        mMsgRecyclerView.setErrorView();
    }

    @Override
    public void showEmptyView() {
        mMsgRecyclerView.setEmptyView(getString(R.string.msg_empty_title));
    }

    @Override
    public void handleNoLoadMoreData() {
        mMsgRecyclerView.setLoadMoreEnd();
    }

    @Override
    public void handleListRefresh(List<MsgBean> list) {
        mMsgRecyclerView.setContentShow();
        mAdapter.addItems(list);
    }

    @Override
    public void handleListAppend(List<MsgBean> list) {
        mAdapter.appendItems(list);
    }

    @Override
    public void finishRefreshLoadMore() {
        mMsgRecyclerView.finishRefresh();
        mMsgRecyclerView.finishLoadMore();
    }

    @Override
    public void onNetErrorRefresh() {
        mMsgRecyclerView.setContentShow();
        mPresenter.getMsgList(true, true);
    }

    @Override
    public void onItemClick(View view, int position) {
        startProtocol(mAdapter.getContentList().get(position).action_url);
    }

    /**
     * 初始化
     */
    private void initView() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(R.string.msg_title);
        mMsgRecyclerView.setEnableRefresh(true);
        mMsgRecyclerView.setEnableLoadMore(true);
        mMsgRecyclerView.setOnRefreshListener(this);
        mMsgRecyclerView.setOnLoadMoreListener(this);
        mMsgRecyclerView.setErrorRefreshListener(this);
        mMsgRecyclerView.setEnableAutoLoadMore(true);
        mMsgRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MsgListAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mMsgRecyclerView.getRecyclerView().setAdapter(mAdapter);
        mPresenter.getMsgList(true, true);
    }

    @Override
    protected Class<MsgPresenter> getPresenterClass() {
        return MsgPresenter.class;
    }

    @Override
    protected Class<IMsgView> getViewClass() {
        return IMsgView.class;
    }
}
