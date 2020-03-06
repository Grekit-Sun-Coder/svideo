package cn.weli.svideo.advert.download;

import android.os.AsyncTask;

import java.util.ArrayList;

import cn.etouch.logger.Logger;
import cn.weli.svideo.common.http.NetManager;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-01-09
 * @see [class/method]
 * @since [1.0.0]
 */
public class DownloadStatusAsyncTask extends AsyncTask<Void, Integer, Void> {

    /**
     * 用于替换的宏
     */
    private final String TS = "$TS";

    private ArrayList<String> trackurls;

    public void setTrackUrls(ArrayList<String> urls) {
        this.trackurls = urls;
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (int i = 0; i < trackurls.size(); i++) {
            String url = trackurls.get(i);
            url = url.replace(TS, System.currentTimeMillis() + "");
            Logger.e("DownloadStatusAsyncTask url=" + url);
            int code = NetManager.getInstance().doGetAsCode(url);
            if (code != 200) {
                NetManager.getInstance().doGetAsCode(url);
            }
        }
        return null;
    }
}
