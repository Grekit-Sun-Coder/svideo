package cn.weli.svideo.baselib.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用recyclerview adapter
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @see CommonRecyclerAdapter
 * @since [1.0.0]
 */
public class CommonRecyclerAdapter <T> extends RecyclerView.Adapter {

    public Context mContext;

    public LayoutInflater mLayoutInflater;

    public OnItemClickListener mItemClickListener;

    public OnItemLongClickListener mItemLongClickListener;

    private List<T> mItems = new ArrayList<>();

    public CommonRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    /**
     * 设置Item点击监听
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    public void addItems(List<? extends T> list) {
        if (list == null) {
            return;
        }
        mItems.clear();
        mItems.addAll(list);
        notifyDataSetChanged();
    }

    public void appendItems(List<T> list) {
        int lastPosition = mItems.size();
        if (list == null) {
            return;
        }
        mItems.addAll(list);
        notifyItemRangeInserted(lastPosition, list.size());
    }

    public void appendItemsNotify(List<T> list) {
        if (list == null) {
            return;
        }
        mItems.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(int index, T data) {
        if (data == null || index < 0 || index > mItems.size()) {
            return;
        }
        mItems.add(index, data);
    }

    public void remove(int index) {
        if (index > mItems.size() || index < 0) {
            return;
        }
        mItems.remove(index);
    }

    public void setContentList(List<T> list) {
        if (list == null) {
            return;
        }
        mItems = list;
    }

    public void clear() {
        mItems.clear();
    }

    public List<T> getContentList() {
        return mItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public Context getContext() {
        return mContext;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
}

