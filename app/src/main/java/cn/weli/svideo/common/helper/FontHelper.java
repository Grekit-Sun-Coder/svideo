package cn.weli.svideo.common.helper;

import android.content.Context;
import android.graphics.Typeface;

import cn.weli.svideo.baselib.helper.font.TypefaceUtils;

/**
 * 特殊字体
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-29
 * @see [class/method]
 * @since [1.0.0]
 */
public class FontHelper {

    /**
     * 获取特殊字体
     *
     * @param context 上下文
     * @return 对应的特殊字体
     */
    public static Typeface getDinAlternateBoldFont(Context context) {
        return TypefaceUtils.load(context.getAssets(), "fonts/din_alternate_bold.ttf");
    }
}
