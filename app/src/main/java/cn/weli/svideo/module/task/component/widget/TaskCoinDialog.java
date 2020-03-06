package cn.weli.svideo.module.task.component.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobad.feeds.NativeResponse;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.util.AdError;
import com.ttshell.sdk.api.TTNativeOb;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.advert.MultiFeedAdHelper;
import cn.weli.svideo.advert.bean.BaseFeedBean;
import cn.weli.svideo.advert.bean.BdFeedBean;
import cn.weli.svideo.advert.bean.GdtFeedBean;
import cn.weli.svideo.advert.bean.TtFeedBean;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAd;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAdData;
import cn.weli.svideo.advert.kuaima.ETKuaiMaAdListener;
import cn.weli.svideo.baselib.component.widget.dialog.BaseDialog;
import cn.weli.svideo.baselib.helper.glide.ILoader;
import cn.weli.svideo.baselib.helper.glide.ImageLoadCallback;
import cn.weli.svideo.baselib.helper.glide.WeImageLoader;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.main.model.AdvertModel;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class TaskCoinDialog extends BaseDialog {

    @BindView(R.id.coin_txt)
    TextView mCoinTxt;
    @BindView(R.id.coin_ad_img)
    ImageView mCoinAdImg;
    @BindView(R.id.coin_ad_txt)
    TextView mCoinAdTxt;
    @BindView(R.id.coin_ad_layout)
    CardView mCoinAdLayout;
    @BindView(R.id.coin_ad_icon_img)
    ImageView mCoinAdIconImg;
    @BindView(R.id.coin_ad_icon_three_img)
    ImageView mCoinThreeAdIconImg;
    @BindView(R.id.three_pic_layout)
    LinearLayout mThreePicLayout;
    @BindView(R.id.coin_ad1_img)
    ImageView mCoinAd1Img;
    @BindView(R.id.coin_ad2_img)
    ImageView mCoinAd2Img;
    @BindView(R.id.coin_ad3_img)
    ImageView mCoinAd3Img;
    @BindView(R.id.coin_ad_place_layout)
    NativeAdContainer mAdPlaceLayout;

    private String mTaskKey;

    private AdConfigBean mAdConfigBean;

    private MultiFeedAdHelper mFeedAdHelper;

    public TaskCoinDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_task_finish_coin);
        ButterKnife.bind(this);
        mCoinTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
        mFeedAdHelper = new MultiFeedAdHelper(mContext);
    }

    public void setCoinTxt(String coin) {
        mCoinTxt.setText(coin);
    }

    public String getTaskKey() {
        return mTaskKey;
    }

    public void setTaskKey(String taskKey) {
        mTaskKey = taskKey;
    }

    public void setTaskAdConfig(AdConfigBean bean) {
        if (bean == null) {
            mAdPlaceLayout.setVisibility(View.GONE);
            return;
        }
        mAdPlaceLayout.setOnClickListener(null);
        mAdConfigBean = bean;
        if (StringUtil.equals(AdConfigBean.VIDEO_AD_TYPE_TT, bean.source)
            || StringUtil.equals(AdConfigBean.VIDEO_AD_TYPE_BD, bean.source)
            || StringUtil.equals(AdConfigBean.VIDEO_AD_TYPE_GDT, bean.source)) {
            loadOtherAd(mAdConfigBean.source, mAdConfigBean.ad_id, mAdConfigBean.backup_source, mAdConfigBean.backup_ad_id);
        } else if (StringUtil.equals(AdConfigBean.VIDEO_AD_TYPE_KM, bean.source)) {
            loadLyAd(bean.ad_id);
        } else {
            mAdPlaceLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 加载鲤跃广告
     *
     * @param adId 广告位id
     */
    private void loadLyAd(String adId) {
        ETKuaiMaAd kmAd = new ETKuaiMaAd((AppBaseActivity) mContext, AdConfigBean.VIDEO_AD_TYPE_KM,
                adId, new ETKuaiMaAdListener() {
            @Override
            public void onADLoaded(List<ETKuaiMaAdData> data) {
                if (data != null && !data.isEmpty()) {
                    ETKuaiMaAdData bean = data.get(0);
                    if (bean != null) {
                        WeImageLoader.getInstance().load(mContext, mCoinAdIconImg, bean.source_icon);
                        mCoinAdTxt.setText(bean.desc);
                        if (bean.imgurls != null && !bean.imgurls.isEmpty()) {
                            WeImageLoader.getInstance().load(mContext, bean.imgurls.get(0), ILoader.Options.defaultOptions(), new ImageLoadCallback() {
                                @Override
                                public void onLoadReady(Drawable drawable) {
                                    if (drawable != null) {
                                        mCoinAdImg.setBackground(drawable);
                                    }
                                }
                            });
                        }
                        mAdPlaceLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bean.onClicked(false);
                                dismiss();
                                try {
                                    JSONObject object = new JSONObject();
                                    object.put("source", AdConfigBean.VIDEO_AD_TYPE_KM);
                                    StatisticsAgent.view(mContext, StatisticsUtils.CID.CID_6011, StatisticsUtils.MD.MD_2, "", object.toString());

                                    if (mAdConfigBean != null) {
                                        AdvertModel.reportAdEvent(mAdConfigBean.space, AdConfigBean.ACTION_TYPE_CLICK);
                                    }
                                } catch (Exception e) {
                                    Logger.e(e.getMessage());
                                }
                            }
                        });
                        bean.onExposured();
                        try {
                            JSONObject object = new JSONObject();
                            object.put("source", AdConfigBean.VIDEO_AD_TYPE_KM);
                            StatisticsAgent.view(mContext, StatisticsUtils.CID.CID_6011, StatisticsUtils.MD.MD_2, "", object.toString());

                            if (mAdConfigBean != null) {
                                AdvertModel.reportAdEvent(mAdConfigBean.space, AdConfigBean.ACTION_TYPE_PV);
                            }
                        } catch (Exception e) {
                            Logger.e(e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onNoAD() {
                Logger.d("There is no kuai ma ad, so use tou tiao ad now!");
                if (mAdConfigBean != null) {
                    loadOtherAd(StringUtil.EMPTY_STR, StringUtil.EMPTY_STR, mAdConfigBean.backup_source, mAdConfigBean.backup_ad_id);
                }
            }
        });
        kmAd.loadAD();
    }

    /**
     * 加载Feed广告
     *
     * @param sdk        目标sdk类型
     * @param adId       目标广告id
     * @param backupSdk  补量sdk类型
     * @param backupAdId 补量广告id
     */
    private void loadOtherAd(String sdk, String adId, String backupSdk, String backupAdId) {
        mFeedAdHelper.setAdListener(new MultiFeedAdHelper.OnMultiFeedAdListener() {
            @Override
            public void onGetFeedAdSuccess(BaseFeedBean feedBean, int picType) {
                bindFeedAd(feedBean, picType);
            }

            @Override
            public void onGeFeedAdFailed(String code, String msg) {
                Logger.d("load feed ad error [" + code + " " + msg + "]");
                mAdPlaceLayout.setVisibility(View.GONE);
            }
        });
        mFeedAdHelper.loadAd(sdk, adId, backupSdk, backupAdId);
    }

    /**
     * 设置feed数据
     *
     * @param feedBean 广告
     * @param picType 图片类型
     */
    private void bindFeedAd(BaseFeedBean feedBean, int picType) {
        if (feedBean == null) {
            mAdPlaceLayout.setVisibility(View.GONE);
            return;
        }
        Logger.d("Get feed ad result is [" + feedBean + "] picType is [" + picType + "]");
        if (feedBean instanceof TtFeedBean) {
            TtFeedBean ttFeedBean = (TtFeedBean) feedBean;
            showTtFeedInfo(ttFeedBean, picType);
        } else if (feedBean instanceof GdtFeedBean) {
            GdtFeedBean gdtFeedBean = (GdtFeedBean) feedBean;
            showGdtFeedInfo(gdtFeedBean, picType);
        } else if (feedBean instanceof BdFeedBean) {
            BdFeedBean bdFeedBean = (BdFeedBean) feedBean;
            showBdFeedInfo(bdFeedBean, picType);
        }
    }

    /**
     * 展示头条feed信息
     *
     * @param ttFeedBean 头条数据
     */
    private void showTtFeedInfo(TtFeedBean ttFeedBean, int picType) {
        if (ttFeedBean == null || ttFeedBean.getAdBean() == null) {
            mAdPlaceLayout.setVisibility(View.GONE);
            return;
        }
        mCoinAdIconImg.setImageResource(R.drawable.toutiao_logo);
        mCoinAdIconImg.setVisibility(View.VISIBLE);
        mCoinThreeAdIconImg.setImageResource(R.drawable.toutiao_logo);
        mCoinThreeAdIconImg.setVisibility(View.VISIBLE);
        if (picType == MultiFeedAdHelper.FEED_TYPE_BIG_PIC) {
            setBigImg(ttFeedBean);
        } else if (picType == MultiFeedAdHelper.FEED_TYPE_THREE_PIC) {
            setThreeImg(ttFeedBean);
        }
        mCoinAdTxt.setText(ttFeedBean.getDesc());
        ttFeedBean.getAdBean().registerViewForInteraction(mAdPlaceLayout, mAdPlaceLayout, new TTNativeOb.ObInteractionListener() {
            @Override
            public void onObClicked(View view, TTNativeOb ttNativeOb) {
                Logger.d("tt ob click");
                dismiss();
                try {
                    JSONObject object = new JSONObject();
                    object.put("source", AdConfigBean.VIDEO_AD_TYPE_TT);
                    StatisticsAgent.click(mContext, StatisticsUtils.CID.CID_6011, StatisticsUtils.MD.MD_2, "", object.toString(), "");

                    if (mAdConfigBean != null) {
                        AdvertModel.reportAdEvent(mAdConfigBean.space, AdConfigBean.ACTION_TYPE_CLICK);
                    }
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }

            @Override
            public void onObCreativeClick(View view, TTNativeOb ttNativeOb) {
                Logger.d("tt ob click");
                dismiss();
                try {
                    JSONObject object = new JSONObject();
                    object.put("source", AdConfigBean.VIDEO_AD_TYPE_TT);
                    StatisticsAgent.click(mContext, StatisticsUtils.CID.CID_6011, StatisticsUtils.MD.MD_2, "", object.toString(), "");

                    if (mAdConfigBean != null) {
                        AdvertModel.reportAdEvent(mAdConfigBean.space, AdConfigBean.ACTION_TYPE_CLICK);
                    }
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }

            @Override
            public void onObShow(TTNativeOb ttNativeOb) {
                Logger.d("tt ob show");
                try {
                    JSONObject object = new JSONObject();
                    object.put("source", AdConfigBean.VIDEO_AD_TYPE_TT);
                    StatisticsAgent.view(mContext, StatisticsUtils.CID.CID_6011, StatisticsUtils.MD.MD_2, "", object.toString());

                    if (mAdConfigBean != null) {
                        AdvertModel.reportAdEvent(mAdConfigBean.space, AdConfigBean.ACTION_TYPE_PV);
                    }
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
        });
    }

    /**
     * 展示广点通feed信息
     *
     * @param gdtFeedBean 广点通数据
     */
    private void showGdtFeedInfo(GdtFeedBean gdtFeedBean, int picType) {
        if (gdtFeedBean == null || gdtFeedBean.getAdBean() == null) {
            mAdPlaceLayout.setVisibility(View.GONE);
            return;
        }
        mCoinAdIconImg.setVisibility(View.INVISIBLE);
        mCoinThreeAdIconImg.setVisibility(View.INVISIBLE);
        if (picType == MultiFeedAdHelper.FEED_TYPE_BIG_PIC) {
            setBigImg(gdtFeedBean);
        } else if (picType == MultiFeedAdHelper.FEED_TYPE_THREE_PIC) {
            setThreeImg(gdtFeedBean);
        }
        mCoinAdTxt.setText(gdtFeedBean.getDesc());
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(mCoinAdLayout);
        clickableViews.add(mThreePicLayout);
        gdtFeedBean.getAdBean().bindAdToView(mContext, mAdPlaceLayout, null, clickableViews);
        gdtFeedBean.getAdBean().setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                Logger.d("gdt ad exposed");
            }

            @Override
            public void onADClicked() {
                Logger.d("gdt ad clicked");
                dismiss();
            }

            @Override
            public void onADError(AdError adError) {
                Logger.d("gdt ad error " + adError.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {

            }
        });
    }

    /**
     * 展示百度feed信息
     *
     * @param bdFeedBean 百度数据
     */
    private void showBdFeedInfo(BdFeedBean bdFeedBean, int picType) {
        if (bdFeedBean == null || bdFeedBean.getAdBean() == null) {
            mAdPlaceLayout.setVisibility(View.GONE);
            return;
        }
        mCoinAdIconImg.setImageResource(R.drawable.baidu_logo);
        mCoinAdIconImg.setVisibility(View.VISIBLE);
        mCoinThreeAdIconImg.setImageResource(R.drawable.baidu_logo);
        mCoinThreeAdIconImg.setVisibility(View.VISIBLE);
        if (picType == MultiFeedAdHelper.FEED_TYPE_BIG_PIC) {
            setBigImg(bdFeedBean);
        } else if (picType == MultiFeedAdHelper.FEED_TYPE_THREE_PIC) {
            setThreeImg(bdFeedBean);
        }
        mCoinAdTxt.setText(bdFeedBean.getTitle());
        bdFeedBean.getAdBean().registerViewForInteraction(mAdPlaceLayout,
                new NativeResponse.AdInteractionListener() {
                    @Override
                    public void onAdClick() {
                        Logger.d("bd feed ad click now");
                    }

                    @Override
                    public void onADExposed() {
                        Logger.d("bd feed ad exposed now");
                    }
                });
        mAdPlaceLayout.setOnClickListener(view -> {
            dismiss();
            bdFeedBean.getAdBean().handleClick(mAdPlaceLayout);
        });
    }


    /**
     * 设置大图
     */
    private void setBigImg(BaseFeedBean bean) {
        mCoinAdLayout.setVisibility(View.VISIBLE);
        mThreePicLayout.setVisibility(View.GONE);
        String imgUrl = bean.getImgUrl();
        if (!StringUtil.isNull(imgUrl)) {
            WeImageLoader.getInstance().load(mContext, imgUrl,
                    ILoader.Options.defaultOptions(), new ImageLoadCallback() {
                        @Override
                        public void onLoadReady(Drawable drawable) {
                            if (drawable != null) {
                                mCoinAdImg.setBackground(drawable);
                            }
                        }
                    });
        }

    }

    /**
     * 设置三图
     */
    private void setThreeImg(BaseFeedBean bean) {
        mCoinAdLayout.setVisibility(View.GONE);
        mThreePicLayout.setVisibility(View.VISIBLE);
        List<String> imageArray = bean.getImageArray();
        if (imageArray != null && !imageArray.isEmpty()) {
            WeImageLoader.getInstance().load(mContext, imageArray.get(0),
                    ILoader.Options.defaultOptions(), new ImageLoadCallback() {
                        @Override
                        public void onLoadReady(Drawable drawable) {
                            if (drawable != null) {
                                mCoinAd1Img.setBackground(drawable);
                            }
                        }
                    });
            if (!StringUtil.isNull(imageArray.get(1))) {
                WeImageLoader.getInstance().load(mContext, imageArray.get(1),
                        ILoader.Options.defaultOptions(), new ImageLoadCallback() {
                            @Override
                            public void onLoadReady(Drawable drawable) {
                                if (drawable != null) {
                                    mCoinAd2Img.setBackground(drawable);
                                }
                            }
                        });
            }
            if (!StringUtil.isNull(imageArray.get(2))) {
                WeImageLoader.getInstance().load(mContext, imageArray.get(2),
                        ILoader.Options.defaultOptions(), new ImageLoadCallback() {
                            @Override
                            public void onLoadReady(Drawable drawable) {
                                if (drawable != null) {
                                    mCoinAd3Img.setBackground(drawable);
                                }
                            }
                        });
            }
        }
    }

    @OnClick(R.id.coin_layout)
    public void onViewClicked() {
        dismiss();
    }
}
