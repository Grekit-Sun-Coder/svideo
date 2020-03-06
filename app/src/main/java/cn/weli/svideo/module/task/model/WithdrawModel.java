package cn.weli.svideo.module.task.model;

import java.util.HashMap;
import java.util.List;

import cn.etouch.cache.CacheManager;
import cn.etouch.logger.Logger;
import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.response.EmptyResponseBean;
import cn.etouch.retrofit.transfer.HttpRxFun;
import cn.etouch.retrofit.transfer.RxComposer;
import cn.weli.common.libs.WeliLib;
import cn.weli.svideo.common.constant.CacheConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.module.task.model.api.WithdrawService;
import cn.weli.svideo.module.task.model.bean.FlowDetailBean;
import cn.weli.svideo.module.task.model.bean.ProfitBean;
import cn.weli.svideo.module.task.model.bean.WithdrawAccountBean;
import cn.weli.svideo.module.task.model.bean.WithdrawProBean;
import cn.weli.svideo.module.task.view.IWithdrawView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * 提现金币金钱相关
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see cn.weli.svideo.module.task.presenter.WithdrawPresenter
 * @since [1.0.0]
 */
public class WithdrawModel {

    private DisposableObserver mWithdrawListObserver;

    private DisposableObserver mProfitObserver;

    private DisposableObserver mFlowObserver;

    private DisposableObserver mBindInfoObserver;

    private DisposableObserver mBindAccountObserver;

    private DisposableObserver mApplyObserver;

    private WithdrawService mWithdrawService;

    public WithdrawModel() {
        mWithdrawService = RetrofitManager.getInstance().getDefaultRetrofit().create(WithdrawService.class);
    }

    /**
     * 获取提现列表
     *
     * @param subscriber 产品回调
     */
    public void getWithdrawProList(String type, HttpSubscriber<List<WithdrawProBean>> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("product_type", type);
        ApiHelper.addCommonParams(map);
        mWithdrawListObserver = mWithdrawService
                .getWithdrawProList(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 获取金币提现收益信息
     *
     * @param subscriber 回调
     */
    public void getUserProfit(HttpSubscriber<ProfitBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mProfitObserver = mWithdrawService
                .getUserProfit(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 获取流水
     *
     * @param subscriber 回调
     */
    public void getFlowDetailList(String type, int page, int pageSize, HttpSubscriber<List<FlowDetailBean>> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("page", String.valueOf(page));
        map.put("page_size", String.valueOf(pageSize));
        ApiHelper.addCommonParams(map);
        mFlowObserver = mWithdrawService
                .getFlowList(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 获取提现账号信息
     *
     * @param subscriber 回调
     */
    public void getWithdrawInfo(HttpSubscriber<WithdrawAccountBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mBindInfoObserver = mWithdrawService
                .getBindInfo(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 绑定提现账号信息
     *
     * @param subscriber 回调
     */
    public void bindWithdrawInfo(int type, String name, String nickName, String account, String id, HttpSubscriber<EmptyResponseBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        if (type == IWithdrawView.TYPE_ALI) {
            map.put("real_name", name);
            map.put("ali_account", account);
        } else if (type == IWithdrawView.TYPE_WX) {
            map.put("real_name", name);
            map.put("wx_nickname", nickName);
            map.put("wx_account", account);
        }
        try {
            map.put("id_no", WeliLib.getInstance().doTheEncrypt(id, 4));
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        ApiHelper.addCommonParams(map);
        mBindAccountObserver = mWithdrawService
                .bindWithdrawAccount(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 提交提现
     *
     * @param subscriber 回调
     */
    public void applyWithdraw(String productId, HttpSubscriber<EmptyResponseBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("product_id", productId);
        ApiHelper.addCommonParams(map);
        mApplyObserver = mWithdrawService
                .withdrawApply(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 缓存用户的收益信息
     *
     * @param bean 收益信息
     */
    public void saveUserProfitInfo(ProfitBean bean) {
        CacheManager.getInstance().saveSerializable(CacheConstant.CACHE_PROFIT_INFO, bean);
    }

    /**
     * 获取缓存用户的收益信息
     *
     * @return 缓存用户的收益信息
     */
    public ProfitBean getUserProfitInfo() {
        return CacheManager.getInstance().loadSerializable(CacheConstant.CACHE_PROFIT_INFO);
    }

    public void cancelWithdrawListRequest() {
        if (mWithdrawListObserver != null && !mWithdrawListObserver.isDisposed()) {
            mWithdrawListObserver.dispose();
        }
    }

    public void cancelProfitRequest() {
        if (mProfitObserver != null && !mProfitObserver.isDisposed()) {
            mProfitObserver.dispose();
        }
    }

    public void cancelFlowRequest() {
        if (mFlowObserver != null && !mFlowObserver.isDisposed()) {
            mFlowObserver.dispose();
        }
    }

    public void cancelAccountRequest() {
        if (mBindInfoObserver != null && !mBindInfoObserver.isDisposed()) {
            mBindInfoObserver.dispose();
        }
    }

    public void cancelBindAccountRequest() {
        if (mBindAccountObserver != null && !mBindAccountObserver.isDisposed()) {
            mBindAccountObserver.dispose();
        }
    }

    public void cancelApplyRequest() {
        if (mApplyObserver != null && !mApplyObserver.isDisposed()) {
            mApplyObserver.dispose();
        }
    }
}
