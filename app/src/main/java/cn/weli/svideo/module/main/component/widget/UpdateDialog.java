package cn.weli.svideo.module.main.component.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;

/**
 * 版本更新Dialog
 *
 * @author ZhengXiang Sun
 * @version [1.0.6]
 * @date 2020-02-20
 * @see [class/method]
 * @since [1.0.6]
 */
public class UpdateDialog extends Dialog {

    @BindView(R.id.update_close)
    ImageView mCloseImg;
    @BindView(R.id.ver_code)
    TextView mVerCodeTxt;
    @BindView(R.id.update_content)
    TextView mUpdateContentTxt;
    @BindView(R.id.update)
    TextView mUpdateTxt;
    @BindView(R.id.title)
    TextView mTitlext;

    public UpdateDialog(Context context, String ver, String content) {
        super(context);
        init();
        mUpdateContentTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
        mVerCodeTxt.setText("V" + ver);       //设置版本号
        mUpdateContentTxt.setText(content);     //设置更新内容
    }

    /**
     * 初始化
     */
    private void init() {
        setDialogTheme();
        setContentView(R.layout.layout_update_dialog);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    /**
     * 点击监听
     */
    @OnClick({R.id.update_close, R.id.update})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                if (mOnClickCallback != null) {
                    mOnClickCallback.onClickUpdate();
                }
                break;
            case R.id.update_close:
                if (mOnClickCallback != null) {
                    mOnClickCallback.onClickClose();
                }
                break;
            default:
                break;
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
            dialogWindow.setAttributes(lp);
        }
    }

    /**
     * set dialog theme(设置对话框主题)
     */
    private void setDialogTheme() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private OnClickCallback mOnClickCallback;

    public interface OnClickCallback {
        void onClickUpdate();

        void onClickClose();
    }

    public void setOnClickCallback(OnClickCallback mOnClickCallback) {
        this.mOnClickCallback = mOnClickCallback;
    }
}
