package cn.weli.svideo.module.video.component.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.weli.svideo.R;

/**
 * 金币弹窗
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-24
 * @see cn.weli.svideo.module.video.ui.VideoPlayFragment
 * @since [1.0.0]
 */
public class GuideCoinDialog extends Dialog {

    private OnGuideCoinListener mCoinListener;

    public GuideCoinDialog(@NonNull Context context) {
        super(context);
        setDialogTheme();
        setContentView(R.layout.dialog_coin_guide_view);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        ImageView imageView = findViewById(R.id.coin_img);
        View closeView = findViewById(R.id.close_img);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mCoinListener != null) {
                    mCoinListener.onGuideCoinClick();
                }
            }
        });
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mCoinListener != null) {
                    mCoinListener.onGuideCancel();
                }
            }
        });
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    public void setCoinListener(OnGuideCoinListener coinListener) {
        mCoinListener = coinListener;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
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

    public interface OnGuideCoinListener {

        void onGuideCoinClick();

        void onGuideCancel();
    }
}
