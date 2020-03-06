package cn.weli.svideo.advert.kuaima;

import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import cn.weli.svideo.advert.download.DownloadManager;

/**
 * 获取广告的路径名
 *
 * @author chenbixin
 * @version [v_1.0.5]
 * @date 2020-02-12
 * @see [class/method]
 * @since [v_1.0.5]
 */
public class AdAppNameAsyncTask extends AsyncTask<String, Double, String> {

    APkDirListener mAPkDirListener;

    public void setAPkDirListener(APkDirListener mAPkDirListener) {
        this.mAPkDirListener = mAPkDirListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
        DownloadManager.ChangeIsDownloading(url, 1);
        URL fileUrl;
        String result = "";
        try {
            // 打开网络流读取输入文件
            fileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(20000);
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setInstanceFollowRedirects(true);
            conn.connect();
            String myUrl = conn.getURL().toString();
            /** 取得文件名 */
            String fileName = conn.getHeaderField("Content-Disposition");
            // 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
            if (TextUtils.isEmpty(fileName)) {
                if (myUrl.contains("?")) {
                    fileName = myUrl.substring(0, myUrl.indexOf("?"));
                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                } else {
                    fileName = myUrl.substring(myUrl.lastIndexOf("/") + 1);
                }
            } else {
                fileName = URLDecoder.decode(fileName.substring(
                        fileName.indexOf("filename=") + 9), "UTF-8");
                // 有些文件名会被包含在""里面，所以要去掉，不然无法读取文件后缀
                fileName = fileName.replaceAll("\"", "");
            }

            if (TextUtils.isEmpty(fileName)) {
                result = "开始下载";
            } else {
                String path = Environment.getExternalStorageDirectory().getPath() + "/download/" + fileName;
                File file = new File(path);
                if (file.exists()) {
                    if (file.length() == conn.getContentLength()) {
                        result = "点击安装";
                    } else {
                        result = "继续下载";
                    }
                } else {
                    result = "开始下载";
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (mAPkDirListener != null) {
            mAPkDirListener.getApkdirStr(s);
        }
    }

    public interface APkDirListener {
        void getApkdirStr(String s);
    }
}
