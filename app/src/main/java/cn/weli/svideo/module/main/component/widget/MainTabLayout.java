package cn.weli.svideo.module.main.component.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.common.widget.GradientColorTextView;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.task.component.event.TaskCoinStatusEvent;

/**
 * 主页底部导航布局
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see MainActivity
 * @since [1.0.0]
 */
public class MainTabLayout extends LinearLayout {

    /**
     * Tab 0-首页，1-任务，2-我的
     */
    public static final int TAB_VIDEO = 0;
    public static final int TAB_MINE = 1;
    public static final int TAB_TASK = 2;

    @BindView(R.id.tab_video_txt)
    GradientColorTextView mTabVideoTxt;
    @BindView(R.id.tab_task_txt)
    GradientColorTextView mTabTaskTxt;
    @BindView(R.id.tab_mine_txt)
    GradientColorTextView mTabMineTxt;
    @BindView(R.id.tab_video_layout)
    RelativeLayout mTabVideoLayout;
    @BindView(R.id.tab_task_layout)
    LinearLayout mTabTaskLayout;
    @BindView(R.id.tab_mine_layout)
    RelativeLayout mTabMineLayout;
    @BindView(R.id.video_tab_img)
    ImageView mVideoTabImg;
    @BindView(R.id.mine_tab_img)
    ImageView mMineTabImg;
    @BindView(R.id.tab_coin_img)
    ImageView mTaskCoinTabImg;
    @BindView(R.id.tab_open_img)
    ImageView mTaskOpenCoinImg;

    private AnimationDrawable mAnimationDrawable;

    private Context mContext;

    private int mCurrentTab = -1;

    private OnMainTabChangeListener mChangeListener;

    private WeakHandler mWeakHandler;

    private Animation mAnimation;

    public interface OnMainTabChangeListener {

        void onMainTabChanged(int tab);

        void startToLogin();
    }

    public MainTabLayout(Context context) {
        this(context, null);
    }

    public MainTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_main_tab, this, true);
        ButterKnife.bind(this, view);
        mWeakHandler = new WeakHandler();
        mAnimationDrawable = (AnimationDrawable) mTaskCoinTabImg.getDrawable();
        if (mAnimationDrawable != null) {
            mAnimationDrawable.setOneShot(true);
        }
        mAnimation = AnimationUtils.loadAnimation(mContext, R.anim.open_coin_anim);
        mAnimation.setInterpolator(new OvershootInterpolator());
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mTaskOpenCoinImg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void setOpenCoinStatus(int status) {
        if (status == TaskCoinStatusEvent.STATUS_CAN_OPEN) {
            if (mTaskOpenCoinImg.getVisibility() == View.VISIBLE) {
                return;
            }
            mTaskOpenCoinImg.startAnimation(mAnimation);
        } else {
            if (mTaskOpenCoinImg.getVisibility() == View.INVISIBLE) {
                return;
            }
            mTaskOpenCoinImg.setVisibility(View.INVISIBLE);
        }
    }

    public void startOpenCoinAnim() {
        mTaskOpenCoinImg.startAnimation(mAnimation);
    }

    public void hideOpenCoin() {
        mTaskOpenCoinImg.setVisibility(View.INVISIBLE);
    }

    public void setCurrentTab(int tabPosition) {
        switch (tabPosition) {
            case TAB_VIDEO:
                mTabVideoLayout.performClick();
                break;
            case TAB_MINE:
                mTabMineLayout.performClick();
                break;
            case TAB_TASK:
                mTabTaskLayout.performClick();
                break;
            default:
                break;
        }
    }

    public void setChangeListener(OnMainTabChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    @OnClick({R.id.tab_video_layout, R.id.tab_task_layout, R.id.tab_mine_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tab_video_layout:
                if (mCurrentTab != TAB_VIDEO) {
                    mCurrentTab = TAB_VIDEO;
                    changeTabStatus(TAB_VIDEO);
                    if (mChangeListener != null) {
                        mChangeListener.onMainTabChanged(TAB_VIDEO);
                    }
                }
                break;
            case R.id.tab_task_layout:
                if (mCurrentTab != TAB_TASK) {
                    if (!WlVideoAppInfo.getInstance().hasLogin()) {
                        if (mChangeListener != null) {
                            mChangeListener.startToLogin();
                        }
                        return;
                    }
                    mCurrentTab = TAB_TASK;
                    changeTabStatus(TAB_TASK);
                    if (mChangeListener != null) {
                        mChangeListener.onMainTabChanged(TAB_TASK);
                    }
                    if (mAnimationDrawable != null && mWeakHandler != null && !mAnimationDrawable.isRunning()) {
                        mAnimationDrawable.start();
                        mWeakHandler.removeCallbacksAndMessages(null);
                        mWeakHandler.postDelayed(() -> {
                           if (mAnimationDrawable != null) {
                               mAnimationDrawable.stop();
                           }
                        }, 1500);
                    }
                }
                break;
            case R.id.tab_mine_layout:
                if (mCurrentTab != TAB_MINE) {
                    if (!WlVideoAppInfo.getInstance().hasLogin()) {
                        if (mChangeListener != null) {
                            mChangeListener.startToLogin();
                        }
                        return;
                    }
                    mCurrentTab = TAB_MINE;
                    changeTabStatus(TAB_MINE);
                    if (mChangeListener != null) {
                        mChangeListener.onMainTabChanged(TAB_MINE);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 修改tab状态
     *
     * @param tabPos tab位置
     */
    private void changeTabStatus(int tabPos) {
        switch (tabPos) {
            case TAB_VIDEO:
                mTabVideoTxt.setSelected(true);
                mTabMineTxt.setSelected(false);
                mTabTaskTxt.setSelected(false);
                mVideoTabImg.setImageResource(R.drawable.tab_icon_home_selected);
                mMineTabImg.setImageResource(R.drawable.tab_icon_my_normal);
                break;
            case TAB_MINE:
                mTabVideoTxt.setSelected(false);
                mTabMineTxt.setSelected(true);
                mTabTaskTxt.setSelected(false);
                mVideoTabImg.setImageResource(R.drawable.tab_icon_home_normal);
                mMineTabImg.setImageResource(R.drawable.tab_icon_my_selected);
                break;
            case TAB_TASK:
                mTabVideoTxt.setSelected(false);
                mTabMineTxt.setSelected(false);
                mTabTaskTxt.setSelected(true);
                mVideoTabImg.setImageResource(R.drawable.tab_icon_home_normal);
                mMineTabImg.setImageResource(R.drawable.tab_icon_my_normal);
                break;
            default:
                break;
        }
    }
}
