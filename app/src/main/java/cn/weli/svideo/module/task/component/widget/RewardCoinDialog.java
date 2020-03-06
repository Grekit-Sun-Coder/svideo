package cn.weli.svideo.module.task.component.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.weli.svideo.R;

/**
 * 登录成功奖励金币
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-24
 * @see cn.weli.svideo.module.task.ui.TaskPageFragment
 * @since [1.0.0]
 */
public class RewardCoinDialog extends Dialog {

    private TextView mCoinTxt;

    private TextView mCoinTitleTxt;

    private OnClickMoreListener mMoreListener;

    public interface OnClickMoreListener {

        void onClickMore();
    }

    public void setMoreListener(OnClickMoreListener moreListener) {
        mMoreListener = moreListener;
    }

    public RewardCoinDialog(@NonNull Context context) {
        super(context);
        setDialogTheme();
        setContentView(R.layout.dialog_coin_reward);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mCoinTxt = findViewById(R.id.login_coin_txt);
        mCoinTitleTxt = findViewById(R.id.coin_title_txt);
        TextView textView = findViewById(R.id.close_txt);
        textView.setOnClickListener(view -> {
            dismiss();
            if (mMoreListener != null) {
                mMoreListener.onClickMore();
            }
        });
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    public void setCoinTxt(long coin) {
        mCoinTxt.setText(String.valueOf(coin));
    }

    public void setCoinTitleTxt(String title) {
        mCoinTitleTxt.setText(title);
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
