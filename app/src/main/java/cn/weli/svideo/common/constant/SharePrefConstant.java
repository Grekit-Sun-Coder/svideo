package cn.weli.svideo.common.constant;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see [class/method]
 * @since [1.0.0]
 */
public interface SharePrefConstant {

    /**
     * 是否获取过aaid和oaid
     */
    String PREF_HAD_DEVICE = "0x001";
    /**
     * 设备aaid
     */
    String PREF_DEVICE_AAID = "0x002";
    /**
     * 设备oaid
     */
    String PREF_DEVICE_OAID = "0x003";
    /**
     * 用户uid
     */
    String PREF_USER_UID = "0x004";
    /**
     * 用户access_token
     */
    String PREF_ACCESS_TOKEN = "0x005";
    /**
     * channel
     */
    String PREF_APP_CHANNEL = "0x006";
    /**
     * imei
     */
    String PREF_APP_IMEI = "0x007";
    /**
     * imsi
     */
    String PREF_APP_IMSI = "0x008";
    /**
     * mac
     */
    String PREF_APP_MAC = "0x009";
    /**
     * user sex
     */
    String PREF_USER_SEX = "0x010";
    /**
     * 用户位置信息
     */
    String PREF_USER_LOCATION = "0x011";
    /**
     * 用户信息
     */
    String PREF_USER_INFO = "0x012";
    /**
     * 邀请码检测
     */
    String PREF_INVITE_CODE = "0x013";
    /**
     * user agent
     */
    String PREF_USER_AGENT = "0x014";
    /**
     * 金币已到账
     */
    String PREF_VIDEO_TASK_COIN = "0x015";
    /**
     * 首次进入app提示红包
     */
    String PREF_VIDEO_GUIDE_COIN = "0x016";
    /**
     * 用户open_id
     */
    String PREF_USER_OPEN_ID = "0x0017";
    /**
     * 客户端配置
     */
    String PREF_APP_CONFIG = "0x0018";
    /**
     * 个人信息保护指引
     */
    String PREF_USER_PRIVACY = "0x0019";
    /**
     * 通知栏权限校验
     */
    String PREF_NO_PERMISSION = "0x0020";
    /**
     * 关闭通知栏权限校验
     */
    String PREF_CLOSE_NO_PERMISSION = "0x0021";
    /**
     * 标记第几次打开APP
     */
    String PREF_TIMES_OPPEN = "0x0022";
    /**
     * 是否是首次打开app
     */
    String PREF_APP_START = "0x0023";
}
