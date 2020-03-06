package cn.weli.svideo.baselib.component.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.weli.svideo.baselib.R;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see cn.weli.svideo.baselib.component.widget.smartrefresh.WeRefreshRecyclerView
 * @since [1.0.0]
 */
public class WeEmptyErrorView extends LinearLayout implements View.OnClickListener {

    private static final int FLAG_EMPTY_STATE = 0;

    private static final int FLAG_ERROR_STATE = 1;

    private Context mContext;

    private ImageView mStatusImg;

    private TextView mStatusTxt;

    private LinearLayout mParentLayout;

    /**
     * 当前状态
     */
    private int mCurrentState = -1;

    public OnEmptyErrorRefreshListener mOnRefreshListener;

    public interface OnEmptyErrorRefreshListener {
        void onEmptyErrorRefresh();
    }

    public void setOnEmptyErrorRefreshListener(OnEmptyErrorRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public WeEmptyErrorView(Context context) {
        this(context, null);
    }

    public WeEmptyErrorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeEmptyErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_empty_error_view, this);
        mStatusImg = view.findViewById(R.id.status_img);
        mStatusTxt = view.findViewById(R.id.status_txt);
        mParentLayout = view.findViewById(R.id.parent_layout);
        setOnClickListener(this);
    }

    /**
     * 设置成无数据
     */
    public void setEmptyState(String content) {
        if (mCurrentState == FLAG_EMPTY_STATE) {
            return;
        }
        mStatusImg.setImageResource(R.drawable.kong_img_nothing);
        mStatusTxt.setText(content);
        mCurrentState = FLAG_EMPTY_STATE;
    }

    /**
     * 设置成错误
     */
    public void setErrorView(boolean netError) {
        if (mCurrentState == FLAG_ERROR_STATE) {
            return;
        }
        if (netError) {
            setNetUnAvailableState();
        } else {
            setNetErrorState();
        }
    }

    /**
     * 设置成无网络
     */
    public void setNetUnAvailableState() {
        if (mCurrentState == FLAG_ERROR_STATE) {
            return;
        }
        mStatusImg.setImageResource(R.drawable.kong_img_network);
        mStatusTxt.setText(R.string.common_str_network_unavailable);
        mCurrentState = FLAG_ERROR_STATE;
    }

    /**
     * 设置成无网络
     */
    public void setNetErrorState() {
        if (mCurrentState == FLAG_ERROR_STATE) {
            return;
        }
        mStatusImg.setImageResource(R.drawable.kong_img_network);
        mStatusTxt.setText(R.string.common_str_network_error);
        mCurrentState = FLAG_ERROR_STATE;
    }

    /**
     * 设置EmptyErrorView距离顶部高度
     *
     * @param dimenId 距离资源id
     */
    public void setTopMargin(int dimenId) {
        mParentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams lp = (LayoutParams) mStatusImg.getLayoutParams();
        lp.setMargins(0, mContext.getResources().getDimensionPixelSize(dimenId), 0, 0);
        mStatusImg.setLayoutParams(lp);
    }

    public void setStatusImg(int resId) {
        mStatusImg.setImageResource(resId);
    }

    public void setTxtTopMargin(int dimenId) {
        LayoutParams lp = (LayoutParams) mStatusTxt.getLayoutParams();
        lp.setMargins(0, mContext.getResources().getDimensionPixelSize(dimenId), 0, 0);
        mStatusTxt.setLayoutParams(lp);
    }

    @Override
    public void onClick(View view) {
        if (mOnRefreshListener != null) {
            mOnRefreshListener.onEmptyErrorRefresh();
        }
    }

    public boolean getEmptyErrorState() {
        return mCurrentState == FLAG_EMPTY_STATE || mCurrentState == FLAG_ERROR_STATE;
    }
}

