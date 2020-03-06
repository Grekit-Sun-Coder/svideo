package cn.weli.svideo.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import cn.weli.svideo.R;

/**
 * 首页渐变色text
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-16
 * @see cn.weli.svideo.module.main.component.widget.MainTabLayout
 * @since [1.0.0]
 */
public class GradientColorTextView extends AppCompatTextView {

    private Context mContext;
    private LinearGradient mLinearGradient;
    private Paint mPaint;
    private int mViewWidth = 0;
    private Rect mTextBound = new Rect();
    public boolean isSelected;

    public GradientColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            mViewWidth = getMeasuredWidth();
            mPaint = getPaint();
            String mTipText = getText().toString();
            mPaint.getTextBounds(mTipText, 0, mTipText.length(), mTextBound);
            mLinearGradient = new LinearGradient(0, 0, mViewWidth, 0,
                    ContextCompat.getColor(mContext, isSelected ? R.color.color_FFDB00 : R.color.color_90_white),
                    ContextCompat.getColor(mContext, isSelected ? R.color.color_FF3838 : R.color.color_90_white),
                    Shader.TileMode.REPEAT);
            mPaint.setShader(mLinearGradient);
            canvas.drawText(mTipText, getMeasuredWidth() / 2 - mTextBound.width() / 2, getMeasuredHeight() / 2 + mTextBound.height() / 2 - 2, mPaint);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
