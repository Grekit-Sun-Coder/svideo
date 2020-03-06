package cn.weli.svideo.baselib.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import static android.text.TextUtils.isEmpty;

public class PackageHelper {
    private PackageInfo info = null;
    private PackageManager pm;

    public PackageHelper(Context context) {
        pm = context.getPackageManager();
        try {
            info = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String getMetaData(Context context, String key) {
        String result = "";
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            result = String.valueOf(ai.metaData.get(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int getLocalVersionCode() {
        return info != null ? info.versionCode : Integer.MAX_VALUE;
    }

    public String getLocalVersionName() {
        return info != null ? info.versionName : "";
    }

    public String getPackageName() {
        return info != null ? info.packageName : "";
    }

    /**
     * 系统版本
     *
     * @return
     */
    public String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 系统版本号 取值为版本号的前两位：如安卓4.1.6 则os_version=41，如果超过10，如10.0.1则100
     *
     * @return
     */
    public int getOSVersion() {
        int result;
        String[] subCodes = Build.VERSION.RELEASE.split("\\.");
        StringBuilder sb = new StringBuilder();
        if (subCodes.length >= 1) {
            sb.append(subCodes[0]);
        }
        if (subCodes.length >= 2) {
            if (!isEmpty(subCodes[1])) {
                sb.append(subCodes[1].substring(0, 1));
            }
        }
        String versionText = sb.toString();
        switch (versionText) {
            case "L":
                result = 50;// L版本暂为5.0
                break;
            case "M":
                result = 60;
                break;
            case "N":
                result = 70;
                break;
            case "O":
                result = 80;
                break;
            case "P":
                result = 90;
                break;
            default:
                try { // 有可能有英文字母
                    result = Integer.parseInt(versionText);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    result = 44;
                }
                break;
        }
        return result;
    }

    /**
     * 手机型号
     *
     * @return
     */
    public static String getPhoneModel() {
        return Build.MODEL;
    }

    /**
     * 获取SIM卡运营商
     *
     * @param context
     * @return
     */
    public static String getOperators(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String operator = "";
//        String IMSI = tm.getSubscriberId();
//        if (IMSI == null || IMSI.equals("")) {
//            return "";
//        }
//        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
//            operator = "中国移动";
//        } else if (IMSI.startsWith("46001")) {
//            operator = "中国联通";
//        } else if (IMSI.startsWith("46003")) {
//            operator = "中国电信";
//        }
        return operator;
    }

    /**
     * 获取SIM卡运营商的英文简写字段
     *
     * @param name
     * @return
     */
    public String getOperatorsValue(String name) {
        if (name.equals("中国移动")) {
            return "CMCC";
        } else if (name.equals("中国联通")) {
            return "CUCC";
        } else if (name.equals("中国电信")) {
            return "CTCC";
        }
        return "";
    }

    public static int getNetworkType(Context context) {
        int result = 0;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                result = 2;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        result = 4;//2G
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        result = 6;//3G
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        result = 4;//4G
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            result = 5;//3G
                        }
                        break;
                }
            }
        }
        return result;
    }

}
