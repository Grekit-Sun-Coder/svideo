package cn.weli.svideo.baselib.utils;

import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.etouch.logger.Logger;

/**
 * TimeOut处理.
 *
 * @author Lei.Jiang
 * @version [1.0.6]
 * @see [WatchDogKillerUtil]
 * @since [1.0.6]
 */
public class WatchDogKillerUtil {

    private static volatile boolean sWatchdogStopped = false;

    public static boolean checkWatchDogAlive() {
        final Class clazz;
        try {
            clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            final Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            final Object watchdog = field.get(null);
            Method isRunningMethod = clazz.getSuperclass().getDeclaredMethod("isRunning");
            isRunningMethod.setAccessible(true);
            return (boolean) isRunningMethod.invoke(watchdog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void stopWatchDog() {
        // Android P 以后不能反射FinalizerWatchdogDaemon
        if (Build.VERSION.SDK_INT >= 28) {
            return;
        }
        if (sWatchdogStopped) {
            return;
        }
        sWatchdogStopped = true;

        try {
            final Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            final Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            final Object watchdog = field.get(null);
            try {
                final Field thread = clazz.getSuperclass().getDeclaredField("thread");
                thread.setAccessible(true);
                thread.set(watchdog, null);
            } catch (Throwable t) {
                Logger.e(t.getMessage());
                try {
                    // 直接调用stop方法，在Android 6.0之前会有线程安全问题
                    final Method method = clazz.getSuperclass().getDeclaredMethod("stop");
                    method.setAccessible(true);
                    method.invoke(watchdog);
                } catch (final Throwable e) {
                    Logger.e(t.getMessage());
                }
            }
        } catch (Throwable t) {
            Logger.e(t.getMessage());
        }
    }
}
