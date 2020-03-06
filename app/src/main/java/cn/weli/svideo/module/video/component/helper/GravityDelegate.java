package cn.weli.svideo.module.video.component.helper;


import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import java.util.Locale;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class GravityDelegate {

    private OrientationHelper verticalHelper;
    private OrientationHelper horizontalHelper;
    private int gravity;
    private boolean isRtlHorizontal;
    private boolean snapLastItem;
    private GravitySnapHelper.SnapListener listener;
    private boolean snapping;
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                snapping = false;
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE && snapping && listener != null) {
                int position = getSnappedPosition(recyclerView);
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSnap(position);
                }
                snapping = false;
            }
        }
    };

    public GravityDelegate(int gravity, boolean enableSnapLast,
                           @Nullable GravitySnapHelper.SnapListener listener) {
        if (gravity != Gravity.START && gravity != Gravity.END
                && gravity != Gravity.BOTTOM && gravity != Gravity.TOP) {
            throw new IllegalArgumentException("Invalid gravity value. Use START " +
                    "| END | BOTTOM | TOP constants");
        }
        this.snapLastItem = enableSnapLast;
        this.gravity = gravity;
        this.listener = listener;
    }

    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
        if (recyclerView != null) {
            recyclerView.setOnFlingListener(null);
            if ((gravity == Gravity.START || gravity == Gravity.END)) {
                isRtlHorizontal = isRtl();
            }
            if (listener != null) {
                recyclerView.addOnScrollListener(mScrollListener);
            }
        }
    }

    private boolean isRtl() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return false;
        }
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault())
                == View.LAYOUT_DIRECTION_RTL;
    }

    @NonNull
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
                                              @NonNull View targetView) {
        int[] out = new int[2];

        if (layoutManager.canScrollHorizontally()) {
            if (gravity == Gravity.START) {
                out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager), false);
            } else { // END
                out[0] = distanceToEnd(targetView, getHorizontalHelper(layoutManager), false);
            }
        } else {
            out[0] = 0;
        }

        if (layoutManager.canScrollVertically()) {
            if (gravity == Gravity.TOP) {
                out[1] = distanceToStart(targetView, getVerticalHelper(layoutManager), false);
            } else { // BOTTOM
                out[1] = distanceToEnd(targetView, getVerticalHelper(layoutManager), false);
            }
        } else {
            out[1] = 0;
        }

        return out;
    }

    @Nullable
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        View snapView = null;
        if (layoutManager instanceof LinearLayoutManager) {
            switch (gravity) {
                case Gravity.START:
                    snapView = findStartView(layoutManager, getHorizontalHelper(layoutManager));
                    break;
                case Gravity.END:
                    snapView = findEndView(layoutManager, getHorizontalHelper(layoutManager));
                    break;
                case Gravity.TOP:
                    snapView = findStartView(layoutManager, getVerticalHelper(layoutManager));
                    break;
                case Gravity.BOTTOM:
                    snapView = findEndView(layoutManager, getVerticalHelper(layoutManager));
                    break;
            }
        }
        snapping = snapView != null;
        return snapView;
    }

    public void enableLastItemSnap(boolean snap) {
        snapLastItem = snap;
    }

    private int distanceToStart(View targetView, @NonNull OrientationHelper helper, boolean fromEnd) {
        if (isRtlHorizontal && !fromEnd) {
            return distanceToEnd(targetView, helper, true);
        }

        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
    }

    private int distanceToEnd(View targetView, @NonNull OrientationHelper helper, boolean fromStart) {
        if (isRtlHorizontal && !fromStart) {
            return distanceToStart(targetView, helper, true);
        }

        return helper.getDecoratedEnd(targetView) - helper.getEndAfterPadding();
    }

    @Nullable
    private View findStartView(RecyclerView.LayoutManager layoutManager,
                               @NonNull OrientationHelper helper) {

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            boolean reverseLayout = linearLayoutManager.getReverseLayout();
            int firstChild = reverseLayout ? linearLayoutManager.findLastVisibleItemPosition()
                    : linearLayoutManager.findFirstVisibleItemPosition();
            int offset = 1;

            if (layoutManager instanceof GridLayoutManager) {
                offset += ((GridLayoutManager) layoutManager).getSpanCount() - 1;
            }

            if (firstChild == RecyclerView.NO_POSITION) {
                return null;
            }

            View child = layoutManager.findViewByPosition(firstChild);

            float visibleWidth;

            if (isRtlHorizontal) {
                visibleWidth = (float) (helper.getTotalSpace() - helper.getDecoratedStart(child))
                        / helper.getDecoratedMeasurement(child);
            } else {
                visibleWidth = (float) helper.getDecoratedEnd(child)
                        / helper.getDecoratedMeasurement(child);
            }

            boolean endOfList;
            if (!reverseLayout) {
                endOfList = ((LinearLayoutManager) layoutManager)
                        .findLastCompletelyVisibleItemPosition()
                        == layoutManager.getItemCount() - 1;
            } else {
                endOfList = ((LinearLayoutManager) layoutManager)
                        .findFirstCompletelyVisibleItemPosition()
                        == 0;
            }

            if (visibleWidth > 0.5f && !endOfList) {
                return child;
            } else if (snapLastItem && endOfList) {
                return child;
            } else if (endOfList) {
                return null;
            } else {
                return reverseLayout ? layoutManager.findViewByPosition(firstChild - offset)
                        : layoutManager.findViewByPosition(firstChild + offset);
            }
        }

        return null;
    }

    @Nullable
    private View findEndView(RecyclerView.LayoutManager layoutManager,
                             @NonNull OrientationHelper helper) {

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            boolean reverseLayout = linearLayoutManager.getReverseLayout();
            int lastChild = reverseLayout ? linearLayoutManager.findFirstVisibleItemPosition()
                    : linearLayoutManager.findLastVisibleItemPosition();
            int offset = 1;

            if (layoutManager instanceof GridLayoutManager) {
                offset += ((GridLayoutManager) layoutManager).getSpanCount() - 1;
            }

            if (lastChild == RecyclerView.NO_POSITION) {
                return null;
            }

            View child = layoutManager.findViewByPosition(lastChild);

            float visibleWidth;

            if (isRtlHorizontal) {
                visibleWidth = (float) helper.getDecoratedEnd(child)
                        / helper.getDecoratedMeasurement(child);
            } else {
                visibleWidth = (float) (helper.getTotalSpace() - helper.getDecoratedStart(child))
                        / helper.getDecoratedMeasurement(child);
            }

            boolean startOfList;
            if (!reverseLayout) {
                startOfList = ((LinearLayoutManager) layoutManager)
                        .findFirstCompletelyVisibleItemPosition() == 0;
            } else {
                startOfList = ((LinearLayoutManager) layoutManager)
                        .findLastCompletelyVisibleItemPosition()
                        == layoutManager.getItemCount() - 1;
            }

            if (visibleWidth > 0.5f && !startOfList) {
                return child;
            } else if (snapLastItem && startOfList) {
                return child;
            } else if (startOfList) {
                return null;
            } else {
                return reverseLayout ? layoutManager.findViewByPosition(lastChild + offset)
                        : layoutManager.findViewByPosition(lastChild - offset);
            }
        }
        return null;
    }

    private int getSnappedPosition(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            if (gravity == Gravity.START || gravity == Gravity.TOP) {
                return ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            } else if (gravity == Gravity.END || gravity == Gravity.BOTTOM) {
                return ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            }
        }

        return RecyclerView.NO_POSITION;
    }

    private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager layoutManager) {
        if (verticalHelper == null) {
            verticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return verticalHelper;
    }

    private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager layoutManager) {
        if (horizontalHelper == null) {
            horizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return horizontalHelper;
    }
}
