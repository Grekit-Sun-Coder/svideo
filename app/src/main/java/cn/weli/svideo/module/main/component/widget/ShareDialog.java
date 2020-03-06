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
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.common.constant.BusinessConstants;

/**
 * Function description
 *
 * @author ZhengXiang Sun
 * @version [1.0.3]
 * @date 2020-01-10
 * @see [class/method]
 * @since [1.0.3]
 */
public class ShareDialog extends Dialog {

    @BindView(R.id.video_share_wx_txt)
    TextView mShareWxTxt;
    @BindView(R.id.video_share_pyq_txt)
    TextView mSharePyqTxt;
    @BindView(R.id.video_share_qq_txt)
    TextView mShareQqTxt;
    @BindView(R.id.video_share_link_url_txt)
    TextView mShareLjTxt;
    @BindView(R.id.video_share_report_txt)
    TextView mShareJbTxt;
    @BindView(R.id.video_share_not_interested_txt)
    TextView mShareNotIntrTxt;
    @BindView(R.id.video_share_cancel)
    TextView mShareCancelTxt;

    private boolean isDisplayWx;
    private boolean isDisplayPyq;
    private boolean isDisplayQq;
    private boolean isDisplayLinkUrl;
    private boolean isDisplayReport;
    private boolean isDisplayNotInter;

    public ShareDialog(@NonNull Context context) {
        this(context, new Builder(context)
                .addWx()
                .addPyq()
                .addQq()
                .addLinlUrl()
                .addJubao()
                .addNotInter()
        );
    }

    private ShareDialog(@NonNull Context context, Builder builder) {
        super(context);
        init();
        isDisplayWx = builder.isDisplayWx;
        isDisplayPyq = builder.isDisplayPyq;
        isDisplayQq = builder.isDisplayQq;
        isDisplayLinkUrl = builder.isDisplayLinlUrl;
        isDisplayReport = builder.isDisplayReport;
        isDisplayNotInter = builder.isDisplayNotInter;
        initDisplay();
    }

    /**
     * 初始化
     */
    private void init() {
        setDialogTheme();
        setContentView(R.layout.dialog_share_video);
        ButterKnife.bind(this);
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogBottomWindowAnim);
        }
    }

    /**
     * 判断建造了哪些平台
     */
    private void initDisplay() {
        if (!isDisplayWx) {
            mShareWxTxt.setVisibility(View.GONE);
        }
        if (!isDisplayPyq) {
            mSharePyqTxt.setVisibility(View.GONE);
        }
        if (!isDisplayQq) {
            mShareQqTxt.setVisibility(View.GONE);
        }
        if (!isDisplayLinkUrl) {
            mShareLjTxt.setVisibility(View.GONE);
        }
        if (!isDisplayReport) {
            mShareJbTxt.setVisibility(View.GONE);
        }
        if (!isDisplayNotInter) {
            mShareNotIntrTxt.setVisibility(View.GONE);
        }
    }

    /**
     * 图片点击事件
     */
    @OnClick({R.id.video_share_wx_txt, R.id.video_share_pyq_txt
            , R.id.video_share_qq_txt, R.id.video_share_link_url_txt
            , R.id.video_share_report_txt, R.id.video_share_not_interested_txt
            , R.id.video_share_cancel})
    void onImgClick(View view) {
        switch (view.getId()) {
            case R.id.video_share_wx_txt:
                if (mOnSharePlatformSelectListener != null) {
                    mOnSharePlatformSelectListener.onPlatformSelected(
                            BusinessConstants.ShareType.PLATFORM_WEIXIN);
                }
                break;
            case R.id.video_share_pyq_txt:
                if (mOnSharePlatformSelectListener != null) {
                    mOnSharePlatformSelectListener.onPlatformSelected(
                            BusinessConstants.ShareType.PLATFORM_WEIXIN_PYQ);
                }
                break;
            case R.id.video_share_qq_txt:
                if (mOnSharePlatformSelectListener != null) {
                    mOnSharePlatformSelectListener.onPlatformSelected(
                            BusinessConstants.ShareType.PLATFORM_QQ);
                }
                break;
            case R.id.video_share_link_url_txt:
                if (mOnSharePlatformSelectListener != null) {
                    mOnSharePlatformSelectListener.onPlatformSelected(
                            BusinessConstants.ShareType.COPY_LINK);
                }
                break;
            case R.id.video_share_report_txt:
                if (mOnSharePlatformSelectListener != null) {
                    mOnSharePlatformSelectListener.onPlatformSelected(
                            BusinessConstants.ShareType.REPORT);
                }
                break;
            case R.id.video_share_not_interested_txt:
                if (mOnSharePlatformSelectListener != null) {
                    mOnSharePlatformSelectListener.onPlatformSelected(
                            BusinessConstants.ShareType.NOT_INTERESTED);
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
            lp.gravity = Gravity.BOTTOM;
            dialogWindow.setAttributes(lp);
        }
    }

    private OnSharePlatformSelectListener mOnSharePlatformSelectListener;

    public interface OnSharePlatformSelectListener {
        void onPlatformSelected(String platform);
    }

    public void setPlatformSelectListener(
            OnSharePlatformSelectListener mOnSharePlatformSelectListener) {
        this.mOnSharePlatformSelectListener = mOnSharePlatformSelectListener;
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

    public static final class Builder {

        private Context context;
        private boolean isDisplayWx;
        private boolean isDisplayPyq;
        private boolean isDisplayQq;
        private boolean isDisplayLinlUrl;
        private boolean isDisplayReport;
        private boolean isDisplayNotInter;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder addWx() {
            isDisplayWx = true;
            return this;
        }

        public Builder addPyq() {
            isDisplayPyq = true;
            return this;
        }

        public Builder addQq() {
            isDisplayQq = true;
            return this;
        }

        public Builder addLinlUrl() {
            isDisplayLinlUrl = true;
            return this;
        }

        public Builder addJubao() {
            isDisplayReport = true;
            return this;
        }

        public Builder addNotInter() {
            isDisplayNotInter = true;
            return this;
        }

        public ShareDialog build() {
            return new ShareDialog(context, this);
        }
    }
}
