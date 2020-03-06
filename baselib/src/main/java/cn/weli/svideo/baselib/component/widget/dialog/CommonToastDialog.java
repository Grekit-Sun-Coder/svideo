package cn.weli.svideo.baselib.component.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.weli.svideo.baselib.R;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 通用提示Dialog
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see [class/method]
 * @since [1.0.0]
 */
public class CommonToastDialog extends Dialog {

    private Context mContext;

    private TextView mContentTxt;

    private TextView mOkTxt;

    private TextView mTitleTxt;

    private DisplayMetrics mDisplayMetrics;

    private float mScale;

    private OkCallback mCallback;

    public CommonToastDialog(@NonNull Builder builder) {
        super(builder.context);
        setDialogTheme();
        initDialogView(builder);
    }

    private void initDialogView(Builder builder) {
        setCanceledOnTouchOutside(false);
        mContext = builder.context;
        mCallback = builder.okCallback;
        mScale = 0.89f;
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        setContentView(R.layout.layout_common_toast);
        mContentTxt = findViewById(R.id.common_toast_txt);
        mContentTxt.setMovementMethod(ScrollingMovementMethod.getInstance());
        mOkTxt = findViewById(R.id.common_ok_txt);
        mTitleTxt = findViewById(R.id.normal_title);
        mOkTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    mCallback.onOkClick(CommonToastDialog.this);
                } else {
                    dismiss();
                }
            }
        });
        if (!StringUtil.isNull(builder.contentStr)) {
            mContentTxt.setText(builder.contentStr);
        }
        if (!StringUtil.isNull(builder.okStr)) {
            mOkTxt.setText(builder.okStr);
        }
        if (!StringUtil.isNull(builder.titleStr)) {
            mTitleTxt.setText(builder.titleStr);
        }
        setCancelable(builder.canCancel);
        setCanceledOnTouchOutside(builder.canCancel);
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    public void setContent(int resId) {
        if (!StringUtil.isNull(mContext.getString(resId))) {
            mContentTxt.setText(mContext.getString(resId));
        }
    }

    public void setContent(String content) {
        if (!StringUtil.isNull(content)) {
            mContentTxt.setText(content);
        }
    }


    /**
     * 展示dialog
     *
     * @param activity 当前宿主
     */
    public void showDialog(Activity activity) {
        if (activity != null && !activity.isFinishing()) {
            show();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (mDisplayMetrics.widthPixels * mScale);
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

    public static class Builder {
        protected Context context;
        private String contentStr;
        private String okStr;
        private String titleStr;
        private boolean canCancel;
        private OkCallback okCallback;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder content(@StringRes int res) {
            this.contentStr = context.getString(res);
            return this;
        }

        public Builder content(@NonNull String text) {
            this.contentStr = text;
            return this;
        }

        public Builder okStr(@StringRes int res) {
            this.okStr = context.getString(res);
            return this;
        }

        public Builder okStr(@NonNull String text) {
            this.okStr = text;
            return this;
        }

        public Builder titleStr(@StringRes int res) {
            this.titleStr = context.getString(res);
            return this;
        }

        public Builder titleStr(@NonNull String text) {
            this.titleStr = text;
            return this;
        }

        public Builder canCancel(boolean canCancel) {
            this.canCancel = canCancel;
            return this;
        }

        public Builder callback(OkCallback callback) {
            this.okCallback = callback;
            return this;
        }

        public CommonToastDialog build() {
            return new CommonToastDialog(this);
        }
    }

    public static abstract class OkCallback {
        public void onOkClick(Dialog dialog) {
        }

        public OkCallback() {
            super();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    }
}
