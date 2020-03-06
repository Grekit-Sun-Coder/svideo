package cn.weli.svideo.baselib.component.widget.toast;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import cn.etouch.logger.Logger;

/**
 * Toast 的安全Context
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @since [1.0.0]
 */
public class SafeToastContext extends ContextWrapper {
    private @NonNull
    Toast toast;

    SafeToastContext(@NonNull Context base, @NonNull Toast toast) {
        super(base);
        this.toast = toast;
    }

    @Override
    public Context getApplicationContext() {
        return new ApplicationContextWrapper(getBaseContext().getApplicationContext());
    }

    private final class ApplicationContextWrapper extends ContextWrapper {

        private ApplicationContextWrapper(@NonNull Context base) {
            super(base);
        }


        @Override
        public Object getSystemService(@NonNull String name) {
            if (Context.WINDOW_SERVICE.equals(name)) {
                // noinspection ConstantConditions
                return new WindowManagerWrapper((WindowManager) getBaseContext().getSystemService(name));
            }
            return super.getSystemService(name);
        }
    }
    private final class WindowManagerWrapper implements WindowManager {

        private static final String TAG = "WindowManagerWrapper";
        private final @NonNull
        WindowManager base;


        private WindowManagerWrapper(@NonNull WindowManager base) {
            this.base = base;
        }


        @Override
        public Display getDefaultDisplay() {
            return base.getDefaultDisplay();
        }


        @Override
        public void removeViewImmediate(View view) {
            base.removeViewImmediate(view);
        }


        @Override
        public void addView(View view, ViewGroup.LayoutParams params) {
            try {
                base.addView(view, params);
            } catch (BadTokenException e) {
                Logger.e(e.getMessage());
            } catch (Throwable throwable) {
                Logger.e(throwable.getMessage());
            }
        }


        @Override
        public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
            base.updateViewLayout(view, params);
        }


        @Override
        public void removeView(View view) {
            base.removeView(view);
        }
    }
}
