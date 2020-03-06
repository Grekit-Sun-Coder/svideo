package cn.weli.svideo.baselib.helper;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import cn.weli.svideo.baselib.helper.handler.WeakHandler;

/**
 * 倒计时
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/15
 * @see [class/method]
 * @since [1.0.0]
 */
public class WeCountDownTimer {

    private long mMillisInFuture;

    private final long mCountdownInterval;

    private long mStopTimeInFuture;

    private boolean mCancelled = false;

    private CountDownCallBack mCountDownCallBack;

    public WeCountDownTimer(long millisInFuture, long countDownInterval) {
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    public synchronized final void cancel() {
        mCancelled = true;
        mHandler.removeMessages(MSG);
    }

    public synchronized final WeCountDownTimer start() {
        mCancelled = false;
        if (mMillisInFuture <= 0) {
            mCountDownCallBack.onFinish();
            return WeCountDownTimer.this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendEmptyMessage(MSG);
        return WeCountDownTimer.this;
    }

    public interface CountDownCallBack {

        public void onTick(long millisUntilFinished);

        public void onFinish();
    }

    public void setCountDownCallBack(CountDownCallBack countDownCallBack) {
        this.mCountDownCallBack = countDownCallBack;
    }

    public void setMillisInFuture(long millisInFuture) {
        this.mMillisInFuture = millisInFuture;
    }

    private static final int MSG = 1;

    private WeakHandler mHandler = new WeakHandler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            synchronized (WeCountDownTimer.this) {
                if (mCancelled) {
                    return false;
                }

                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    mCountDownCallBack.onFinish();
                } else if (millisLeft < mCountdownInterval) {
                    mHandler.sendEmptyMessageDelayed(MSG, millisLeft);
                } else {
                    long lastTickStart = SystemClock.elapsedRealtime();
                    mCountDownCallBack.onTick(millisLeft);

                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                    while (delay < 0) delay += mCountdownInterval;

                    mHandler.sendEmptyMessageDelayed(MSG, delay);
                }
            }
            return false;
        }
    });
}

