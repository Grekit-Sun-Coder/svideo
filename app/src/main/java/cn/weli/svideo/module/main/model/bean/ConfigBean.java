package cn.weli.svideo.module.main.model.bean;

/**
 * 配置数据
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-28
 * @see [class/method]
 * @since [1.0.0]
 */
public class ConfigBean {

    /**
     * 请求接口地址baseUrl
     */
    public String videoBaseUrl;
    /**
     * 鲤跃广告请求接口地址baseUrl
     */
    public String liYueBaseUrl;
    /**
     * 头条广告sdk app id
     */
    public String ttAdAppId;
    /**
     * 头条激励视频广告位默认id
     */
    public String rewardVideoId;
    /**
     * 头条广告Draw视频广告位默认id
     */
    public String drawVideoId;
    /**
     * 鲤跃广告视频广告位默认id
     */
    public String liYueVideoId;
    /**
     * 文案配置
     */
    public DocumentBean document;

    public static class DocumentBean {
        /**
         * 收益说明文案
         */
        public String profitDesc;
        /**
         * 提现规则说明文案
         */
        public String withdrawDesc;
        /**
         * 账号绑定说明文案
         */
        public String accountDesc;
    }
}
