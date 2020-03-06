package cn.weli.svideo.advert.kuaima;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 快马下载类广告扩展字段
 */
public class ETKuaiMaAdDownloadData {
    public ArrayList<String> download_start_track_urls = new ArrayList<>();//开始下载回调地址
    public ArrayList<String> download_success_track_urls = new ArrayList<>();//下载成功回调地址
    public ArrayList<String> install_start_track_urls = new ArrayList<>();//开始安装回调地址
    public ArrayList<String> install_success_track_urls = new ArrayList<>();//安装成功回调地址
    public String package_name = "";

    public void parseJson(JSONObject object) {
        try {
            package_name = object.optString("package_name");
            JSONArray download_start_urls = object.optJSONArray("download_start_track_urls");
            if (download_start_urls != null) {
                for (int i = 0; i < download_start_urls.length(); i++) {
                    String url = download_start_urls.optString(i, "");
                    download_start_track_urls.add(url);
                }
            }
            JSONArray download_success_urls = object.optJSONArray("download_success_track_urls");
            if (download_success_urls != null) {
                for (int i = 0; i < download_success_urls.length(); i++) {
                    String url = download_success_urls.optString(i, "");
                    download_success_track_urls.add(url);
                }
            }
            JSONArray install_start_urls = object.optJSONArray("install_start_track_urls");
            if (install_start_urls != null) {
                for (int i = 0; i < install_start_urls.length(); i++) {
                    String url = install_start_urls.optString(i, "");
                    install_start_track_urls.add(url);
                }
            }
            JSONArray install_success_urls = object.optJSONArray("install_success_track_urls");
            if (install_success_urls != null) {
                for (int i = 0; i < install_success_urls.length(); i++) {
                    String url = install_success_urls.optString(i, "");
                    install_success_track_urls.add(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
