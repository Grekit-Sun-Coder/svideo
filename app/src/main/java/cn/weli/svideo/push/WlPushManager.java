package cn.weli.svideo.push;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Looper;

import com.igexin.sdk.PushManager;

import java.util.Random;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.helper.glide.GlideDownloadHelper;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.baselib.utils.GsonUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.module.main.model.bean.PushBean;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.main.ui.SplashActivity;
import cn.weli.svideo.module.main.view.IMainView;

/**
 * 推送管理类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see WlVideoIntentService
 * @since [1.0.0]
 */
public class WlPushManager {

    /**
     * push action id，显示和点击
     */
    public static final int PUSH_ACTION_ID_SHOW = 90005;
    public static final int PUSH_ACTION_ID_CLICK = 90004;

    /**
     * 处理server推送过来的消息
     */
    public static void handlerMsgFromServerPush(final Context context, String pushData, final String messageId, final String taskId) {
        final PushBean pushBean = (PushBean) GsonUtil.fromJsonToObject(pushData, PushBean.class);
        if (pushBean == null) {
            return;
        }
        PushBean.C pushContent = pushBean.c;
        //没有具体的内容;
        if (pushContent == null) {
            return;
        }
        String imgUrl = pushContent.img;
        if (StringUtil.isNull(imgUrl) || Build.BRAND.equalsIgnoreCase("xiaomi")) {
            showNotification(context, messageId, taskId, pushBean, null);
        } else {
            try {
                int dp20 = DensityUtil.dp2px(context, 20);
                boolean mainThread = Looper.getMainLooper() == Looper.myLooper();
                if (mainThread) {
                    GlideDownloadHelper.imageBitmap(context.getApplicationContext(), imgUrl, dp20, dp20,
                            new GlideDownloadHelper.Callback<Bitmap>() {
                                @Override
                                public void onSuccess(Bitmap bitmap) {
                                    if (bitmap != null && !bitmap.isRecycled()) {
                                        showNotification(context, messageId, taskId, pushBean, bitmap);
                                    } else {
                                        showNotification(context, messageId, taskId, pushBean, null);
                                    }
                                }

                                @Override
                                public void onFail() {
                                    showNotification(context, messageId, taskId, pushBean, null);
                                }
                            });
                } else {
                    Bitmap bitmap = GlideDownloadHelper.getBitmap(context.getApplicationContext(), imgUrl, dp20, dp20);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        showNotification(context, messageId, taskId, pushBean, bitmap);
                    } else {
                        showNotification(context, messageId, taskId, pushBean, null);
                    }
                }
            } catch (Exception ex) {
                Logger.e(ex.getMessage());
            }
        }
    }

    private static void showNotification(Context context, String messageid, String taskid, PushBean pushBean, Bitmap bmpIcon) {
        if (pushBean == null) {
            return;
        }
        String title = pushBean.a;
        String content = pushBean.b;
        Random random = new Random();
        int randomInt = random.nextInt(Integer.MAX_VALUE);

        if (StringUtil.isNull(title)) {
            title = context.getString(R.string.app_name);
        }
        Intent intent = new Intent();
        if (WlVideoAppInfo.getInstance().isAppRunning()) {
            Logger.d("当app已经运行，跳转到MainActivity");
            // 当app已经运行，跳转到MainActivity
            intent.setClass(context, MainActivity.class);
        } else {
            Logger.d("若app未运行，先跳转到SplashActivity");
            // 若app未运行，先跳转到SplashActivity
            intent.setClass(context, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        String protocol = pushBean.c.b;
        intent.putExtra(IMainView.EXTRA_PROTOCOL, protocol);
        intent.putExtra(IMainView.EXTRA_FROM_PUSH, true);
        intent.putExtra(IMainView.EXTRA_PUSH_TASK_ID, taskid);
        intent.putExtra(IMainView.EXTRA_PUSH_MSG_ID, messageid);
        Logger.d("notification title=" + title + " content=" + content + " protocol=" + protocol);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, randomInt,
                new Intent[]{intent}, PendingIntent.FLAG_CANCEL_CURRENT);

        new NotificationUtils(context).sendNotification(randomInt, title, content, pendingIntent);
        PushManager.getInstance().sendFeedbackMessage(WlVideoAppInfo.sAppCtx, taskid, messageid, PUSH_ACTION_ID_SHOW);
        StatisticsAgent.push(context, StatisticsUtils.EventName.PUSH_MSG_VIEW, StatisticsUtils.CID.CID_1, StatisticsUtils.MD.MD_9);
    }

}
