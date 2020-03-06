package cn.weli.svideo.module.main.view;

import cn.weli.svideo.baselib.view.IBaseView;

/**
 * 主页V层接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see cn.weli.svideo.module.main.ui.MainActivity
 * @since [1.0.0]
 */
public interface IMainView extends IBaseView {

    String EXTRA_PROTOCOL = "protocol";

    String EXTRA_TAB_FLAG_VALUE = "tab";

    String EXTRA_FROM_PUSH = "push";
    String EXTRA_PUSH_TASK_ID = "taskId";
    String EXTRA_PUSH_MSG_ID = "msgId";

    /**
     * tab名称，video视频，mine我的
     */
    String TAB_VIDEO = "video";
    String TAB_MINE = "mine";
    String TAB_TASK = "task";

    /**
     * 通知权限code
     */
    int REQUEST_CODE_NOTIFICATION = 0x100;

    /**
     * 提醒再按一次返回键退出应用
     */
    void showQuitAppToast();

    /**
     * 隐藏所有的fragment.
     * <p>
     * 发现清理内存后，应用重启，saveInstance不为空，导致fragment重叠
     */
    void hideAllFragment();

    /**
     * 切换fragment.
     *
     * @param from 当前fragment的标识
     * @param to   需要切换的fragment的标识
     */
    void switchContent(String from, String to);

    /**
     * 切换tab到视频
     */
    void changeTabToVideoPage();

    /**
     * 展示版本更新信息
     *
     * @param content     内容
     * @param forceUpdate 是否强制更新
     * @param link        下载地址
     * @param version_name 版本号
     */
    void showVersionUpdateDialog(String content, boolean forceUpdate, String link,
            String version_name);

    /**
     * 提示去设置开启通知
     */
    void showNotifyPermissionTip();
}
