package cn.weli.svideo.module.task.presenter;

import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.module.task.model.WithdrawModel;
import cn.weli.svideo.module.task.model.bean.WithdrawAccountBean;
import cn.weli.svideo.module.task.view.IAccountPayBindView;
import cn.weli.svideo.module.task.view.IWithdrawView;
import cn.weli.svideo.wxapi.model.WxModel;
import cn.weli.svideo.wxapi.model.bean.WxBindBean;
import cn.weli.svideo.wxapi.model.bean.WxTokenBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class AccountPayBindPresenter implements IPresenter {

    private IAccountPayBindView mView;

    private WithdrawModel mWithdrawModel;

    private WxModel mWxModel;

    private WithdrawAccountBean mAccountBean;

    private int mCurrentType;

    public AccountPayBindPresenter(IAccountPayBindView view) {
        mView = view;
        mWithdrawModel = new WithdrawModel();
        mWxModel = new WxModel();
        mAccountBean = new WithdrawAccountBean();
    }

    /**
     * 处理输入框变化
     *
     * @param name    姓名
     * @param account 账号
     * @param id      身份证
     */
    public void handleEditChanged(String name, String account, String id) {
        if (mCurrentType == IWithdrawView.TYPE_ALI) {
            if (!StringUtil.isNull(name) && !StringUtil.isNull(account) && !StringUtil.isNull(id)) {
                mView.setCompleteTxtEnable(true);
            } else {
                mView.setCompleteTxtEnable(false);
            }
        } else if (mCurrentType == IWithdrawView.TYPE_WX) {
            if (!StringUtil.isNull(mAccountBean.wx_account) && !StringUtil.isNull(mAccountBean.wx_nickname)
                    && !StringUtil.isNull(name) && !StringUtil.isNull(id)) {
                mView.setCompleteTxtEnable(true);
            } else {
                mView.setCompleteTxtEnable(false);
            }
        }

    }

    /**
     * 初始化提现信息
     *
     * @param bean 信息
     */
    public void initWithdrawInfo(WithdrawAccountBean bean, int type) {
        mCurrentType = type;
        if (bean != null) {
            mAccountBean = bean;
            mView.setWithdrawInfo(bean, mCurrentType);
            if (mCurrentType == IWithdrawView.TYPE_ALI) {
                mView.setCompleteStatus(!StringUtil.isNull(mAccountBean.alipay_account));
            } else if (mCurrentType == IWithdrawView.TYPE_WX) {
                mView.setCompleteStatus(!StringUtil.isNull(mAccountBean.wx_account));
            }
        } else {
            mView.setWithdrawInfoView(mCurrentType);
        }
    }

    /**
     * 处理微信绑定点击
     */
    public void handleWxBind() {
        if (mCurrentType == IWithdrawView.TYPE_WX) {
            if (StringUtil.isNull(mAccountBean.wx_account)) {
                mView.startWxBind();
            }
        }
    }

    /**
     * 处理保存提现信息
     *
     * @param name    姓名
     * @param account 账号
     * @param id      身份证
     */
    public void handleComplete(String name, String account, String id) {
        String bindNickname = mCurrentType == IWithdrawView.TYPE_ALI ? StringUtil.EMPTY_STR : mAccountBean.wx_nickname;
        String bindAccount = mCurrentType == IWithdrawView.TYPE_ALI ? account : mAccountBean.wx_account;
        mWithdrawModel.bindWithdrawInfo(mCurrentType, name, bindNickname, bindAccount, id, new HttpSubscriber<EmptyResponseBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(EmptyResponseBean bean) {
                if (mCurrentType == IWithdrawView.TYPE_ALI) {
                    mAccountBean.alipay_account = account;
                } else if (mCurrentType == IWithdrawView.TYPE_WX) {
                    mAccountBean.wx_nickname = bindNickname;
                    mAccountBean.wx_account = bindAccount;
                }
                mAccountBean.real_name = name;
                mAccountBean.id_card = id;
                mView.handleBindSuccess(mAccountBean);
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
     * 处理获取微信信息
     *
     * @param appId     appId
     * @param appSecret appSecret
     * @param code      码
     */
    public void handleWxAuthorToken(String appId, String appSecret, String code) {
        mWxModel.getWxAccessToken(appId, appSecret, code, new HttpSubscriber<WxTokenBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(WxTokenBean bean) {
                getWxInfo(bean);
            }

            @Override
            public void onResponseError(String desc, String s1) {
                mView.showNetworkError();
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
     * 获取微信信息
     *
     * @param bean 信息
     */
    private void getWxInfo(WxTokenBean bean) {
        if (bean == null) {
            return;
        }
        mWxModel.getWxInfo(bean.access_token, bean.openid, new HttpSubscriber<WxBindBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(WxBindBean wxBindBean) {
                if (wxBindBean != null) {
                    mAccountBean.wx_account = wxBindBean.getOpenid();
                    mAccountBean.wx_nickname = wxBindBean.getNickname();
                    mView.showWxBindView(wxBindBean.getNickname());
                }
            }

            @Override
            public void onResponseError(String s, String s1) {
                mView.showNetworkError();
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
        mWithdrawModel.cancelBindAccountRequest();
    }
}
