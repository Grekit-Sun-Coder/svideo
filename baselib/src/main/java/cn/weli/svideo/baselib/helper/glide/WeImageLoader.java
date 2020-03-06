package cn.weli.svideo.baselib.helper.glide;

/**
 * 图片加载.
 *
 * @author Lei.Jiang
 * @version [1.0.0]
 * @see [相关类]
 * @since [1.0.0]
 */
public class WeImageLoader {

    private static ILoader sImageLoader;

    public static ILoader getInstance() {
        if (sImageLoader == null) {
            synchronized (WeImageLoader.class) {
                if (sImageLoader == null) {
                    sImageLoader = new GlideLoader();
                }
            }
        }
        return sImageLoader;
    }
}
