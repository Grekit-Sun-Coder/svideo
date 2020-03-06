package cn.weli.svideo.baselib.component.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

/**
 * 平滑进度条
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see cn.weli.svideo.common.widget.webview.WeWebView
 * @since [1.0.0]
 */
public class WeWebProgressBar extends ProgressBar {

    public static final int MAX_PROGRESS = 100;

    private boolean isAnimStart;

    private int currentProgress;

    public WeWebProgressBar(Context context) {
        this(context, null);
    }

    public WeWebProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeWebProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setProgress(0);
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public boolean isAnimStart() {
        return isAnimStart;
    }

    /**
     * progressBar递增动画
     */
    public void startProgressAnimation(int newProgress) {
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "progress", currentProgress,
                newProgress);
        animator.setDuration(100);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    /**
     * progressBar消失动画
     */
    public void startDismissAnimation(final int progress) {
        isAnimStart = true;
        ObjectAnimator anim = ObjectAnimator.ofFloat(this, View.ALPHA, 1.0f, 0.0f);
        anim.setDuration(500);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();      // 0.0f ~ 1.0f
                int offset = 100 - progress;
                setProgress((int) (progress + offset * fraction));
            }
        });

        anim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束
                setProgress(0);
                setVisibility(View.GONE);
                isAnimStart = false;
            }
        });
        anim.start();
    }
}

