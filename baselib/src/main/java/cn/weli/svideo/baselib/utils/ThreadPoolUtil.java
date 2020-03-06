package cn.weli.svideo.baselib.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 简单线程池.
 *
 * @author Lei.Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see ThreadPoolUtil
 * @since [1.0.0]
 */
public class ThreadPoolUtil {

    /**
     * 单例首先私有化构造方法，然后饿汉式一开始就开始创建，并提供get方法
     */
    private static ThreadPoolUtil mInstance;

    public static ThreadPoolUtil getInstance() {
        if (mInstance == null) {
            synchronized (ThreadPoolUtil.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPoolUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    private int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 1;

    private TimeUnit unit = TimeUnit.HOURS;

    private ThreadPoolExecutor executor;

    /**
     * 给corePoolSize赋值：当前设备可用处理器核心数*2 + 1,能够让cpu的效率得到最大程度执行（有研究论证的）
     */
    private ThreadPoolUtil() {
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        //虽然maximumPoolSize用不到，但是需要赋值，否则报错
        maximumPoolSize = corePoolSize;
        initThreadPool();
    }

    /**
     * 初始化
     */
    private void initThreadPool() {
        executor = new ThreadPoolExecutor(
                //当某个核心任务执行完毕，会依次从缓冲队列中取出等待任务
                corePoolSize,
                //5,先corePoolSize,然后new LinkedBlockingQueue<Runnable>(),然后maximumPoolSize,
                // 但是它的数量是包含了corePoolSize的
                maximumPoolSize,
                //表示的是maximumPoolSize当中等待任务的存活时间
                keepAliveTime,
                unit,
                //缓冲队列，用于存放等待任务，Linked的先进先出
                new LinkedBlockingQueue<Runnable>(),
                //创建线程的工厂
                new DefaultThreadFactory(Thread.NORM_PRIORITY, "wl-pool-"),
                //用来对超出maximumPoolSize的任务的处理策略
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 执行任务
     */
    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (executor == null) {
            initThreadPool();
        }
        executor.execute(runnable);
    }

    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (executor == null) {
            initThreadPool();
        }
        executor.remove(runnable);
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    /**
     * 创建线程的工厂，设置线程的优先级，group，以及命名
     */
    private static class DefaultThreadFactory implements ThreadFactory {
        /**
         * 线程池的计数
         */
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);

        /**
         * 线程的计数
         */
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(threadPriority);
            return t;
        }
    }
}
