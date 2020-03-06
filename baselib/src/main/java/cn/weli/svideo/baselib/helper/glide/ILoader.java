package cn.weli.svideo.baselib.helper.glide;

import android.content.Context;
import android.widget.ImageView;

import cn.weli.svideo.baselib.R;

/**
 * 图片加载.
 *
 * @author Lei.Jiang
 * @version [1.0.0]
 * @see [相关类]
 * @since [1.0.0]
 */
public interface ILoader {

    void load(Context context, ImageView target, String url);

    void load(Context context, ImageView target, String url, Options options);

    void load(Context context, ImageView target, int resId);

    void loadGif(Context context, ImageView target, int resId, Options options);

    void load(Context context, ImageView target, int resId, Options options);

    void load(Context context, ImageView target, Object source);

    void load(Context context, ImageView target, Object source, Options options);

    void load(Context context, String url, Options options, ImageLoadCallback callback);

    void loadCorner(Context context, ImageView target, String url, int radius);

    void loadCircle(Context context, ImageView target, String url);

    void loadCircle(Context context, ImageView target, String url, Options options);

    void loadWithAnim(Context context, ImageView target, String url);

    void loadWithAnim(Context context, ImageView target, String url, Options options);

    void loadWithAnim(Context context, String url, Options options, ImageLoadCallback callback);

    void resume(Context context);

    void pause(Context context);

    class Options {
        /**
         * 加载中的资源id
         */
        public int loadingResId = RES_NONE;
        /**
         * 加载失败的资源id
         */
        public int loadErrorResId = RES_NONE;
        /**
         * 加载图片显示模式
         */
        public ImageView.ScaleType scaleType;

        public static final int RES_NONE = -1;

        public static Options defaultOptions() {
            return new Options(R.drawable.shape_common_error_bg, R.drawable
                    .shape_common_error_bg, ImageView.ScaleType.CENTER_CROP);
        }

        public Options(int loadingResId, int loadErrorResId, ImageView.ScaleType scaleType) {
            this.loadingResId = loadingResId;
            this.loadErrorResId = loadErrorResId;
            this.scaleType = scaleType;
        }

        public Options(int loadingResId, int loadErrorResId) {
            this.loadingResId = loadingResId;
            this.loadErrorResId = loadErrorResId;
            this.scaleType = ImageView.ScaleType.CENTER_CROP;
        }

        public Options(ImageView.ScaleType scaleType) {
            this.loadingResId = R.drawable.shape_common_error_bg;
            this.loadErrorResId = R.drawable.shape_common_error_bg;
            this.scaleType = scaleType;
        }

        public Options scaleType(ImageView.ScaleType scaleType) {
            this.scaleType = scaleType;
            return this;
        }

        public Options loadingRes(int resId) {
            this.loadingResId = resId;
            return this;
        }

        public Options errorRes(int resId) {
            this.loadErrorResId = resId;
            return this;
        }
    }
}
