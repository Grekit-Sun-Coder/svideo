package cn.weli.svideo.push;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.weli.svideo.R;

/**
 * 通知栏工具类(兼容低版本).
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see [class/method]
 * @since [1.0.0]
 */
public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;

    public static final String mId = "common";

    public static final String mChannel = "common_channel";

    public NotificationUtils(Context base) {
        super(base);
    }

    public void sendNotification(int id, String title, String content) {
        sendNotification(id, title, content, null);
    }

    public void sendNotification(int id, String title, String content,
                                 PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            Notification notification = getNotification(title, content, pendingIntent).build();
            getManager().notify(id, notification);
        } else {
            Notification notification = getNotificationBelow25(title, content,
                    pendingIntent).build();
            notification.defaults = Notification.DEFAULT_SOUND;
            getManager().notify(id, notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(mId, mChannel,
                NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        getManager().createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder getNotification(String title, String content,
                                                 PendingIntent pendingIntent) {
        return new Notification.Builder(getApplicationContext(), mId)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.push_small)
                .setAutoCancel(true);
    }

    private NotificationCompat.Builder getNotificationBelow25(String title, String content,
                                                              PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.push_small)
                .setAutoCancel(true);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    /**
     * 判断通知权限是否打开(api >= 19)
     *
     * @param context context
     * @return 通知权限是否打开
     */
    public static boolean isNotificationEnable(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return isNotificationEnable24(context);
        } else {
            return isNotificationEnable19(context);
        }
    }

    /**
     * 判断通知权限是否打开(api >= 19)
     *
     * @param context context
     * @return 通知权限是否打开
     */
    private static boolean isNotificationEnable19(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return false;
        }
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();

        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        // Context.APP_OPS_MANAGER
        Class appOpsClass = null;

        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE,
                    Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
            int value = (int) opPostNotificationValue.get(Integer.class);
            return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg)
                    == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 判断通知权限是否打开(api >= 24)
     *
     * @param context context
     * @return 通知权限是否打开
     */
    private static boolean isNotificationEnable24(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(
                context);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    public static void setNotification(Activity activity) {
        setNotification(activity, -1);
    }

    public static void setNotification(Activity activity, int requestCode) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // >=26
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
            if (requestCode != -1) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivity(intent);
            }
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // >=5.0
            try {
                Intent intent = new Intent();
                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                intent.putExtra("app_package", activity.getPackageName());
                intent.putExtra("app_uid", activity.getApplicationInfo().uid);
                if (requestCode != -1) {
                    activity.startActivityForResult(intent, requestCode);
                } else {
                    activity.startActivity(intent);
                }
            } catch (ActivityNotFoundException e) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                if (requestCode != -1) {
                    activity.startActivityForResult(intent, requestCode);
                } else {
                    activity.startActivity(intent);
                }
            }
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            // 4.4
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            if (requestCode != -1) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivity(intent);
            }
        }
    }

}
