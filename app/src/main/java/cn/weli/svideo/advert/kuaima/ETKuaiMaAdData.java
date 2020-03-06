package cn.weli.svideo.advert.kuaima;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Toast;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.advert.download.DownloadMarketService;
import cn.weli.svideo.advert.kuaima.deeplink.ETKuaiMaAdDeeplinkEvent;
import cn.weli.svideo.advert.kuaima.deeplink.OpenAppSuccEvent;
import cn.weli.svideo.baselib.component.widget.toast.ToastCompat;
import cn.weli.svideo.baselib.component.widget.toast.WeToast;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.ThreadPoolUtil;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.http.NetManager;
import cn.weli.svideo.module.main.ui.WebViewActivity;
import cn.weli.svideo.module.main.view.IWebView;
import cn.weli.svideo.module.task.component.helper.DrawAdRewardHelper;

/**
 * Created by sulei on 2016/10/25.
 */
public class ETKuaiMaAdData {
    private Context context;
    public String type;
    public int actionType = 0;//广告交互类型:0-未知类型;1-跳转类;2-下载类
    public String title = "";
    public String desc = "";
    public String iconurl = "";
    public String imgurl = "";
    public ArrayList<String> imgurls = new ArrayList<>();
    public String clickurl = "";
    public int exposure_time = -1;
    public String id = "";
    public int third_sdk = -1;
    public long start_time, end_time;
    public String domain = "";
    public ArrayList<String> viewtrackurls = new ArrayList<>();
    public ArrayList<String> clicktrackurls = new ArrayList<>();
    public ETKuaiMaAdDownloadData kuaiMaAdDownloadData = new ETKuaiMaAdDownloadData();
    public ETKuaiMaVideoData kuaiMaVideoData = new ETKuaiMaVideoData();
    public int video_type;
    public int ad_layout;//0.未知1.单小图2.单大图3.三小图4.视频 6沉浸式视频
    public ArrayList<String> dislike_urls = new ArrayList<>();
    public String dislike_url;
    public String app_name;
    public String source_icon;//广告来源icon地址
    public String deep_link_url;
    public ArrayList<ETKuaiMaAdDeeplinkEvent> deep_link_track_events = new ArrayList<>();
    private static HashMap<String, Integer> KuaiMaPvMap = new HashMap<>();
    public String wx_id;
    public String wx_btn_content;
    public ArrayList<String> wx_call_success_track_urls = new ArrayList<>();
    public ArrayList<String> wx_call_fail_track_urls = new ArrayList<>();

    /**
     * 播放总时长
     */
    private int mTotalPlayTime;
    /**
     * 播放有效次数
     */
    private int mValidTimeCount;

    /**
     * 用于替换的宏
     */
    private final String TS = "$TS";
    private final String CLK_DOWN_X = "$CLK_DOWN_X";
    private final String CLK_DOWN_Y = "$CLK_DOWN_Y";
    private final String CLK_UP_X = "$CLK_UP_X";
    private final String CLK_UP_Y = "$CLK_UP_Y";
    private final String DISLIKE_REASON = "__REASON__";
    private final String CLICK_TYPE = "$CLICK_TYPE";

    private final String __EVENT_TYPE__ = "__EVENT_TYPE__";
    private final String __OFFSET_PCT__ = "__OFFSET_PCT__";
    private final String __OFFSET__ = "__OFFSET__";
    private final String __NETWORK__ = "__NETWORK__";
    private final String __VALID_COUNT__ = "__VALID_COUNT__";
    private final String __TOTAL_OFFSET__ = "__TOTAL_OFFSET__";

    public ETKuaiMaAdData(Activity context, String type) {
        this.context = context;
        this.type = type;
    }

