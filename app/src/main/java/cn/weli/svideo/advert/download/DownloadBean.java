package cn.weli.svideo.advert.download;

import android.app.Notification;
import android.graphics.Bitmap;

import java.util.ArrayList;

import cn.weli.svideo.advert.kuaima.ETKuaiMaAdDownloadData;


public class DownloadBean {

    /**
     * 标示不同的下载入口，在DownloadMarketService入口处使用
     */
    public int doorflag = 0;

    /**
     * 用于标示不同的任务
     */
    int taskId = 3;
    /**
     * 任务名称和下载地址
     */
    public String name = "", downloadUrl = "";
    /**
     * 下载图标
     **/
    public String iconUrl = "";

    /**
     * 如果是zip文件是否需要解压
     */
    public boolean isZipIsNeedExtend = false;
    /**
     * 如果是zip文件如果需要解压，解压保存的目录
     */
    public String extendDir = "";

    /**
     * 下载apk时有用，由服务器返回，最后需要回传给服务器
     */
    public long apkid = 0;

    public long totalSize = 1, downloadSize = 0;
    /**
     * 0表示没有开始下载，1表示正在下载，2表示已经下载完毕,3表示正在解压缩,404表示下载失败
     */
    public int isDownloading = 0;//
    /**
     * 下载任务的标示字段，用户传入什么则到时原封不动返回
     */
    public String flag = "";

    /**
     * 下载apk时有用，该apk所在的广告接口的rtp值
     */
    public String ad_rtp = "";

    public ETKuaiMaAdDownloadData etKuaiMaAdDownloadData;


    public Download mDownload;
    /**
     * 广告平台开始下载后的回调地址
     */
    public ArrayList<String> callbackStart = new ArrayList<>();
    /**
     * 广告平台完成下载后的回调地址
     */
    public ArrayList<String> callbackFinish = new ArrayList<>();
    /**
     * 广告平台成功安装后的回调地址
     */
    public String callbackInstall = "";
    /**
     * 如果是万年历桌面插件皮肤的话，则是解压后得到的皮肤的根目录
     */
    public String skinRootDir = "";
    /**
     * 如果更换了url,需要重新注册监听事件
     **/
    public boolean notifyNeedUpdate = false;

    /**
     * 通知对象
     */
    Notification notification = null;

    Bitmap mBitmap = null;
    /**
     * 标识是否显示等待下载状态了
     */
    public boolean isShowWaitDown = false;

    public boolean isNeedCheckMd5 = false;
    public String apkMd5 = "";
    public boolean isSilentDownApp = false;//是否为静默下载应用
    public boolean showNotification = true;
    public int versionCode = 0;//在静默下载的时候需要用到保存下来的版本号和版本名
    public String versionName = "";//采用值传入的方式是怕软件杀死，server还在运行的时候，回调实际是没有用的

    //定义：xujun  这个type用int类型定义，用来控制是哪个位置的下载，方便在下载过程中，和下载完成后进行自己独有的操作
    //默认-1，万年历官网更新apk：0，如果后期有定义必须参考前面的值是否有使用 ，值定义在DownLoadBeanType当中
    public int type = -1;

}
