package cn.weli.svideo.advert.bean;

import java.util.List;

/**
 * 抽象基础广告bean
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see [class/method]
 * @since [1.0.0]
 */
public abstract class BaseFeedBean {

    public abstract String getTitle();

    public abstract String getDesc();

    public abstract String getImgUrl();

    public abstract List<String> getImageArray();
}
