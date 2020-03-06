package cn.weli.svideo.baselib.helper.glide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.helper.glide.config.GlideApp;
import cn.weli.svideo.baselib.helper.glide.transform.GlideCircleTransform;
import cn.weli.svideo.baselib.helper.glide.transform.GlideCornersTransform;

/**
 * Description.
 *
 * @author Lei.Jiang
 * @version [1.0.0]
 * @see [相关类]
 * @since [1.0.0]
 */
public class GlideLoader implements ILoader {

    @Override
    public void load(Context context, ImageView target, String url) {
        load(context, target, url, null);
    }

    @Override
    public void load(Context context, ImageView target, String url, Options options) {
        loadImg(context, target, url, options);
    }

    @Override
    public void load(Context context, ImageView target, int resId) {
        load(context, target, resId, null);
    }

    @Override
    public void loadGif(Context context, ImageView target, int resId, Options options) {
        loadGifImg(context, target, resId, options);
    }

    @Override
    public void load(Context context, ImageView target, int resId, Options options) {
        loadImg(context, target, resId, options);
    }

    @Override
    public void load(Context context, ImageView target, Object source) {
        loadImg(context, target, source, null);
    }

    @Override
    public void load(Context context, ImageView target, Object source, Options options) {
        loadImg(context, target, source, options);
    }

    @Override
    public void load(Context context, String url, Options options, ImageLoadCallback callback) {
        loadImg(context, url, options, callback);
    }

    @Override
    public void loadCorner(Context context, ImageView target, String url, int radius) {
        loadCornerImg(context, target, url, null, radius);
    }

    @Override
    public void loadCircle(Context context, ImageView target, String url) {
        loadCircleImg(context, target, url, null);
    }

    @Override
    public void loadCircle(Context context, ImageView target, String url, Options options) {
        loadCircleImg(context, target, url, options);
    }

    @Override
    public void loadWithAnim(Context context, ImageView target, String url) {
        loadImgWithAnim(context, target, url, null);
    }

    @Override
    public void loadWithAnim(Context context, ImageView target, String url, Options options) {
        loadImgWithAnim(context, target, url, options);
    }

    @Override
    public void loadWithAnim(Context context, String url, Options options, ImageLoadCallback
            callback) {
        loadImgWithAnim(context, url, options, callback);
    }

    @Override
    public void resume(Context context) {
        GlideApp.with(context).resumeRequests();
    }

    @Override
    public void pause(Context context) {
        GlideApp.with(context).pauseRequests();
    }

    /**
     * 加载图片
     *
     * @param context 上下文
     * @param target  图片
     * @param source  资源
     * @param options 配置
     */
    private void loadGifImg(Context context, ImageView target, Object source, Options options) {
        try {
            if (options == null) {
                options = Options.defaultOptions();
            }
            GlideApp.with(context)
                    .asGif()
                    .load(source)
                    .apply(dealOptions(options))
                    .into(target);
        } catch (IllegalArgumentException exception) {
            Logger.e("You cannot start a load for a destroyed activity.");
        }
    }

    /**
     * 加载图片
     *
     * @param context 上下文
     * @param target  图片
     * @param source  资源
     * @param options 配置
     */
    private void loadImg(Context context, ImageView target, Object source, Options options) {
        try {
            if (options == null) {
                options = Options.defaultOptions();
            }
            GlideApp.with(context)
                    .load(source)
                    .apply(dealOptions(options))
                    .dontAnimate()
                    .into(target);
        } catch (IllegalArgumentException exception) {
            Logger.e("You cannot start a load for a destroyed activity.");
        }
    }

    /**
     * 加载图片
     *
     * @param context 上下文
     * @param target  图片
     * @param source  资源
     * @param options 配置
     */
    private void loadCircleImg(Context context, ImageView target, Object source, Options options) {
        try {
            if (options == null) {
                options = Options.defaultOptions();
            }
            GlideApp.with(context)
                    .load(source)
                    .apply(dealOptions(options))
                    .dontAnimate()
                    .transform(new GlideCircleTransform())
                    .into(target);
        } catch (IllegalArgumentException exception) {
            Logger.e("You cannot start a load for a destroyed activity.");
        }
    }

    /**
     * 加载图片
     *
     * @param context 上下文
     * @param target  图片
     * @param source  资源
     * @param options 配置
     */
    private void loadCornerImg(Context context, ImageView target, Object source, Options options,
                               int radius) {
        try {
            if (options == null) {
                options = Options.defaultOptions();
            }
            GlideApp.with(context)
                    .load(source)
                    .apply(dealOptions(options))
                    .dontAnimate()
                    .transform(new GlideCornersTransform(context, radius,
                            GlideCornersTransform.CornerType.ALL))
                    .into(target);
        } catch (IllegalArgumentException exception) {
            Logger.e("You cannot start a load for a destroyed activity.");
        }
    }

    /**
     * 加载图片
     *
     * @param context  上下文
     * @param source   资源
     * @param options  配置
     * @param callback 回调
     */
    private void loadImg(Context context, Object source, Options options,
                         final ImageLoadCallback callback) {
        try {
            if (options == null) {
                options = Options.defaultOptions();
            }
            GlideApp.with(context)
                    .load(source)
                    .apply(dealOptions(options))
                    .dontAnimate()
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (callback != null) {
                                callback.onLoadReady(resource);
                            }
                        }
                    });
        } catch (IllegalArgumentException exception) {
            Logger.e("You cannot start a load for a destroyed activity.");
        }
    }

    /**
     * 加载图片
     *
     * @param context  上下文
     * @param source   资源
     * @param options  配置
     * @param callback 回调
     */
    private void loadImgWithAnim(Context context, Object source, Options options,
                                 final ImageLoadCallback callback) {
        try {
            if (options == null) {
                options = Options.defaultOptions();
            }
            GlideApp.with(context)
                    .load(source)
                    .apply(dealOptions(options))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                            if (callback != null) {
                                callback.onLoadReady(resource);
                            }
                        }
                    });
        } catch (IllegalArgumentException exception) {
            Logger.e("You cannot start a load for a destroyed activity.");
        }
    }

    /**
     * 加载图片
     *
     * @param context 上下文
     * @param target  图片
     * @param source  资源
     * @param options 配置
     */
    private void loadImgWithAnim(Context context, ImageView target, Object source,
                                 Options options) {
        try {
            if (options == null) {
                options = Options.defaultOptions();
            }
            GlideApp.with(context)
                    .load(source)
                    .apply(dealOptions(options))
                    .into(target);
        } catch (IllegalArgumentException exception) {
            Logger.e("You cannot start a load for a destroyed activity.");
        }
    }

    @SuppressLint("CheckResult")
    private RequestOptions dealOptions(Options options) {
        RequestOptions request = new RequestOptions();
        if (options != null) {
            if (options.scaleType != null) {
                if (options.loadingResId != Options.RES_NONE) {
                    request.placeholder(options.loadingResId);
                }
                if (options.loadErrorResId != Options.RES_NONE) {
                    request.error(options.loadErrorResId);
                }
                switch (options.scaleType) {
                    case MATRIX:
                    case FIT_XY:
                    case FIT_START:
                    case FIT_END:
                    case CENTER:
                    case CENTER_INSIDE:
                        break;
                    case FIT_CENTER:
                        request.fitCenter();
                        break;
                    case CENTER_CROP:
                        request.centerCrop();
                        break;
                }
            } else {
                request.centerCrop();
            }
        } else {
            request.centerCrop();
        }
        return request;
    }
}
