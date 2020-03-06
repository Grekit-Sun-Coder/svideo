package cn.weli.svideo.module.main.component.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import cn.weli.svideo.R;
import cn.weli.svideo.module.main.model.bean.ShareInfoBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see [class/method]
 * @since [1.0.0]
 */
public class ShareSelectedDialog extends Dialog implements View.OnClickListener {

    private OnSharePlatformSelectListener mPlatformSelectListener;

    public interface OnSharePlatformSelectListener {

        void onPlatformSelected(String platform);
    }
    public void setPlatformSelectListener(OnSharePlatformSelectListener platformSelectListener) {
        mPlatformSelectListener = platformSelectListener;
    }

    public ShareSelectedDialog(@NonNull Context context) {
        super(context);
        setDialogTheme();
        setContentView(R.layout.dialog_share_select);
        LinearLayout wxLayout = findViewById(R.id.share_wx_layout);
        LinearLayout qqLayout = findViewById(R.id.share_qq_layout);
        wxLayout.setOnClickListener(this);
        qqLayout.setOnClickListener(this);
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogBottomWindowAnim);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.share_wx_layout) {
            if (mPlatformSelectListener != null) {
                mPlatformSelectListener.onPlatformSelected(ShareInfoBean.PLATFORM_WEIXIN);
            }
        } else if (view.getId() == R.id.share_qq_layout) {
            if (mPlatformSelectListener != null) {
                mPlatformSelectListener.onPlatformSelected(ShareInfoBean.PLATFORM_QQ);
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
            lp.width = display.getWidth();
            lp.gravity = Gravity.BOTTOM;
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
