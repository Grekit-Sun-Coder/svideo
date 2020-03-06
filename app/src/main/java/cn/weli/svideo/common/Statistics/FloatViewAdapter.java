package cn.weli.svideo.common.Statistics;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

import cn.weli.svideo.R;

public class FloatViewAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private LinkedList<EventDataBean> mDataList;
    private StringBuilder sb = new StringBuilder();

    public FloatViewAdapter(Context context, LinkedList<EventDataBean> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_window_float_view, parent, false);
        return new TongjiViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TongjiViewHolder tj = (TongjiViewHolder) holder;
        tj.setContent(mDataList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    public class TongjiViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_content;

        public TongjiViewHolder(View itemView) {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
        }

        public void setContent(EventDataBean item) {
            sb.delete(0, sb.length());
            sb.append(item.event_type + "->");
            sb.append("c_id:" + item.content_id);
            sb.append("  md:" + item.module);
            if (!TextUtils.isEmpty(item.position)) {
                sb.append("  pos:" + item.position);
            }
            if (!TextUtils.isEmpty(item.args)) {
                sb.append("  args:" + item.args);
            }
            tv_content.setText(sb.toString());
        }
    }


}
