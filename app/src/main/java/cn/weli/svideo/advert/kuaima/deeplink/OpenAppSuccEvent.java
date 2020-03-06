package cn.weli.svideo.advert.kuaima.deeplink;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.WlVideoApplication;

public class OpenAppSuccEvent {
    private final static String THREAD_NAME = "OpenAppSuccEvent_HandlerThread";
    private final static int WHAT_SEND_SUCCESS_EVENT = 100;
    private static volatile OpenAppSuccEvent sInstance;
    private HandlerThread mThread;
    private final Handler mHandler;
    private final Executor mExecutor = Executors.newCachedThreadPool(); //默认的检测策略配置
    private Policy mPolicy = Policy.defaultPolicy();

    //单例模式
    public static OpenAppSuccEvent obtain() {
        if (sInstance == null) {
            synchronized (OpenAppSuccEvent.class) {
                if (sInstance == null) {
                    sInstance = new OpenAppSuccEvent();
                }
            }
        }
        return sInstance;
    }

    //构造对象，初始化数据
    private OpenAppSuccEvent() {
        if (mThread == null) {
            mThread = new HandlerThread(THREAD_NAME,
                    Process.THREAD_PRIORITY_BACKGROUND);
            mThread.start();
        }
        mHandler = new Handler(mThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == WHAT_SEND_SUCCESS_EVENT) {
                    EventData eventData = null;
                    if (msg.obj instanceof EventData) {
                        eventData = (EventData) msg.obj;
                    }
                    if (eventData != null) {
                        //检测消息处理
                        onHandleOpenAppSuccEvent(eventData);
                    }
                }
                return true;
            }
        });
    }

    /**
     * 初始化轮训测试
     *
     * @param policy 具体策略 * @return
     */
    public OpenAppSuccEvent initPolicy(Policy policy) {
        if (policy == null) {
            return this;
        }
        mPolicy = policy;
        return this;
    }

    /**
     * 需要检测事件时，外部调用;
     */
    public void start(OnEventCallBack callBack) {
        mHandler.removeMessages(WHAT_SEND_SUCCESS_EVENT);
        Message msg = mHandler.obtainMessage();
        msg.what = WHAT_SEND_SUCCESS_EVENT;
        msg.obj = new EventData(callBack); //开始发送检测事件
        msg.sendToTarget();
    }

    //应用在前台的处理逻辑
    private void loopCheckForeground(EventData eventData) {
        if (eventData == null) {
            return;
        }
        eventData.check();
        //如果检测时间到了策略的限制则上报失败
        if (eventData.countTimes() * mPolicy.mRateTime > mPolicy.mMaxTime) {
            //app 在规定时间内没有离开过，则上报失败
            onSendEvent(eventData.setResult(false));
            return;
        }
        Message msg = mHandler.obtainMessage();
        msg.what = WHAT_SEND_SUCCESS_EVENT;
        msg.obj = eventData;
        mHandler.sendMessageDelayed(msg, mPolicy.mRateTime);
    }

    //收到检测消息开始处理
    private void onHandleOpenAppSuccEvent(EventData eventData) {
        if (eventData == null) {
            return;
        }
        boolean isForeground = WlVideoAppInfo.getInstance().isRunningForeground();
        if (isForeground) {
            loopCheckForeground(eventData);
        } else {
            //app 退到后台，发送成功日志
            onSendEvent(eventData.setResult(true));
        }
    }

    //发送日志
    private void onSendEvent(EventData eventData) {
        if (eventData == null) {
            return;
        }
        mExecutor.execute(eventData);
    }

    //日志发送和处理类
    private static class EventData implements Runnable, Serializable {
        public final AtomicInteger sAtomicInteger = new AtomicInteger(0);
        public final AtomicBoolean sResult = new AtomicBoolean(false);
        private OnEventCallBack callBack;


        public EventData(OnEventCallBack callBack) {
            this.callBack = callBack;
        }

        public EventData setResult(boolean isSuccess) {
            sResult.set(isSuccess);
            return this;
        }

        public int countTimes() {
            return sAtomicInteger.get();
        }

        public void check() {
            sAtomicInteger.incrementAndGet();
        }

        @Override
        public void run() {
            if (callBack != null) {
                WlVideoApplication.runOnUiThread(() -> callBack.onCallBack(sResult.get()));
            }
        }
    }

    //检测策略类
    private static class Policy {
        public final static int DEFAULT_RATE_TIME = 200;
        public final static int DEFAULT_MAX_TIME = 3 * 1000;
        public int mRateTime = DEFAULT_RATE_TIME;//事件间隔即频率，单位:毫秒，默认值为:200
        public int mMaxTime = DEFAULT_MAX_TIME;//检测最大时长，单位:毫秒，默认值为:3000

        public Policy() {
        }

        public Policy(int rateTime, int maxTime) {
            mRateTime = rateTime;
            mMaxTime = maxTime;
        }

        public static Policy create(int rateTime, int maxTime) {
            return new Policy(rateTime, maxTime);
        }

        public static Policy defaultPolicy() {
            return new Policy();
        }
    }

    public interface OnEventCallBack {
        void onCallBack(boolean success);
    }
}