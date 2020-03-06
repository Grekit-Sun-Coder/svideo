package cn.weli.svideo.advert.bean;

import com.baidu.mobad.feeds.XAdNativeResponse;

import java.io.Serializable;

import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 百度draw视频数据
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class BdDrawVideoBean extends BaseDrawVideoBean implements Serializable {

    private XAdNativeResponse mAdBean;

    public BdDrawVideoBean(XAdNativeResponse adBean) {
        mAdBean = adBean;
    }

    @Override
    public String getBtnDesc() {
        if (mAdBean != null) {
            return getBtnTxt(mAdBean);
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public String getTitle() {
        if (mAdBean != null) {
            return mAdBean.getBrandName();
        }
        return StringUtil.EMPTY_STR;
    }

    @Override
    public String getDesc() {
        if (mAdBean != null) {
            return mAdBean.getTitle();
        }
        return StringUtil.EMPTY_STR;
    }

    public XAdNativeResponse getAdBean() {
        return mAdBean;
    }

    private String getBtnTxt(XAdNativeResponse adBean) {
        if (adBean.isDownloadApp()) {
            return WlVideoAppInfo.sAppCtx.getString(R.string.str_download);
        } else {
            return WlVideoAppInfo.sAppCtx.getString(R.string.today_detail_txt);
        }
    }
}
