package cn.weli.svideo.module.task.component.helper.listener;

/**
 * Function description
 *
 * @author chenbixin
 * @version [v_1.0.6]
 * @date 2020-02-21
 * @see [class/method]
 * @since [v_1.0.6]
 */
public interface DrawAdRewardListener {

    /**
     * 广告下载状态改变
     **/
    void onAdDownLoadStatusChange(int status, int position);

    /**
     * 成功拿到奖励
     **/
    void onAdReward(int reward);

    /**
     * 拿奖励成功
     **/
    void onAdRewardFail();

    /**
     * 已拿到奖励
     **/
    void onAdRewardEd();

}
