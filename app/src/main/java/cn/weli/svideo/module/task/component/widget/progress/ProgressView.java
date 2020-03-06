package cn.weli.svideo.module.task.component.widget.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.baselib.utils.ConstUtil;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-21
 * @see [class/method]
 * @since [1.0.0]
 */
public class ProgressView extends View {

    /**
     * 一圈总角度
     */
    private static final int FLAG_TOTAL_ANGLE = 360;
    /**
     * 50毫秒更新一次进度
     */
    private static final int FLAG_UPDATE_DELAY = 50;
    /**
     * 奖励倒计时 时间单位s
     */
    public static int FLAG_DEFAULT_TOTAL = 30000;
    /**
     * 展示的消息
     */
    private final int MSG_SHOW_ANI = 0x100;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 圆弧画笔
     */
    private Paint mPaintArc;
    /**
     * 圈半径
     */
    private int mCircleRadius;
    /**
     * 最外层圆弧的宽度
     */
    private float mPaintArcWidth;
    /**
     * 宽
     */
    private int mWidth;
    /**
     * 高
     */
    private int mHeight;
    /**
     * 渐变色数组
     */
    private int[] mColors;
    /**
     * 颜色渐变
     */
    private SweepGradient mSweepGradient;
    /**
     * 开始角度
     */
    private float mStartAngle;
    /**
     * 总时长
     */
    private long mTotalAngleTime;
    /**
     * 扫过的角度
     */
    private float mSweepAngle;
    /**
     * 一份角度值
     */
    private float mSweepAngleUnit;
    /**
     * 一圈结束的监听
     */
    private OnLoadingFinishListener mFinishListener;
    /**
     * 当前转动视频的id
     */
    private long mCurrentVideoId = -1;
    /**
     * 剩下转动的时间
     */
    private long mRestSweepTime;
    /**
     * 上次结束时间
     */
    private long mLastFinishTime;


    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mCircleRadius = context.getResources().getDimensionPixelSize(R.dimen.common_len_54px);
        mPaintArcWidth = context.getResources().getDimensionPixelSize(R.dimen.common_len_6px);
        mTotalAngleTime = FLAG_DEFAULT_TOTAL;
        init();
    }

    private void init() {
        mColors = new int[]{
                ContextCompat.getColor(mContext, R.color.color_FFDB00),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_AA43F8),
                ContextCompat.getColor(mContext, R.color.color_AA43F8),
                ContextCompat.getColor(mContext, R.color.color_AA43F8),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FF3838),
                ContextCompat.getColor(mContext, R.color.color_FFDB00),
                ContextCompat.getColor(mContext, R.color.color_AA43F8)};
        mSweepGradient = new SweepGradient(getMeasuredWidth() / 2, getMeasuredHeight() / 2, mColors, null);
        mPaintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setAntiAlias(true);
        mPaintArc.setStrokeWidth(mPaintArcWidth);
        mPaintArc.setStrokeCap(Paint.Cap.ROUND);
        mPaintArc.setShader(mSweepGradient);

        mStartAngle = -90;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mCircleRadius * 2, mCircleRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF rectF = new RectF(0 + mPaintArcWidth / 2, 0 + mPaintArcWidth / 2
                , mWidth - mPaintArcWidth / 2, mHeight - mPaintArcWidth / 2);
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mPaintArc);
    }

    /**
     * 设置总时长
     *
     * @param totalAngleTime 总时长
     */
    public void setTotalAngleTime(int totalAngleTime) {
        mTotalAngleTime = totalAngleTime * ConstUtil.SEC;
        mSweepAngleUnit = FLAG_TOTAL_ANGLE * 1.0f / (mTotalAngleTime * 1.0f / FLAG_UPDATE_DELAY);
    }

    /**
     * 重置角度
     */
    public void resetProgress(long videoId) {
        mSweepAngle = 0f;
        invalidate();
        if (mRestSweepTime > 0 && videoId != -1) {
            long consumeTime = System.currentTimeMillis() - mLastFinishTime;
            if (consumeTime < mRestSweepTime) {
                mRestSweepTime = mRestSweepTime - consumeTime;
            }
            start(mCurrentVideoId, mRestSweepTime);
        }
    }

    /**
     * 开始转圈
     */
    public void start(long videoId, long duration) {
        try {
            if (mTotalAngleTime <= 0 || mSweepAngleUnit <= 0 || duration <= 0) {
                Logger.w("Start failed, videoId=" + videoId);
                return;
            }
            if (videoId != mCurrentVideoId) {
                mRestSweepTime = duration;
            }
            mCurrentVideoId = videoId;
            mHandler.removeMessages(MSG_SHOW_ANI);
            mHandler.sendEmptyMessage(MSG_SHOW_ANI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 结束转圈
     */
    public void stop() {
        try {
            mHandler.removeMessages(MSG_SHOW_ANI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFinishListener(OnLoadingFinishListener finishListener) {
        mFinishListener = finishListener;
    }

    private WeakHandler mHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_ANI:
                    if (mRestSweepTime <= 0) {
                        mRestSweepTime = 0;
                        return false;
                    }
                    if (mSweepAngle >= FLAG_TOTAL_ANGLE) {
                        mSweepAngle = FLAG_TOTAL_ANGLE;
                        invalidate();
                        stop();
                        mLastFinishTime = System.currentTimeMillis();
                        if (mFinishListener != null) {
                            mFinishListener.onProgressFinish();
                        }
                        return false;
                    }
                    mSweepAngle = mSweepAngle + mSweepAngleUnit;
                    mRestSweepTime = mRestSweepTime - FLAG_UPDATE_DELAY;
                    invalidate();
                    mHandler.sendEmptyMessageDelayed(MSG_SHOW_ANI, 50);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public interface OnLoadingFinishListener {
        /**
         * 一圈结束
         */
        void onProgressFinish();
    }
}
