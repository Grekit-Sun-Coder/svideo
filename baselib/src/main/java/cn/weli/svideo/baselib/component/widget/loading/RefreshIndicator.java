package cn.weli.svideo.baselib.component.widget.loading;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

import cn.weli.svideo.baselib.R;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-21
 * @see RefreshLoadingView
 * @since [1.0.0]
 */
public class RefreshIndicator extends Indicator {

    public static final float SCALE = 1.0f;
    private float[] scaleFloats = new float[]{SCALE, SCALE, SCALE};

    @Override
    public void draw(Canvas canvas, Paint paint) {
        float circleSpacing = 4;
        float radius = (Math.min(getWidth(), getHeight()) - circleSpacing * 2) / 6;
        float x = getWidth() / 2 - (radius * 2 + circleSpacing);
        float y = getHeight() / 2;
        for (int i = 0; i < 3; i++) {
            canvas.save();
            float translateX = x + (radius * 2) * i + circleSpacing * i;
            canvas.translate(translateX, y);
            canvas.scale(scaleFloats[i], scaleFloats[i]);
            if (i == 0) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.color_FFDB00));
            } else if (i == 1) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.color_FF3838));
            } else {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.color_AA43F8));
            }
            canvas.drawCircle(0, 0, radius, paint);
            canvas.restore();
        }
    }

    @Override
    public ArrayList<ValueAnimator> onCreateAnimators() {
        ArrayList<ValueAnimator> animators = new ArrayList<>();
        int[] delays = new int[]{120, 240, 360};
        for (int i = 0; i < 3; i++) {
            final int index = i;
            ValueAnimator scaleAnim = ValueAnimator.ofFloat(1, 0.3f, 1);
            scaleAnim.setDuration(750);
            scaleAnim.setRepeatCount(-1);
            scaleAnim.setStartDelay(delays[i]);
            addUpdateListener(scaleAnim, new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    scaleFloats[index] = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            animators.add(scaleAnim);
        }
        return animators;
    }


}

