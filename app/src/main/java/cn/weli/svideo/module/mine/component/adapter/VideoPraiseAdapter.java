package cn.weli.svideo.module.mine.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerViewHolder;
import cn.weli.svideo.baselib.helper.glide.ILoader;
import cn.weli.svideo.baselib.helper.glide.WeImageLoader;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.module.mine.ui.MinePraiseVideoFragment;
import cn.weli.svideo.module.video.model.bean.VideoBean;

/**
 * 视频点赞列表
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see MinePraiseVideoFragment
 * @since [1.0.0]
 */
public class VideoPraiseAdapter extends CommonRecyclerAdapter<VideoBean> {

    private int mItemHeight;

    public VideoPraiseAdapter(Context context) {
        super(context);
        mItemHeight = (int) ((DensityUtil.getInstance().getScreenWidth() - context.getResources().getDimensionPixelSize(R.dimen.common_len_90px)) * 0.67f);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PraiseVideoHolder(mLayoutInflater.inflate(R.layout.item_video_praise_view, parent, false), mItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        bindView((PraiseVideoHolder) holder, getContentList().get(position), position);
    }

    private void bindView(PraiseVideoHolder holder, VideoBean bean, int position) {
        if (holder != null && bean != null) {
            holder.mVideoTitleTxt.setText(bean.title);
            WeImageLoader.getInstance().load(mContext, holder.mVideoCoverImg, bean.img_url,
                    new ILoader.Options(R.drawable.img_moren_black, R.drawable.img_moren_black));

            StatisticsAgent.view(mContext, bean.item_id, StatisticsUtils.MD.MD_5);
        }
    }

    class PraiseVideoHolder extends CommonRecyclerViewHolder {

        @BindView(R.id.video_cover_img)
        ImageView mVideoCoverImg;
        @BindView(R.id.video_title_txt)
        TextView mVideoTitleTxt;

        public PraiseVideoHolder(View itemView, OnItemClickListener listener) {
            super(itemView, listener);
            ButterKnife.bind(this, itemView);
            try {
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                lp.width = RecyclerView.LayoutParams.MATCH_PARENT;
                lp.height = mItemHeight;
                mVideoCoverImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }
}
