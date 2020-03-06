package cn.weli.svideo.module.task.model.bean;

import java.io.Serializable;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class TaskBean implements Serializable {

    public static final String REWARD_VIDEO = "watch_inspire_video";

    public static final String STATUS_FINISHED = "FINISHED";
    public static final String STATUS_PROCESSING = "PROCESSING";
    public static final String STATUS_UNFINISHED = "UNFINISHED";

    public String button;
    public String desc;
    public int finish_times;
    public String key;
    public String name;
    public String reward;
    public String target_url;
    public String task_status;
    public int times_limit;
    public String icon;

    public AdConfigBean ad_config;
}
