package cn.weli.svideo.module.video.component.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import cn.weli.svideo.R;

/**
 * 视频加载进度条
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-09-23
 * @see VideoPlayView
 * @since [1.0.0]
 */
public class VideoLoadingView extends View {

    private Context mContext;
    private int mWidth, mHeight;
    private int mDefaultWidth, mDefaultHeight;
    private int mProgressWidth;
    private int mMinProgressWidth;
    private Paint mPaint;
    private int mColor;
    private boolean canLoading;
    private SweepGradient mSweepGradient;

    public VideoLoadingView(Context context) {
        this(context, null);
    }

    public VideoLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mDefaultWidth = context.getResources().getDimensionPixelSize(R.dimen.common_len_300px);
        mDefaultHeight = context.getResources().getDimensionPixelSize(R.dimen.common_len_4px);
        mProgressWidth = context.getResources().getDimensionPixelSize(R.dimen.common_len_200px);
        mMinProgressWidth = context.getResources().getDimensionPixelSize(R.dimen.common_len_200px);
        mColor = ContextCompat.getColor(context, R.color.color_white);

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
    }

    public void setCanLoading(boolean canLoading) {
        this.canLoading = canLoading;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getValue(widthMeasureSpec, true);
        int height = getValue(heightMeasureSpec, false);
        setMeasuredDimension(width, height);
    }

    /**
     * 获取view的宽高值
     *
     * @param measureSpec
     * @param isWidth     是否是测量宽度
     * @return
     */
    private int getValue(int measureSpec, boolean isWidth) {
        try {
            int mode = MeasureSpec.getMode(measureSpec);
            int size = MeasureSpec.getSize(measureSpec);
            switch (mode) {
                case MeasureSpec.EXACTLY:
                    return size;
                case MeasureSpec.AT_MOST:
                    return Math.min(isWidth ? mDefaultWidth : mDefaultHeight, size);
                case MeasureSpec.UNSPECIFIED:
                    return isWidth ? mDefaultWidth : mDefaultHeight;
                default:
                    return isWidth ? mDefaultWidth : mDefaultHeight;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        try {
            mWidth = w;
            mHeight = h;
            mPaint.setStrokeWidth(mHeight);
            mSweepGradient = new SweepGradient(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f,
                    ContextCompat.getColor(mContext, R.color.color_43FFE5), ContextCompat.getColor(mContext, R.color.color_D40CD7));
            mPaint.setShader(mSweepGradient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (!canLoading) {
                return;
            }
            if (mProgressWidth < mWidth) {
                mProgressWidth += 20;
            } else {
                mProgressWidth = mMinProgressWidth;
            }
            int currentColorValue = 255 - 255 * mProgressWidth / mWidth;
            if (currentColorValue > 255) {
                currentColorValue = 255;
            }
            if (currentColorValue < 60) {
                currentColorValue = 60;
            }
            mPaint.setColor(getAlphaColor(mColor, currentColorValue));
            canvas.drawLine(mWidth / 2 - mProgressWidth / 2, mDefaultHeight / 2, mWidth / 2 + mProgressWidth / 2, mDefaultHeight / 2, mPaint);
            postInvalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据透明值获取绘制时要绘制透明的颜色
     *
     * @param color 原色
     * @param alpha 透明度
     */
    private int getAlphaColor(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
}
