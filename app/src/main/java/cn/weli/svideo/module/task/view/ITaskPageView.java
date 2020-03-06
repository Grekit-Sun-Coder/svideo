package cn.weli.svideo.module.task.view;

import java.util.List;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.task.model.bean.CoinOpenBean;
import cn.weli.svideo.module.task.model.bean.ProfitBean;
import cn.weli.svideo.module.task.model.bean.SignInBean;
import cn.weli.svideo.module.task.model.bean.TaskBean;

/**
 * 视频首页 V层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see cn.weli.svideo.module.task.ui.TaskPageFragment
 * @since [1.0.0]
 */
public interface ITaskPageView extends IBaseView {

    String DEFAULT_COIN = "0";
    String DEFAULT_CASH = "0.00";

    String EXTRA_LOGIN_COIN = "loginCoin";

    String TASK_KEY = "taskKey";

    String TASK_SPACE = "taskSpace";

    int FLAG_SHOW_COIN_DELAY = 300;

    /**
     * 展示
     */
    void handleFragmentShow();

    /**
     * 隐藏
     */
    void handleFragmentHide();

    /**
     * 展示金额金币信息
     *
     * @param bean 信息
     */
    void showUserProfitInfo(ProfitBean bean);

    /**
     * 展示任务列表
     */
    void showTaskList(List<TaskBean> list);

    /**
     * 展示奖励的金币
     *
     * @param coin 金币数量
     */
    void showLoginCoinReward(long coin);

    /**
     * 提示任务已经完成
     */
    void showTaskHasFinished();

    /**
     * 展示签到信息
     *
     * @param bean 签到信息
     */
    void showSignInInfo(SignInBean bean);

    /**
     * 网络错误
     */
    void showErrorView(boolean netUnAvailable);

    /**
     * 刷新当前条目
     *
     * @param position 位置
     */
    void notifyCurrentItem(int position);

    /**
     * 展示获得金币弹窗
     *
     * @param coin 金币
     */
    void showCoinDialog(int coin, String taskKey, AdConfigBean bean);

    /**
     * 刷新任务列表
     */
    void refreshTaskList();

    /**
     * 结束刷新
     */
    void finishRefresh();

    /**
     * 获取粘贴板数据
     */
    void checkInviteCodeFromClip();

    /**
     * 展示绑定邀请码提示
     *
     * @param text 邀请码
     */
    void showBindInviteCodeTip(String text);

    /**
     * 隐藏关闭宝箱
     */
    void hideCoinOpenFloat();

    /**
     * 设置开宝箱状态
     *
     * @param status 状态
     * @param msg    信息
     */
    void setCoinOpenFloatStatus(int status, String msg);

    /**
     * 显示开宝箱弹窗
     *
     * @param coin 金币
     * @param bean 激励视频任务
     */
    void showCoinOpenDialog(int coin, CoinOpenBean.PreviousTask bean);

    /**
     * 设置开启通知是否可见
     *
     * @param enable 是否开启
     */
    void setNotificationPerStatus(boolean enable);
}
