package cn.weli.svideo.module.task.component.widget.floatdrag.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.weli.svideo.module.task.component.widget.floatdrag.anchor.InViewGroupDragger;
import cn.weli.svideo.module.task.component.widget.floatdrag.view.FloatView;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class FloatViewLayout extends FrameLayout {
    private final String SPFILE = "floatlocation";
    private final String PREFS_KEY_ANCHOR_SIDE = "anchor_side";
    private final String PREFS_KEY_ANCHOR_Y = "anchor_y";
    private SharedPreferences mPrefs;
    private FloatViewHoledr mFloatViewHolder;
    private InViewGroupDragger mDragger;


    public FloatViewLayout(Context context) {
        this(context, null);
    }

    public FloatViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPrefs = getContext().getSharedPreferences(SPFILE, Context.MODE_PRIVATE);
        mDragger = new InViewGroupDragger(this, ViewConfiguration.get(getContext()).getScaledTouchSlop());
        mDragger.setDebugMode(false);
        mFloatViewHolder = new FloatViewHoledr(getContext(), null, mDragger, loadSavedAnchorState());
    }

    public void setFloatViewClickListener(FloatViewHoledr.OnFloatViewClickListener listener) {
        if (mFloatViewHolder != null) {
            mFloatViewHolder.setViewClickListener(listener);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addView(mFloatViewHolder, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDetachedFromWindow() {
        saveAnchorState();
        removeView(mFloatViewHolder);
        mFloatViewHolder = null;
        mDragger.deactivate();
        super.onDetachedFromWindow();
    }

    private void saveAnchorState() {
        PointF anchorState = mFloatViewHolder.getAnchorState();
        mPrefs.edit()
                .putFloat(PREFS_KEY_ANCHOR_SIDE, anchorState.x)
                .putFloat(PREFS_KEY_ANCHOR_Y, anchorState.y)
                .apply();
    }

    private PointF loadSavedAnchorState() {
        return new PointF(
                mPrefs.getFloat(PREFS_KEY_ANCHOR_SIDE, 2),
                mPrefs.getFloat(PREFS_KEY_ANCHOR_Y, 1f)
        );
    }

    public void setmFloatView(FloatView floatView) {
        if (mFloatViewHolder != null)
            mFloatViewHolder.setFloatView(floatView);
    }
}
