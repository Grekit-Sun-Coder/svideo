package cn.weli.svideo.common.constant;


/**
 * 文件描述.
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see BusinessConstants
 * @since [1.0.0]
 */
public interface BusinessConstants {

    String FLAG_LOG_TAG = "wlvideo";

    String DEFAULT_CHANNEL = "own";

    /**
     * MetaData参数名称
     */
    interface MetaData {
        String APP_KAY = "APP_KEY";
        String APP_SECRET = "APP_SECRET";
        String UCLOUD_KEY = "UCLOUCD_APP_KEY";
        String UMENG_APPKEY = "UMENG_APPKEY";
        String CHANNEL = "UMENG_CHANNEL";
        String QQ_APP_ID = "QQ_APP_ID";
        String QQ_APP_KEY = "QQ_APP_KEY";
        String Wx_APP_ID = "WX_APP_ID";
        String Wx_APP_SECRET = "WX_APP_SECRET";
        String LINKEDME_KEY = "LINKEDME_KEY";
        String USER_PRIVACY = "USER_PRIVACY";
        String GDT_APP_ID = "GDT_APP_ID";
    }

    /**
     * 配置
     */
    interface Config {
        /**
         * 客户端统一配置
         */
        String CONFIG_APP_ID = "61453301";
        String CONFIG_SECRET_KEY = "tinker88d0a07282e0a653";
        String CONFIG_EN = "prod";
    }

    /**
     * 分享
     */
    interface ShareType {

        /**
         * 分享平台，微信好友、微信朋友圈、QQ
         */
        String PLATFORM_WEIXIN = "wx_chat";
        String PLATFORM_WEIXIN_PYQ = "wx_pyq";
        String PLATFORM_QQ = "qq";
        /**
         * 复制链接
         */
        String COPY_LINK = "copy_link";
        /**
         * 举报
         */
        String REPORT = "report";
        /**
         * 不感兴趣
         */
        String NOT_INTERESTED = "not_interested";
    }
}
