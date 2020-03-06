package cn.weli.svideo.module.mine.presenter;

import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.module.mine.view.IAboutAppView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-23
 * @see [class/method]
 * @since [1.0.0]
 */
public class AboutAppPresenter implements IPresenter {

    private IAboutAppView mView;

    public AboutAppPresenter(IAboutAppView view) {
        mView = view;
    }

    @Override
    public void clear() {
    }
}
