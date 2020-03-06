package cn.weli.svideo.module.video.presenter;

import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.module.video.view.IVideoDetailView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class VideoDetailPresenter implements IPresenter {

    private IVideoDetailView mView;

    public VideoDetailPresenter(IVideoDetailView view) {
        mView = view;
    }

    @Override
    public void clear() {
    }
}
