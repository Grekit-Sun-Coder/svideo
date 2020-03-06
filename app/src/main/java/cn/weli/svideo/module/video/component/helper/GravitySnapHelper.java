package cn.weli.svideo.module.video.component.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
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
public class GravitySnapHelper extends LinearSnapHelper {

    @NonNull
    private final GravityDelegate delegate;

    public GravitySnapHelper(int gravity) {
        this(gravity, false, null);
    }

    public GravitySnapHelper(int gravity, boolean enableSnapLastItem) {
        this(gravity, enableSnapLastItem, null);
    }

    public GravitySnapHelper(int gravity, boolean enableSnapLastItem,
                             @Nullable SnapListener snapListener) {
        delegate = new GravityDelegate(gravity, enableSnapLastItem, snapListener);
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        delegate.attachToRecyclerView(recyclerView);
        super.attachToRecyclerView(recyclerView);
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        return delegate.calculateDistanceToFinalSnap(layoutManager, targetView);
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        return delegate.findSnapView(layoutManager);
    }

    public void enableLastItemSnap(boolean snap) {
        delegate.enableLastItemSnap(snap);
    }

    public interface SnapListener {
        void onSnap(int position);
    }

}
