package cn.weli.svideo.module.task.presenter;

import java.util.List;

import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.task.model.WithdrawModel;
import cn.weli.svideo.module.task.model.bean.WithdrawAccountBean;
import cn.weli.svideo.module.task.model.bean.WithdrawProBean;
import cn.weli.svideo.module.task.view.IWithdrawView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class WithdrawPresenter implements IPresenter {

    private IWithdrawView mView;

    private WithdrawModel mModel;

    private WithdrawAccountBean mAccountBean;

    private int mCurrentType = -1;

    public WithdrawPresenter(IWithdrawView view) {
        mView = view;
        mModel = new WithdrawModel();
    }

    /**
     * 处理类型选择
     *
     * @param type 类型
     */
    public void handleTypeSelect(int type, int selectPosition) {
        if (mCurrentType == type) {
            return;
        }
        if (type == IWithdrawView.TYPE_ALI) {
            mView.showAliType(selectPosition >= 0);
        } else if (type == IWithdrawView.TYPE_WX) {
            mView.showWxType(selectPosition >= 0);
        }
        mCurrentType = type;
    }

    /**
     * 获取提现账号信息
     */
    public void getWithdrawAccountInfo() {
        mModel.getWithdrawInfo(new HttpSubscriber<WithdrawAccountBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(WithdrawAccountBean bean) {
                mAccountBean = bean;
                mView.setWithdrawAccount(mAccountBean);
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
                getWithdrawProList();
                mView.finishLoadView();
            }
        });
    }

    /**
     * 获取提现产品列表
     */
    public void getWithdrawProList() {
        getAliPayList();
    }

    /**
     * 获取支付宝列表
     */
    private void getAliPayList() {
        mModel.getWithdrawProList(IWithdrawView.TYPE_ALI_STR, new SimpleHttpSubscriber<List<WithdrawProBean>>() {

            @Override
            public void onResponseSuccess(List<WithdrawProBean> list) {
                if (list != null && !list.isEmpty()) {
                    getWxPayList(list);
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
            }
        });
    }

    /**
     * 获取支付宝列表
     */
    private void getWxPayList(List<WithdrawProBean> aliList) {
        mModel.getWithdrawProList(IWithdrawView.TYPE_WX_STR, new SimpleHttpSubscriber<List<WithdrawProBean>>() {

            @Override
            public void onResponseSuccess(List<WithdrawProBean> list) {
                if (list != null && !list.isEmpty()) {
                    mView.showWithdrawProList(aliList, list);
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
        });
    }

    /**
     * 处理产品点击
     *
     * @param position 位置
     */
    public void handleProductClick(int position) {
        if (mAccountBean != null) {
            if (mCurrentType == IWithdrawView.TYPE_ALI) {
                if (!StringUtil.isNull(mAccountBean.alipay_account)) {
                    mView.handleAliProductSelected(position);
                } else {
                    mView.showAccountEmptyTip();
                }
            } else if (mCurrentType == IWithdrawView.TYPE_WX) {
                if (!StringUtil.isNull(mAccountBean.wx_account)) {
                    mView.handleWxProductSelected(position);
                } else {
                    mView.showAccountEmptyTip();
                }
            }
        } else {
            mView.showAccountEmptyTip();
        }
    }

    /**
     * 处理绑定结果
     *
     * @param bean 绑定数据
     */
    public void handleBindResult(WithdrawAccountBean bean) {
        mAccountBean = bean;
        mView.setWithdrawAccount(mAccountBean);
        getWithdrawProList();
    }

    /**
     * 提交提现
     *
     * @param bean 提现数据
     */
    public void applyWithdraw(WithdrawProBean bean) {
        mModel.applyWithdraw(String.valueOf(bean.id), new HttpSubscriber<EmptyResponseBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(EmptyResponseBean emptyResponseBean) {
                mView.handleWithdrawSuccess();
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
     * 处理产品信息
     *
     * @param bean 产品
     */
    public void handleProductInfo(WithdrawProBean bean, int position) {
        if (bean != null) {
            if (bean.can_buy) {
                if (mCurrentType == IWithdrawView.TYPE_ALI) {
                    mView.setCurrentAliProductSelected(position);
                } else {
                    mView.setCurrentWxProductSelected(position);
                }
            } else {
                mView.showToast(bean.can_not_buy_msg);
            }
        }
    }

    public WithdrawAccountBean getAccountBean() {
        return mAccountBean;
    }

    public int getCurrentType() {
        return mCurrentType;
    }

    @Override
    public void clear() {
        mModel.cancelWithdrawListRequest();
        mModel.cancelAccountRequest();
        mModel.cancelApplyRequest();
    }
}
