package cn.weli.svideo.advert.bean;

import com.qq.e.ads.nativ.NativeUnifiedADData;

import java.io.Serializable;

import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 广点通draw视频数据
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class GdtDrawVideoBean extends BaseDrawVideoBean implements Serializable {

    private NativeUnifiedADData mAdBean;

    public GdtDrawVideoBean(NativeUnifiedADData adBean) {
        mAdBean = adBean;
    }

    @Override
    public String getBtnDesc() {
        if (mAdBean != null) {
            return getAdAction(mAdBean);
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
            return mAdBean.getDesc();
        }
        return StringUtil.EMPTY_STR;
    }

    public NativeUnifiedADData getAdBean() {
        return mAdBean;
    }

    private String getAdAction(NativeUnifiedADData ad) {
        if (!ad.isAppAd()) {
            return WlVideoAppInfo.sAppCtx.getString(R.string.today_detail_txt);
        }
        switch (ad.getAppStatus()) {
            case 0:
                return WlVideoAppInfo.sAppCtx.getString(R.string.str_download);
            case 1:
                return WlVideoAppInfo.sAppCtx.getString(R.string.ad_open_app);
            case 2:
                return WlVideoAppInfo.sAppCtx.getString(R.string.main_update_str);
            case 4:
                return ad.getProgress() + "%";
            case 8:
                return WlVideoAppInfo.sAppCtx.getString(R.string.ad_setup_app);
            case 16:
                return WlVideoAppInfo.sAppCtx.getString(R.string.ad_download_failed_app);
            default:
                return WlVideoAppInfo.sAppCtx.getString(R.string.today_detail_txt);
        }
    }
}
