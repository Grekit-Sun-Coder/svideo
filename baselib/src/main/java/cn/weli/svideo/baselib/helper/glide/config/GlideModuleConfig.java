package cn.weli.svideo.baselib.helper.glide.config;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.File;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.utils.StorageUtils;
import okhttp3.OkHttpClient;

/**
 * Glide配置
 *
 * @author Lei.Jiang
 * @version [1.0.0]
 * @see [相关类]
 * @since [1.0.0]
 */

@GlideModule
public class GlideModuleConfig extends AppGlideModule {

    private static final int DISK_SIZE = 1024 * 1024 * 160;
    private static final int TIME_OUT = 20;
    private static final int MEMORY_SIZE = (int) (Runtime.getRuntime().maxMemory()) / 16;
    private static final String GLIDE_CACHE = "glide_disk_cache";

    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        builder.setMemoryCache(new LruResourceCache(MEMORY_SIZE));
        builder.setBitmapPool(new LruBitmapPool(MEMORY_SIZE));
        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                // 为产品想做清除缓存做准备
                File cacheDir = new File(StorageUtils.getCacheDirectory(context), GLIDE_CACHE);
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs();
                }
                return DiskLruCacheWrapper.create(cacheDir, DISK_SIZE);
            }
        });
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        // 支持https证书
        X509TrustManager xtm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        HostnameVerifier unSafeVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslContext.getSocketFactory());
            builder.hostnameVerifier(unSafeVerifier);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
            builder.readTimeout(TIME_OUT, TimeUnit.SECONDS);

            registry.replace(GlideUrl.class, InputStream.class,
                    new OkHttpUrlLoader.Factory(builder.build()));
        } catch (Exception e) {
            Logger.w("register components failed [" + e.getMessage() + "]");
        }
    }
}
