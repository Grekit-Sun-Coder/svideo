package cn.weli.svideo.advert.kuaima;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.weli.svideo.advert.kuaima.deeplink.ETKuaiMaAdDeeplinkEvent;

public class ETKuaiMaVideoData {

    public ArrayList<VideoAd> ads = new ArrayList<>();

    public void parseJson(JSONObject object) {
        try {
            JSONArray adArray = object.optJSONArray("ads");
            for (int i = 0; adArray != null && i < adArray.length(); i++) {
                JSONObject ad = adArray.getJSONObject(i);
                VideoAd videoAd = new VideoAd();
                videoAd.id = ad.optString("id");
                videoAd.sequence = ad.optInt("sequence");
                videoAd.conditional_ad = ad.optBoolean("conditional_ad");
                videoAd.type = ad.optInt("type");
                videoAd.inline = new InlineAd();
                JSONObject inline = ad.optJSONObject("inline");
                videoAd.inline.parseJson(inline);
                ads.add(videoAd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class VideoAd {
        public String id = "";
        public int sequence;
        public boolean conditional_ad;
        public int type;
        public InlineAd inline;
    }

    public class InlineAd {
        public ArrayList<String> error_urls = new ArrayList<>();
        public InlineAdSystem ad_system;
        public String title = "";
        public String desc = "";
        public String cover = "";
        public String end_card_url = "";
        public ArrayList<String> impressions = new ArrayList<>();
        public ArrayList<String> categories = new ArrayList<>();
        public Advertiser advertiser;
        public Pricing pricing;
        public ArrayList<String> dislike_urls = new ArrayList<>();
        public ArrayList<Creative> creatives = new ArrayList<>();

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            JSONArray errorUrls = object.optJSONArray("error_urls");
            for (int i = 0; errorUrls != null && i < errorUrls.length(); i++) {
                error_urls.add(errorUrls.optString(i));
            }
            JSONArray dislikeUrls = object.optJSONArray("dislike_urls");
            for (int i = 0; dislikeUrls != null && i < dislikeUrls.length(); i++) {
                dislike_urls.add(dislikeUrls.optString(i));
            }
            JSONObject adSystem = object.optJSONObject("ad_system");
            ad_system = new InlineAdSystem();
            ad_system.parseJson(adSystem);
            title = object.optString("title");
            desc = object.optString("desc");
            cover = object.optString("cover");
            end_card_url = object.optString("end_card_url");
            JSONArray impressionsA = object.optJSONArray("impressions");
            for (int i = 0; impressionsA != null && i < impressionsA.length(); i++) {
                impressions.add(impressionsA.optString(i));
            }
            JSONArray categoriesA = object.optJSONArray("categories");
            for (int i = 0; categoriesA != null && i < categoriesA.length(); i++) {
                categories.add(categoriesA.optString(i));
            }
            JSONObject advertiserO = object.optJSONObject("advertiser");
            advertiser = new Advertiser();
            advertiser.parseJson(advertiserO);
            JSONObject pricingO = object.optJSONObject("pricing");
            pricing = new Pricing();
            pricing.parseJson(pricingO);
            JSONArray creativesA = object.optJSONArray("creatives");
            for (int i = 0; creativesA != null && i < creativesA.length(); i++) {
                Creative creative = new Creative();
                creative.parseJson(creativesA.optJSONObject(i));
                creatives.add(creative);
            }
        }
    }

    public class InlineAdSystem {
        public String name = "";
        public String version = "";

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            name = object.optString("name");
            version = object.optString("version");
        }
    }

    public class Advertiser {
        public String id = "";
        public String name = "";

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            name = object.optString("name");
            id = object.optString("id");
        }
    }

    public class Pricing {
        public String model = "";
        public String currency = "";
        public int amount;

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            model = object.optString("model");
            currency = object.optString("currency");
            amount = object.optInt("amount");
        }
    }

