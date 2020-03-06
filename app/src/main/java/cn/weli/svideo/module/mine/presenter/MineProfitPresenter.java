package cn.weli.svideo.module.mine.presenter;

import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.module.mine.view.IMineProfitView;
import cn.weli.svideo.module.task.model.WithdrawModel;
import cn.weli.svideo.module.task.model.bean.ProfitBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class MineProfitPresenter implements IPresenter {

    private IMineProfitView mView;

    private ProfitBean mProfitBean;

    private WithdrawModel mWithdrawModel;

    public MineProfitPresenter(IMineProfitView view) {
        mView = view;
        mWithdrawModel = new WithdrawModel();
    }

    /**
     * 初始化我的收益
     */
    public void initMineProfit() {
        mProfitBean = mWithdrawModel.getUserProfitInfo();
        if (mProfitBean != null) {
            mView.showUserProfitInfo(mProfitBean);

        }
        requestUserProfit();
    }

    /**
     * 获取用户收益数据
     */
    public void requestUserProfit() {
        mWithdrawModel.getUserProfit(new HttpSubscriber<ProfitBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(ProfitBean bean) {
                if (bean != null) {
                    mProfitBean = bean;
                    mView.showUserProfitInfo(bean);
                    mWithdrawModel.saveUserProfitInfo(bean);
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

    /**
     * 获取用户收益数据
     *
     * @return 收益
     */
    public ProfitBean getProfitBean() {
        return mProfitBean;
    }

    @Override
    public void clear() {
        mWithdrawModel.cancelProfitRequest();
    }
}
