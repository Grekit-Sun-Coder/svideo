package cn.weli.svideo.common.http.bean;

/**
 * 蜂巢数据
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-15
 * @see cn.weli.svideo.common.helper.ApiHelper
 * @since [1.0.0]
 */
public class RequestWrapperBean {
    /**
     * app : {"app_key":"string","app_version":"string","app_version_code":"string","market_channel":"string","package_name":"string"}
     * device : {"android_id":"string","carrier":"UNKNOWN","density":"string","duid":"string","idfa":"string","imei":"string","imsi":"string","ip":"string","language":"string","mac":"string","model":"string","network":"UNKNOW","open_udid":"string","orientation":"VERTICAL","os":"Android","os_version":"string","resolution":{"height":0,"width":0},"ssid":"string","ua":"string","vendor":"string"}
     * geo : {"city_key":"string","lat":"string","lon":"string","zone":"string"}
     */

    private AppBean app;
    private DeviceBean device;
    private GeoBean geo;

    public AppBean getApp() {
        return app;
    }

    public void setApp(AppBean app) {
        this.app = app;
    }

    public DeviceBean getDevice() {
        return device;
    }

    public void setDevice(DeviceBean device) {
        this.device = device;
    }

    public GeoBean getGeo() {
        return geo;
    }

    public void setGeo(GeoBean geo) {
        this.geo = geo;
    }

    public static class AppBean {
        /**
         * app_key : string
         * app_version : string
         * app_version_code : string
         * market_channel : string
         * package_name : string
         */

        private String app_key;
        private String app_version;
        private String app_version_code;
        private String market_channel;
        private String package_name;

        public String getApp_key() {
            return app_key;
        }

        public void setApp_key(String app_key) {
            this.app_key = app_key;
        }

        public String getApp_version() {
            return app_version;
        }

        public void setApp_version(String app_version) {
            this.app_version = app_version;
        }

        public String getApp_version_code() {
            return app_version_code;
        }

        public void setApp_version_code(String app_version_code) {
            this.app_version_code = app_version_code;
        }

        public String getMarket_channel() {
            return market_channel;
        }

        public void setMarket_channel(String market_channel) {
            this.market_channel = market_channel;
        }

        public String getPackage_name() {
            return package_name;
        }

        public void setPackage_name(String package_name) {
            this.package_name = package_name;
        }
    }

    public static class DeviceBean {
        /**
         * android_id : string
         * carrier : UNKNOWN
         * density : string
         * duid : string
         * idfa : string
         * imei : string
         * imsi : string
         * ip : string
         * language : string
         * mac : string
         * model : string
         * network : UNKNOW
         * open_udid : string
         * orientation : VERTICAL
         * os : Android
         * os_version : string
         * resolution : {"height":0,"width":0}
         * ssid : string
         * ua : string
         * vendor : string
         */

        private String android_id;
        private String carrier;
        private String density;
        private String duid;
        private String idfa;
        private String imei;
        private String imsi;
        private String ip;
        private String language;
        private String mac;
        private String model;
        private String network;
        private String open_udid;
        private String orientation;
        private String os;
        private String os_version;
        private ResolutionBean resolution;
        private String ssid;
        private String ua;
        private String vendor;

        public String getAndroid_id() {
            return android_id;
        }

        public void setAndroid_id(String android_id) {
            this.android_id = android_id;
        }

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public String getDensity() {
            return density;
        }

        public void setDensity(String density) {
            this.density = density;
        }

        public String getDuid() {
            return duid;
        }

        public void setDuid(String duid) {
            this.duid = duid;
        }

        public String getIdfa() {
            return idfa;
        }

        public void setIdfa(String idfa) {
            this.idfa = idfa;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public String getImsi() {
            return imsi;
        }

        public void setImsi(String imsi) {
            this.imsi = imsi;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getOpen_udid() {
            return open_udid;
        }

        public void setOpen_udid(String open_udid) {
            this.open_udid = open_udid;
        }

        public String getOrientation() {
            return orientation;
        }

        public void setOrientation(String orientation) {
            this.orientation = orientation;
        }

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

        public String getOs_version() {
            return os_version;
        }

        public void setOs_version(String os_version) {
            this.os_version = os_version;
        }

        public ResolutionBean getResolution() {
            return resolution;
        }

        public void setResolution(ResolutionBean resolution) {
            this.resolution = resolution;
        }

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getUa() {
            return ua;
        }

        public void setUa(String ua) {
            this.ua = ua;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public static class ResolutionBean {

            public ResolutionBean(int height, int width) {
                this.height = height;
                this.width = width;
            }

            /**
             * height : 0
             * width : 0
             */

            private int height;
            private int width;

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }
        }
    }

    public static class GeoBean {
        /**
         * city_key : string
         * lat : string
         * lon : string
         * zone : string
         */

        private String city_key;
        private String lat;
        private String lon;
        private String zone;

        public String getCity_key() {
            return city_key;
        }

        public void setCity_key(String city_key) {
            this.city_key = city_key;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getZone() {
            return zone;
        }

        public void setZone(String zone) {
            this.zone = zone;
        }
    }
}
