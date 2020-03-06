package cn.weli.svideo.common.http;

import cn.etouch.retrofit.operation.HttpSubscriber;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-13
 * @see [class/method]
 * @since [1.0.0]
 */
public class SimpleHttpSubscriber<T> extends HttpSubscriber<T> {
    @Override
    public void onPreExecute() {

    }

    @Override
    public void onNetworkUnavailable() {

    }

    @Override
    public void onResponseSuccess(T t) {

    }

    @Override
    public void onResponseError(String s, String s1) {

    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onPostExecute() {

    }
}
