package cn.weli.svideo.advert.bean;

import java.io.Serializable;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-24
 * @see [class/method]
 * @since [1.0.0]
 */
public abstract class BaseDrawVideoBean implements Serializable {

    /**
     * 按钮内容
     *
     * @return 按钮内容
     */
    public abstract String getBtnDesc();
    /**
     * 标题
     *
     * @return 标题
     */
    public abstract String getTitle();
    /**
     * 内容
     *
     * @return 内容
     */
    public abstract String getDesc();

}
