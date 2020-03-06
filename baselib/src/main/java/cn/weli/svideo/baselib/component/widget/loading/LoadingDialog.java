package cn.weli.svideo.baselib.component.widget.loading;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import cn.weli.svideo.baselib.R;

/**
 * Loading正在加载
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see cn.weli.svideo.baselib.ui.fragment.BaseFragment,cn.weli.svideo.baselib.ui.activity.BaseActivity
 * @since [1.0.0]
 */
public class LoadingDialog extends Dialog {

    private Context mContext;

    private LoadingIndicatorView mView;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.LoadingDialog);
        mContext = context;
        setContentView(R.layout.layout_loading_view);
        setCanceledOnTouchOutside(false);
        mView = findViewById(R.id.loading_view);
        mView.setContext(context);
    }

    /**
     * 设置dialog宽度
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_100px);
            window.setAttributes(lp);
            window.setGravity(Gravity.CENTER);
        }
    }
}
