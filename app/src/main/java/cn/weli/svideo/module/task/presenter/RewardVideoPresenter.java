package cn.weli.svideo.module.task.presenter;

import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.module.task.view.IRewardVideoView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class RewardVideoPresenter implements IPresenter {

    private IRewardVideoView mView;

    public RewardVideoPresenter(IRewardVideoView view) {
        mView = view;
    }

    @Override
    public void clear() {
    }
}
