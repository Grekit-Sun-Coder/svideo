package cn.weli.svideo.module.mine.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerViewHolder;
import cn.weli.svideo.baselib.utils.TimeUtil;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.module.task.model.bean.FlowDetailBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class FlowDetailAdapter extends CommonRecyclerAdapter<FlowDetailBean> {

    public static final int TYPE_COIN = 0;
    public static final int TYPE_CASH = 1;

    private int mType;

    public FlowDetailAdapter(Context context) {
        super(context);
    }

    public void setType(int type) {
        mType = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FlowHolder(mLayoutInflater.inflate(R.layout.item_flow_detail, parent, false), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindView((FlowHolder) holder, getContentList().get(position));
    }

    private void bindView(FlowHolder holder, FlowDetailBean bean) {
        if (holder != null && bean != null) {
            holder.mFlowTitleTxt.setText(bean.title);
            holder.mFlowTimeTxt.setText(TimeUtil.milliseconds2String(bean.time, TimeUtil.TIME_FORMAT_MINUTE));
            if (mType == TYPE_COIN) {
                if (bean.type == FlowDetailBean.TYPE_ADD) {
                    holder.mFlowNumTxt.setText(mContext.getString(R.string.profit_add_title, bean.amount));
                    holder.mFlowPlusTxt.setText(mContext.getString(R.string.common_str_plus));
                    holder.mFlowPlusTxt.setTextColor(ContextCompat.getColor(mContext, R.color.color_80_DF343B));
                    holder.mFlowNumTxt.setTextColor(ContextCompat.getColor(mContext, R.color.color_80_DF343B));
                } else if (bean.type == FlowDetailBean.TYPE_REMOVE) {
                    holder.mFlowNumTxt.setText(mContext.getString(R.string.profit_remove_title, bean.amount));
                    holder.mFlowPlusTxt.setText(mContext.getString(R.string.common_str_minus));
                    holder.mFlowPlusTxt.setTextColor(ContextCompat.getColor(mContext, R.color.color_80_65973C));
                    holder.mFlowNumTxt.setTextColor(ContextCompat.getColor(mContext, R.color.color_80_65973C));
                }
            } else if (mType == TYPE_CASH) {
                if (bean.type == FlowDetailBean.TYPE_ADD) {
                    holder.mFlowNumTxt.setText(mContext.getString(R.string.profit_money_add_title, bean.amount));
                    holder.mFlowPlusTxt.setText(mContext.getString(R.string.common_str_plus));
                    holder.mFlowPlusTxt.setTextColor(ContextCompat.getColor(mContext, R.color.color_80_DF343B));
                    holder.mFlowNumTxt.setTextColor(ContextCompat.getColor(mContext, R.color.color_80_DF343B));
                } else if (bean.type == FlowDetailBean.TYPE_REMOVE) {
                    holder.mFlowNumTxt.setText(mContext.getString(R.string.profit_money_remove_title, bean.amount));
                    holder.mFlowPlusTxt.setText(mContext.getString(R.string.common_str_minus));
                    holder.mFlowPlusTxt.setTextColor(ContextCompat.getColor(mContext, R.color.color_80_65973C));
                    holder.mFlowNumTxt.setTextColor(ContextCompat.getColor(mContext, R.color.color_80_65973C));
                }
            }
        }
    }

    class FlowHolder extends CommonRecyclerViewHolder {

        @BindView(R.id.flow_title_txt)
        TextView mFlowTitleTxt;
        @BindView(R.id.flow_time_txt)
        TextView mFlowTimeTxt;
        @BindView(R.id.flow_num_txt)
        TextView mFlowNumTxt;
        @BindView(R.id.flow_plus_txt)
        TextView mFlowPlusTxt;

        public FlowHolder(View itemView, OnItemClickListener listener) {
            super(itemView, listener);
            ButterKnife.bind(this, itemView);
            try {
                mFlowNumTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }
}
