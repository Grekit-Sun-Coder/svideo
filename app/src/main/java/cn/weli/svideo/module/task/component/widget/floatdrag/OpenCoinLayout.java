package cn.weli.svideo.module.task.component.widget.floatdrag;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.component.widget.toast.WeToast;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.module.task.component.event.TaskCoinStatusEvent;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class OpenCoinLayout extends RelativeLayout {

    private static final int MSG_COIN_ANIM = 0x100;
    private static final long OPEN_ANIM_DELAY = 3000;

    @BindView(R.id.coin_open_txt)
    TextView mCoinOpenTxt;
    @BindView(R.id.coin_open_layout)
    RelativeLayout mCoinOpenLayout;

    private Context mContext;

    private int mCurrentStatus;

    private RotateAnimation mRotateAnimation;

    private RotateAnimation mRecoverRotateAnimation;

    private OnTaskOpenCoinListener mOpenCoinListener;

    private WeakHandler mWeakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == MSG_COIN_ANIM) {
                if (mCurrentStatus == TaskCoinStatusEvent.STATUS_CAN_OPEN && mRotateAnimation != null) {
                    mCoinOpenLayout.startAnimation(mRotateAnimation);
                }
            }
            return false;
        }
    });

    public interface OnTaskOpenCoinListener {

        void onTaskCoinOpen();
    }

    public OpenCoinLayout(Context context) {
        this(context, null);
    }

    public OpenCoinLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OpenCoinLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void setOpenCoinListener(OnTaskOpenCoinListener openCoinListener) {
        mOpenCoinListener = openCoinListener;
    }

    /**
     * 设置开宝箱状态
     *
     * @param status 状态
     * @param msg    信息
     */
    public void setCoinStatus(int status, String msg) {
        mCurrentStatus = status;
        mCoinOpenTxt.setText(msg);
        if (mCurrentStatus == TaskCoinStatusEvent.STATUS_CAN_OPEN && mRotateAnimation != null) {
            if (mWeakHandler != null) {
                mWeakHandler.sendEmptyMessageDelayed(MSG_COIN_ANIM, OPEN_ANIM_DELAY);
            }
        } else {
            cancelCoinOpenAnim();
        }
    }

    /**
     * 开始动画
     */
    public void continueCoinOpenAnim() {
        if (mCurrentStatus == TaskCoinStatusEvent.STATUS_CAN_OPEN && mRotateAnimation != null) {
            if (mWeakHandler != null) {
                mWeakHandler.sendEmptyMessageDelayed(MSG_COIN_ANIM, OPEN_ANIM_DELAY);
            }
        } else {
            cancelCoinOpenAnim();
        }
    }

    /**
     * 取消宝箱动画
     */
    private void cancelCoinOpenAnim() {
        if (mRotateAnimation != null) {
            mRotateAnimation.cancel();
        }
        if (mRecoverRotateAnimation != null) {
            mRecoverRotateAnimation.cancel();
        }
        if (mWeakHandler != null) {
            mWeakHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 处理开宝箱
     */
    public void handleOpenClick() {
        if (mCurrentStatus == TaskCoinStatusEvent.STATUS_CAN_OPEN) {
            if (mOpenCoinListener != null) {
                mOpenCoinListener.onTaskCoinOpen();
            }
            cancelCoinOpenAnim();
        } else {
            WeToast.getInstance().showToast(WlVideoAppInfo.sAppCtx, R.string.task_open_coin_time_title);
        }
    }

    /**
     * 销毁
     */
    public void onDestory() {
        cancelCoinOpenAnim();
    }

    /**
     * 初始化
     */
    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_open_coin, this, true);
        ButterKnife.bind(this, view);
        mRotateAnimation = new RotateAnimation(-15, 15, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setDuration(150);
        mRotateAnimation.setFillAfter(false);
        mRotateAnimation.setRepeatCount(4);
        mRotateAnimation.setRepeatMode(Animation.REVERSE);
        mRotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mRecoverRotateAnimation != null) {
                    mCoinOpenLayout.startAnimation(mRecoverRotateAnimation);
                }
                if (mWeakHandler != null) {
                    mWeakHandler.sendEmptyMessageDelayed(MSG_COIN_ANIM, OPEN_ANIM_DELAY);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mRecoverRotateAnimation = new RotateAnimation(15, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRecoverRotateAnimation.setDuration(150);
    }
}
