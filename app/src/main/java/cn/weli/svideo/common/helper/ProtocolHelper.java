package cn.weli.svideo.common.helper;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.ProtocolConstants;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.main.ui.WebViewActivity;
import cn.weli.svideo.module.main.view.IWebView;
import cn.weli.svideo.module.mine.ui.MineProfitActivity;
import cn.weli.svideo.module.mine.ui.MsgActivity;
import cn.weli.svideo.module.task.ui.RewardVideoActivity;

/**
 * 私有协议Schema跳转帮助类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see cn.weli.svideo.common.ui.AppBaseActivity
 * @since [1.0.0]
 */
public class ProtocolHelper {

    /**
     * 处理协议跳转
     *
     * @param activity 上下文
     * @param url      跳转地址
     */
    public static void handleProtocolEvent(Activity activity, String url) {
        handleProtocolEvent(activity, url, null);
    }

    /**
     * 处理协议跳转
     *
     * @param activity 上下文
     * @param url      跳转地址
     * @param extraMap 额外需要带的参数
     */
    public static void handleProtocolEvent(Activity activity, String url, Map<String, Object> extraMap) {
        if (activity == null || StringUtil.isNull(url)) {
            return;
        }
        if (!StringUtil.isNull(url) && url.startsWith(ProtocolConstants.HTTP_SCHEME_HEADER)) {
            // Http开头的网页
            Intent intent = new Intent(activity, WebViewActivity.class);
            intent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, url);
            activity.startActivity(intent);
        } else if (!StringUtil.isNull(url) && url.startsWith(ProtocolConstants.APP_SCHEME_HEADER)) {
            // App内部跳转
            String path = ProtocolHelper.getProtocolAction(url);
            Map<String, Object> paramsMap = getProtocolParams(url);
            Intent intent = getPageIntent(activity, path);
            if (intent != null) {
                if (paramsMap != null && !paramsMap.isEmpty()) {
                    for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                        String key = entry.getKey();
                        String value = (String) entry.getValue();
                        if (!StringUtil.isNull(key) && !StringUtil.isNull(value)) {
                            intent.putExtra(entry.getKey(), (String) entry.getValue());
                        }
                    }
                }
                if (extraMap != null && !extraMap.isEmpty()) {
                    for (Map.Entry<String, Object> entry : extraMap.entrySet()) {
                        String key = entry.getKey();
                        String value = (String) entry.getValue();
                        if (!StringUtil.isNull(key) && !StringUtil.isNull(value)) {
                            intent.putExtra(entry.getKey(), (String) entry.getValue());
                        }
                    }
                }
                activity.startActivity(intent);
                if (StringUtil.equals(path, ProtocolConstants.SCHEME_PAGE_REWARD_PAGE)) {
                    activity.overridePendingTransition(0, 0);
                }
            }
        }
    }

    /**
     * 私有协议分析,获取协议事件.
     */
    public static String getProtocolAction(String linkUrl) {
        String result = null;
        try {
            if (linkUrl == null) {
                return null;
            }
            int firstIndex = linkUrl.indexOf("//");
            int lastIndex = linkUrl.indexOf("?");
            if (lastIndex == -1) {
                lastIndex = linkUrl.length();
            }
            if (firstIndex == -1) {
                firstIndex = 0;
            }
            result = linkUrl.substring(firstIndex + 2, lastIndex);
        } catch (Exception e) {
            Logger.e("Get protocol action error is [" + e.getMessage() + "]");
        }
        return result;
    }

    /**
     * 解析私有协议的参数,其中page字段是需要打开的页面.
     */
    public static Map<String, Object> getProtocolParams(String urlString) {
        Map<String, Object> paramsMap = new HashMap<>();
        try {
            if (urlString == null || urlString.length() == 0) {
                return paramsMap;
            }
            int questIndex = urlString.indexOf('?');
            if (questIndex == -1) {
                return paramsMap;
            }
            String queryString = urlString.substring(questIndex + 1, urlString.length());
            if (queryString.length() > 0) {
                int ampersandIndex;
                int lastAmpersandIndex = 0;
                String subStr;
                String param;
                String value;
                String[] paramPair;
                do {
                    ampersandIndex = queryString.indexOf('&', lastAmpersandIndex) + 1;
                    if (ampersandIndex > 0) {
                        subStr = queryString.substring(lastAmpersandIndex, ampersandIndex - 1);
                        lastAmpersandIndex = ampersandIndex;
                    } else {
                        subStr = queryString.substring(lastAmpersandIndex);
                    }
                    paramPair = subStr.split("=");
                    param = paramPair[0];
                    value = paramPair.length == 1 ? "" : paramPair[1];
                    paramsMap.put(param, value);
                } while (ampersandIndex > 0);
            }
        } catch (Exception e) {
            Logger.e("Get protocol params error is [" + e.getMessage() + "]");
        }
        return paramsMap;
    }

    public static Intent getPageIntent(Activity activity, String page) {
        Intent intent = null;
        if (StringUtil.isNull(page)) {
            return null;
        }
        switch (page) {
            case ProtocolConstants.SCHEME_PAGE_MAIN_PAGE:
                intent = new Intent(activity, MainActivity.class);
                break;
            case ProtocolConstants.SCHEME_PAGE_REWARD_PAGE:
                intent = new Intent(activity, RewardVideoActivity.class);
                break;
            case ProtocolConstants.SCHEME_PAGE_MINE_PROFIT:
                intent = new Intent(activity, MineProfitActivity.class);
                break;
            case ProtocolConstants.SCHEME_PAGE_MSG_CENTER:
                intent = new Intent(activity, MsgActivity.class);
                break;
            default:
                break;
        }
        if (activity != null && activity.getClass() != null && !StringUtil.isNull(activity.getClass().getSimpleName()) && intent != null) {
            intent.putExtra(ProtocolConstants.SCHEME_FROM, activity.getClass().getSimpleName());
        }
        return intent;
    }
}