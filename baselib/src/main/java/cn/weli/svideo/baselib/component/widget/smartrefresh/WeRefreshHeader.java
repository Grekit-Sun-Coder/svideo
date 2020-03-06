package cn.weli.svideo.baselib.component.widget.smartrefresh;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.internal.InternalAbstract;

import cn.weli.svideo.baselib.R;
import cn.weli.svideo.baselib.component.widget.loading.RefreshLoadingView;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 刷新头部
 *
 * @author Lei Jiang
 * @version [7.5.9]
 * @date 2019/7/18
 * @see [class/method]
 * @since [7.5.9]
 */
public class WeRefreshHeader extends InternalAbstract implements RefreshHeader {

    private RefreshLoadingView mRefreshLoadingView;

    private TextView mTitleTxt;

    private String mPullStr;

    private String mLoadingStr;

    private String mReleaseStr;

    private String mFinishStr;

    public WeRefreshHeader(Context context) {
        this(context, null);
    }

    public WeRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_refresh_header, this);
        mRefreshLoadingView = view.findViewById(R.id.refresh_view);
        mTitleTxt = view.findViewById(R.id.refresh_txt);
        mRefreshLoadingView.setContext(context);
        mPullStr = context.getString(R.string.pull_to_refresh_pull_label_down);
        mLoadingStr = context.getString(R.string.pull_to_refresh_refreshing_label);
        mReleaseStr = context.getString(R.string.pull_to_refresh_release_down);
        mFinishStr = context.getString(R.string.pull_to_refresh_finish_label);
    }

    public void setLabels(String releaseLabel, String pullLabel, String refreshingLabel) {
        if (!StringUtil.isNull(pullLabel)) {
            mPullStr = pullLabel;
        }
        if (!StringUtil.isNull(refreshingLabel)) {
            mLoadingStr = refreshingLabel;
        }
        if (!StringUtil.isNull(releaseLabel)) {
            mReleaseStr = releaseLabel;
        }
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                mTitleTxt.setText(mPullStr);
                break;
            case RefreshReleased:
                mTitleTxt.setText(mLoadingStr);
                break;
            case ReleaseToRefresh:
                mTitleTxt.setText(mReleaseStr);
                break;
            case ReleaseToTwoLevel:
                mTitleTxt.setText(mPullStr);
                break;
            case Loading:
                break;
            case RefreshFinish:
                mTitleTxt.setText(mFinishStr);
                break;
            default:
                break;
        }
    }
}
