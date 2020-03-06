package cn.weli.svideo.advert.baidu;

import android.content.Context;

import com.baidu.mobad.feeds.BaiduNative;
import com.baidu.mobad.feeds.BaiduNativeManager;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.baidu.mobad.feeds.XAdNativeResponse;

import java.util.ArrayList;
import java.util.List;

import cn.etouch.logger.Logger;
import cn.weli.svideo.advert.MultiFeedAdHelper;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 百度广告帮助类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class BdAdHelper {

    private Context mContext;
    /**
     * feed信息流列表
     */
    private List<NativeResponse> mFeedItems;
    /**
     * draw视频列表
     */
    private List<NativeResponse> mDrawVideoItems;

    public interface OnBdFeedAdListener {

        /**
         * 获取广告成功
         *
         * @param adBean 广告数据
         * @param adType 广告大图or三图类型
         */
        void onGetFeedAdSuccess(NativeResponse adBean, int adType);

        /**
         * 获取广告失败
         *
         * @param code 编码
         */
        void onGetFeedAdError(String code);
    }

    public interface OnBdDrawAdListener {

        /**
         * 获取广告成功
         *
         * @param adBean 广告数据
         */
        void onGetDrawAdSuccess(XAdNativeResponse adBean);

        /**
         * 获取广告失败
         *
         * @param code 编码
         */
        void onGetDrawAdError(String code);
    }

    public BdAdHelper(Context context) {
        mContext = context;
    }

    /**
     * 获取feed信息流图文广告
     *
     * @param adId     广告id
     * @param listener 回调
     */
    public void getFeedAd(String adId, OnBdFeedAdListener listener) {
        try {
            if (mFeedItems != null && !mFeedItems.isEmpty()) {
                NativeResponse ad = mFeedItems.get(0);
                if (listener != null) {
                    listener.onGetFeedAdSuccess(ad, getFeedAdType(ad));
                }
                mFeedItems.remove(ad);
                return;
            }
            BaiduNativeManager manager = new BaiduNativeManager(mContext, adId);
            RequestParameters requestParameters = new RequestParameters.Builder()
                    .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE)
                    .build();
            manager.loadFeedAd(requestParameters, new BaiduNativeManager.FeedAdListener() {
                @Override
                public void onNativeLoad(List<NativeResponse> list) {
                    if (mFeedItems == null) {
                        mFeedItems = new ArrayList<>();
                    }
                    mFeedItems.clear();
                    mFeedItems.addAll(list);
                    if (mFeedItems != null && !mFeedItems.isEmpty()) {
                        NativeResponse ad = mFeedItems.get(0);
                        if (listener != null) {
                            listener.onGetFeedAdSuccess(ad, getFeedAdType(ad));
                        }
                        mFeedItems.remove(ad);
                    }
                }

                @Override
                public void onNativeFail(NativeErrorCode code) {
                    if (listener != null && code != null) {
                        listener.onGetFeedAdError(code.name());
                    }
                }

                @Override
                public void onVideoDownloadSuccess() {
                }

                @Override
                public void onVideoDownloadFailed() {
                }

                @Override
                public void onLpClosed() {
                }
            });
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
    public void getDrawVideoAd(String adId, OnBdDrawAdListener listener) {
        try {
            if (mDrawVideoItems != null && !mDrawVideoItems.isEmpty()) {
                NativeResponse ad = mDrawVideoItems.get(0);
                if (listener != null) {
                    listener.onGetDrawAdSuccess((XAdNativeResponse) ad);
                }
                mDrawVideoItems.remove(ad);
                return;
            }
            BaiduNative baiduNative = new BaiduNative(mContext, adId,
                    new BaiduNative.VideoCacheListener() {
                        @Override
                        public void onVideoDownloadSuccess() {
                        }

                        @Override
                        public void onVideoDownloadFailed() {
                        }

                        @Override
                        public void onLpClosed() {
                        }

                        @Override
                        public void onAdClick(NativeResponse nativeResponse) {
                            Logger.d("baidu draw ad click");
                        }

                        @Override
                        public void onNativeLoad(List<NativeResponse> list) {
                            if (mDrawVideoItems == null) {
                                mDrawVideoItems = new ArrayList<>();
                            }
                            mDrawVideoItems.clear();
                            mDrawVideoItems.addAll(list);
                            if (mDrawVideoItems != null && !mDrawVideoItems.isEmpty()) {
                                NativeResponse ad = mDrawVideoItems.get(0);
                                if (listener != null) {
                                    listener.onGetDrawAdSuccess((XAdNativeResponse) ad);
                                }
                                mDrawVideoItems.remove(ad);
                            }
                        }

                        @Override
                        public void onNativeFail(NativeErrorCode code) {
                            if (listener != null && code != null) {
                                listener.onGetDrawAdError(code.name());
                                Logger.d("baidu draw video failed is [" + code.name() + "]");
                            }
                        }
                    }, true);
            RequestParameters requestParameters = new RequestParameters.Builder()
                    .setWidth(DensityUtil.getInstance().getScreenWidth())
                    .setHeight(DensityUtil.getInstance().getScreenHeight())
                    .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE)
                    .build();
            baiduNative.makeRequest(requestParameters);
            Logger.d("baidu draw video request start");
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
    private int getFeedAdType(NativeResponse adBean) {
        if (adBean != null) {
            if (!StringUtil.isNull(adBean.getImageUrl())) {
                return MultiFeedAdHelper.FEED_TYPE_BIG_PIC;
            }
            if (adBean.getMultiPicUrls() != null
                    && adBean.getMultiPicUrls().size() >= MultiFeedAdHelper.FEED_TYPE_THREE_PIC) {
                return MultiFeedAdHelper.FEED_TYPE_THREE_PIC;
            }
        }
        return MultiFeedAdHelper.FEED_TYPE_BIG_PIC;
    }
}