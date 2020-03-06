package cn.weli.svideo.module.mine.presenter;

import com.hwangjr.rxbus.RxBus;

import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.module.mine.component.event.UserInfoChangeEvent;
import cn.weli.svideo.module.mine.model.UserModel;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.mine.view.IUserInfoView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-23
 * @see [class/method]
 * @since [1.0.0]
 */
public class UserInfoPresenter implements IPresenter {

    private IUserInfoView mView;

    private UserInfoBean mUserInfoBean;

    private UserModel mModel;

    public UserInfoPresenter(IUserInfoView view) {
        mView = view;
        mModel = new UserModel();
    }

    /**
     * 初始化用户信息
     */
    public void initUserInfo() {
        mUserInfoBean = WlVideoAppInfo.getInstance().getUserInfoBean();
        if (mUserInfoBean != null) {
            mView.showUserInfoBean(mUserInfoBean);
        }
    }

    /**
     * 处理性别点击
     */
    public void handleUserSexSelect() {
        if (mUserInfoBean != null) {
            mView.showUserSexSelectDialog(mUserInfoBean.sex);
        }
    }

    /**
     * 处理性别改变
     *
     * @param sex 性别
     */
    public void handleUserSexChange(String sex) {
        if (mUserInfoBean == null) {
            return;
        }
        if (!StringUtil.equals(sex, mUserInfoBean.sex)) {
            mModel.updateUserInfo(null, null, sex, new HttpSubscriber<UserInfoBean>() {
                @Override
                public void onPreExecute() {
                    mView.showLoadView(0);
                }

                @Override
                public void onNetworkUnavailable() {
                    mView.showNetworkUnAvailable();
                }

                @Override
                public void onResponseSuccess(UserInfoBean bean) {
                    mUserInfoBean.sex = sex;
                    mModel.saveUserInfo(mUserInfoBean);
                    mView.handleUserSexChangeSuccess(sex);
                }

                @Override
                public void onResponseError(String desc, String s1) {
                    mView.showToast(desc);
                }

                @Override
                public void onNetworkError() {
                    mView.showNetworkError();
                }

                @Override
                public void onPostExecute() {
                    mView.finishLoadView();
                }
            });
        } else {
            mView.handleUserSexChangeSuccess(sex);
        }
    }

    /**
     * 处理昵称编辑
     */
    public void handleUserNickEdit() {
        if (mUserInfoBean == null) {
            return;
        }
        mView.showUserNickDialog(mUserInfoBean.nick_name);
    }

    /**
     * 处理昵称改变
     *
     * @param nickname 昵称
     */
    public void handleUserNickChange(String nickname) {
        if (mUserInfoBean == null) {
            return;
        }
        if (!StringUtil.equals(nickname, mUserInfoBean.nick_name)) {
            mModel.updateUserInfo(null, nickname, null, new HttpSubscriber<UserInfoBean>() {

                @Override
                public void onPreExecute() {
                    mView.showLoadView(0);
                }

                @Override
                public void onNetworkUnavailable() {
                    mView.showNetworkUnAvailable();
                }

                @Override
                public void onResponseSuccess(UserInfoBean bean) {
                    mUserInfoBean.nick_name = nickname;
                    mModel.saveUserInfo(mUserInfoBean);
                    RxBus.get().post(new UserInfoChangeEvent(mUserInfoBean));
                    mView.handleUserNickChangeSuccess(nickname);
                }

                @Override
                public void onResponseError(String desc, String code) {
                    mView.showToast(desc);
                }

                @Override
                public void onNetworkError() {
                    mView.showNetworkError();
                }

                @Override
                public void onPostExecute() {
                    mView.finishLoadView();
                }
            });
        } else {
            mView.handleUserNickChangeSuccess(nickname);
        }
    }

    /**
     * 处理头像改变
     *
     * @param url 头像地址
     */
    public void handleUploadHeadImg(String url) {
        if (mUserInfoBean == null) {
            return;
        }
        if (!StringUtil.equals(url, mUserInfoBean.avatar)) {
            mModel.updateUserInfo(url, null, null, new HttpSubscriber<UserInfoBean>() {

                @Override
                public void onPreExecute() {
                    mView.showLoadView(0);
                }

                @Override
                public void onNetworkUnavailable() {
                    mView.showNetworkUnAvailable();
                }

                @Override
                public void onResponseSuccess(UserInfoBean bean) {
                    mUserInfoBean.avatar = url;
                    mModel.saveUserInfo(mUserInfoBean);
                    RxBus.get().post(new UserInfoChangeEvent(mUserInfoBean));
                    mView.handleUserHeadImgUploadSuccess(url);
                }

                @Override
                public void onResponseError(String desc, String code) {
                    mView.showToast(desc);
                }

                @Override
                public void onNetworkError() {
                    mView.showNetworkError();
                }

                @Override
                public void onPostExecute() {
                    mView.finishLoadView();
                }
            });
        }
    }

    @Override
    public void clear() {
        mModel.cancelUpdateUser();
    }
}
