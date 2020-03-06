package cn.weli.svideo.advert.bean;

import com.qq.e.ads.nativ.NativeUnifiedADData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class GdtFeedBean extends BaseFeedBean implements Serializable {

    private NativeUnifiedADData mAdBean;

    public GdtFeedBean(NativeUnifiedADData adBean) {
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
            return mAdBean.getImgUrl();
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public List<String> getImageArray() {
        if (mAdBean != null) {
            return mAdBean.getImgList();
        }
        return new ArrayList<>();
    }

    public NativeUnifiedADData getAdBean() {
        return mAdBean;
    }
}
