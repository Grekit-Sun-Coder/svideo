package cn.weli.svideo.module.task.component.widget.floatdrag.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Stack;

import cn.weli.svideo.module.task.component.widget.floatdrag.anchor.Navigator;
import cn.weli.svideo.module.task.component.widget.floatdrag.anchor.NavigatorContent;

/**
 * Implementation of a {@link Navigator} without any decoration or special features.
 */
public class DefaultNavigator extends FrameLayout implements Navigator {

    private final boolean mIsFullscreen;
    private Stack<NavigatorContent> mContentStack;
    private ViewGroup.LayoutParams mContentLayoutParams;

    public DefaultNavigator(@NonNull Context context) {
        this(context, true);
    }

    public DefaultNavigator(@NonNull Context context, boolean isFullscreen) {
        super(context);
        mIsFullscreen = isFullscreen;
        init();
    }

    private void init() {
        int heightMode = mIsFullscreen ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        mContentStack = new Stack<>();
        mContentLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightMode);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightMode));
    }

    @Override
    public void pushContent(@NonNull NavigatorContent content) {
        // Remove the currently visible content (if there is any).
        if (!mContentStack.isEmpty()) {
            removeView(mContentStack.peek().getView());
            mContentStack.peek().onHidden();
        }

        // Push and display the new page.
        mContentStack.push(content);
        showContent(content);
    }

    @Override
    public boolean popContent() {
        if (mContentStack.size() > 1) {
            // Remove the currently visible content.
            removeCurrentContent();

            // Add back the previous content (if there is any).
            if (!mContentStack.isEmpty()) {
                showContent(mContentStack.peek());
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void clearContent() {
        if (mContentStack.isEmpty()) {
            // Nothing to clear.
            return;
        }

        // Pop every content View that we can.
        boolean didPopContent = popContent();
        while (didPopContent) {
            didPopContent = popContent();
        }

        // Clear the root View.
        removeCurrentContent();
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    private void showContent(@NonNull NavigatorContent content) {
        addView(content.getView(), mContentLayoutParams);
        content.onShown(this);
    }

    private void removeCurrentContent() {
        NavigatorContent visibleContent = mContentStack.pop();
        removeView(visibleContent.getView());
        visibleContent.onHidden();
    }
}
