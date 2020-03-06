package cn.weli.svideo.module.video.component.widget.videoholder;

import android.view.View;

import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerViewHolder;

/**
 * 小视频base holder
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-12
 * @see cn.weli.svideo.module.video.component.adapter.VideoPlayAdapter
 * @since [1.0.0]
 */
public class VideoBaseHolder extends CommonRecyclerViewHolder {

    public VideoBaseHolder(View itemView, CommonRecyclerAdapter.OnItemClickListener listener) {
        super(itemView, listener);
    }
}
