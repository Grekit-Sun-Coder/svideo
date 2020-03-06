package cn.weli.svideo.baselib.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;

/**
 * 权限请求封装
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @see StatusBarHelper
 * @since [1.0.0]
 */
public class PermissionRequestHelper {

    public static void requestMultiPermission(Context context, Action grantedAction,
            Action deniedAction, String... permissions) {
        requestMultiPermission(context, grantedAction, deniedAction, null, permissions);
    }

    /**
     * 请求权限组.
     *
     * @param context       Context
     * @param grantedAction 权限允许监听
     * @param deniedAction  权限拒绝监听
     * @param rationale     请求失败的回调，用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框
     * @param permissions   权限组
     */
    public static void requestMultiPermission(Context context, Action grantedAction,
            Action deniedAction, Rationale rationale, String... permissions) {
        AndPermission.with(context)
                .permission(permissions)
                .onGranted(grantedAction)
                .onDenied(deniedAction)
                .rationale(rationale)
                .start();
    }

    public static void requestSinglePermission(Context context, Action grantedAction,
            Action deniedAction, String permission) {
        requestSinglePermission(context, grantedAction, deniedAction, null, permission);
    }

    /**
     * 请求单个权限.
     *
     * @param context       Context
     * @param grantedAction 权限允许监听
     * @param deniedAction  权限拒绝监听
     * @param rationale     请求失败的回调，用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框
     * @param permission    权限组
     */
    public static void requestSinglePermission(Context context, Action grantedAction,
            Action deniedAction, Rationale rationale, String permission) {
        AndPermission.with(context)
                .permission(permission)
                .onGranted(grantedAction)
                .onDenied(deniedAction)
                .rationale(rationale)
                .start();
    }

    /**
     * 检查权限是否拥有
     *
     * @param context    Context
     * @param permission 权限名称
     * @return 是否拥有
     */
    public static boolean hasGrantedPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission)
                == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * 跳转权限页面
     *
     * @param context Context
     */
    public static void startPermissionSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + context.getPackageName()))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e1) {
            }
        }
    }
}
