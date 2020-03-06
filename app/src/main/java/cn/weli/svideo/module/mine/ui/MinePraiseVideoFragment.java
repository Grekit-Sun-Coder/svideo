package cn.weli.svideo.module.mine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.widget.smartrefresh.WeRefreshRecyclerView;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.ui.AppBaseFragment;
import cn.weli.svideo.module.mine.component.adapter.VideoPraiseAdapter;
import cn.weli.svideo.module.mine.presenter.MinePraiseVideoPresenter;
import cn.weli.svideo.module.mine.view.IMinePraiseVideoView;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.video.VideoInfo;
import cn.weli.svideo.module.video.component.event.VideoCancelListEvent;
import cn.weli.svideo.module.video.component.event.VideoPraiseChangeEvent;
import cn.weli.svideo.module.video.model.bean.VideoBean;
import cn.weli.svideo.module.video.ui.VideoDetailActivity;
import cn.weli.svideo.module.video.view.IVideoPlayView;

/**
 * 我喜欢的视频
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see MinePraiseVideoFragment
 * @since [1.0.0]
 */
public class MinePraiseVideoFragment extends AppBaseFragment<MinePraiseVideoPresenter, IMinePraiseVideoView>
        implements IMinePraiseVideoView, OnLoadMoreListener, WeRefreshRecyclerView.OnNetErrorRefreshListener,
        CommonRecyclerAdapter.OnItemClickListener, VideoInfo.OnVideoSyncListener {

    @BindView(R.id.video_recycler_view)
    WeRefreshRecyclerView mRefreshRecyclerView;
    private View mFragmentView;
    private VideoPraiseAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater.inflate(R.layout.fragment_mine_praise_video, container, false);
            ButterKnife.bind(this, mFragmentView);
            RxBus.get().register(this);
            initView();
        } else {
            if (mFragmentView.getParent() != null) {
                ((ViewGroup) mFragmentView.getParent()).removeView(mFragmentView);
            }
        }
        return mFragmentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.get().unregister(this);
        VideoInfo.getInstance().removeSyncListener(this);
    }

    @Subscribe
    public void onVideoCancelListEvent(VideoCancelListEvent event) {
        if (isAdded() && getActivity() != null) {
            mPresenter.handleVideoCancelList(event.getCancelCollectList(), mAdapter.getContentList());
        }
    }

    @Subscribe
    public void onVideoPraiseChangeEvent(VideoPraiseChangeEvent event) {
        if (isAdded() && getActivity() != null) {
            mPresenter.setNeedRefresh(true);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        if (isAdded()) {
            mPresenter.requestCollectList(false, false);
        }
    }

    @Override
    public void finishLoadMore() {
        if (isAdded()) {
            mRefreshRecyclerView.finishLoadMore();
        }
    }

    @Override
    public void showEmptyView() {
        if (isAdded()) {
            mRefreshRecyclerView.setEmptyViewWithMargin(getString(R.string.mine_collect_video_empty_title),
                    R.dimen.common_len_200px);
        }
    }

    @Override
    public void handleNoLoadMoreData() {
        if (isAdded()) {
            mRefreshRecyclerView.setLoadMoreEnd();
        }
    }

    @Override
    public void handleVideoListRefresh(List<VideoBean> list) {
        if (isAdded()) {
            mRefreshRecyclerView.setContentShow();
            mAdapter.addItems(list);
        }
    }

    @Override
    public void handleVideoListAppend(List<VideoBean> list) {
        if (isAdded()) {
            mAdapter.appendItems(list);
        }
    }

    @Override
    public void clearList() {
        if (isAdded()) {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyCurrentList() {
        if (isAdded()) {
            mAdapter.notifyDataSetChanged();
            if (mAdapter.getContentList().isEmpty()) {
                showEmptyView();
            }
        }
    }

    @Override
    public void onNetErrorRefresh() {
        mRefreshRecyclerView.setContentShow();
        mPresenter.requestCollectList(true, true);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (isAdded() && mAdapter != null && position >= 0 && position < mAdapter.getItemCount()) {
            VideoBean bean = mAdapter.getContentList().get(position);
            if (StringUtil.equals(mAdapter.getContentList().get(position).feed_item_type, AdConfigBean.VIDEO_TYPE_POST)) {
                try {
                    Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
                    VideoInfo.getInstance().setCacheVideoList(mAdapter.getContentList());
                    intent.putExtra(IVideoPlayView.EXTRA_VIDEO_POSITION, position);
                    intent.putExtra(IVideoPlayView.EXTRA_VIDEO_PAGE, mPresenter.getCurrentPage());
                    intent.putExtra(IVideoPlayView.EXTRA_VIDEO_TYPE, IVideoPlayView.TYPE_VIDEO_COLLECT);
                    startActivity(intent);

                    StatisticsAgent.click(getActivity(), bean.item_id, StatisticsUtils.MD.MD_5);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
        }
    }

    @Override
    public void onVideoListAppend(int videoType, List<VideoBean> list, int currentPage) {
        if (isAdded() && videoType == IVideoPlayView.TYPE_VIDEO_COLLECT) {
            Logger.d("VideoPraiseChangeEvent list append change, video type is [" + videoType + "]");
            mAdapter.appendItems(list);
            mPresenter.setCurrentPage(currentPage);
        }
    }

    @Override
    public void onVideoPositionChange(int videoType, int position) {
        if (isAdded() && videoType == IVideoPlayView.TYPE_VIDEO_COLLECT) {
            Logger.d("Video position change, video type is [" + videoType + "]");
            mGridLayoutManager.scrollToPosition(position);
            mGridLayoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    public void onVideoPraiseChange(int videoType, int postId, int hasPraise, long praise) {
    }

    /**
     * 登陆成功
     */
    public void onLoginSuccess() {
        if (isAdded()) {
            mPresenter.requestCollectList(true, true);
        }
    }

    /**
     * 退出登陆
     */
    public void onLogout() {
        if (isAdded()) {
            clearList();
        }
    }

    /**
     * 页面展示
     */
    public void onFragmentShow() {
        if (mAdapter != null && mPresenter != null) {
            mPresenter.handlePageShow(mAdapter.getContentList());
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        mRefreshRecyclerView.setEnableRefresh(false);
        mRefreshRecyclerView.setEnableLoadMore(true);
        mRefreshRecyclerView.setOnLoadMoreListener(this);
        mRefreshRecyclerView.setEnableAutoLoadMore(true);
        mRefreshRecyclerView.setEnableOverScrollDrag(false);
        mRefreshRecyclerView.setErrorRefreshListener(this);
        mAdapter = new VideoPraiseAdapter(getActivity());
        mAdapter.setOnItemClickListener(this);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRefreshRecyclerView.getRecyclerView().setLayoutManager(mGridLayoutManager);
        mRefreshRecyclerView.getRecyclerView().setAdapter(mAdapter);
        VideoInfo.getInstance().addSyncListener(this);
        mPresenter.initPraiseList();
        mPresenter.requestCollectList(true, true);
    }

    @Override
    protected Class<MinePraiseVideoPresenter> getPresenterClass() {
        return MinePraiseVideoPresenter.class;
    }

    @Override
    protected Class<IMinePraiseVideoView> getViewClass() {
        return IMinePraiseVideoView.class;
    }
}
