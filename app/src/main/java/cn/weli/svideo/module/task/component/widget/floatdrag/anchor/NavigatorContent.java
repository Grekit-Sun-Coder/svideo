package cn.weli.svideo.module.task.component.widget.floatdrag.anchor;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Content to be displayed by a {@link Navigator}.
 */
public interface NavigatorContent {

    /**
     * Returns the visual display of this content.
     *
     * @return the visual representation of this content
     */
    @NonNull
    View getView();

    /**
     * Called when this content is displayed to the user.
     *
     * @param navigator the {@link Navigator} that is displaying this content.
     */
    void onShown(@NonNull Navigator navigator);

    /**
     * Called when this content is no longer displayed to the user.
     *
     * Implementation Note: a {@code NavigatorContent} can be brought back due to user navigation so
     * this call must not release resources that are required to show this content again.
     */
    void onHidden();

}
