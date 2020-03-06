package cn.weli.svideo.baselib.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 通用recyclerview item holder
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @see CommonRecyclerViewHolder
 * @since [1.0.0]
 */
public class CommonRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

    public Context mContext;

    public CommonRecyclerAdapter.OnItemClickListener mClickListener;

    public CommonRecyclerAdapter.OnItemLongClickListener mLongClickListener;

    public CommonRecyclerViewHolder(View itemView, CommonRecyclerAdapter.OnItemClickListener listener) {
        super(itemView);
        this.mClickListener = listener;
        mContext = itemView.getContext();
        itemView.setOnClickListener(new BaseCustomClickListener() {
            @Override
            protected void onSingleClick(View v) {
                if (mClickListener != null && getAdapterPosition() >= 0) {
                    mClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        });
    }

    public CommonRecyclerViewHolder(View itemView,
                                    CommonRecyclerAdapter.OnItemClickListener clickListener,
                                    CommonRecyclerAdapter.OnItemLongClickListener longClickListener) {
        super(itemView);
        mContext = itemView.getContext();
        mClickListener = clickListener;
        mLongClickListener = longClickListener;
        itemView.setOnClickListener(new BaseCustomClickListener() {
            @Override
            protected void onSingleClick(View v) {
                if (mClickListener != null && getAdapterPosition() >= 0) {
                    mClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        });
        itemView.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {
        if (mLongClickListener != null && getAdapterPosition() >= 0) {
            mLongClickListener.onItemLongClick(view, getAdapterPosition());
        }
        return true;
    }
}

