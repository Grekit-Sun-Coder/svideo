package cn.weli.svideo.advert.bean;

import com.ttshell.sdk.api.TTFeedOb;
import com.ttshell.sdk.api.model.TTObImage;

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
public class TtFeedBean extends BaseFeedBean implements Serializable {

    private TTFeedOb mAdBean;

    public TtFeedBean(TTFeedOb adBean) {
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
            return mAdBean.getDescription();
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public String getImgUrl() {
        if (mAdBean != null && mAdBean.getImageList() != null) {
            TTObImage image = mAdBean.getImageList().get(0);
            return image.getImageUrl();
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public List<String> getImageArray() {
        if (mAdBean != null && mAdBean.getImageList() != null) {
            List<String> urls = new ArrayList<>();
            for (TTObImage image : mAdBean.getImageList()) {
                urls.add(image.getImageUrl());
            }
            return urls;
        }
        return new ArrayList<>();
    }

    public TTFeedOb getAdBean() {
        return mAdBean;
    }
}
