package cn.weli.svideo.module.main.model.bean;

import java.io.Serializable;

/**
 * 高德地址Bean
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see cn.weli.svideo.common.helper.LocationHelper
 * @since [1.0.0]
 */
public class LocationBean implements Serializable {

    private static final long serialVersionUID = -1437069832412934032L;

    /**
     * latitude=31.971637#
     * longitude=118.752114#
     * province=江苏省#
     * city=南京市#
     * district=江宁区#
     * cityCode=025#
     * adCode=320114#
     * address=江苏省南京市江宁区秣周东路12号未来网络小镇#
     * country=中国#
     * road=秣周东路#
     * poiName=未来网络小镇#
     * street=秣周东路#
     * streetNum=12号#
     * aoiName=未来网络小镇#
     */
    private double latitude;
    private double longitude;
    private String province;
    private String city;
    private String district;
    private String cityCode;
    private String adCode;
    private String address;
    private String country;
    private String poiName;
    private String street;
    private String streetNum;
    private String aoiName;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNum() {
        return streetNum;
    }

    public void setStreetNum(String streetNum) {
        this.streetNum = streetNum;
    }

    public String getAoiName() {
        return aoiName;
    }

    public void setAoiName(String aoiName) {
        this.aoiName = aoiName;
    }
}
