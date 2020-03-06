package cn.weli.svideo.baselib.component.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.weli.svideo.baselib.R;

/**
 * 通用弹窗dialog
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see [class/method]
 * @since [1.0.0]
 */
public class CommonDialog extends Dialog {
    private DisplayMetrics mDisplayMetric;
    private TextView mTitle;
    private TextView mContent;
    private TextView mPosButton;
    private TextView mNegButton;
    private LinearLayout mLayout;
    private ButtonCallback mCallBack;
    private float mScaleX;
    private boolean backDismiss;

    public CommonDialog(Builder builder) {
        super(builder.context);
        mDisplayMetric = builder.context.getResources().getDisplayMetrics();
        // 占屏幕宽度比例
        mScaleX = 0.89f;
        backDismiss = builder.backDismiss;
        setDialogTheme(builder.context);
        setContentView(R.layout.layout_normal_dialog);
        setCanceledOnTouchOutside(false);
        init(builder);
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    @Override
    public void setTitle(@StringRes int res) {
        mTitle.setText(res);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void setTitle(String res) {
        mTitle.setText(res);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void setContentText(String res) {
        mContent.setText(res);
        mContent.setVisibility(View.VISIBLE);
    }

    public void setContentText(@StringRes int res) {
        mContent.setText(res);
        mContent.setVisibility(View.VISIBLE);
    }

    private void init(Builder builder) {
        mCallBack = builder.callback;
        mLayout = (LinearLayout) findViewById(R.id.normal_layout);
        mTitle = (TextView) findViewById(R.id.normal_title);
        mContent = (TextView) findViewById(R.id.normal_content);
        mPosButton = (TextView) findViewById(R.id.normal_button_pos);
        mNegButton = (TextView) findViewById(R.id.normal_button_neg);
        mPosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onPositive(CommonDialog.this);
                }

            }
        });
        mNegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null) {
                    mCallBack.onNegative(CommonDialog.this);
                }
            }
        });
        if (builder.positiveText != null) {
            mPosButton.setText(builder.positiveText);
        } else {
            mPosButton.setVisibility(View.GONE);
        }
        if (builder.negativeText != null) {
            mNegButton.setText(builder.negativeText);
        } else {
            mNegButton.setVisibility(View.GONE);
        }
        if (builder.contentText != null) {
            mContent.setText(builder.contentText);
            mContent.setVisibility(View.VISIBLE);
        } else {
            mContent.setVisibility(View.INVISIBLE);
        }
        if (builder.titleText != null && !"".equals(builder.titleText)) {
            mTitle.setText(builder.titleText);
            mTitle.setVisibility(View.VISIBLE);
        } else {
            mTitle.setVisibility(View.GONE);
        }
        if (builder.positiveColor != 0) {
            mPosButton.setTextColor(builder.positiveColor);
        }
        if (builder.negativeColor != 0) {
            mNegButton.setTextColor(builder.negativeColor);
        }
        if (builder.contentColor != 0) {
            mContent.setTextColor(builder.contentColor);
        }
        if (builder.titleColor != 0) {
            mTitle.setTextColor(builder.titleColor);
        }
        if (builder.backgroundColor != 0) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(mContent.getResources().getDimension(R.dimen.common_len_8px));
            drawable.setColor(builder.backgroundColor);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                //noinspection deprecation
                mLayout.setBackgroundDrawable(drawable);
            } else {
                mLayout.setBackground(drawable);
            }
        }
        if (builder.titleSize != 0) {
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.titleSize);
        }
        if (builder.contentSize != 0) {
            mContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.contentSize);
        }
        if (builder.contentSize != 0) {
            mContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.contentSize);
        }
        if (builder.gravity != 0) {
            mContent.setGravity(builder.gravity);
        }
        if (builder.typeface != null) {
            mTitle.setTypeface(builder.typeface);
            mContent.setTypeface(builder.typeface);
            mNegButton.setTypeface(builder.typeface);
            mPosButton.setTypeface(builder.typeface);
        }
        if (builder.titleTypeface != null) {
            mTitle.setTypeface(builder.titleTypeface);
        }
        if (builder.contentTypeface != null) {
            mContent.setTypeface(builder.contentTypeface);
        }
        if (builder.posButtonTypeface != null) {
            mPosButton.setTypeface(builder.posButtonTypeface);
        }
        if (builder.negButtonTypeface != null) {
            mNegButton.setTypeface(builder.negButtonTypeface);
        }
    }

    public void content(@NonNull String text) {
        mContent.setText(text);
        mContent.setVisibility(View.VISIBLE);
    }

    public void content(@StringRes int res) {
        mContent.setText(res);
        mContent.setVisibility(View.VISIBLE);
    }

    public void contentGravity(int gravity) {
        if (null != mContent) {
            mContent.setGravity(gravity);
        }
    }

    /**
     * 展示dialog
     *
     * @param activity 当前宿主
     */
    public void showDialog(Activity activity) {
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            show();
        }
    }

    /**
     * set dialog theme(设置对话框主题)
     */
    private void setDialogTheme(Context context) {
        // android:windowNoTitle
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // android:windowBackground
        getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        // android:backgroundDimEnabled默认是true的
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * 设置dialog宽度
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            int dialogWidth = Math.min(mDisplayMetric.widthPixels, mDisplayMetric.heightPixels);
            lp.width = (int) (dialogWidth * mScaleX);
            dialogWindow.setAttributes(lp);
        }
    }

    @Override
    public void onBackPressed() {
        if (backDismiss) {
            super.onBackPressed();
        }
    }

    public static class Builder {
        protected Context context;
        private CharSequence positiveText;
        private CharSequence negativeText;
        private float titleSize;
        private float buttonSize;
        private float contentSize;
        private String titleText;
        private String contentText;
        private int negativeColor;
        private int positiveColor;
        private int titleColor;
        private int contentColor;
        private int backgroundColor;
        private int diverColor;
        private Typeface typeface;
        private ButtonCallback callback;
        private int gravity;
        private Typeface titleTypeface;
        private Typeface contentTypeface;
        private Typeface negButtonTypeface;
        private Typeface posButtonTypeface;
        private boolean backDismiss = true;

        public Builder contentTypeface(Typeface contentTypeface) {
            this.contentTypeface = contentTypeface;
            return this;
        }

        public Builder negButtonTypeface(Typeface negButtonTypeface) {
            this.negButtonTypeface = negButtonTypeface;
            return this;
        }

        public Builder posButtonTypeface(Typeface posButtonTypeface) {
            this.posButtonTypeface = posButtonTypeface;
            return this;
        }

        public Builder titleTypeface(Typeface titleTypeface) {
            this.titleTypeface = titleTypeface;
            return this;
        }

        public Builder canBackDismiss(boolean backDismiss) {
            this.backDismiss = backDismiss;
            return this;
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Builder positiveText(@StringRes int positiveTex) {
            return positiveText(this.context.getString(positiveTex));
        }

        public Builder positiveText(CharSequence positiveTex) {
            this.positiveText = positiveTex;
            return this;
        }

        public Builder negativeText(@StringRes int negativeRes) {
            return negativeText(this.context.getText(negativeRes));
        }

        public Builder negativeText(@NonNull CharSequence message) {
            this.negativeText = message;
            return this;
        }

        public Builder titleSize(@NonNull float size) {
            this.titleSize = size;
            return this;
        }

        public Builder title(@NonNull String text) {
            this.titleText = text;
            return this;
        }

        public Builder title(@StringRes int res) {
            this.titleText = context.getString(res);
            return this;
        }

        public Builder content(@NonNull String text) {
            this.contentText = text;
            return this;
        }

        public Builder content(@StringRes int res) {
            this.contentText = context.getString(res);
            return this;
        }

        public Builder negativeColor(@ColorInt int color) {
            this.negativeColor = color;
            return this;

        }

        public Builder positiveColor(@ColorInt int color) {
            this.positiveColor = color;
            return this;
        }

        public Builder titleColor(@ColorInt int color) {
            this.titleColor = color;
            return this;
        }

        public Builder contentColor(@ColorInt int color) {
            this.contentColor = color;
            return this;
        }

        public Builder diverColor(@ColorInt int color) {
            this.diverColor = color;
            return this;
        }

        public Builder backgroundColor(@ColorInt int color) {
            this.backgroundColor = color;
            return this;
        }

        public Builder buttonSize(int size) {
            this.buttonSize = size;
            return this;
        }

        public Builder contentSize(int size) {
            this.contentSize = size;
            return this;
        }

        public Builder callback(ButtonCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder typeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public Builder contentGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public CommonDialog build() {
            return new CommonDialog(this);
        }
    }

    public static abstract class ButtonCallback {


        public void onPositive(Dialog dialog) {
            dialog.dismiss();
        }

        public void onNegative(Dialog dialog) {
            dialog.dismiss();
        }

        public ButtonCallback() {
            super();
        }

        @Override
        protected final Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public final boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        protected final void finalize() throws Throwable {
            super.finalize();
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }

        @Override
        public final String toString() {
            return super.toString();
        }
    }
}
