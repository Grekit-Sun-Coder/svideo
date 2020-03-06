package cn.weli.svideo.module.main.model.bean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class ShareInfoBean {

    /**
     * 分享类型：0-标准，1-粘贴(只取title字段)
     */
    public static final int TYPE_STANDARD = 0;
    public static final int TYPE_PASTE = 1;
    /**
     * 分享平台，微信好友、微信朋友圈、QQ
     */
    public static final String PLATFORM_WEIXIN = "wx_chat";
    public static final String PLATFORM_WEIXIN_PYQ = "wx_pyq";
    public static final String PLATFORM_QQ = "qq";
    /**
     * 分享类型
     */
    public int shareType;
    /**
     * 分享标题
     */
    public String title;
    /**
     * 分享摘要
     */
    public String subTitle;
    /**
     * 分享图片
     */
    public String imgUrl;
    /**
     * 分享地址
     */
    public String linkUrl;
    /**
     * 分享平台
     */
    public String platform;
}
