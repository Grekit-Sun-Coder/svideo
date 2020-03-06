package cn.weli.svideo.baselib.helper.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.concurrent.ExecutionException;

import cn.weli.svideo.baselib.helper.glide.config.GlideApp;

/**
 * Glide下载图片
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see [class/method]
 * @since [1.0.0]
 */
public class GlideDownloadHelper {

    public static void imageBitmap(Context context, String imgUrl, final Callback<Bitmap> callback) {
        imageBitmap(context, imgUrl, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, callback);
    }

    public static void imageBitmap(final Context context, String imgUrl, int width, int height, final Callback<Bitmap> callback) {
        RequestOptions options = new RequestOptions();
        GlideApp.with(context).asBitmap().load(imgUrl).apply(options.override(width, height)).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                if (callback != null) {
                    callback.onSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (callback != null) {
                    callback.onFail();
                }
            }
        });
    }


    public static void imageDrawable(final Context context, String imgUrl, final Callback<Drawable> callback) {
        GlideApp.with(context).load(imgUrl).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (callback != null) {
                    callback.onSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (callback != null) {
                    callback.onFail();
                }
            }
        });
    }

    public static void imageFile(Context context, String imgUrl, final Callback<File> callback) {
        GlideApp.with(context).asFile().load(imgUrl).into(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                if (callback != null) {
                    callback.onSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                if (callback != null) {
                    callback.onFail();
                }
            }
        });
    }


    /**
     * 异步下载图片
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static File imageAsynFile(Context context, String imgUrl) throws ExecutionException, InterruptedException {
        return Glide.with(context).asFile().load(imgUrl).submit().get();
    }

    /**
     * 异步加载图片
     *
     * @param width  如果width<=0,就按原图加载
     * @param height 如果height<=0,就按原图加载
     */
    public static Bitmap getBitmap(Context context, String url, int width, int height) {
        try {
            if (width <= 0) {
                width = Target.SIZE_ORIGINAL;
            }
            if (height <= 0) {
                height = Target.SIZE_ORIGINAL;
            }
            return Glide.with(context)
                    .asBitmap()
                    .load(url)
                    .submit(width, height)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    public interface Callback<T> {

        void onSuccess(T t);

        void onFail();
    }
}
