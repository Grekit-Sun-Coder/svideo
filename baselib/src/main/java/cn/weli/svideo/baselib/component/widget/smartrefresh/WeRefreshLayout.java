package cn.weli.svideo.baselib.component.widget.smartrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import cn.weli.svideo.baselib.R;

/**
 * 下拉刷新，上拉加载ViewGroup
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see WeRefreshRecyclerView
 * @since [1.0.0]
 */
public class WeRefreshLayout extends SmartRefreshLayout {

    private static final float FLAG_DRAG_RATE = 0.7f;

    private Context mContext;

    public WeRefreshLayout(Context context) {
        this(context, null);
    }

    public WeRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;
        // header
        addHeader();

        // footer
        addFooter();
        setPrimaryColorsId(R.color.color_white);
        setDragRate(FLAG_DRAG_RATE);
        setEnableOverScrollDrag(true);
        setEnableOverScrollBounce(false);
        setEnableLoadMore(false);
        setEnableAutoLoadMore(false);
    }

    /**
     * 添加头部
     */
    private void addHeader() {
        WeRefreshHeader header = new WeRefreshHeader(mContext);
        setRefreshHeader(header, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * 结束加载更多
     */
    public void setLoadMoreEnd() {
        super.setNoMoreData(true);
    }

    /**
     * 添加头部
     */
    private void addFooter() {
        WeLoadMoreFooter footer = new WeLoadMoreFooter(mContext);
        footer.setTextSizeTitle(15);
        footer.setDrawableArrowSize(14);
        footer.setDrawableProgressSize(16);
        footer.setFinishDuration(50);
        setRefreshFooter(footer, ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.common_len_100px));
    }
}
