package cn.weli.svideo.module.video.component.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;

/**
 * 旋转imageView
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-04
 * @see [class/method]
 * @since [1.0.0]
 */
public class RotateImageView extends AppCompatImageView {

    private static final int ROTATE_TIME = 20000;
    private ObjectAnimator mObjectAnimator;
    private long mCurrentPlayTime;
    private boolean stop = true;

    public RotateImageView(Context context) {
        this(context, null);
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RotateImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        if (mObjectAnimator == null) {
            mObjectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0, 360);
            mObjectAnimator.setDuration(ROTATE_TIME);
            mObjectAnimator.setInterpolator(new LinearInterpolator());
            mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        }
    }

    /**
     * 开始旋转
     */
    public void startAnimation() {
//        if (stop) {
//            mObjectAnimator.start();
//            mObjectAnimator.setCurrentPlayTime(mCurrentPlayTime);
//            stop = false;
//        }
    }

    /**
     * 暂停旋转
     */
    public void stopAnimation() {
//        if (!stop) {
//            mCurrentPlayTime = mObjectAnimator.getCurrentPlayTime();
//            mObjectAnimator.cancel();
//            stop = true;
//        }
    }
}
