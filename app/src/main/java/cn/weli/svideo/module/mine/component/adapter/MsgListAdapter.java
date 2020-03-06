package cn.weli.svideo.module.mine.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerViewHolder;
import cn.weli.svideo.baselib.helper.glide.ILoader;
import cn.weli.svideo.baselib.helper.glide.WeImageLoader;
import cn.weli.svideo.baselib.utils.TimeUtil;
import cn.weli.svideo.module.mine.model.bean.MsgBean;

/**
 * 消息列表adapter
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class MsgListAdapter extends CommonRecyclerAdapter<MsgBean> {

    public MsgListAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MsgHolder(mLayoutInflater.inflate(R.layout.item_msg_list, parent, false), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindView((MsgHolder) holder, getContentList().get(position));
    }

    private void bindView(MsgHolder holder, MsgBean bean) {
        if (holder != null && bean != null) {
            holder.mMsgTitleTxt.setText(bean.title);
            holder.mMsgTimeTxt.setText(TimeUtil.milliseconds2String(bean.create_time, TimeUtil.TIME_FORMAT_WITHOUT_YEAR));
            holder.mMsgContentTxt.setText(bean.content);
            WeImageLoader.getInstance().load(mContext, holder.mMsgIconImg, bean.action_url,
                    new ILoader.Options(R.drawable.xiaoxi_icon_xitong, R.drawable.xiaoxi_icon_xitong));
        }
    }

    class MsgHolder extends CommonRecyclerViewHolder {

        @BindView(R.id.msg_icon_img)
        ImageView mMsgIconImg;
        @BindView(R.id.msg_title_txt)
        TextView mMsgTitleTxt;
        @BindView(R.id.msg_time_txt)
        TextView mMsgTimeTxt;
        @BindView(R.id.msg_content_txt)
        TextView mMsgContentTxt;

        public MsgHolder(View itemView, OnItemClickListener listener) {
            super(itemView, listener);
            ButterKnife.bind(this, itemView);
        }
    }
}
