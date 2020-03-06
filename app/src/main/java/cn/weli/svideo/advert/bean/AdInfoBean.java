package cn.weli.svideo.advert.bean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class AdInfoBean {

    public String sdkType;

    public String adId;

    public AdInfoBean(String sdkType, String adId) {
        this.sdkType = sdkType;
        this.adId = adId;
    }
}
