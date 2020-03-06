package cn.weli.svideo.module.task.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerViewHolder;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.module.task.model.bean.WithdrawProBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class WithdrawProAdapter extends CommonRecyclerAdapter<WithdrawProBean> {

    private int mCurrentPosition = -1;

    public WithdrawProAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProHolder(mLayoutInflater.inflate(R.layout.item_withdraw_product, parent, false), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindView((ProHolder) holder, getContentList().get(position), position);
    }

    private void bindView(ProHolder holder, WithdrawProBean bean, int position) {
        if (holder != null && bean != null) {
            holder.mPriceTxt.setText(bean.actual_price);
            holder.mUnitTxt.setTextColor(ContextCompat.getColor(mContext, mCurrentPosition == position ? R.color.color_FFC244 : R.color.color_888888));
            holder.mPriceTxt.setTextColor(ContextCompat.getColor(mContext, mCurrentPosition == position ? R.color.color_FFC244 : R.color.color_888888));
            holder.mProLayout.setSelected(mCurrentPosition == position);
        }
    }

    public void setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
        notifyDataSetChanged();
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public WithdrawProBean getCurrentProBean() {
        if (getContentList() != null && !getContentList().isEmpty() && mCurrentPosition != -1 && mCurrentPosition < getContentList().size()) {
            return getContentList().get(mCurrentPosition);
        }
        return null;
    }

    class ProHolder extends CommonRecyclerViewHolder {

        @BindView(R.id.price_txt)
        TextView mPriceTxt;
        @BindView(R.id.unit_txt)
        TextView mUnitTxt;
        @BindView(R.id.pro_layout)
        LinearLayout mProLayout;

        public ProHolder(View itemView, OnItemClickListener listener) {
            super(itemView, listener);
            ButterKnife.bind(this, itemView);
            mPriceTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
            mUnitTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
        }
    }
}