    public void parseJson(JSONObject object) {
        try {
            app_name = object.optString("app_name", "");
            actionType = object.optInt("action_type", 0);
            title = object.optString("title", "");
            desc = object.optString("desc", "");
            third_sdk = object.optInt("third_sdk", -1);
            start_time = object.optLong("start_time", 0);
            end_time = object.optLong("end_time", 0);
            iconurl = object.optString("icon_url", "");
            JSONArray imageUrls = object.optJSONArray("image_urls");
            if (imageUrls != null && imageUrls.length() > 0) {
                imgurl = imageUrls.optString(0);
                for (int i = 0; i < imageUrls.length(); i++) {
                    String url = imageUrls.optString(i, "");
                    imgurls.add(url);
                }
            }
            clickurl = object.optString("click_url", "");
            id = object.optString("id", "");
            exposure_time = object.optInt("exposure_time", -1);
            JSONArray viewtrackurl = object.optJSONArray("view_track_urls");
            if (viewtrackurl != null) {
                for (int i = 0; i < viewtrackurl.length(); i++) {
                    String url = viewtrackurl.optString(i, "");
                    viewtrackurls.add(url);
                }
            }
            JSONArray clicktrackurl = object.optJSONArray("click_track_urls");
            if (clicktrackurl != null) {
                for (int i = 0; i < clicktrackurl.length(); i++) {
                    String url = clicktrackurl.optString(i, "");
                    clicktrackurls.add(url);
                }
            }
            JSONArray dislikeurls = object.optJSONArray("dislike_urls");
            if (dislikeurls != null) {
                for (int i = 0; i < dislikeurls.length(); i++) {
                    String url = dislikeurls.optString(i, "");
                    dislike_urls.add(url);
                }
            }
            dislike_url = object.optString("dislike_url", "");
            deep_link_url = object.optString("deep_link_url", "");
            JSONArray deeplinktrackevents = object.optJSONArray("deep_link_track_events");
            if (deeplinktrackevents != null) {
                for (int i = 0; i < deeplinktrackevents.length(); i++) {
                    JSONObject deeplinkObj = deeplinktrackevents.optJSONObject(i);
                    ETKuaiMaAdDeeplinkEvent event = new ETKuaiMaAdDeeplinkEvent();
                    event.parseJson(deeplinkObj);
                    deep_link_track_events.add(event);
                }
            }
            domain = object.optString("domain", "");
            source_icon = object.optString("source_icon", "");
            ad_layout = object.optInt("ad_layout");
            kuaiMaAdDownloadData.parseJson(object);
            JSONObject video = object.optJSONObject("video");
            video_type = object.optInt("video_type");
            if (video != null) {
                kuaiMaVideoData.parseJson(video);
            }
            JSONArray wxcallsuccesstrack_urls = object.optJSONArray("wx_call_success_track_urls");
            if (wxcallsuccesstrack_urls != null) {
                for (int i = 0; i < wxcallsuccesstrack_urls.length(); i++) {
                    String url = wxcallsuccesstrack_urls.optString(i, "");
                    wx_call_success_track_urls.add(url);
                }
            }
            JSONArray wxcallfailtrack_urls = object.optJSONArray("wx_call_fail_track_urls");
            if (wxcallfailtrack_urls != null) {
                for (int i = 0; i < wxcallfailtrack_urls.length(); i++) {
                    String url = wxcallfailtrack_urls.optString(i, "");
                    wx_call_fail_track_urls.add(url);
                }
            }
            wx_id = object.optString("wx_id", "");
            wx_btn_content = object.optString("wx_btn_content", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onExposured() {
        boolean shouldPv = true;
        try {
            String key = id;
            try {
                if (ad_layout == 4 || ad_layout == 5 || ad_layout == 6) {
                    key = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.media_files.get(0).media.id;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (exposure_time <= 0) {
                shouldPv = true;
            } else {
                if (KuaiMaPvMap == null) {
                    KuaiMaPvMap = new HashMap<>();
                }
                if (KuaiMaPvMap.containsKey(key)) {
                    int num = KuaiMaPvMap.get(key);
                    if (num < exposure_time) {
                        KuaiMaPvMap.put(key, num + 1);
                        shouldPv = true;
                    } else {
                        shouldPv = false;
                    }
                } else {
                    KuaiMaPvMap.put(key, 1);
                    shouldPv = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ad_layout == 4 || ad_layout == 5 || ad_layout == 6) {
            try {
                viewtrackurls.clear();
                viewtrackurls.addAll(kuaiMaVideoData.ads.get(0).inline.impressions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (shouldPv && viewtrackurls != null && viewtrackurls.size() > 0) {
            CustomViewAsyncTask customViewAsyncTask = new CustomViewAsyncTask();
            customViewAsyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
        }
    }

    public static void clearMapData() {
        if (KuaiMaPvMap != null) {
            KuaiMaPvMap.clear();
            KuaiMaPvMap = null;
        }
    }

    /**
     * 获取微信按钮文案，如果为空，则证明不是微信样式
     */
    public String getWxBtn() {
        if (StringUtil.isNull(wx_btn_content)) {
            return "加微聊聊";
        }
        return wx_btn_content;
    }

    /**
     * 是否为微信按钮类型
     */
    public boolean isWxAd() {
        return !StringUtil.isNull(wx_id);
    }

    private long timestamp;
    private float dx, dy, ux, uy;

    /**
     * 设置点击时需要替换宏的额外参数,需要在onClicked方法前调用
     */
    public void setClickTrackParas(long timestamp, float dx, float dy, float ux, float uy) {
        this.timestamp = timestamp;
        this.dx = dx;
        this.dy = dy;
        this.ux = ux;
        this.uy = uy;
    }

    public void onDislikeClick(String reason) {
        if (ad_layout == 4 || ad_layout == 5 || ad_layout == 6) {
            try {
                dislike_urls.clear();
                dislike_urls.addAll(kuaiMaVideoData.ads.get(0).inline.dislike_urls);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ((!TextUtils.isEmpty(dislike_url) || dislike_urls.size() > 0) && !TextUtils.isEmpty(reason)) {
            DislikeClickAsyncTask customClickAsyncTask = new DislikeClickAsyncTask();
            customClickAsyncTask.setReason(reason);
            customClickAsyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
        }
    }

    public void onTongjiTrack(long timestamp, int event_type, float offset_pct, int offset) {
        CustomVideoPlayAsyncTask videoPlayAsyncTask = new CustomVideoPlayAsyncTask();
        videoPlayAsyncTask.setTrackParas(timestamp, event_type, offset_pct, offset);
        videoPlayAsyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
    }

    /**
     * 增加参数isFromLoadingView,点击开屏而来传true,其余都传false,
     * 用于WebViewActivity点back时返回主界面
     */
    public void onClicked(boolean fromLoadingView) {
        onClicked(fromLoadingView, 0);
    }

    /**
     * 增加参数isFromLoadingView,点击开屏而来传true,其余都传false,
     * 用于WebViewActivity点back时返回主界面
     */
    public void onClicked(boolean fromLoadingView, int md) {
        boolean isVideo = ad_layout == 4 || ad_layout == 5 || ad_layout == 6;
        String deepLink = "";
        String packageName = "";
        if (isVideo) {
            deepLink = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.deep_link_url;
            packageName = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.package_name;
        } else {
            deepLink = deep_link_url;
            packageName = kuaiMaAdDownloadData.package_name;
        }

        if (!TextUtils.isEmpty(deepLink)) {
            if (TextUtils.isEmpty(packageName)) {
                //上报open_url_app
                launchDeepLink(fromLoadingView, md, deepLink);
                deepLinkTongji("open_url_app");
            } else {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                if (null != intent) {
                    //上报open_url_app
                    launchDeepLink(fromLoadingView, md, deepLink);
                    deepLinkTongji("open_url_app");
                } else {//上报open_fallback_url
                    normalClick(fromLoadingView, md);
                    deepLinkTongji("open_fallback_url");
                }
            }
        } else {
            normalClick(fromLoadingView, md);
        }
    }

    private void launchDeepLink(boolean fromLoadingView, int md, String deepLink) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PackageManager packageManager = context.getPackageManager();
            List<ResolveInfo> applist = packageManager.queryIntentActivities(intent, 0);
            if (applist != null && applist.size() > 0) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        OpenAppSuccEvent.obtain().start(new OpenAppSuccEvent.OnEventCallBack() {
            @Override
            public void onCallBack(boolean success) {
                if (success) {//上报dpl_success
                    deepLinkTongji("dpl_success");
                } else {//上报dpl_fail
                    deepLinkTongji("dpl_fail");
                    normalClick(fromLoadingView, md);
                }
            }
        });
    }

    private void normalClick(boolean fromLoadingView, int md) {
        boolean isDownload = false;
        boolean isVideo = ad_layout == 4 || ad_layout == 5 || ad_layout == 6;
        if (isVideo) {
            try {
                clickurl = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.click_through;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(clickurl)) {
            String url;
            try {
                String oldToken = clickurl;
                url = clickurl.replace(TS, timestamp + "")
                        .replace(CLK_DOWN_X, dx + "")
                        .replace(CLK_DOWN_Y, dy + "")
                        .replace(CLK_UP_X, ux + "")
                        .replace(CLK_UP_Y, uy + "");
                DrawAdRewardHelper.getInstance().updateTaskItem(oldToken, url);
            } catch (Exception e) {
                e.printStackTrace();
                url = clickurl;
            }
            if (isVideo) {
                try {
                    isDownload = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).action_type == 2;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                isDownload = actionType == 2;
            }
            String iconUrl = "";
            if (isDownload) {
                if (isVideo) {
                    try {
                        ETKuaiMaAdDownloadData kuaiMaAdDownloadData = new ETKuaiMaAdDownloadData();
                        kuaiMaAdDownloadData.package_name = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.package_name;
                        kuaiMaAdDownloadData.download_start_track_urls.addAll(kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.download_start_track_urls);
                        kuaiMaAdDownloadData.download_success_track_urls.addAll(kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.download_success_track_urls);
                        kuaiMaAdDownloadData.install_start_track_urls.addAll(kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.install_start_track_urls);
                        kuaiMaAdDownloadData.install_success_track_urls.addAll(kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.install_success_track_urls);
                        this.kuaiMaAdDownloadData = kuaiMaAdDownloadData;
                        app_name = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.app_name;
                        iconUrl = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.icon.url;
                        DrawAdRewardHelper.getInstance().upDateTaskStatus(kuaiMaAdDownloadData.package_name,
                                DrawAdRewardHelper.STATUS_DOWNLOADING);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                DownloadMarketService.DownloadTheApk(context, 0, "", title, url, iconUrl, kuaiMaAdDownloadData);
            } else {
                Intent web = new Intent(context, WebViewActivity.class);
                web.putExtra(IWebView.EXTRA_WEB_VIEW_TITLE, title);
                web.putExtra(IWebView.EXTRA_WEB_VIEW_URL, url);
                web.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                web.putExtra(IWebView.EXTRA_WEB_VIEW_TOKEN, url);
                web.putExtra(IWebView.EXTRA_WEB_VIEW_FROM_AD, true);
                context.startActivity(web);
            }
        }

        executeClickTrackUrls(isVideo);
    }

    public void onClickWx() {
        boolean isVideo = ad_layout == 4 || ad_layout == 5 || ad_layout == 6;
        executeClickTrackUrls(isVideo, 1);
        String WECHAT_APP_ID = PackageHelper.getMetaData(WlVideoAppInfo.sAppCtx, BusinessConstants.MetaData.Wx_APP_ID);
        ;
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        IWXAPI api = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, true);
        if (api.isWXAppInstalled()) {
            ClipboardManager clipboardManager = (ClipboardManager) WlVideoAppInfo.sAppCtx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (null != clipboardManager) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText("", wx_id));
                try {
                    ToastCompat.makeText(WlVideoAppInfo.sAppCtx, WlVideoAppInfo.sAppCtx.getResources().getString(R.string.share_wx_has_copy),
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            api.openWXApp();
            OpenAppSuccEvent.obtain().start(success -> {
                if (success) {//上报wx_call_success_track_urls
                    executeCallWxTrackUrls(true);
                } else {//上报wx_call_success_track_urls
                    executeCallWxTrackUrls(false);
                }
            });
        } else {
            WeToast.getInstance().showToast(WlVideoAppInfo.sAppCtx, R.string.share_wx_not_installed);
            executeCallWxTrackUrls(false);
        }
    }

    private void executeCallWxTrackUrls(boolean success) {
        CustomCallWxAsyncTask customCallWxAsyncTask = new CustomCallWxAsyncTask();
        customCallWxAsyncTask.setCallSuccess(success);
        customCallWxAsyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
    }

    private void executeClickTrackUrls(boolean isVideo) {
        executeClickTrackUrls(isVideo, 0);
    }

    /**
     * click_from 0表示普通点击 1表示微信按钮点击
     */
    private void executeClickTrackUrls(boolean isVideo, int click_from) {
        if (isVideo) {
            try {
                clicktrackurls.clear();
                clicktrackurls.addAll(kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.click_trackings);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (clicktrackurls != null && clicktrackurls.size() > 0) {
            CustomClickAsyncTask customClickAsyncTask = new CustomClickAsyncTask();
            customClickAsyncTask.setClickTrackParas(timestamp, dx, dy, ux, uy, click_from);
            customClickAsyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
        }
    }

    private void deepLinkTongji(String event_name) {
        CustomDeepLinkAsyncTask deepLinkAsyncTask = new CustomDeepLinkAsyncTask();
        deepLinkAsyncTask.setTrackParas(event_name);
        deepLinkAsyncTask.executeOnExecutor(ThreadPoolUtil.getInstance().getExecutor());
    }

    private class CustomViewAsyncTask extends AsyncTask<Void, Integer, Void> {
        /**
         * 替换宏的额外参数
         */
        private String replaceUrl(String url) {
            return url.replace(TS, System.currentTimeMillis() + "");
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < viewtrackurls.size(); i++) {
                String url = viewtrackurls.get(i);
                url = replaceUrl(url);
                int code = NetManager.getInstance().doGetAsCode(url);
                if (code > 400) {//code可能存在3xx或2xx成功的情况
                    NetManager.getInstance().doGetAsCode(url);
                }
            }
            return null;
        }
    }

    private class CustomClickAsyncTask extends AsyncTask<Void, Integer, Void> {

        private long timestamp;
        private float dx, dy, ux, uy;
        private int click_from;

        /**
         * 设置点击时需要替换宏的额外参数
         */
        void setClickTrackParas(long timestamp, float dx, float dy, float ux, float uy, int click_from) {
            this.timestamp = timestamp;
            this.dx = dx;
            this.dy = dy;
            this.ux = ux;
            this.uy = uy;
            this.click_from = click_from;
        }

        /**
         * 替换字符串
         */
        String replaceUrl(String url) {
            return url.replace(TS, timestamp + "")
                    .replace(CLK_DOWN_X, dx + "")
                    .replace(CLK_DOWN_Y, dy + "")
                    .replace(CLK_UP_X, ux + "")
                    .replace(CLK_UP_Y, uy + "")
                    .replace(CLICK_TYPE, click_from + "");
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < clicktrackurls.size(); i++) {
                String url = clicktrackurls.get(i);
                url = replaceUrl(url);
                int code = NetManager.getInstance().doGetAsCode(url);
                if (code > 400) {//code可能存在3xx或2xx成功的情况
                    NetManager.getInstance().doGetAsCode(url);
                }
            }
            return null;
        }
    }

    private class DislikeClickAsyncTask extends AsyncTask<Void, Integer, Void> {
        private String reason;

        public void setReason(String reason) {
            this.reason = reason;
        }

        /**
         * 替换字符串
         */
        String replaceUrl(String url) {
            return url.replace(DISLIKE_REASON, reason);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < dislike_urls.size(); i++) {
                String url = dislike_urls.get(i);
                url = replaceUrl(url);
                int code = NetManager.getInstance().doGetAsCode(url);
                if (code > 400) {//code可能存在3xx或2xx成功的情况
                    NetManager.getInstance().doGetAsCode(url);
                }
            }
            if (!TextUtils.isEmpty(dislike_url)) {
                String url = replaceUrl(dislike_url);
                int code = NetManager.getInstance().doGetAsCode(url);
                if (code > 400) {//code可能存在3xx或2xx成功的情况
                    NetManager.getInstance().doGetAsCode(url);
                }
            }
            return null;
        }
    }

    private class CustomVideoPlayAsyncTask extends AsyncTask<Void, Integer, Void> {
        private int event_type;
        private long timestamp;
        private float offset_pct;
        private int offset;
        private int mTotalTime;
        private int mValidTimes;

        /**
         * 替换字符串
         */
        String replaceUrl(String url) {
            int network;
            String net = NetManager.getNetworkType(context);
            if (net.equals("UNKNOW")) {
                network = 0;
            } else if (net.equals("WIFI")) {
                network = 1;
            } else {
                network = 2;
            }

            return url.replace(TS, timestamp + "")
                    .replace(__EVENT_TYPE__, event_type + "")
                    .replace(__OFFSET__, offset + "")
                    .replace(__OFFSET_PCT__, formatFloat(offset_pct))
                    .replace(__NETWORK__, network + "")
                    .replace(__TOTAL_OFFSET__, mTotalTime + "")
                    .replace(__VALID_COUNT__, mValidTimes + "");
        }

        private String formatFloat(float num) {
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            return decimalFormat.format(num);//format 返回的是字符串
        }

        public void setTrackParas(long timestamp, int event_type, float offset_pct, int offset) {
            this.timestamp = timestamp;
            this.event_type = event_type;
            this.offset_pct = offset_pct;
            this.offset = offset;
            mTotalPlayTime = (int) (mTotalPlayTime + offset * offset_pct);
            mTotalTime = mTotalPlayTime;
            try {
                if (mTotalPlayTime > kuaiMaVideoData.ads.get(0).inline.creatives.get(0).valid_time) {
                    mValidTimeCount++;
                    mValidTimes = mValidTimeCount;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getEventName(int event_type) {
            if (event_type == 200) {
                return "start";
            } else if (event_type == 201) {
                return "pause";
            } else if (event_type == 205) {
                return "complete";
            }
            return "";
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ArrayList<String> urls = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.general_event_trackings;
                for (int i = 0; i < urls.size(); i++) {
                    String url = urls.get(i);
                    url = replaceUrl(url);
                    int code = NetManager.getInstance().doGetAsCode(url);
                    if (code > 400) {//code可能存在3xx或2xx成功的情况
                        NetManager.getInstance().doGetAsCode(url);
                    }
                }
                String event_name = getEventName(event_type);
                ArrayList<ETKuaiMaVideoData.EventTracking> event_trackings = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.event_trackings;
                for (int i = 0; i < event_trackings.size(); i++) {
                    ETKuaiMaVideoData.EventTracking eventTracking = event_trackings.get(i);
                    if (TextUtils.equals(eventTracking.event, event_name)) {
                        String url = eventTracking.url;
                        url = replaceUrl(url);
                        int code = NetManager.getInstance().doGetAsCode(url);
                        if (code > 400) {//code可能存在3xx或2xx成功的情况
                            NetManager.getInstance().doGetAsCode(url);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class CustomDeepLinkAsyncTask extends AsyncTask<Void, Integer, Void> {
        private String event_name;

        public void setTrackParas(String event_name) {
            this.event_name = event_name;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                onDeepLinkTongji(event_name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private void onDeepLinkTongji(String event_name) {
            boolean isVideo = ad_layout == 4 || ad_layout == 5;
            ArrayList<ETKuaiMaAdDeeplinkEvent> deep_link_events;
            if (isVideo) {
                deep_link_events = kuaiMaVideoData.ads.get(0).inline.creatives.get(0).linear.deep_link_track_events;
            } else {
                deep_link_events = deep_link_track_events;
            }
            for (int i = 0; i < deep_link_events.size(); i++) {
                ETKuaiMaAdDeeplinkEvent eventTracking = deep_link_events.get(i);
                if (TextUtils.equals(eventTracking.event, event_name)) {
                    String url = eventTracking.url;
                    int code = NetManager.getInstance().doGetAsCode(url);
                    if (code > 400) {//code可能存在3xx或2xx成功的情况
                        NetManager.getInstance().doGetAsCode(url);
                    }
                }
            }
        }
    }

    private class CustomCallWxAsyncTask extends AsyncTask<Void, Integer, Void> {
        private boolean success;

        public void setCallSuccess(boolean success) {
            this.success = success;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<String> urls;
            if (success) {
                urls = wx_call_success_track_urls;
            } else {
                urls = wx_call_fail_track_urls;
            }

            for (int i = 0; i < urls.size(); i++) {
                String url = urls.get(i);
                int code = NetManager.getInstance().doGetAsCode(url);
                if (code > 400) {//code可能存在3xx或2xx成功的情况
                    NetManager.getInstance().doGetAsCode(url);
                }
            }
            return null;
        }
    }
}
