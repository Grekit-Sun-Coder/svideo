package cn.weli.svideo.advert;

import android.content.Context;

import com.baidu.mobad.feeds.NativeResponse;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.ttshell.sdk.api.TTFeedOb;

import java.util.ArrayList;
import java.util.List;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.advert.baidu.BdAdHelper;
import cn.weli.svideo.advert.bean.AdInfoBean;
import cn.weli.svideo.advert.bean.BaseFeedBean;
import cn.weli.svideo.advert.bean.BdFeedBean;
import cn.weli.svideo.advert.bean.GdtFeedBean;
import cn.weli.svideo.advert.bean.TtFeedBean;
import cn.weli.svideo.advert.gdt.GdtAdHelper;
import cn.weli.svideo.advert.toutiao.TtVideoAdHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.ConfigConstants;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;

/**
 * 混合广告帮助类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-21
 * @see [class/method]
 * @since [1.0.0]
 */
public class MultiFeedAdHelper {

    /**
     * feed信息流大图or一图
     */
    public static final int FEED_TYPE_BIG_PIC = 1;
    /**
     * feed信息流三图
     */
    public static final int FEED_TYPE_THREE_PIC = 3;

    private Context mContext;
    /**
     * 广告列表
     */
    private List<AdInfoBean> mAdList;
    /**
     * 回调
     */
    private OnMultiFeedAdListener mAdListener;
    /**
     * 混合draw视频回调
     */
    public interface OnMultiFeedAdListener {

        /**
         * 获取成功
         *
         * @param feedBean draw视频数据
         * @param picType 广告大图or三图类型
         */
        void onGetFeedAdSuccess(BaseFeedBean feedBean, int picType);

        /**
         * 获取失败
         *
         * @param code 编码
         * @param msg  信息
         */
        void onGeFeedAdFailed(String code, String msg);
    }

    /**
     * 设置回调监听
     *
     * @param adListener 回调
     */
    public void setAdListener(OnMultiFeedAdListener adListener) {
        mAdListener = adListener;
    }

    public MultiFeedAdHelper(Context context) {
        mContext = context;
    }

    /**
     * 加载Feed广告
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
            mAdList.add(new AdInfoBean(AdConfigBean.VIDEO_AD_TYPE_TT, ConfigConstants.TT_FEED_IMG_ID));
        }
        loadFeedVideo();
    }

    /**
     * 加载draw视频广告
     */
    private void loadFeedVideo() {
        if (mAdList != null && !mAdList.isEmpty()) {
            AdInfoBean bean = mAdList.get(0);
            if (bean != null) {
                if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_TT)) {
                    // 头条
                    loadTtFeedVideo(bean);
                } else if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_BD)) {
                    // 百度
                    loadBdFeedVideo(bean);
                } else if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_GDT)) {
                    // 广点通
                    loadGdtFeedVideo(bean);
                }
            }
        } else {
            if (mAdListener != null) {
                mAdListener.onGeFeedAdFailed(StringUtil.EMPTY_STR, mContext.getString(R.string.ad_get_failed));
            }
        }
    }

    /**
     * 加载头条Feed视频广告
     *
     * @param bean 广告数据
     */
    private void loadTtFeedVideo(AdInfoBean bean) {
        TtVideoAdHelper helper = new TtVideoAdHelper(mContext);
        helper.getFeedAd(bean.adId, new TtVideoAdHelper.OnTtFeedAdListener() {
            @Override
            public void onGetFeedAdSuccess(TTFeedOb feedOb, int adType) {
                if (mAdListener != null) {
                    mAdListener.onGetFeedAdSuccess(new TtFeedBean(feedOb), adType);
                }
            }

            @Override
            public void onGetFeedAdError(int code, String msg) {
                Logger.d("Get tt feed error [" + code + " " + msg + "]");
                removeCurrentAd(bean);
            }
        });
    }

    /**
     * 加载广点通Feed视频广告
     *
     * @param bean 广告数据
     */
    private void loadGdtFeedVideo(AdInfoBean bean) {
        GdtAdHelper helper = new GdtAdHelper(mContext);
        helper.getFeedAd(bean.adId, new GdtAdHelper.OnGdtFeedAdListener() {
            @Override
            public void onGetFeedAdSuccess(NativeUnifiedADData adBean, int adType) {
                if (mAdListener != null) {
                    mAdListener.onGetFeedAdSuccess(new GdtFeedBean(adBean), adType);
                }
            }

            @Override
            public void onGetFeedAdError(String code, String msg) {
                Logger.d("Get gdt feed error [" + code + " " + msg + "]");
                removeCurrentAd(bean);
            }
        });
    }

    /**
     * 加载百度Feed视频广告
     *
     * @param bean 广告数据
     */
    private void loadBdFeedVideo(AdInfoBean bean) {
        BdAdHelper helper = new BdAdHelper(mContext);
        helper.getFeedAd(bean.adId, new BdAdHelper.OnBdFeedAdListener() {
            @Override
            public void onGetFeedAdSuccess(NativeResponse adBean, int adType) {
                if (mAdListener != null) {
                    mAdListener.onGetFeedAdSuccess(new BdFeedBean(adBean), adType);
                }
            }

            @Override
            public void onGetFeedAdError(String code) {
                Logger.d("Get bd feed error [" + code + "]");
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
            loadFeedVideo();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }
}
