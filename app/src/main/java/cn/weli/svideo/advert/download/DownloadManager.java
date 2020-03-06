package cn.weli.svideo.advert.download;

import android.os.Environment;

import java.util.ArrayList;

import cn.weli.svideo.baselib.utils.StringUtil;

public class DownloadManager {

    // /////////////////////////下载令牌///////////////////////////////////////
    //只有该首歌的拿到下载令牌方可开启线程下载，一共设置3个下载令牌
    public static int TotleToken = 3;//保持不变
    public static int DownloadToken = 3;
    //////////////////////////////////////////////////////////////////////////
    public static ArrayList<DownloadBean> downloadList = new ArrayList<DownloadBean>();//下载列表

    /**
     * 获取下一个任务id(队列的最大的任务id+1,从50开始)
     */
    public static int getTheNextTaskId() {
        int taskId = 49;
        for (DownloadBean db : downloadList) {
            if (db.taskId > taskId) {
                taskId = db.taskId;
            }
        }
        return taskId + 1;
    }

    //ChangeTotleSize
    public static boolean ChangeTotleSize(String songUrl, int totleSize) {
        int length = downloadList.size();
        int i = 0;
        for (i = 0; i < length; i++) {
            if (downloadList.get(i).downloadUrl.equals(songUrl)) {
                downloadList.get(i).totalSize = totleSize;
                break;
            }//if
        }//for
        if (i >= length) {
            return false;
        } else {
            return true;
        }
    }//ChangeTotleSize

    //ChangeDownloadSize
    public static boolean ChangeDownloadSize(String songUrl, int DownloadSize) {
        int length = downloadList.size();
        int i = 0;
        for (i = 0; i < length; i++) {
            if (downloadList.get(i).downloadUrl.equals(songUrl)) {
                downloadList.get(i).downloadSize = DownloadSize;
                break;
            }//if
        }//for
        if (i >= length)
            return false;
        else
            return true;
    }//ChangeDownloadSize

    //ChangeIsDownloading
    public static boolean ChangeIsDownloading(String songUrl, int state) {
        int length = downloadList.size();
        int i = 0;
        for (i = 0; i < length; i++) {
            if (downloadList.get(i).downloadUrl.equals(songUrl)) {
                downloadList.get(i).isDownloading = state;
                break;
            }//if
        }//for
        if (i >= length)
            return false;
        else
            return true;
    }//ChangeIsDownloading

    /**
     * 修改指定任务的  桌面插件皮肤根目录
     */
    public static boolean ChangeTheTaskSkinRootDir(String songUrl, String skinrootDir) {
        int length = downloadList.size();
        int i = 0;
        for (i = 0; i < length; i++) {
            if (downloadList.get(i).downloadUrl.equals(songUrl)) {
                downloadList.get(i).skinRootDir = skinrootDir;
                break;
            }//if
        }//for
        if (i >= length)
            return false;
        else
            return true;
    }


    /**
     * 如果已有该任务则不再添加
     */
    public static void addItem(DownloadBean downBean) {
        boolean isFound = false;
        for (DownloadBean db : downloadList) {
            if (db.downloadUrl.equals(downBean.downloadUrl)) {
                isFound = true;
                if (db.isDownloading == 404) {
                    db.isDownloading = 0;
                    db.downloadSize = 0;
                    db.totalSize = 1;
                }
                break;
            }
            if(downBean.etKuaiMaAdDownloadData==null|| StringUtil.isNull(downBean.etKuaiMaAdDownloadData.package_name)){
                return;
            }
            if(db.etKuaiMaAdDownloadData==null|| StringUtil.isNull(db.etKuaiMaAdDownloadData.package_name)){
                return;
            }
            if (downBean.etKuaiMaAdDownloadData.package_name.equals(db.etKuaiMaAdDownloadData.package_name)) {
                isFound = true;
            }
        }//end for
        if (!isFound) {
            downloadList.add(downBean);
        }
    }

    //getItem
    public static DownloadBean getItem(String songUrl) {
        int length = downloadList.size();
        int i = 0;
        for (i = 0; i < length; i++) {
            if (downloadList.get(i).downloadUrl.equals(songUrl)) {
                return downloadList.get(i);
            }//if
        }//for
        return new DownloadBean();
    }//getItem

    //removeItem
    public static boolean removeItem(String songUrl) {
//		System.out.println("old="+songUrl);
        int length = downloadList.size();
        int i = 0;
        for (i = 0; i < length; i++) {
//			System.out.println(downloadList.get(i).downloadUrl);
            if (downloadList.get(i).downloadUrl.equals(songUrl)) {
                downloadList.remove(i);
                break;
            }//if
        }//for
        if (i >= length)
            return false;
        else
            return true;
    }//removeItem


    /**
     * 根据下载url获取下载文件的路径
     *
     * @param url 下载url
     */
    public static String getDownloadFilePath(String url) {
        String path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getPath() + "/download/"; //获取根目录
        } else {
            path = Environment.getDownloadCacheDirectory().getPath() + "/";
        }

        String fileName;
        if (url.contains("?")) {
            fileName = url.substring(0, url.indexOf("?"));
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        } else {
            fileName = url.substring(url.lastIndexOf("/") + 1);
        }
        return path + fileName;
    }
}
