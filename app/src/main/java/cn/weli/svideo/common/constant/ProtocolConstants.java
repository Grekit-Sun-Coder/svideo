package cn.weli.svideo.common.constant;

/**
 * 协议常量类 wlvideo://video/detail?phone=12345678912&password=123456
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see cn.weli.svideo.common.helper.ProtocolHelper
 * @since [1.0.0]
 */
public interface ProtocolConstants {

    /**
     * App内部页面私有协议头
     */
    String APP_SCHEME_HEADER = "wlvideo";

    /**
     * 外部页面私有协议头
     */
    String HTTP_SCHEME_HEADER = "http";

    /**
     * 协议来源
     */
    String SCHEME_FROM = "from";

    /**
     * 签到
     */
    String SCHEMA_ACTION_SIGN_IN = "wlvideo://signIn";

    /**
     * 主页
     */
    String SCHEME_PAGE_MAIN_PAGE = "main";

    /**
     * 激励视频
     */
    String SCHEME_PAGE_REWARD_PAGE = "rewardVideo";

    /**
     * 我的收益
     */
    String SCHEME_PAGE_MINE_PROFIT = "mineProfit";

    /**
     * 消息中心
     */
    String SCHEME_PAGE_MSG_CENTER = "msgCenter";

    /**
     * 视频播放详情页
     */
    String SCHEME_PAGE_VIDEO_DETAIL = "video/detail";
}
