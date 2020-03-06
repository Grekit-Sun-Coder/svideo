package cn.weli.svideo.module.mine.model;

import java.util.HashMap;

import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.transfer.HttpRxFun;
import cn.etouch.retrofit.transfer.RxComposer;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.module.mine.model.api.MsgService;
import cn.weli.svideo.module.mine.model.bean.MsgResponseBean;
import cn.weli.svideo.module.mine.model.bean.RedPointBean;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see [class/method]
 * @since [1.0.0]
 */
public class MsgModel {

    private DisposableObserver mMsgListObserver;

    private DisposableObserver mPointObserver;

    private MsgService mMsgService;

    public MsgModel() {
        mMsgService = RetrofitManager.getInstance().getDefaultRetrofit().create(MsgService.class);
    }

    /**
     * 获取验证码
     *
     * @param lastTime   时间戳
     * @param subscriber 回调
     */
    public void getMsgList(String lastTime, HttpSubscriber<MsgResponseBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("last_read_time", lastTime);
        ApiHelper.addCommonParams(map);
        mMsgListObserver = mMsgService
                .getMsgList(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 获取小红点
     *
     * @param location   位置
     * @param subscriber 回调
     */
    public void getRedPoint(String location, HttpSubscriber<RedPointBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("location", location);
        ApiHelper.addCommonParams(map);
        mPointObserver = mMsgService
                .queryRedPoint(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    public void cancelMsgList() {
        if (mMsgListObserver != null && !mMsgListObserver.isDisposed()) {
            mMsgListObserver.dispose();
        }
    }

    public void cancelRedPoint() {
        if (mPointObserver != null && !mPointObserver.isDisposed()) {
            mPointObserver.dispose();
        }
    }
}
