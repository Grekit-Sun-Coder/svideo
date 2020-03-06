package cn.weli.svideo.baselib.helper;

import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * Delivers responses and errors.
 */
public class ExecutorDelivery {
    /**
     * Used for posting responses, typically to the main thread.
     */
    private final Executor mResponsePoster;

    /**
     * Creates a new response delivery interface.
     *
     * @param handler {@link Handler} to post responses on
     */
    public ExecutorDelivery(final Handler handler) {
        // Make an Executor that just wraps the handler.
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }

    /**
     * Creates a new response delivery interface, mockable version
     * for testing.
     *
     * @param executor For running delivery tasks
     */
    public ExecutorDelivery(Executor executor) {
        mResponsePoster = executor;
    }

    public void post(Runnable runnable) {
        mResponsePoster.execute(runnable);
    }


}
