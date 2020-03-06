package cn.weli.svideo.advert.gdt;

import android.content.Context;

import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

import cn.etouch.logger.Logger;
import cn.weli.svideo.advert.MultiFeedAdHelper;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.BusinessConstants;

/**
 * 广点通广告
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-23
 * @see MultiFeedAdHelper
 * @since [1.0.0]
 */
public class GdtAdHelper {

    /**
     * 默认拉取广告数量
     */
    private static final int AD_COUNT = 3;

    private Context mContext;

    /**
     * draw视频列表
     */
    private List<NativeUnifiedADData> mDrawVideoItems;
    /**
     * feed图文广告
     */
    private List<NativeUnifiedADData> mFeedItems;

    public interface OnGdtFeedAdListener {

        /**
         * 获取广告成功
         *
         * @param adBean 广告数据
         * @param adType 广告大图or三图类型
         */
        void onGetFeedAdSuccess(NativeUnifiedADData adBean, int adType);

        /**
         * 获取广告失败
         *
         * @param code 编码
         * @param msg  信息
         */
        void onGetFeedAdError(String code, String msg);
    }

    public interface OnGdtDrawAdListener {

        /**
         * 获取广告成功
         *
         * @param adBean 广告数据
         */
        void onGetDrawAdSuccess(NativeUnifiedADData adBean);

        /**
         * 获取广告失败
         *
         * @param code 编码
         * @param msg  信息
         */
        void onGetDrawAdError(String code, String msg);
    }

    public GdtAdHelper(Context context) {
        mContext = context;
    }

    /**
     * 获取draw视频广告
     *
     * @param adId     广告id
     * @param listener 回调
     */
    public void getFeedAd(String adId, OnGdtFeedAdListener listener) {
        try {
            if (StringUtil.isNull(adId)) {
                return;
            }
            if (mFeedItems != null && !mFeedItems.isEmpty()) {
                NativeUnifiedADData ad = mFeedItems.get(0);
                if (listener != null) {
                    listener.onGetFeedAdSuccess(ad, getFeedAdType(ad));
                }
                mFeedItems.remove(ad);
                return;
            }
            NativeUnifiedAD nativeAd = new NativeUnifiedAD(mContext,
                    PackageHelper.getMetaData(mContext,
                            BusinessConstants.MetaData.GDT_APP_ID), adId,
                    new NativeADUnifiedListener() {
                        @Override
                        public void onADLoaded(List<NativeUnifiedADData> list) {
                            if (mFeedItems == null) {
                                mFeedItems = new ArrayList<>();
                            }
                            mFeedItems.clear();
                            mFeedItems.addAll(list);
                            if (mFeedItems != null && !mFeedItems.isEmpty()) {
                                NativeUnifiedADData ad = mFeedItems.get(0);
                                if (listener != null) {
                                    listener.onGetFeedAdSuccess(ad, getFeedAdType(ad));
                                }
                                mFeedItems.remove(ad);
                            }
                        }

                        @Override
                        public void onNoAD(AdError adError) {
                            if (listener != null && adError != null) {
                                listener.onGetFeedAdError(String.valueOf(adError.getErrorCode()),
                                        adError.getErrorMsg());
                            }
                        }
                    });
            nativeAd.loadData(AD_COUNT);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 获取draw视频广告
     *
     * @param adId     广告id
     * @param listener 回调
     */
    public void getDrawVideoAd(String adId, OnGdtDrawAdListener listener) {
        try {
            if (StringUtil.isNull(adId)) {
                return;
            }
            if (mDrawVideoItems != null && !mDrawVideoItems.isEmpty()) {
                NativeUnifiedADData ad = mDrawVideoItems.get(0);
                if (listener != null) {
                    listener.onGetDrawAdSuccess(ad);
                }
                mDrawVideoItems.remove(ad);
                return;
            }
            NativeUnifiedAD nativeAd = new NativeUnifiedAD(mContext,
                    PackageHelper.getMetaData(mContext,
                            BusinessConstants.MetaData.GDT_APP_ID), adId,
                    new NativeADUnifiedListener() {
                        @Override
                        public void onADLoaded(List<NativeUnifiedADData> list) {
                            if (mDrawVideoItems == null) {
                                mDrawVideoItems = new ArrayList<>();
                            }
                            mDrawVideoItems.clear();
                            mDrawVideoItems.addAll(list);
                            if (mDrawVideoItems != null && !mDrawVideoItems.isEmpty()) {
                                NativeUnifiedADData ad = mDrawVideoItems.get(0);
                                if (listener != null) {
                                    listener.onGetDrawAdSuccess(ad);
                                }
                                mDrawVideoItems.remove(ad);
                            }
                        }

                        @Override
                        public void onNoAD(AdError adError) {
                            if (listener != null && adError != null) {
                                listener.onGetDrawAdError(String.valueOf(adError.getErrorCode()),
                                        adError.getErrorMsg());
                                Logger.d("gdt draw video failed is [" + adError.getErrorMsg() + "]");
                            }
                        }
                    });
            nativeAd.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK);
            nativeAd.loadData(AD_COUNT);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 获取feed模式
     *
     * @param adBean 广告
     * @return 大图or三图
     */
    private int getFeedAdType(NativeUnifiedADData adBean) {
        if (adBean != null) {
            if (adBean.getAdPatternType() == AdPatternType.NATIVE_3IMAGE) {
                return MultiFeedAdHelper.FEED_TYPE_THREE_PIC;
            }
        }
        return MultiFeedAdHelper.FEED_TYPE_BIG_PIC;
    }
}
