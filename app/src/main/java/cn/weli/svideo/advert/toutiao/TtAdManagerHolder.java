package cn.weli.svideo.advert.toutiao;

import android.content.Context;

import com.ttshell.sdk.api.config.TTObConfig;
import com.ttshell.sdk.api.config.TTObConstant;
import com.ttshell.sdk.api.config.TTObManager;
import com.ttshell.sdk.api.config.TTObSdk;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.common.constant.ConfigConstants;

/**
 * 用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class TtAdManagerHolder {

    private static boolean sInit;

    public static TTObManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTObSdk.getAdManager();
    }

    public static void init(Context context) {
        doInit(context);
    }

    private static void doInit(Context context) {
        if (!sInit) {
            TTObSdk.init(context, buildConfig(context));
            sInit = true;
        }
    }

    public static void reInit(Context context) {
        sInit = false;
        TTObSdk.init(context, buildConfig(context));
        sInit = true;
    }

    private static TTObConfig buildConfig(Context context) {
        Logger.d("tou ad manager holder buildConfig =" + ConfigConstants.TT_AD_APP_ID);
        return new TTObConfig.Builder()
                .appId(ConfigConstants.TT_AD_APP_ID)
                .useTextureView(true)
                .appName(context.getString(R.string.app_name))
                .titleBarTheme(TTObConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true)
                .directDownloadNetworkType(TTObConstant.NETWORK_STATE_WIFI)
                .supportMultiProcess(false)
                .build();
    }
}
