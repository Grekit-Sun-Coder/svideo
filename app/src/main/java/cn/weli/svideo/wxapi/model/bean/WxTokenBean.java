package cn.weli.svideo.wxapi.model.bean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class WxTokenBean {

    public String access_token;

    public long expires_in;

    public String refresh_token;

    public String openid;

    public String scope;

    public String unionid;

    public String message;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
