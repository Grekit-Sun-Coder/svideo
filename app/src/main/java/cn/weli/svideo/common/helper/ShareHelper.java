package cn.weli.svideo.common.helper;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.component.widget.toast.WeToast;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.BusinessConstants;

/**
 * 分享帮助类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-01-13
 * @see [class/method]
 * @since [1.0.0]
 */
public class ShareHelper {

    /**
     * 分享
     *
     * @param activity 页面
     * @param platform 平台
     * @param url      地址
     * @param title    标题
     * @param thumb    封面
     * @param desc     副标题
     * @param listener 监听
     */
    public static void share(Activity activity, String platform, String url, String title,
            String thumb,
            String desc, OnShareResultListener listener) {
        if (activity == null || StringUtil.isNull(platform) || StringUtil.isNull(url)) {
            return;
        }
        switch (platform) {
            case BusinessConstants.ShareType.PLATFORM_WEIXIN:
                share2Third(activity, url, title, thumb, desc, SHARE_MEDIA.WEIXIN, listener);
                break;
            case BusinessConstants.ShareType.PLATFORM_WEIXIN_PYQ:
                share2Third(activity, url, title, thumb, desc, SHARE_MEDIA.WEIXIN_CIRCLE, listener);
                break;
            case BusinessConstants.ShareType.PLATFORM_QQ:
                share2Third(activity, url, title, thumb, desc, SHARE_MEDIA.QQ, listener);
                break;
            case BusinessConstants.ShareType.COPY_LINK:
                copy2Board(url);
                break;
            default:
                break;
        }
    }

    /**
     * 分享到第三方
     */
    private static void share2Third(Activity activity, String url, String title, String thumb,
            String desc, SHARE_MEDIA media, OnShareResultListener listener) {
        if (media == SHARE_MEDIA.WEIXIN || media == SHARE_MEDIA.WEIXIN_CIRCLE) {
            PlatformConfig.setWeixin(PackageHelper.getMetaData(WlVideoAppInfo.sAppCtx,
                    BusinessConstants.MetaData.Wx_APP_ID),
                    PackageHelper.getMetaData(WlVideoAppInfo.sAppCtx,
                            BusinessConstants.MetaData.Wx_APP_SECRET));
        } else if (media == SHARE_MEDIA.QQ) {
            PlatformConfig.setQQZone(PackageHelper.getMetaData(WlVideoAppInfo.sAppCtx,
                    BusinessConstants.MetaData.QQ_APP_ID),
                    PackageHelper.getMetaData(WlVideoAppInfo.sAppCtx,
                            BusinessConstants.MetaData.QQ_APP_KEY));
        }
        UMWeb web = new UMWeb(url);
        web.setTitle(title);
        web.setThumb(new UMImage(activity, thumb));
        web.setDescription(desc);
        new ShareAction(activity)
                .setPlatform(media)
                .withMedia(web)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA media) {
                    }

                    @Override
                    public void onResult(SHARE_MEDIA media) {
                        if (listener != null) {
                            listener.onShareSuccess();
                        }
                    }

                    @Override
                    public void onError(SHARE_MEDIA media, Throwable throwable) {
                        Logger.e(throwable.getMessage());
                        if (listener != null) {
                            listener.onShareFailed(throwable.getMessage());
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA media) {
                        if (listener != null) {
                            listener.onShareCancel();
                        }
                    }
                })
                .share();
    }

    /**
     * 复制链接
     *
     * @param url 地址
     */
    private static void copy2Board(String url) {
        ClipboardManager clipboardManager = (ClipboardManager) WlVideoAppInfo.sAppCtx.getSystemService(
                Context.CLIPBOARD_SERVICE);
        if (null != clipboardManager) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText("share", url));
            try {
                WeToast.getInstance().showToast(WlVideoAppInfo.sAppCtx, WlVideoAppInfo.sAppCtx.getResources().getString(R.string.share_copy_title));
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    public interface OnShareResultListener {

        /**
         * 分享成功
         */
        void onShareSuccess();

        /**
         * 分享失败
         *
         * @param msg   错误信息，不要用，可以测试用
         */
        void onShareFailed(String msg);

        /**
         * 分享取消
         */
        void onShareCancel();
    }
}
