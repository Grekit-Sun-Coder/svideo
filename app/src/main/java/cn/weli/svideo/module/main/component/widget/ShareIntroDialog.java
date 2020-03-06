package cn.weli.svideo.module.main.component.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import cn.weli.svideo.R;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see [class/method]
 * @since [1.0.0]
 */
public class ShareIntroDialog extends Dialog implements View.OnClickListener {

    private onShareActionListener mActionListener;

    public void setActionListener(onShareActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface onShareActionListener {

        void onShareActionClick();
    }

    public ShareIntroDialog(@NonNull Context context) {
        super(context);
        setDialogTheme();
        setContentView(R.layout.dialog_share_intro);

        ImageView closeImg = findViewById(R.id.close_img);
        TextView actionTxt = findViewById(R.id.action_txt);
        closeImg.setOnClickListener(this);
        actionTxt.setOnClickListener(this);

        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.close_img) {
            dismiss();
        } else if (view.getId() == R.id.action_txt) {
            if (mActionListener != null) {
                mActionListener.onShareActionClick();
            }
            dismiss();
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
