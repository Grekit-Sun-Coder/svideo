package cn.weli.svideo.module.task.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerViewHolder;
import cn.weli.svideo.baselib.helper.glide.ILoader;
import cn.weli.svideo.baselib.helper.glide.WeImageLoader;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.common.widget.GradientColorTextView;
import cn.weli.svideo.module.task.model.bean.TaskBean;

/**
 * 任务列表adapter
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class TaskListAdapter extends CommonRecyclerAdapter<TaskBean> {

    public TaskListAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TaskHolder(mLayoutInflater.inflate(R.layout.item_task_view, parent, false), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindView((TaskHolder) holder, getContentList().get(position));
    }

    private void bindView(TaskHolder holder, TaskBean bean) {
        if (holder != null && bean != null) {
            holder.mTaskTitleTxt.setText(bean.name);
            holder.mTaskContentTxt.setText(bean.desc);
            if (StringUtil.equals(bean.task_status, TaskBean.STATUS_FINISHED)) {
                holder.mTaskActionTxt.setText(R.string.task_done_title);
            } else {
                holder.mTaskActionTxt.setText(StringUtil.isNull(bean.button) ?
                        mContext.getString(R.string.task_do_title) : bean.button);
            }
            holder.mTaskProfitTxt.setText(bean.reward);
            if (bean.times_limit > 1) {
                holder.mTaskProgressTxt.setVisibility(View.VISIBLE);
                holder.mTaskProgressTxt.setText(mContext.getString(R.string.task_do_progress_title,
                        String.valueOf(bean.finish_times), String.valueOf(bean.times_limit)));
            } else {
                holder.mTaskProgressTxt.setVisibility(View.GONE);
            }
            WeImageLoader.getInstance().load(mContext, holder.mTaskImg, bean.icon, ILoader.Options.defaultOptions());

            try {
                JSONObject object = new JSONObject();
                object.put("key", bean.key);
                StatisticsAgent.view(mContext, StatisticsUtils.CID.CID_3, StatisticsUtils.MD.MD_2, "", object.toString());
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }

        }
    }

    class TaskHolder extends CommonRecyclerViewHolder {

        @BindView(R.id.task_img)
        ImageView mTaskImg;
        @BindView(R.id.task_title_txt)
        TextView mTaskTitleTxt;
        @BindView(R.id.task_progress_txt)
        TextView mTaskProgressTxt;
        @BindView(R.id.task_profit_txt)
        TextView mTaskProfitTxt;
        @BindView(R.id.task_action_txt)
        GradientColorTextView mTaskActionTxt;
        @BindView(R.id.task_content_txt)
        TextView mTaskContentTxt;

        public TaskHolder(View itemView, OnItemClickListener listener) {
            super(itemView, listener);
            ButterKnife.bind(this, itemView);
            mTaskActionTxt.setSelected(true);
            mTaskProfitTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
        }
    }
}
