package cn.weli.svideo.common.constant;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see [class/method]
 * @since [1.0.0]
 */
public interface ThirdSdkConstants {

    /**
     * 又拍云
     */
    interface Upy {
        String BUCKET = "wlvideo-img";
        String SAVE_KEY = "/uploads/{year}{mon}{day}/{random32}{.suffix}";
        String RETURN_URL = "httpbin.org/post";
        String ACCOUNT = "wlkkinner2018";
        String PASSWORD = "4b010230869853be142040b09c95dfd8";
        String HEADER = "http://wlvideo-img.weli010.cn/";
    }

    /**
     * 第三方协议头
     */
    interface Scheme {

        String WEIXIN = "weixin://";

        String ALI = "alipays://";

        String TABBAO = "tbopen://";
    }
}
