package cn.weli.svideo.advert.toutiao;

import android.content.Context;

import com.ttshell.sdk.api.TTDrawFeedOb;
import com.ttshell.sdk.api.TTFeedOb;
import com.ttshell.sdk.api.TTObNative;
import com.ttshell.sdk.api.TTRewardVideoOb;
import com.ttshell.sdk.api.config.TTObConstant;
import com.ttshell.sdk.api.model.TTObSlot;

import java.util.ArrayList;
import java.util.List;

import cn.etouch.logger.Logger;
import cn.weli.svideo.advert.MultiFeedAdHelper;

/**
 * 头条视频
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class TtVideoAdHelper {

    private Context mContext;

    private TTObNative mAdNative;

    private OnTtVideoAdListener mAdListener;

    private OnTtRewardVideoAdListener mRewardVideoAdListener;

    private OnTtFeedAdListener mFeedAdListener;

    private List<TTDrawFeedOb> mDrawItems;

    public TtVideoAdHelper(Context context) {
        mContext = context;
        mDrawItems = new ArrayList<>();
        mAdNative = TtAdManagerHolder.get().createObNative(context.getApplicationContext());
    }

    public interface OnTtVideoAdListener {

        /**
         * 获取视频成功
         *
         * @param drawFeedAd 视频广告
         */
        void onDrawNativeVideoGet(TTDrawFeedOb drawFeedAd);

        void onDrawNativeVideoFailed(String code, String msg);
    }

    public interface OnTtRewardVideoAdListener {

        void onGetRewardVideoAdStart();

        void onGetRewardVideoAdSuccess(TTRewardVideoOb videoAd);

        void onGetRewardVideoAdError(int code, String msg);
    }

    public interface OnTtFeedAdListener {

        void onGetFeedAdSuccess(TTFeedOb feedOb, int adType);

        void onGetFeedAdError(int code, String msg);
    }

    /**
     * 设置监听
     *
     * @param adListener 监听
     */
    public void setAdListener(OnTtVideoAdListener adListener) {
        mAdListener = adListener;
    }

    public void setRewardVideoAdListener(OnTtRewardVideoAdListener rewardVideoAdListener) {
        mRewardVideoAdListener = rewardVideoAdListener;
    }

    public void setFeedAdListener(OnTtFeedAdListener feedAdListener) {
        mFeedAdListener = feedAdListener;
    }

    /**
     * 获取当前广告位的视频广告
     *
     * @param adId 广告id
     */
    public void getDrawNativeVideo(String adId) {
        if (mDrawItems != null && !mDrawItems.isEmpty()) {
            TTDrawFeedOb ad = mDrawItems.get(0);
            if (mAdListener != null) {
                mAdListener.onDrawNativeVideoGet(ad);
            }
            mDrawItems.remove(ad);
            return;
        }
        TTObSlot adSlot = new TTObSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setObCount(3)
                .build();
        mAdNative.loadDrawFeedOb(adSlot, new TTObNative.DrawFeedObListener() {
            @Override
            public void onError(int code, String message) {
                Logger.e("get draw native video error code is [" + code + "] msg is [" + message + "]");
                if (mAdListener != null) {
                    mAdListener.onDrawNativeVideoFailed(String.valueOf(code), message);
                }
            }

            @Override
            public void onDrawFeedObLoad(List<TTDrawFeedOb> list) {
                if (mDrawItems == null) {
                    mDrawItems = new ArrayList<>();
                }
                mDrawItems.clear();
                mDrawItems.addAll(list);
                if (mDrawItems != null && !mDrawItems.isEmpty()) {
                    TTDrawFeedOb ad = mDrawItems.get(0);
                    if (mAdListener != null) {
                        mAdListener.onDrawNativeVideoGet(ad);
                    }
                    mDrawItems.remove(ad);
                }
            }
        });
    }

    /**
     * 获取激励视频广告
     *
     * @param adId 广告id
     */
    public void getRewardVideoAd(String adId, int orientation, OnTtRewardVideoAdListener listener) {
        TTObSlot adSlot = new TTObSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setUserID("user123")
                .setOrientation(orientation)
                .build();
        if (listener != null) {
            listener.onGetRewardVideoAdStart();
        }
        mAdNative.loadRewardVideoOb(adSlot, new TTObNative.RewardVideoObListener() {
            @Override
            public void onError(int code, String message) {
                if (listener != null) {
                    listener.onGetRewardVideoAdError(code, message);
                }
            }

            @Override
            public void onRewardVideoObLoad(TTRewardVideoOb ttRewardVideoOb) {
                //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
                if (listener != null) {
                    listener.onGetRewardVideoAdSuccess(ttRewardVideoOb);
                }
            }

            @Override
            public void onRewardVideoCached() {
            }
        });
    }

    /**
     * 获取feed广告
     *
     * @param adId 广告id
     */
    public void getFeedAd(String adId, OnTtFeedAdListener listener) {
        TTObSlot adSlot = new TTObSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(580, 380)
                .setObCount(1)
                .build();
        mAdNative.loadFeedOb(adSlot, new TTObNative.FeedObListener() {
            @Override
            public void onError(int code, String message) {
                if (listener != null) {
                    listener.onGetFeedAdError(code, message);
                }
            }

            @Override
            public void onFeedObLoad(List<TTFeedOb> ads) {
                if (ads != null && !ads.isEmpty()) {
                    if (listener != null) {
                        listener.onGetFeedAdSuccess(ads.get(0), getFeedAdType(ads.get(0)));
                    }
                }
            }
        });
    }

    /**
     * 获取feed模式
     *
     * @param adBean 广告
     * @return 大图or三图
     */
    private int getFeedAdType(TTFeedOb adBean) {
        if (adBean != null) {
            if (adBean.getImageMode() == TTObConstant.IMAGE_MODE_GROUP_IMG) {
                return MultiFeedAdHelper.FEED_TYPE_THREE_PIC;
            }
        }
        return MultiFeedAdHelper.FEED_TYPE_BIG_PIC;
    }
}
