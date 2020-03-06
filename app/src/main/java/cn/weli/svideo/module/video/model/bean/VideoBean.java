package cn.weli.svideo.module.video.model.bean;

import java.io.Serializable;

import cn.weli.svideo.advert.bean.BaseDrawVideoBean;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAdData;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.task.model.bean.AdDownloadTaskBean;
import cn.weli.svideo.module.task.model.bean.AdJumpTaskBean;

/**
 * 视频流数据
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see [class/method]
 * @since [1.0.0]
 */
public class VideoBean implements Serializable {

    /**
     * 视频横竖
     */
    public static final String VIDEO_DIRECTION_H = "H";
    public static final String VIDEO_DIRECTION_V = "V";

    public Stats stats;

    public AdConfigBean ad_config;

    public AdJumpTaskBean ad_jump_task;
    public AdDownloadTaskBean ad_down_task;

    public int close_enable;

    public Object content_model;

    public String direction;

    public String feed_item_type = AdConfigBean.VIDEO_TYPE_POST;

    public String img_url;

    public long item_id;

    public String play_url;

    public String share_link;

    public String source;

    public String source_name;

    public String title;

    public BaseDrawVideoBean mDrawVideoAd;

    public ETKuaiMaAdData mETKuaiMaAdData;

    public static class Stats implements Serializable {

        public static final int HAS_NOT_PRAISE = 0;
        public static final int HAS_PRAISE = 1;

        public long click;

        public long comment;

        public long praise;

        public int has_praise;

        public long share;

        public boolean hasPraised() {
            return has_praise == HAS_PRAISE;
        }
    }
}
