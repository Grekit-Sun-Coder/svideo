package cn.weli.svideo.baselib.component.widget.smartrefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import cn.weli.svideo.baselib.R;
import cn.weli.svideo.baselib.component.widget.WeEmptyErrorView;

/**
 * 下拉刷新，上拉加载RecyclerView
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see WeRefreshRecyclerView
 * @since [1.0.0]
 */
public class WeRefreshRecyclerView extends SmartRefreshLayout implements WeEmptyErrorView.OnEmptyErrorRefreshListener {

    private Context mContext;

    private WeRefreshHeader mWeRefreshHeader;

    private WeEmptyErrorView mEmptyErrorView;

    private RecyclerView mRecyclerView;

    private OnNetErrorRefreshListener mErrorRefreshListener;

    public interface OnNetErrorRefreshListener {

        /**
         * 网络错误时刷新
         */
        void onNetErrorRefresh();

    }

    public WeRefreshRecyclerView(Context context) {
        this(context, null);
    }

    public WeRefreshRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeRefreshRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 设置网络错误页面
     */
    public void setErrorView() {
        finishRefresh();
        mRecyclerView.setVisibility(View.GONE);
        mEmptyErrorView.setVisibility(View.VISIBLE);
        mEmptyErrorView.setNetUnAvailableState();
    }

    /**
     * 设置网络错误页面
     */
    public void setErrorView(int marginRes) {
        finishRefresh();
        mRecyclerView.setVisibility(View.GONE);
        mEmptyErrorView.setVisibility(View.VISIBLE);
        mEmptyErrorView.setNetUnAvailableState();
        mEmptyErrorView.setTopMargin(marginRes);
    }

    /**
     * 设置空页面
     *
     * @param emptyStr 空页面文案
     */
    public void setEmptyView(String emptyStr) {
        finishRefresh();
        mRecyclerView.setVisibility(View.GONE);
        mEmptyErrorView.setVisibility(View.VISIBLE);
        mEmptyErrorView.setEmptyState(emptyStr);
    }

    /**
     * 设置空页面
     *
     * @param emptyStr 空页面文案
     */
    public void setEmptyViewWithMargin(String emptyStr, int marginRes) {
        finishRefresh();
        mRecyclerView.setVisibility(View.GONE);
        mEmptyErrorView.setVisibility(View.VISIBLE);
        mEmptyErrorView.setEmptyState(emptyStr);
        mEmptyErrorView.setTopMargin(marginRes);
    }

    /**
     * 设置空页面
     *
     * @param emptyStr 空页面文案
     */
    public void setEmptyView(String emptyStr, int color) {
        finishRefresh();
        mRecyclerView.setVisibility(View.GONE);
        mEmptyErrorView.setVisibility(View.VISIBLE);
        mEmptyErrorView.setEmptyState(emptyStr);
        mEmptyErrorView.setBackgroundColor(color);
    }

    /**
     * 设置内容显示
     */
    public void setContentShow() {
        finishRefresh();
        super.setNoMoreData(false);
        setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyErrorView.setVisibility(View.GONE);
    }

    /**
     * 结束加载更多
     */
    public void setLoadMoreEnd() {
        super.setNoMoreData(true);
    }

    /**
     * 设置头的刷新文案
     *
     * @param releaseLabel
     * @param pullLabel
     * @param refreshingLabel
     */
    public void setWeRefreshHeader(String releaseLabel, String pullLabel, String refreshingLabel) {
        if (mWeRefreshHeader != null) {
            mWeRefreshHeader.setLabels(releaseLabel, pullLabel, refreshingLabel);
        }
    }

    @Override
    public void onEmptyErrorRefresh() {
        if (mErrorRefreshListener != null) {
            mErrorRefreshListener.onNetErrorRefresh();
        }
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_we_refresh_recycler_view, null);
        mEmptyErrorView = contentView.findViewById(R.id.refresh_empty_error_view);
        mEmptyErrorView.setOnEmptyErrorRefreshListener(this);
        mRecyclerView = contentView.findViewById(R.id.refresh_recycler_view);
        // header
        addHeader();
        // content
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(contentView, lp);
        // footer
        addFooter();

        setPrimaryColorsId(R.color.color_white);
        setEnableOverScrollDrag(true);
        setEnableOverScrollBounce(false);
        setEnableAutoLoadMore(false);
        setEnableLoadMoreWhenContentNotFull(false);
    }

    /**
     * 添加头部
     */
    private void addHeader() {
        mWeRefreshHeader = new WeRefreshHeader(mContext);
        setRefreshHeader(mWeRefreshHeader, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 添加头部
     */
    private void addFooter() {
        WeLoadMoreFooter footer = new WeLoadMoreFooter(mContext);
        footer.setTextSizeTitle(14);
        footer.setDrawableArrowSize(14);
        footer.setDrawableProgressSize(16);
        footer.setFinishDuration(50);
        setRefreshFooter(footer, ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.common_len_100px));
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setErrorRefreshListener(OnNetErrorRefreshListener errorRefreshListener) {
        mErrorRefreshListener = errorRefreshListener;
    }
}
