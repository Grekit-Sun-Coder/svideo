package cn.weli.svideo.module.task.view;

import cn.weli.svideo.baselib.view.IBaseView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-25
 * @see [class/method]
 * @since [1.0.0]
 */
public interface IRewardVideoView extends IBaseView {

    /**
     * 目标广告id
     */
    String EXTRA_AD_ID = "adId";
    /**
     * 目标广告sdk
     */
    String EXTRA_AD_SDK = "sdk";
    /**
     * 补量广告id
     */
    String EXTRA_BACKUP_AD_ID = "backupAdId";
    /**
     * 补量广告sdk
     */
    String EXTRA_BACKUP_AD_SDK = "backupSdk";
}
