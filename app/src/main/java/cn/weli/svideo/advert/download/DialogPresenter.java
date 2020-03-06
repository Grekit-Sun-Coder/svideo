package cn.weli.svideo.advert.download;

import cn.weli.svideo.baselib.presenter.IPresenter;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-27
 * @see [class/method]
 * @since [1.0.0]
 */
public class DialogPresenter implements IPresenter {

    private IDialogView mView;

    public DialogPresenter(IDialogView view) {
        mView = view;
    }

    @Override
    public void clear() {
    }
}
