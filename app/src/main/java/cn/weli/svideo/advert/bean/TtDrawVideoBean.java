package cn.weli.svideo.advert.bean;

import com.ttshell.sdk.api.TTDrawFeedOb;

import java.io.Serializable;

import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 头条draw视频数据
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class TtDrawVideoBean extends BaseDrawVideoBean implements Serializable {

    private TTDrawFeedOb mAdBean;

    public TtDrawVideoBean(TTDrawFeedOb adBean) {
        mAdBean = adBean;
    }

    @Override
    public String getBtnDesc() {
        if (mAdBean != null) {
            return mAdBean.getButtonText();
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public String getTitle() {
        if (mAdBean != null) {
            return mAdBean.getTitle();
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public String getDesc() {
        if (mAdBean != null) {
            return mAdBean.getDescription();
        }
        return StringUtil.EMPTY_STR;
    }

    public TTDrawFeedOb getAdBean() {
        return mAdBean;
    }
}
