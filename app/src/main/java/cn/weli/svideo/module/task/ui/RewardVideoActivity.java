package cn.weli.svideo.module.task.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.baidu.mobads.MobadsPermissionSettings;
import com.baidu.mobads.rewardvideo.RewardVideoAd;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;
import com.ttshell.sdk.api.TTRewardVideoOb;
import com.ttshell.sdk.api.config.TTObConstant;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.advert.bean.AdInfoBean;
import cn.weli.svideo.advert.toutiao.TtVideoAdHelper;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.constant.ConfigConstants;
import cn.weli.svideo.common.constant.ProtocolConstants;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.main.model.AdvertModel;
import cn.weli.svideo.module.task.component.helper.RewardVideoHelper;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.task.presenter.RewardVideoPresenter;
import cn.weli.svideo.module.task.view.IRewardVideoView;
import cn.weli.svideo.module.task.view.ITaskPageView;

/**
 * 激励视频
 *
 * @author Lei Jiang
 * @version [1.0.6]
 * @date 2019-11-25
 * @see TaskPageFragment
 * @since [1.0.0]
 */
public class RewardVideoActivity extends AppBaseActivity<RewardVideoPresenter, IRewardVideoView>
        implements IRewardVideoView {

    /**
     * 激励视频来源
     */
    private String mFrom;
    /**
     * 激励视频任务key
     */
    private String mTaskKey;
    /**
     * 激励视频任务位置名称
     */
    private String mTaskSpace;
    /**
     * 广告列表
     */
    private List<AdInfoBean> mAdList;
    /**
     * 百度ad数据
     */
    private RewardVideoAd mBdRewardVideoAd;
    /**
     * 广点通ad数据
     */
    private RewardVideoAD mGdtRewardVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);
        ButterKnife.bind(this);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(StringUtil.EMPTY_STR);
        setToolBarVisible(View.INVISIBLE);
        Intent intent = getIntent();
        if (intent != null) {
            String targetAdId = intent.getStringExtra(EXTRA_AD_ID);
            String targetSdk = intent.getStringExtra(EXTRA_AD_SDK);
            String backupAdId = intent.getStringExtra(EXTRA_BACKUP_AD_ID);
            String backupSdk = intent.getStringExtra(EXTRA_BACKUP_AD_SDK);
            mFrom = intent.getStringExtra(ProtocolConstants.SCHEME_FROM);
            mTaskKey = intent.getStringExtra(ITaskPageView.TASK_KEY);
            mTaskSpace = intent.getStringExtra(ITaskPageView.TASK_SPACE);
            Logger.d("Reward video :\n" + "targetAdId=" + targetAdId + "\ntargetSdk=" + targetSdk
                + "\nbackupAdId=" + backupAdId + "\nbackupSdk=" + backupSdk + "\nfrom=" + mFrom
                + "\ntaskKey=" + mTaskKey + "\ntaskSpace=" + mTaskSpace);
            initRewardVideo(targetSdk, targetAdId, backupSdk, backupAdId);
        }
    }

    /**
     * 加载激励视频
     *
     * @param targetSdk      目标sdk类型
     * @param targetAdId     目标广告id
     * @param supplementSdk  补量sdk类型
     * @param supplementAdId 补量广告id
     */
    private void initRewardVideo(String targetSdk, String targetAdId, String supplementSdk, String supplementAdId) {
        showLoadView(0);
        RewardVideoHelper.getInstance().onRewardVideoStart(mFrom, mTaskKey);
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
            mAdList.add(new AdInfoBean(AdConfigBean.VIDEO_AD_TYPE_TT, ConfigConstants.REWARD_VIDEO_ID));
        }
        loadRewardVideo();
    }

    /**
     * 加载激励视频，按照list顺序取
     */
    private void loadRewardVideo() {
        if (mAdList != null && !mAdList.isEmpty()) {
            AdInfoBean bean = mAdList.get(0);
            if (bean != null) {
                if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_TT)) {
                    // 头条
                    loadTtRewardVideo(bean);
                } else if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_BD)) {
                    // 百度
                    loadBdRewardVideo(bean);
                } else if (StringUtil.equals(bean.sdkType, AdConfigBean.VIDEO_AD_TYPE_GDT)) {
                    // 广点通
                    loadGdtRewardVideo(bean);
                }
            }
        } else {
            finishLoadView();
            showToast(R.string.common_str_network_error);
            finishActivity();
        }
    }

    /**
     * 加载激励视频广告
     *
     * @param bean 广告
     */
    private void loadTtRewardVideo(AdInfoBean bean) {
        Logger.d("Start load toutiao reward video");
        TtVideoAdHelper adHelper = new TtVideoAdHelper(this);
        adHelper.getRewardVideoAd(bean.adId, TTObConstant.VERTICAL,
                new TtVideoAdHelper.OnTtRewardVideoAdListener() {
                    @Override
                    public void onGetRewardVideoAdStart() {

                    }

                    @Override
                    public void onGetRewardVideoAdSuccess(TTRewardVideoOb videoAd) {
                        finishLoadView();
                        startTtVideoAd(videoAd);
                    }

                    @Override
                    public void onGetRewardVideoAdError(int code, String msg) {
                        Logger.e("Get toutiao reward video ad error is [" + code + "] [" + msg + "]");
                        StatisticsAgent.view(RewardVideoActivity.this, StatisticsUtils.CID.CID_602, StatisticsUtils.MD.MD_2);
                        removeCurrentAd(bean);
                    }
                });
    }

    /**
     * 展示激励视频广告
     *
     * @param videoAd 视频广告
     */
    private void startTtVideoAd(TTRewardVideoOb videoAd) {
        if (videoAd != null) {
            videoAd.setRewardObInteractionListener(new TTRewardVideoOb.RewardObInteractionListener() {

                @Override
                public void onObShow() {
                    Logger.d("Toutiao onAdShow");
                    AdvertModel.reportAdEvent(mTaskSpace, AdConfigBean.ACTION_TYPE_PV);
                }

                @Override
                public void onObVideoBarClick() {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("task", mTaskKey);
                        StatisticsAgent.click(RewardVideoActivity.this, StatisticsUtils.CID.CID_603, StatisticsUtils.MD.MD_2, "", object.toString(), "");

                        AdvertModel.reportAdEvent(mTaskSpace, AdConfigBean.ACTION_TYPE_CLICK);
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }

                @Override
                public void onObClose() {
                    finishActivity();
                }

                @Override
                public void onVideoComplete() {
                    Logger.d("Toutiao onVideoComplete");
                    RewardVideoHelper.getInstance().onRewardVideoComplete(mFrom, mTaskKey);
                }

                @Override
                public void onVideoError() {
                }

                @Override
                public void onRewardVerify(boolean verify, int i, String s) {
                }

                @Override
                public void onSkippedVideo() {
                }
            });
            videoAd.showRewardVideoOb(RewardVideoActivity.this, TTObConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
        }
    }

    /**
     * 加载百度激励视频
     *
     * @param bean 百度广告信息
     */
    private void loadBdRewardVideo(AdInfoBean bean) {
        Logger.d("Start load baidu reward video");
        MobadsPermissionSettings.setPermissionReadDeviceID(true);
        MobadsPermissionSettings.setPermissionAppList(true);
        //静态内部类的使用解决内存泄漏
        BdRewardVideoAdListener bdRewardVideoAdListener = new BdRewardVideoAdListener(this, bean);
        mBdRewardVideoAd = new RewardVideoAd(this, bean.adId, bdRewardVideoAdListener, true);
        mBdRewardVideoAd.load();
    }

    /**
     * 加载广点通激励视频
     *
     * @param bean 广点通广告信息
     */
    private void loadGdtRewardVideo(AdInfoBean bean) {
        Logger.d("Start load gdt reward video");
        mGdtRewardVideoAd = new RewardVideoAD(this, PackageHelper.getMetaData(this,
                BusinessConstants.MetaData.GDT_APP_ID), bean.adId, new RewardVideoADListener() {
            @Override
            public void onADLoad() {
                finishLoadView();
                // 视频缓存成功，如果想一定走本地播放，那么收到该回调之后，可以调用show
                if (mGdtRewardVideoAd != null) {
                    mGdtRewardVideoAd.showAD();
                }
            }

            @Override
            public void onVideoCached() {}

            @Override
            public void onADShow() {
                Logger.d("Gdt onAdShow");
            }

            @Override
            public void onADExpose() {}

            @Override
            public void onReward() {}

            @Override
            public void onADClick() {
                try {
                    JSONObject object = new JSONObject();
                    object.put("task", mTaskKey);
                    StatisticsAgent.click(RewardVideoActivity.this, StatisticsUtils.CID.CID_603, StatisticsUtils.MD.MD_2, "", object.toString(), "");

                    AdvertModel.reportAdEvent(mTaskSpace, AdConfigBean.ACTION_TYPE_CLICK);
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }

            @Override
            public void onVideoComplete() {
                Logger.d("Gdt reward video onVideoComplete");
                RewardVideoHelper.getInstance().onRewardVideoComplete(mFrom, mTaskKey);
            }

            @Override
            public void onADClose() {
                finishActivity();
            }

            @Override
            public void onError(AdError adError) {
                if (adError != null) {
                    Logger.e("Get gdt reward video ad error is [" + adError.getErrorCode() + "] [" + adError.getErrorMsg() + "]");
                }
                StatisticsAgent.view(RewardVideoActivity.this, StatisticsUtils.CID.CID_602, StatisticsUtils.MD.MD_2);
                removeCurrentAd(bean);
            }
        });
        mGdtRewardVideoAd.loadAD();
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
            loadRewardVideo();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    static class BdRewardVideoAdListener implements RewardVideoAd.RewardVideoAdListener {

        private WeakReference<RewardVideoActivity> mRef;
        private AdInfoBean mBean;

        private BdRewardVideoAdListener(RewardVideoActivity context, AdInfoBean bean) {
            mRef = new WeakReference<>(context);
            this.mBean = bean;
        }

        @Override
        public void onAdShow() {
            Logger.d("Baidu onAdShow");
            if (mRef == null) {
                return;
            }
            RewardVideoActivity activity = mRef.get();
            if (activity != null) {
                AdvertModel.reportAdEvent(activity.mTaskSpace, AdConfigBean.ACTION_TYPE_PV);
            }
        }

        @Override
        public void onAdClick() {
            try {
                if (mRef == null) {
                    return;
                }
                JSONObject object = new JSONObject();
                RewardVideoActivity activity = mRef.get();
                if (activity != null) {
                    object.put("task", activity.mTaskKey);
                    StatisticsAgent.click(activity, StatisticsUtils.CID.CID_603,
                            StatisticsUtils.MD.MD_2, "", object.toString(), "");
                    AdvertModel.reportAdEvent(activity.mTaskSpace,
                            AdConfigBean.ACTION_TYPE_CLICK);
                }
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }

        @Override
        public void onAdClose(float playScale) {
            if (mRef == null) {
                return;
            }
            RewardVideoActivity activity = mRef.get();
            if (activity != null) {
                activity.finishActivity();
            }
        }

        @Override
        public void onAdFailed(String msg) {
            Logger.e("Get baidu reward video ad error is [" + msg + "]");
            if (mRef == null) {
                return;
            }
            RewardVideoActivity activity = mRef.get();
            if (activity != null) {
                StatisticsAgent.view(activity, StatisticsUtils.CID.CID_602,
                        StatisticsUtils.MD.MD_2);
                activity.removeCurrentAd(mBean);
            }
        }

        @Override
        public void onVideoDownloadSuccess() {
            if (mRef == null) {
                return;
            }
            RewardVideoActivity activity = mRef.get();
            if (activity != null) {
                activity.finishLoadView();
                // 视频缓存成功，如果想一定走本地播放，那么收到该回调之后，可以调用show
                if (activity.mBdRewardVideoAd != null && activity.mBdRewardVideoAd.isReady()) {
                    activity.mBdRewardVideoAd.show();
                }
            }
        }

        @Override
        public void onVideoDownloadFailed() {
            onAdFailed(StringUtil.EMPTY_STR);
        }

        @Override
        public void playCompletion() {
            Logger.d("Baidu reward video onVideoComplete");
            if (mRef == null) {
                return;
            }
            RewardVideoActivity activity = mRef.get();
            if (activity != null) {
                RewardVideoHelper.getInstance().onRewardVideoComplete(activity.mFrom,
                        activity.mTaskKey);
            }
        }
    }

    @Override
    protected Class<RewardVideoPresenter> getPresenterClass() {
        return RewardVideoPresenter.class;
    }

    @Override
    protected Class<IRewardVideoView> getViewClass() {
        return IRewardVideoView.class;
    }
}
