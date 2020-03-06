package cn.weli.svideo.baselib.component.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import cn.weli.svideo.baselib.R;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-23
 * @see [class/method]
 * @since [1.0.0]
 */
public class BaseDialog extends Dialog {

    public Context mContext;

    public BaseDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        setDialogTheme();
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager windowManager = dialogWindow.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (display.getWidth() * 0.83f);
            dialogWindow.setAttributes(lp);
        }
    }

    /**
     * set dialog theme(设置对话框主题)
     */
    private void setDialogTheme() {
        Window window = getWindow();
        if (window != null) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }
}
