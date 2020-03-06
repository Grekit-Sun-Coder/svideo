package cn.weli.svideo.baselib.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Constructor;
import java.util.Map;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.R;
import cn.weli.svideo.baselib.component.widget.loading.LoadingDialog;
import cn.weli.svideo.baselib.component.widget.toast.WeToast;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.baselib.presenter.IPresenter;

/**
 * Fragment基类，用于生成presenter类，以及基础的公用信息
 *
 * @author Lei Jiang
 * @version [1.0.0]
 */
public abstract class BaseFragment<T extends IPresenter, K> extends Fragment {

    protected T mPresenter;
    private WeakHandler mHandler;
    private LoadingDialog mLoadingDialog;
    private Runnable mShowLoadViewRunnable;
    /**
     * 标记已加载完成，保证懒加载只能加载一次
     */
    private boolean hasLoaded = false;
    /**
     * 标记Fragment是否已经onCreate
     */
    private boolean isCreated = false;
    /**
     * 界面对于用户是否可见
     */
    private boolean isVisibleToUser = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mShowLoadViewRunnable != null) {
            removeHandlerCallbacks(mShowLoadViewRunnable);
        }
        if (mPresenter != null) {
            mPresenter.clear();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isCreated = true;
        lazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (isVisibleToUser) {
            lazyLoad();
        }
    }

    /**
     * 判断懒加载条件
     */
    public void lazyLoad() {
        if (isVisibleToUser && isCreated && !hasLoaded) {
            onLazyLoad();
            hasLoaded = true;
        }
    }

    /**
     * 懒加载
     */
    public void onLazyLoad() {
    }

    @Override
    public void startActivity(Intent intent) {
        if (isAdded() && getActivity() != null) {
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (isAdded() && getActivity() != null) {
            getActivity().startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 延迟处理事件.
     *
     * @param runnable    线程
     * @param delayMillis 延迟时间
     */
    public void handleEventDelay(Runnable runnable, long delayMillis) {
        if (isAdded()) {
            if (mHandler == null) {
                mHandler = new WeakHandler();
            }
            mHandler.postDelayed(runnable, delayMillis);
        }
    }

    /**
     * 删除事件.
     *
     * @param runnable 线程
     */
    public void removeHandlerCallbacks(Runnable runnable) {
        if (isAdded()) {
            if (mHandler == null) {
                mHandler = new WeakHandler();
            }
            mHandler.removeCallbacks(runnable);
        }
    }

    /**
     * 显示加载Dialog.
     */
    public void showLoadView() {
        showLoadView(1000);
    }

    /**
     * 显示加载Dialog.延迟显示
     */
    public void showLoadView(long delay) {
        if (getActivity() != null && isAdded() && !getActivity().isFinishing()) {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingDialog(getActivity());
            }
            if (mShowLoadViewRunnable == null) {
                mShowLoadViewRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null && isAdded() && !getActivity().isFinishing()) {
                            if (!mLoadingDialog.isShowing()) {
                                mLoadingDialog.show();
                            }
                        }
                    }
                };
            }
            handleEventDelay(mShowLoadViewRunnable, delay);
        }
    }

    /**
     * 关闭加载动画
     **/
    public void finishLoadView() {
        if (getActivity() != null && isAdded() && !getActivity().isFinishing() && mShowLoadViewRunnable != null) {
            removeHandlerCallbacks(mShowLoadViewRunnable);
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }

    public void startProtocol(String url) {
    }

    public void startProtocol(String url, Map<String, Object> extraMap) {
    }

    /**
     * 提示无网络
     */
    public void showNetworkUnAvailable() {
        if (isAdded() && !isHidden()) {
            showToast(R.string.common_str_network_unavailable);
        }
    }

    /**
     * 提示网络错误
     */
    public void showNetworkError() {
        if (isAdded() && !isHidden()) {
            showToast(R.string.common_str_network_error);
        }
    }

    /**
     * 关闭当前页面
     */
    public void finishCurrentActivity() {
        if (isAdded() && getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * 关闭当前页面
     */
    public void finishActivity() {
        if (isAdded() && getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * 收起系统软键盘.
     */
    public void hideSoftInputFromWindow(View view) {
        if (isAdded() && getActivity() != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (null != inputManager && null != view) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * 弹出系统软键盘.
     */
    public void showSoftInputFromWindow(View view) {
        if (isAdded() && getActivity() != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (null != inputManager && null != view) {
                view.requestFocus();
                inputManager.showSoftInput(view, 0);
            }
        }
    }

    /**
     * 返回逻辑处理的具体类型.
     */
    protected abstract Class<T> getPresenterClass();

    /**
     * 返回View层的接口类.
     */
    protected abstract Class<K> getViewClass();

    /**
     * 初始化Presenter
     */
    protected void initPresenter() {
        try {
            Constructor constructor = getPresenterClass().getConstructor(getViewClass());
            mPresenter = (T) constructor.newInstance(this);
        } catch (Exception e) {
            Logger.e("Init presenter throw an error : [" + e.getMessage() + "]");
        }
    }

    /**
     * 显示Toast提示.
     */
    public void showToast(@NonNull String info) {
        if (isAdded() && getActivity() != null && !isHidden()) {
            WeToast.getInstance().showToast(getActivity(), info);
        }
    }

    /**
     * 显示Toast提示.
     */
    public void showToast(@StringRes int resId) {
        if (isAdded() && getActivity() != null && !isHidden()) {
            WeToast.getInstance().showToast(getActivity(), resId);
        }
    }
}
