package cn.weli.svideo.advert.bean;

import com.baidu.mobad.feeds.NativeResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 百度feed信息流图文bean
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class BdFeedBean extends BaseFeedBean implements Serializable {

    private NativeResponse mAdBean;

    public BdFeedBean(NativeResponse adBean) {
        mAdBean = adBean;
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
            return mAdBean.getDesc();
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public String getImgUrl() {
        if (mAdBean != null) {
            return mAdBean.getImageUrl();
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public List<String> getImageArray() {
        if (mAdBean != null && mAdBean.getMultiPicUrls() != null) {
            return mAdBean.getMultiPicUrls();
        }
        return new ArrayList<>();
    }

    public NativeResponse getAdBean() {
        return mAdBean;
    }
}
