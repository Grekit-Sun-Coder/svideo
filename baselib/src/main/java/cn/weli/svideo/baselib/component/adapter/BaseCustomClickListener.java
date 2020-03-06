package cn.weli.svideo.baselib.component.adapter;

import android.view.View;

/**
 * 防止快速点击
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @see CommonRecyclerAdapter
 * @since [1.0.0]
 */
public abstract class BaseCustomClickListener implements View.OnClickListener {

    private long mLastClickTime;
    private long timeInterval = 500L;

    public BaseCustomClickListener() {

    }

    public BaseCustomClickListener(long interval) {
        this.timeInterval = interval;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > timeInterval) {
            // 单次点击事件
            onSingleClick(v);
            mLastClickTime = nowTime;
        } else {
            // 快速点击事件
//            onFastClick();
        }
    }

    protected abstract void onSingleClick(View v);
//    protected abstract void onFastClick();
}