    public class Creative {
        public String id = "";
        public String ad_id = "";
        public int sequence;
        public String api_framework = "";
        public UniversalId universal_id;
        public int type;//素材类型。1.linear；2.nonlinear；3.companion
        public int action_type;//广告交互类型:0-未知类型;1-跳转类;2-下载类
        public Linear linear;
        public NonLinear nonlinear;
        public int valid_time = 3;//默认为3

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            id = object.optString("id");
            ad_id = object.optString("ad_id");
            sequence = object.optInt("sequence");
            api_framework = object.optString("api_framework");
            JSONObject universalId = object.optJSONObject("universal_id");
            universal_id = new UniversalId();
            universal_id.parseJson(universalId);
            type = object.optInt("type");
            action_type = object.optInt("action_type");
            JSONObject linearO = object.optJSONObject("linear");
            linear = new Linear();
            linear.parseJson(linearO);
            JSONObject nonlinearO = object.optJSONObject("nonlinear");
            nonlinear = new NonLinear();
            nonlinear.parseJson(nonlinearO);
            valid_time = object.optInt("valid_time");
        }
    }

    public class UniversalId {
        public String id = "";
        public String id_registry = "";
        public String id_value = "";

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            id = object.optString("id");
            id_registry = object.optString("id_registry");
            id_value = object.optString("id_value");
        }
    }

    public class Linear {
        public String click_through = "";
        public ArrayList<String> click_trackings = new ArrayList<>();
        public ArrayList<String> general_event_trackings = new ArrayList<>();
        public ArrayList<EventTracking> event_trackings = new ArrayList<>();
        public int skipoffset;
        public int duration;
        public ArrayList<MediaFile> media_files = new ArrayList<>();
        public Icon icon;
        public String click_btn_content = "";
        public String package_name = "";
        public String app_name = "";
        public ArrayList<String> download_start_track_urls = new ArrayList<>();
        public ArrayList<String> download_success_track_urls = new ArrayList<>();
        public ArrayList<String> install_start_track_urls = new ArrayList<>();
        public ArrayList<String> install_success_track_urls = new ArrayList<>();
        public String deep_link_url = "";
        public ArrayList<ETKuaiMaAdDeeplinkEvent> deep_link_track_events = new ArrayList<>();


        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            click_through = object.optString("click_through");
            JSONArray clickTrackings = object.optJSONArray("click_trackings");
            for (int i = 0; clickTrackings != null && i < clickTrackings.length(); i++) {
                click_trackings.add(clickTrackings.optString(i));
            }
            JSONArray generalEventTrackings = object.optJSONArray("general_event_trackings");
            for (int i = 0; generalEventTrackings != null && i < generalEventTrackings.length(); i++) {
                general_event_trackings.add(generalEventTrackings.optString(i));
            }
            JSONArray eventTrackingsA = object.optJSONArray("event_trackings");
            for (int i = 0; eventTrackingsA != null && i < eventTrackingsA.length(); i++) {
                EventTracking eventTracking = new EventTracking();
                eventTracking.parseJson(eventTrackingsA.optJSONObject(i));
                event_trackings.add(eventTracking);
            }
            skipoffset = object.optInt("skipoffset");
            duration = object.optInt("duration") * 1000;//换成ms
            JSONArray mediaFiles = object.optJSONArray("media_files");
            for (int i = 0; mediaFiles != null && i < mediaFiles.length(); i++) {
                MediaFile mediaFile = new MediaFile();
                mediaFile.parseJson(mediaFiles.optJSONObject(i));
                media_files.add(mediaFile);
            }
            JSONObject iconO = object.optJSONObject("icon");
            icon = new Icon();
            icon.parseJson(iconO);
            package_name = object.optString("package_name");
            app_name = object.optString("app_name");
            JSONArray download_start_trackUrls = object.optJSONArray("download_start_track_urls");
            for (int i = 0; download_start_trackUrls != null && i < download_start_trackUrls.length(); i++) {
                download_start_track_urls.add(download_start_trackUrls.optString(i));
            }
            JSONArray download_success_trackUrls = object.optJSONArray("download_success_track_urls");
            for (int i = 0; download_success_trackUrls != null && i < download_success_trackUrls.length(); i++) {
                download_success_track_urls.add(download_success_trackUrls.optString(i));
            }
            JSONArray install_start_trackUrls = object.optJSONArray("install_start_track_urls");
            for (int i = 0; install_start_trackUrls != null && i < install_start_trackUrls.length(); i++) {
                install_start_track_urls.add(install_start_trackUrls.optString(i));
            }
            JSONArray install_success_trackUrls = object.optJSONArray("install_success_track_urls");
            for (int i = 0; install_success_trackUrls != null && i < install_success_trackUrls.length(); i++) {
                install_success_track_urls.add(install_success_trackUrls.optString(i));
            }
            click_btn_content = object.optString("click_btn_content");

            deep_link_url = object.optString("deep_link_url", "");
            JSONArray deeplinktrackevents = object.optJSONArray("deep_link_track_events");
            if (deeplinktrackevents != null) {
                for (int i = 0; i < deeplinktrackevents.length(); i++) {
                    JSONObject deeplinkObj = deeplinktrackevents.optJSONObject(i);
                    ETKuaiMaAdDeeplinkEvent event = new ETKuaiMaAdDeeplinkEvent();
                    event.parseJson(deeplinkObj);
                    deep_link_track_events.add(event);
                }
            }
        }
    }

    public class NonLinear {
        public ArrayList<Resource> resources = new ArrayList<>();
        public String click_through = "";
        public ArrayList<String> click_trackings = new ArrayList<>();
        public ArrayList<String> general_event_trackings = new ArrayList<>();
        public ArrayList<EventTracking> event_trackings = new ArrayList<>();

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            JSONArray resourcesA = object.optJSONArray("resources");
            for (int i = 0; resourcesA != null && i < resourcesA.length(); i++) {
                Resource resource = new Resource();
                resource.parseJson(resourcesA.optJSONObject(i));
                resources.add(resource);
            }
            click_through = object.optString("click_through");
            JSONArray clickTrackings = object.optJSONArray("click_trackings");
            for (int i = 0; clickTrackings != null && i < clickTrackings.length(); i++) {
                click_trackings.add(clickTrackings.optString(i));
            }
            JSONArray generalEventTrackings = object.optJSONArray("general_event_trackings");
            for (int i = 0; generalEventTrackings != null && i < generalEventTrackings.length(); i++) {
                general_event_trackings.add(generalEventTrackings.optString(i));
            }
            JSONArray eventTrackingsA = object.optJSONArray("event_trackings");
            for (int i = 0; eventTrackingsA != null && i < eventTrackingsA.length(); i++) {
                EventTracking eventTracking = new EventTracking();
                eventTracking.parseJson(eventTrackingsA.optJSONObject(i));
                event_trackings.add(eventTracking);
            }
        }

    }

    public class EventTracking {
        public String event = "";
        public String url = "";

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            event = object.optString("event");
            url = object.optString("url");
        }
    }

    public class MediaFile {
        public int type;
        public Media media;

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            type = object.optInt("type");
            JSONObject mediaO = object.optJSONObject("media");
            media = new Media();
            media.parseJson(mediaO);
        }
    }

    public class Media {
        public String id = "";
        public String url = "";//媒体的url地址
        public int delivery;
        public String mime_type = "";
        public int width;
        public int height;
        public String codecs = "";
        public Bitrate bitrate;
        public boolean scalable;
        public boolean maintain_aspect_ratio;
        public String api_framework;

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            id = object.optString("id");
            url = object.optString("url");
            delivery = object.optInt("delivery");
            mime_type = object.optString("mime_type");
            width = object.optInt("width");
            height = object.optInt("height");
            codecs = object.optString("codecs");
            scalable = object.optBoolean("scalable");
            maintain_aspect_ratio = object.optBoolean("maintain_aspect_ratio");
            api_framework = object.optString("api_framework");
            JSONObject bitrateO = object.optJSONObject("bitrate");
            bitrate = new Bitrate();
            bitrate.parseJson(bitrateO);
        }
    }

    public class Bitrate {
        public String unit = "";
        public int min;
        public int max;
        public int exact;

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            unit = object.optString("unit");
            min = object.optInt("min");
            max = object.optInt("max");
            exact = object.optInt("exact");
        }
    }

    public class Icon {
        public String url = "";
        public int width;
        public int height;

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            url = object.optString("url");
            width = object.optInt("width");
            height = object.optInt("height");
        }
    }

    public class Resource {
        public int type;
        public String content_type;
        public ArrayList<String> content = new ArrayList<>();

        public void parseJson(JSONObject object) {
            if (object == null) {
                return;
            }
            type = object.optInt("type");
            content_type = object.optString("content_type");
            JSONArray contentA = object.optJSONArray("content");
            for (int i = 0; contentA != null && i < contentA.length(); i++) {
                content.add(contentA.optString(i));
            }
        }
    }
}
