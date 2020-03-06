package cn.weli.svideo.module.video.component.widget.videoheart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.helper.BitmapHelper;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;

/**
 * 点击出现爱心的效果
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class HeartRelativeLayout extends RelativeLayout implements View.OnTouchListener {

    /**
     * 双击间格毫秒延时
     */
    private static final int TIME_OUT_DELAY = 400;
    /**
     * 双击点赞大小
     */
    private static final int DEFAULT_SCALE_SIZE = 4;
    /**
     * 透明度，默认为255，0为消失不可见
     */
    private static final int MAX_ALPHA = 255;
    /**
     * 存放多个心形图
     */
    private List<HeartBean> list = new ArrayList<>();
    /**
     * true为开始动画，false为结束动画
     */
    private boolean START = true;
    /**
     * 动画刷新频率
     */
    private int refreshRate = 16;
    /**
     * 最小旋转角度
     */
    private int degreesMin = -30;
    /**
     * 最大旋转角度
     */
    private int degreesMax = 30;
    /**
     * 初始图片
     */
    private Bitmap bitmap;
    /**
     * 控制bitmap旋转角度和缩放的矩阵
     */
    private Matrix matrix = new Matrix();
    /**
     * 记录第一次点击的时间
     */
    private long singleClickTime;
    /**
     * 是否需要抖动效果 默认抖动
     */
    private boolean isShake = true;
    /**
     * 是否是双击
     */
    private boolean isDoubleClick;

    private boolean enableDoubleClick;

    private OnHeartTouchListener mTouchListener;

    private GestureDetector mGestureDetector;

    public interface OnHeartTouchListener {

        void onSingleClick();

        void onDoubleClick();

    }

    public void setTouchListener(OnHeartTouchListener touchListener) {
        mTouchListener = touchListener;
    }

    public void setEnableDoubleClick(boolean enableDoubleClick) {
        this.enableDoubleClick = enableDoubleClick;
    }

    private WeakHandler handler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        refresh();
                        invalidate();
                        if (list != null && list.size() > 0) {
                            handler.sendEmptyMessageDelayed(0, refreshRate);// 延时
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public HeartRelativeLayout(Context context) {
        super(context);
    }

    public HeartRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HeartViewGroup);
            isShake = typedArray.getBoolean(R.styleable.HeartViewGroup_heart_shake, isShake);
            refreshRate = typedArray.getInt(R.styleable.HeartViewGroup_heart_refresh_rate, refreshRate);
            degreesMin = typedArray.getInt(R.styleable.HeartViewGroup_heart_degrees_interval_min, degreesMin);
            degreesMax = typedArray.getInt(R.styleable.HeartViewGroup_heart_degrees_interval_max, degreesMax);
            typedArray.recycle();
            singleClickTime = System.currentTimeMillis();

            GestureListener gestureListener = new GestureListener();
            mGestureDetector = new GestureDetector(context, gestureListener);
            mGestureDetector.setOnDoubleTapListener(new DoubleGestureListener());
            //为当前View设置触控
            setOnTouchListener(this);
            setLongClickable(true);
            setClickable(true);
            setFocusable(true);
        } catch (Exception e) {
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        try {
            if (list == null || matrix == null || bitmap == null) {
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                HeartBean heartBean = list.get(i);
                // 重置
                matrix.reset();
                // 缩放原图
                matrix.postScale(heartBean.scanle * DEFAULT_SCALE_SIZE,
                        heartBean.scanle * DEFAULT_SCALE_SIZE,
                        heartBean.X + bitmap.getWidth() / 2,
                        heartBean.Y + bitmap.getHeight() / 2);
                // 旋转
                matrix.postRotate(heartBean.degrees,
                        heartBean.X + bitmap.getWidth() / 2,
                        heartBean.Y + bitmap.getHeight() / 2);

                canvas.save();
                canvas.concat(matrix);
                canvas.drawBitmap(bitmap,
                        heartBean.X - bitmap.getWidth() / 2,
                        heartBean.Y - bitmap.getHeight() / 2,
                        heartBean.paint);
                canvas.restore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        try {
            return mGestureDetector.onTouchEvent(event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    public boolean onTouchEvent(final MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                long newClickTime = System.currentTimeMillis();
//                //双击以上事件都会调用心动动画
//                if (newClickTime - singleClickTime < TIME_OUT_DELAY) {
//                    //开始心动动画
//                    startSwipe(event);
//                }
//                singleClickTime = newClickTime;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Logger.e("onTouchEvent MotionEvent.ACTION_MOVE");
//                break;
//            case MotionEvent.ACTION_UP:
//                Logger.e("onTouchEvent MotionEvent.ACTION_UP");
//                break;
//            default:
//                break;
//        }
//        return mGestureDetector.onTouchEvent(event);
//    }

    /**
     * 初始化paint
     */
    private Paint initPaint(int alpha) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setAlpha(alpha);
        return paint;
    }

    /**
     * 开始心动动画
     *
     * @param event 点击事件
     */
    private void startSwipe(MotionEvent event) {
        try {
            if (list == null || handler == null) {
                return;
            }
            HeartBean bean = new HeartBean();
            bean.scanle = 1;
            bean.alpha = MAX_ALPHA;
            bean.X = (int) event.getX();
            bean.Y = (int) event.getY();
            bean.paint = initPaint(bean.alpha);
            bean.degrees = degrees(degreesMin, degreesMax);

            if (list.size() == 0) {
                START = true;
            }
            list.add(bean);
            invalidate();
            if (START) {
                handler.sendEmptyMessage(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 刷新
     */
    private void refresh() {
        try {
            if (list == null) {
                return;
            }
            List<HeartBean> removeList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                HeartBean bean = list.get(i);
                bean.count++;
                if (!START && bean.alpha == 0) {
                    //透明度减为0后，从list里清除
                    removeList.add(bean);
                    bean.paint = null;
                    continue;
                } else if (START) {
                    START = false;
                }
                if (bean.count <= 1 && isShake) {
                    //初始为1.9倍大小 步骤A
                    bean.scanle = 1.9f;
                } else if (bean.count <= 6 && isShake) {
                    //每次缩小0.2，缩小5帧后为0.9 步骤B
                    bean.scanle -= 0.2;
                } else if (bean.count <= 15 && isShake) {
                    //恢复原图大小 步骤C ABC三个步骤主要实现一个初始跳动心心的效果
                    bean.scanle = 1;
                } else {
                    //放大倍数 每次放大0.1
                    bean.scanle += 0.1;
                    //透明度
                    bean.alpha -= 10;
                    if (bean.alpha < 0) {
                        bean.alpha = 0;
                    }
                }
                bean.paint.setAlpha(bean.alpha);
            }
            if (!removeList.isEmpty()) {
                list.removeAll(removeList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成一个随机整数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 整数
     */
    private int degrees(int min, int max) {
        try {
            //若最小值大于最大值，则重新赋值正位
            if (min > max) {
                int x = min;
                min = max;
                max = x;
            }
            Random random = new Random();
            return random.nextInt((max - min) + 1) + min;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return min;
    }

    /**
     * 设置跳动的图片
     *
     * @param id 图片资源id
     */
    public void setSwipeImage(int id) {
        try {
            bitmap = BitmapFactory.decodeResource(getResources(), id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置是否抖动一下
     * 默认抖动
     *
     * @param isShake true为抖动
     */
    public void setShake(boolean isShake) {
        this.isShake = isShake;
    }

    /**
     * 设置动画刷新频率
     * 默认16ms
     *
     * @param refreshRate 刷新频率，单位：毫秒
     */
    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    /**
     * 图片旋转角度区间
     * 0-360
     *
     * @param min 最小旋转角度
     * @param max 最大旋转角度
     */
    public void setDegreesInterval(int min, int max) {
        degreesMin = min;
        degreesMax = max;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            bitmap = BitmapHelper.readBitMap(WlVideoAppInfo.sAppCtx, R.drawable.home_icon_dianzan_selected, DEFAULT_SCALE_SIZE);
            matrix = new Matrix();
            list = new ArrayList<>();
        } catch (Exception e) {
        }
    }

    /**
     * viewGroup销毁时释放资源
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            if (bitmap != null) {
                bitmap.recycle();
            }
            bitmap = null;
            matrix = null;
            list = null;
        } catch (Exception e) {
        }
    }

    private class GestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (!enableDoubleClick) {
                if (mTouchListener != null) {
                    mTouchListener.onSingleClick();
                }
                return true;
            }
            handler.postDelayed(() -> {
                if (isDoubleClick) {
                    isDoubleClick = false;
                } else {
                    if (mTouchListener != null) {
                        mTouchListener.onSingleClick();
                    }
                }
            }, TIME_OUT_DELAY);
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }

    private class DoubleGestureListener implements GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            if (!enableDoubleClick) {
                return true;
            }
            isDoubleClick = true;
            startSwipe(event);
            if (mTouchListener != null) {
                mTouchListener.onDoubleClick();
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }
    }
}