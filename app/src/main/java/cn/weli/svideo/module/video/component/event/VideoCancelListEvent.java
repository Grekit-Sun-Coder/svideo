package cn.weli.svideo.module.video.component.event;

import java.util.ArrayList;
import java.util.List;

import cn.weli.svideo.module.video.model.bean.VideoBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class VideoCancelListEvent {

    private List<VideoBean> mCancelCollectList = new ArrayList<>();

    public VideoCancelListEvent(List<VideoBean> cancelCollectList) {
        mCancelCollectList = cancelCollectList;
    }

    public List<VideoBean> getCancelCollectList() {
        return mCancelCollectList;
    }
}
