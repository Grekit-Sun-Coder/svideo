package cn.weli.svideo.module.task.component.helper;

import com.hwangjr.rxbus.RxBus;

import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.helper.WeCountDownTimer;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.baselib.utils.ConstUtil;
import cn.weli.svideo.baselib.utils.TimeUtil;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.task.component.event.TaskCoinStatusEvent;
import cn.weli.svideo.module.task.model.TaskModel;
import cn.weli.svideo.module.task.model.bean.CoinOpenStatusBean;

/**
 * 开箱领金币
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-25
 * @see cn.weli.svideo.module.task.ui.TaskPageFragment
 * @since [1.0.0]
 */
public class TaskCoinOpenHelper implements WeCountDownTimer.CountDownCallBack {

    private static TaskCoinOpenHelper sInstance = null;

    private TaskModel mTaskModel;

    private CoinOpenStatusBean mStatusBean;

    private WeCountDownTimer mCountDownTimer;

    private WeakHandler mWeakHandler;

    public static TaskCoinOpenHelper getInstance() {
        if (sInstance == null) {
            synchronized (TaskCoinOpenHelper.class) {
                if (sInstance == null) {
                    sInstance = new TaskCoinOpenHelper();
                }
            }
        }
        return sInstance;
    }

    public TaskCoinOpenHelper() {
        mTaskModel = new TaskModel();
        mWeakHandler = new WeakHandler();
        mCountDownTimer = new WeCountDownTimer(ConstUtil.DAY, ConstUtil.SEC);
        mCountDownTimer.setCountDownCallBack(this);
    }

    /**
     * 查询开宝箱状态
     */
    public void queryTaskCoinOpenStatus() {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            return;
        }
        mTaskModel.getCoinOpenStatus(new SimpleHttpSubscriber<CoinOpenStatusBean>() {
            @Override
            public void onResponseSuccess(CoinOpenStatusBean bean) {
                mStatusBean = bean;
                if (bean.seconds > 0) {
                    startCurrentTimer(bean.seconds * ConstUtil.SEC);
                } else {
                    RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_CAN_OPEN,
                            WlVideoAppInfo.sAppCtx.getString(R.string.task_open_coin_title)));
                }
            }

            @Override
            public void onNetworkUnavailable() {
                RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_ERROR,
                        WlVideoAppInfo.sAppCtx.getString(R.string.common_str_network_error)));
            }

            @Override
            public void onResponseError(String s, String s1) {
                RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_ERROR,
                        WlVideoAppInfo.sAppCtx.getString(R.string.common_str_network_error)));
            }

            @Override
            public void onNetworkError() {
                RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_ERROR,
                        WlVideoAppInfo.sAppCtx.getString(R.string.common_str_network_error)));
            }
        });
    }

    /**
     * 检查开宝箱状态
     */
    public void checkTaskCoinStatus() {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            return;
        }
        if (mStatusBean != null) {
            if (mStatusBean.seconds > 0) {
                RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_TIMEING,
                        TimeUtil.milliseconds2String(getLeftMilliseconds(mStatusBean.seconds), TimeUtil.TIME_FORMAT_HOUR)));
            } else {
                RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_CAN_OPEN,
                        WlVideoAppInfo.sAppCtx.getString(R.string.task_open_coin_title)));
            }
        } else {
            RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_ERROR,
                    WlVideoAppInfo.sAppCtx.getString(R.string.common_str_network_error)));
        }
    }

    /**
     * 开启倒计时
     *
     * @param seconds 总时长
     */
    private void startCurrentTimer(long seconds) {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            return;
        }
        mCountDownTimer.cancel();
        mCountDownTimer.setMillisInFuture(seconds);
        mCountDownTimer.start();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long second = millisUntilFinished / ConstUtil.SEC;
        if (mStatusBean != null) {
            mStatusBean.seconds = second;
        }
        if (second == ConstUtil.MSEC) {
            mWeakHandler.postDelayed(this::onFinish, ConstUtil.SEC);
        }
        if (second > 0) {
            RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_TIMEING,
                    TimeUtil.milliseconds2String(getLeftMilliseconds(second), TimeUtil.TIME_FORMAT_HOUR)));
        } else {
            RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_CAN_OPEN,
                    WlVideoAppInfo.sAppCtx.getString(R.string.task_open_coin_title)));
        }
    }

    @Override
    public void onFinish() {
        mCountDownTimer.cancel();
        if (mStatusBean != null) {
            mStatusBean.seconds = 0;
        }
        RxBus.get().post(new TaskCoinStatusEvent(TaskCoinStatusEvent.STATUS_CAN_OPEN,
                WlVideoAppInfo.sAppCtx.getString(R.string.task_open_coin_title)));
    }

    /**
     * 获取剩下时间的对应毫秒
     *
     * @param second 秒
     * @return 毫秒
     */
    private long getLeftMilliseconds(long second) {
        return second * ConstUtil.SEC - 8 * ConstUtil.HOUR;
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        mTaskModel.cancelOpenCoinRequest();
        mTaskModel.cancelOpenActRequest();
    }
}
