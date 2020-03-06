package cn.weli.svideo.wxapi;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-24
 * @see [class/method]
 * @since [1.0.0]
 */
public class WxAuthorizeEvent {

    private String code;

    public WxAuthorizeEvent(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
