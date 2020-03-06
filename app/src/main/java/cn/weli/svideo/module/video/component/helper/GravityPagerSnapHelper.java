package cn.weli.svideo.module.video.component.helper;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class GravityPagerSnapHelper extends PagerSnapHelper {

    @NonNull
    private final GravityDelegate delegate;

    public GravityPagerSnapHelper(int gravity) {
        this(gravity, false, null);
    }

    public GravityPagerSnapHelper(int gravity, boolean enableSnapLastItem) {
        this(gravity, enableSnapLastItem, null);
    }

    public GravityPagerSnapHelper(int gravity, boolean enableSnapLastItem,
                                  @Nullable GravitySnapHelper.SnapListener snapListener) {
        delegate = new GravityDelegate(gravity, enableSnapLastItem, snapListener);
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        if (recyclerView != null
                && (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)
                || recyclerView.getLayoutManager() instanceof GridLayoutManager)) {
            throw new IllegalStateException("GravityPagerSnapHelper needs a RecyclerView" +
                    " with a LinearLayoutManager");
        }
        delegate.attachToRecyclerView(recyclerView);
        super.attachToRecyclerView(recyclerView);
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        return delegate.calculateDistanceToFinalSnap(layoutManager, targetView);
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        return delegate.findSnapView(layoutManager);
    }

    public void enableLastItemSnap(boolean snap) {
        delegate.enableLastItemSnap(snap);
    }
}
