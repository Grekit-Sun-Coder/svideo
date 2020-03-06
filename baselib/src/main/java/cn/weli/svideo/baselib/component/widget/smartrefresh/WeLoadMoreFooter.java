package cn.weli.svideo.baselib.component.widget.smartrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

import cn.weli.svideo.baselib.R;

/**
 * 加载更多
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see WeRefreshRecyclerView
 * @since [1.0.0]
 */
public class WeLoadMoreFooter extends ClassicsFooter {

    public WeLoadMoreFooter(Context context) {
        this(context, null);
    }

    public WeLoadMoreFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeLoadMoreFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        REFRESH_FOOTER_NOTHING = context.getString(R.string.no_more_data);
        REFRESH_FOOTER_LOADING = context.getString(R.string.pull_to_refresh_load_more_label);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (noMoreData) {
            mProgressView.setVisibility(View.GONE);
        }
        return super.setNoMoreData(noMoreData);
    }
}
