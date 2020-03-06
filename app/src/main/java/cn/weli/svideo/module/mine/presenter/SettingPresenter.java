package cn.weli.svideo.module.mine.presenter;

import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.module.mine.model.UserModel;
import cn.weli.svideo.module.mine.view.ISettingView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-18
 * @see [class/method]
 * @since [1.0.0]
 */
public class SettingPresenter implements IPresenter {

    private ISettingView mView;

    private UserModel mModel;

    public SettingPresenter(ISettingView view) {
        mView = view;
        mModel = new UserModel();
    }

    /**
     * 退出登陆
     */
    public void handleLogout() {
        mModel.logout(new HttpSubscriber<EmptyResponseBean>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onNetworkUnavailable() {
            }

            @Override
            public void onResponseSuccess(EmptyResponseBean emptyResponseBean) {
            }

            @Override
            public void onResponseError(String s, String s1) {
            }

            @Override
            public void onNetworkError() {
            }

            @Override
            public void onPostExecute() {
                // 这里不监听退出登陆回调，让用户感觉退出登陆成功
                mModel.clearUserInfo();
                mView.handleLogoutSuccess();
            }
        });
    }

    @Override
    public void clear() {
        mModel.cancelLogout();
    }
}
