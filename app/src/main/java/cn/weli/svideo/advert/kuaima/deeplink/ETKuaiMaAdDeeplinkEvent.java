package cn.weli.svideo.advert.kuaima.deeplink;

import org.json.JSONObject;

public class ETKuaiMaAdDeeplinkEvent {
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
