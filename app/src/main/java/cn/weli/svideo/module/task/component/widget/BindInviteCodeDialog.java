package cn.weli.svideo.module.task.component.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-27
 * @see [class/method]
 * @since [1.0.0]
 */
public class BindInviteCodeDialog extends Dialog implements View.OnClickListener {

    private Context mContext;

    private TextView mContentTxt;

    private OnBindInviteActionListener mActionListener;

    public interface OnBindInviteActionListener {

        void onBindInviteCodeNow();
    }

    public void setActionListener(OnBindInviteActionListener actionListener) {
        mActionListener = actionListener;
    }

    public BindInviteCodeDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        setDialogTheme();
        setContentView(R.layout.dialog_bind_invite_code);
        mContentTxt = findViewById(R.id.normal_content);
        TextView okTxt = findViewById(R.id.normal_button_pos);
        TextView cancelTxt = findViewById(R.id.normal_button_neg);
        okTxt.setOnClickListener(this);
        cancelTxt.setOnClickListener(this);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    public void setContentTxt(String code) {
        try {
            String leftStr = mContext.getString(R.string.task_has_title);
            String rightStr = mContext.getString(R.string.task_auto_title);
            SpannableString spannableString = new SpannableString(leftStr + code + rightStr);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.color_333333)),
                    leftStr.length(), spannableString.length() - rightStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                    leftStr.length(), spannableString.length() - rightStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mContentTxt.setText(spannableString);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.normal_button_pos) {
            if (mActionListener != null) {
                mActionListener.onBindInviteCodeNow();
            }
        }
        dismiss();
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
