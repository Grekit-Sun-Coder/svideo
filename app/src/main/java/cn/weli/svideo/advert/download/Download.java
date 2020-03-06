package cn.weli.svideo.advert.download;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.weli.svideo.baselib.utils.ThreadPoolUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Download {

    private int taskId = 0;
    private String url = "";
    /**
     * 如果是zip文件是否需要解压
     */
    private boolean isZipIsNeedExtend = false;
    /**
     * 如果是zip文件如果需要解压，解压保存的目录
     */
    private String extendsDir = "";
    private DownloadMarketService.OnDownloadChangeListener downloadListener = null;
    DownloadThread mDownloadThread;
    private ThreadPoolExecutor executor;// 线程池

    public boolean isDown;

    /**
     * isZipIsNeedExtend 如果是zip文件下载完成时是否需要解压缩 extendDir 如果是zip文件，解压缩的保存目录 url
     * 下载的url地址 listener 下载监听
     */
    public Download(Context context, int taskId, boolean isZipIsNeedExtend,
                    String extendDir, String url,
                    DownloadMarketService.OnDownloadChangeListener listener) {
        this.taskId = taskId;
        this.isZipIsNeedExtend = isZipIsNeedExtend;
        this.extendsDir = extendDir;
        this.url = url;
        this.downloadListener = listener;
        executor = ThreadPoolUtil.getInstance().getExecutor();
    }

    /**
     * 线程开启方法
     */
    public void start() {
        if(isDown){
            return;
        }
        if (mDownloadThread == null) {
            mDownloadThread = new DownloadThread();
        }
        DownloadManager.ChangeIsDownloading(url, 1);
        if (DownloadManager.DownloadToken > 0) {
            DownloadManager.DownloadToken--;
        }
        isDown = true;
        executor.execute(mDownloadThread);
        if (downloadListener != null) {
            downloadListener.onDownloadStart(url);
        }
    }

    /**
     * 线程停止方法
     */
    public void stop() {
        if(!isDown){
            return;
        }
        if (mDownloadThread != null) {
            isDown = false;
            executor.remove(mDownloadThread);
            if (DownloadManager.DownloadToken < 3) {
                DownloadManager.DownloadToken++;
            }
            mDownloadThread = null;
            DownloadManager.ChangeIsDownloading(url, 0);
            if (downloadListener != null) {
                downloadListener.onDownloadPause(url);
            }
        }
    }

    public class DownloadThread extends Thread {
        @Override
        public void run() {
            super.run();
            startDownload();
        }
    }

    public void startDownload() {
        DownloadManager.ChangeIsDownloading(url, 1);
        URL fileUrl;
        try {
            // 打开网络流读取输入文件
            fileUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(20000);
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setInstanceFollowRedirects(true);
            conn.connect();
            handleConnection(conn, url);
        } catch (Exception e) {
            if (DownloadManager.DownloadToken < 3) {
                DownloadManager.DownloadToken++;
            }
            DownloadManager.ChangeIsDownloading(url, 404);
            if (downloadListener != null) {
                downloadListener.onDownloadError(url);
            }
            e.printStackTrace();
        }
    }// run

    /**
     * @param conn
     */
    private void handleConnection(HttpURLConnection conn, String url) {
        try {
            // 先连接一次，解决跳转下载
            int code = conn.getResponseCode();
            String myUrl = conn.getURL().toString();
            if (code == HttpURLConnection.HTTP_MOVED_TEMP
                    || code == HttpURLConnection.HTTP_MOVED_PERM
                    || code == HttpURLConnection.HTTP_SEE_OTHER) {
                handle302Turn(conn, url);
                return;
            }
            int totleSize = conn.getContentLength();
            if (!DownloadManager.ChangeTotleSize(url, totleSize))
                return;
            // 打开输出文件
            String path = Environment.getExternalStorageDirectory().getPath() + "/download/";
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }

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
            String str = path + fileName;
            file = new File(str);
            if (file.exists() && file.length() == totleSize) {
                DownloadManager.ChangeIsDownloading(url, 2);
                DownloadManager.DownloadToken++;
                if (downloadListener != null) {
                    downloadListener.onDownloadSuccess(file.getAbsolutePath(), url);
                }
                return;
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            /**
             * 断点续传
             * **/
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            OkHttpClient okHttpClient = new OkHttpClient();
            long downLoadSize = file.length();// 文件的大小
            raf.seek(downLoadSize);
            Request request = new Request.Builder().url(url).
                    addHeader("Range", "bytes=" + downLoadSize + "-" + totleSize).build();
            Response response = okHttpClient.newCall(request).execute();
            InputStream ins = response.body().byteStream();
            //上面的就是简单的OKHttp连接网络，通过输入流进行写入到本地
            byte[] by = new byte[1024];
            int length = 0;
            int songDownloadedSize = (int) file.length();
            if (downloadListener != null) {
                downloadListener.onDownloadStart(url);
            }
            while ((length = ins.read(by)) != -1 && isDown) {
                raf.write(by, 0, length);
                songDownloadedSize += length;
                if (!DownloadManager
                        .ChangeDownloadSize(url, songDownloadedSize)) {// 停止下载
                    conn.disconnect();
                    ins.close();
                    // 歌曲下载完成，释放下载令牌
                    if (DownloadManager.DownloadToken < 3) {
                        DownloadManager.DownloadToken++;
                    }
                    return;
                }
            }// end while
            conn.disconnect();
            ins.close();
            if (!isDown) {
                return;
            }
            if (fileName.toLowerCase().endsWith(".zip") && isZipIsNeedExtend
                    && !extendsDir.equals("")) {
                DownloadManager.ChangeIsDownloading(url, 3);
                if (downloadListener != null) {
                    downloadListener.onStartExtend(url);
                }
                String skinDir = DownloadZipManager.extnativeZipFileList(file
                        .getAbsolutePath(), extendsDir);
                DownloadManager.ChangeTheTaskSkinRootDir(url, skinDir);
            }
            if (DownloadManager.DownloadToken < 3) {
                DownloadManager.DownloadToken++;
            }
            DownloadManager.ChangeIsDownloading(url, 2);
            if (downloadListener != null) {
                downloadListener.onDownloadSuccess(file.getAbsolutePath(), url);
            }
        } catch (Exception e) {
            if (DownloadManager.DownloadToken < 3) {
                DownloadManager.DownloadToken++;
            }
            DownloadManager.ChangeIsDownloading(url, 404);
            if (downloadListener != null) {
                downloadListener.onDownloadError(url);
            }
            e.printStackTrace();
        }
    }


    /**
     * 处理302
     */
    private void handle302Turn(HttpURLConnection conn, String url) throws Exception {
        String newUrl = conn.getHeaderField("Location");
        DownloadBean db = DownloadManager.getItem(url);
        this.url = newUrl;
        if (db != null) {
            db.downloadUrl = newUrl;
            db.notifyNeedUpdate = true;
        }
        Log.println(Log.ERROR, "Download", "Download Redirects newUrl:" + newUrl);
        conn = (HttpURLConnection) new URL(newUrl).openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(20000);
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setInstanceFollowRedirects(true);
        conn.connect();
        handleConnection(conn, newUrl);
    }
}
