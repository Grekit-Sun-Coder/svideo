package cn.weli.svideo.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 通用弱弹窗
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-12
 * @see [class/method]
 * @since [1.0.0]
 */
public class CommonWeakDialog extends Dialog {

    @BindView(R.id.weak_txt)
    TextView mWeakTxt;

    public CommonWeakDialog(@NonNull Context context) {
        super(context);
        setDialogTheme();
        setContentView(R.layout.dialog_common_weak);
        ButterKnife.bind(this);
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    public void showWeakTxt(String title) {
        if (!StringUtil.isNull(title)) {
            mWeakTxt.setText(title);
            show();
        }
    }

    @OnClick(R.id.weak_layout)
    public void onViewClicked() {
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
            lp.width = (int) (display.getWidth() * 0.64f);
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
            window.setDimAmount(0);
        }
    }
}
