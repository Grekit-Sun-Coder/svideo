package cn.weli.svideo.advert;

import android.content.Context;

import com.baidu.mobad.feeds.XAdNativeResponse;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.ttshell.sdk.api.TTDrawFeedOb;

import java.util.ArrayList;
import java.util.List;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.advert.baidu.BdAdHelper;
import cn.weli.svideo.advert.bean.AdInfoBean;
import cn.weli.svideo.advert.bean.BaseDrawVideoBean;
import cn.weli.svideo.advert.bean.BdDrawVideoBean;
import cn.weli.svideo.advert.bean.GdtDrawVideoBean;
import cn.weli.svideo.advert.bean.TtDrawVideoBean;
import cn.weli.svideo.advert.gdt.GdtAdHelper;
import cn.weli.svideo.advert.toutiao.TtVideoAdHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.ConfigConstants;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;

/**
 * 混合draw广告帮助类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class MultiDrawAdHelper {

    private Context mContext;
    /**
     * 广告列表
     */
    private List<AdInfoBean> mAdList;
    /**
     * 回调
     */
    private OnMultiDrawAdListener mAdListener;

    /**
     * 混合draw视频回调
     */
    public interface OnMultiDrawAdListener {

        /**
         * 获取成功
         *
         * @param drawVideoBean draw视频数据
         */
        void onGetDrawAdSuccess(BaseDrawVideoBean drawVideoBean);

        /**
         * 获取失败
         *
         * @param code 编码
         * @param msg  信息
         */
        void onGeDrawAdFailed(String code, String msg);
    }

    /**
     * 设置回调监听
     *
     * @param adListener 回调
     */
    public void setAdListener(OnMultiDrawAdListener adListener) {
        mAdListener = adListener;
    }

    public MultiDrawAdHelper(Context context) {
        mContext = context;
    }

    /**
     * 加载draw视频
     *
     * @param targetSdk      目标sdk类型
     * @param targetAdId     目标广告id
     * @param supplementSdk  补量sdk类型
     * @param supplementAdId 补量广告id
     */
    public void loadAd(String targetSdk, String targetAdId, String supplementSdk, String supplementAdId) {
        Logger.d("Draw video :\n" + "targetAdId=" + targetAdId + "\ntargetSdk=" + targetSdk
                + "\nbackupAdId=" + supplementAdId + "\nbackupSdk=" + supplementSdk);
        mAdList = new ArrayList<>();
        boolean hasTtAd = false;
        if (!StringUtil.isNull(targetSdk) && !StringUtil.isNull(targetAdId)) {
            // 目标广告存在，则加载对应的广告
            hasTtAd = StringUtil.equals(targetSdk, AdConfigBean.VIDEO_AD_TYPE_TT);
            mAdList.add(new AdInfoBean(targetSdk, targetAdId));
        }
        if (!StringUtil.isNull(supplementSdk) && !StringUtil.isNull(supplementAdId)) {
            // 补量广告存在，则加载对应的广告
            hasTtAd = StringUtil.equals(supplementSdk, AdConfigBean.VIDEO_AD_TYPE_TT);
            mAdList.add(new AdInfoBean(supplementSdk, supplementAdId));
        }
        // 如果目标和补量都没有头条广告配置，则打底至少有，头条的
        if (!hasTtAd) {
            mAdList.add(new AdInfoBean(AdConfigBean.VIDEO_AD_TYPE_TT, ConfigConstants.TT_DRAW_VIDEO_ID));
        }
        loadDrawVideo();
    }

    /**
     * 加载draw视频广告
     */
    private void loadDrawVideo() {
        if (mAdList != null && !mAdList.isEmpty()) {
            AdInfoBean bean = mAdList.get(0);
            if (bean != null) {
                if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_TT)) {
                    // 头条
                    loadTtDrawVideo(bean);
                } else if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_BD)) {
                    // 百度
                    loadBdDrawVideo(bean);
                } else if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_GDT)) {
                    // 广点通
                    loadGdtDrawVideo(bean);
                }
            }
        } else {
            if (mAdListener != null) {
                mAdListener.onGeDrawAdFailed(StringUtil.EMPTY_STR, mContext.getString(R.string.ad_get_failed));
            }
        }
    }

    /**
     * 加载头条draw视频广告
     *
     * @param bean 广告数据
     */
    private void loadTtDrawVideo(AdInfoBean bean) {
        TtVideoAdHelper helper = new TtVideoAdHelper(mContext);
        helper.setAdListener(new TtVideoAdHelper.OnTtVideoAdListener() {
            @Override
            public void onDrawNativeVideoGet(TTDrawFeedOb drawFeedAd) {
                if (mAdListener != null) {
                    mAdListener.onGetDrawAdSuccess(new TtDrawVideoBean(drawFeedAd));
                }
            }

            @Override
            public void onDrawNativeVideoFailed(String code, String msg) {
                removeCurrentAd(bean);
            }
        });
        helper.getDrawNativeVideo(bean.adId);
    }

    /**
     * 加载百度draw视频广告
     *
     * @param bean 广告数据
     */
    private void loadBdDrawVideo(AdInfoBean bean) {
        BdAdHelper helper = new BdAdHelper(mContext);
        helper.getDrawVideoAd(bean.adId, new BdAdHelper.OnBdDrawAdListener() {
            @Override
            public void onGetDrawAdSuccess(XAdNativeResponse adBean) {
                if (mAdListener != null) {
                    mAdListener.onGetDrawAdSuccess(new BdDrawVideoBean(adBean));
                }
            }

            @Override
            public void onGetDrawAdError(String code) {
                removeCurrentAd(bean);
            }
        });
    }

    /**
     * 加载广点通draw视频广告
     *
     * @param bean 广告数据
     */
    private void loadGdtDrawVideo(AdInfoBean bean) {
        GdtAdHelper helper = new GdtAdHelper(mContext);
        helper.getDrawVideoAd(bean.adId, new GdtAdHelper.OnGdtDrawAdListener() {
            @Override
            public void onGetDrawAdSuccess(NativeUnifiedADData adBean) {
                if (mAdListener != null) {
                    mAdListener.onGetDrawAdSuccess(new GdtDrawVideoBean(adBean));
                }
            }

            @Override
            public void onGetDrawAdError(String code, String msg) {
                removeCurrentAd(bean);
            }
        });
    }

    /**
     * 移除当前广告
     *
     * @param bean 广告
     */
    private void removeCurrentAd(AdInfoBean bean) {
        try {
            if (mAdList != null && !mAdList.isEmpty()) {
                mAdList.remove(bean);
            }
            loadDrawVideo();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }
}
