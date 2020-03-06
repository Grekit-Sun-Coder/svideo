package cn.weli.svideo.baselib.component.helper;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import cn.weli.svideo.baselib.R;
import cn.weli.svideo.baselib.component.widget.dialog.CommonDialog;
import cn.weli.svideo.baselib.component.widget.dialog.CommonToastDialog;

/**
 * dialog帮助类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see cn.weli.svideo.baselib.ui.activity.BaseActivity
 * @since [1.0.0]
 */
public class DialogHelper {

    public static CommonDialog.Builder commonDialog(Context ctx) {
        return new CommonDialog.Builder(ctx)
                .positiveColor(ContextCompat.getColor(ctx, R.color.color_FF6122))
                .negativeColor(ContextCompat.getColor(ctx, R.color.color_888888))
                .contentColor(ContextCompat.getColor(ctx, R.color.color_666666))
                .titleColor(ContextCompat.getColor(ctx, R.color.color_333333))
                .title(R.string.common_dialog_title)
                .positiveText(R.string.common_str_ok)
                .negativeText(R.string.common_str_cancel);
    }

    public static CommonToastDialog.Builder commonToastDialog(Context ctx) {
        return new CommonToastDialog.Builder(ctx)
                .content(R.string.app_name)
                .okStr(R.string.common_str_know);
    }
}
