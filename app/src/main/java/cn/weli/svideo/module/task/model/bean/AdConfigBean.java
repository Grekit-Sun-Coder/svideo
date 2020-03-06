package cn.weli.svideo.module.task.model.bean;

import java.io.Serializable;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class AdConfigBean implements Serializable {

    public static final String VIDEO_TYPE_POST = "video";

    public static final String VIDEO_AD_TYPE_TT = "toutiao";
    public static final String VIDEO_AD_TYPE_BD = "baidu";
    public static final String VIDEO_AD_TYPE_GDT = "qqgdt";
    public static final String VIDEO_AD_TYPE_KM = "liyue";

    public static final String ACTION_TYPE_CLICK = "click";
    public static final String ACTION_TYPE_PV = "pv";

    public String ad_id;

    public String source = VIDEO_AD_TYPE_TT;

    public String space;

    public String backup_source;

    public String backup_ad_id;
}
