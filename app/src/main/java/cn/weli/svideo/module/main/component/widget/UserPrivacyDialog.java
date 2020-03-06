package cn.weli.svideo.module.main.component.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.module.main.ui.SplashActivity;
import cn.weli.svideo.module.main.ui.WebViewActivity;
import cn.weli.svideo.module.main.view.IWebView;
import cn.weli.svideo.utils.SpannableStringUtils;

/**
 * 用户隐私协议弹窗
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-02
 * @see SplashActivity
 * @since [1.0.0]
 */
public class UserPrivacyDialog extends Dialog {

    private Context mContext;

    @BindView(R.id.privacy_title_txt)
    TextView mPrivacyTitleTxt;

    private OnUserPrivacyListener mPrivacyListener;

    public interface OnUserPrivacyListener {

        void onAgreeUserPrivacy();

        void onDisAgreeUserPrivacy();
    }

    public UserPrivacyDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_user_privacy);
        ButterKnife.bind(this);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        init(context);
    }

    public void setPrivacyListener(OnUserPrivacyListener privacyListener) {
        mPrivacyListener = privacyListener;
    }

    @OnClick({R.id.privacy_exit_txt, R.id.privacy_ok_txt})
    public void onViewClicked(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.privacy_exit_txt:
                if (mPrivacyListener != null) {
                    mPrivacyListener.onDisAgreeUserPrivacy();
                }
                break;
            case R.id.privacy_ok_txt:
                if (mPrivacyListener != null) {
                    mPrivacyListener.onAgreeUserPrivacy();
                }
                break;
        }
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mContext = context;
        SpannableStringUtils.Builder builder = new SpannableStringUtils.Builder()
                .append(context.getString(R.string.splash_privacy_title1))
                .setForegroundColor(ContextCompat.getColor(context, R.color.color_333333))
                .append(context.getString(R.string.splash_privacy_title2))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent agreeIntent = new Intent(mContext, WebViewActivity.class);
                        agreeIntent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, HttpConstant.H5_URL_USER_AGREEMENT);
                        mContext.startActivity(agreeIntent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(ContextCompat.getColor(context, R.color.color_5D9EFF));
                        ds.setUnderlineText(false);
                    }
                })
                .append(context.getString(R.string.common_str_and))
                .setForegroundColor(ContextCompat.getColor(context, R.color.color_333333))
                .append(context.getString(R.string.splash_privacy_title3))
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent policyIntent = new Intent(mContext, WebViewActivity.class);
                        policyIntent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, HttpConstant.H5_URL_PRIVACY_POLICY);
                        mContext.startActivity(policyIntent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(ContextCompat.getColor(context, R.color.color_5D9EFF));
                        ds.setUnderlineText(false);
                    }
                })
                .append(context.getString(R.string.splash_privacy_title4))
                .setForegroundColor(ContextCompat.getColor(context, R.color.color_333333));
        mPrivacyTitleTxt.setText(builder.create());
        mPrivacyTitleTxt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager windowManager = dialogWindow.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (display.getWidth() * 0.9f);
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
