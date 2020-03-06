package cn.weli.svideo.module.mine.presenter;

import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.RegularUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.module.mine.model.UserModel;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import cn.weli.svideo.module.mine.model.bean.VerifyCodeBean;
import cn.weli.svideo.module.mine.view.ILoginView;

/**
 * 登陆 P层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see [class/method]
 * @since [1.0.0]
 */
public class LoginPresenter implements IPresenter {

    /**
     * 手机号位数
     */
    private static final int FLAG_PHONE_SIZE = 11;
    /**
     * 验证码位数
     */
    private static final int FLAG_CODE_SIZE = 4;

    private ILoginView mView;

    private UserModel mModel;

    public LoginPresenter(ILoginView view) {
        mView = view;
        mModel = new UserModel();
    }

    /**
     * 处理手机号、验证码输入
     *
     * @param phone 手机号
     * @param code 验证码
     */
    public void checkLoginCodeChange(String phone, String code) {
        mView.setLoginCodeTitleStatus(StringUtil.isNull(code));
        mView.setLoginPhoneTitleStatus(StringUtil.isNull(phone));
        if (!StringUtil.isNull(phone) && !StringUtil.isNull(code)
                && FLAG_PHONE_SIZE == phone.length() && FLAG_CODE_SIZE == code.length()) {
            mView.setLoginBtnStatus(true);
        } else {
            mView.setLoginBtnStatus(false);
        }
    }

    /**
     * 获取验证码点击
     *
     * @param phone 手机号
     */
    public void handleGetCode(String phone) {
        if (StringUtil.isNull(phone) || !RegularUtil.isMobileSimple(phone)) {
            mView.showPhoneNumberErrorTip();
            return;
        }
        mModel.getVerifyCode(phone, new HttpSubscriber<VerifyCodeBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(VerifyCodeBean bean) {
                mView.handleGetPhoneCodeSuccess();
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

    /**
     * 登录
     *
     * @param isArgueAgreed 是否同意协议
     * @param phone 手机号
     * @param code 验证码
     */
    public void handleLogin(boolean isArgueAgreed, String phone, String code) {
        if (!isArgueAgreed) {
            mView.showAgreeArgueTip();
            return;
        }
        if (!StringUtil.isNull(phone) && !StringUtil.isNull(code)) {
            if (!RegularUtil.isMobileSimple(phone)) {
                mView.showPhoneNumberErrorTip();
                return;
            }
            mModel.login(phone, code, new HttpSubscriber<UserInfoBean>() {
                @Override
                public void onPreExecute() {
                    mView.showLoadView();
                }

                @Override
                public void onNetworkUnavailable() {
                    mView.showNetworkUnAvailable();
                    mView.finishLoadView();
                }

                @Override
                public void onResponseSuccess(UserInfoBean userInfoBean) {
                    if (userInfoBean != null && !StringUtil.isNull(userInfoBean.access_token)) {
                        mModel.saveUserAccessToken(userInfoBean.access_token);
                        getUserInfo(userInfoBean);
                    }
                }

                @Override
                public void onResponseError(String desc, String code) {
                    mView.showToast(desc);
                    mView.finishLoadView();
                }

                @Override
                public void onNetworkError() {
                    mView.showNetworkError();
                    mView.finishLoadView();
                }

                @Override
                public void onPostExecute() {
                }
            });
        }
    }

    /**
     * 获取用户信息并保存
     */
    private void getUserInfo(UserInfoBean loginBean) {
        mModel.getUserInfoData(new HttpSubscriber<UserInfoBean>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(UserInfoBean bean) {
                if (bean != null) {
                    bean.access_token = loginBean.access_token;
                    bean.open_uid = loginBean.open_uid;
                    bean.gold = loginBean.gold;
                    mModel.saveUserInfo(bean);
                    mView.finishLoadView();
                    if (bean.gold > 0) {
                        mView.handleLoginStart2TaskPage(bean.gold);
                    }
                    mView.handleLoginSuccess(bean.gold > 0);
                }
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

    @Override
    public void clear() {
        mModel.cancelLogin();
        mModel.cancelPhoneCode();
    }
}
