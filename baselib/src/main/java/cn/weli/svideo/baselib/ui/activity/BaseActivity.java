package cn.weli.svideo.baselib.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.util.Map;

import cn.etouch.logger.Logger;
import cn.weli.svideo.baselib.R;
import cn.weli.svideo.baselib.component.widget.loading.LoadingDialog;
import cn.weli.svideo.baselib.component.widget.toast.WeToast;
import cn.weli.svideo.baselib.helper.handler.WeakHandler;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.DensityUtil;

/**
 * Activity基类，用于生成presenter类，以及基础的公用信息
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @since [1.0.0]
 */
public abstract class BaseActivity<T extends IPresenter, K> extends AppCompatActivity {

    protected T mPresenter;
    private WeakHandler mHandler;

    private RelativeLayout mToolbarLayout;
    private ImageView mBackImg;
    private ImageView mMenuImg;
    private TextView mTitleTxt;
    private TextView mRightTxt;
    private LoadingDialog mLoadingDialog;
    private Runnable mShowLoadViewRunnable;
    private boolean needSetOrientation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        setOrientation();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_bottom_silent, R.anim.activity_bottom_out);
    }

    @Override
    public void startActivity(Intent intent) {
        if (intent != null) {
            super.startActivity(intent);
            overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_silent);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (intent != null) {
            super.startActivityForResult(intent, requestCode);
            overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_silent);
        }
    }

    protected void setOrientation() {
        if (needSetOrientation) {
            DensityUtil.getInstance().setDefault(this);
        }

    }

    protected void setOrientation(String orientation) {
        if (needSetOrientation) {
            DensityUtil.getInstance().setOrientation(this, orientation);
        }
    }

    /**
     * 延迟处理事件.
     *
     * @param runnable    线程
     * @param delayMillis 延迟时间
     */
    public void handleEventDelay(Runnable runnable, long delayMillis) {
        if (mHandler == null) {
            mHandler = new WeakHandler();
        }
        mHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * 删除事件.
     *
     * @param runnable 线程
     */
    public void removeHandlerCallbacks(Runnable runnable) {
        if (mHandler == null) {
            mHandler = new WeakHandler();
        }
        mHandler.removeCallbacks(runnable);
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
        if (!isFinishing()) {
            if (mLoadingDialog == null) {
                mLoadingDialog = new LoadingDialog(this);
            }
            if (mShowLoadViewRunnable == null) {
                mShowLoadViewRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
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
        if (!isFinishing()) {
            if (mShowLoadViewRunnable != null) {
                removeHandlerCallbacks(mShowLoadViewRunnable);
            }
            if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
                mLoadingDialog.dismiss();
            }
        }
    }

    /**
     * 提示无网络
     */
    public void showNetworkUnAvailable() {
        showToast(R.string.common_str_network_unavailable);
    }

    /**
     * 提示网络错误
     */
    public void showNetworkError() {
        showToast(R.string.common_str_network_error);
    }

    /**
     * 关闭当前页面
     */
    public void finishActivity() {
        this.finish();
    }

    public void startProtocol(String url) {
    }
    public void startProtocol(String url, Map<String, Object> extraMap) {
    }

    /**
     * 收起系统软键盘.
     */
    public void hideSoftInputFromWindow(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (null != inputManager && null != view) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 弹出系统软键盘.
     */
    public void showSoftInputFromWindow(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (null != inputManager && null != view) {
            view.requestFocus();
            inputManager.showSoftInput(view, 0);
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
            e.printStackTrace();
        }
    }

    /**
     * 显示Toast提示.
     */
    public void showToast(@NonNull String info) {
        WeToast.getInstance().showToast(this, info);
    }

    public void showToast(@StringRes int resId) {
        WeToast.getInstance().showToast(this, resId);
    }

    /**
     * 设置toolbar的背景.
     *
     * @param res 背景资源
     */
    public void setToolbarBackground(@DrawableRes int res) {
        initToolbar();
        mToolbarLayout.setBackgroundResource(res);
    }

    /**
     * 设置toolbar的背景颜色.
     *
     * @param res 背景颜色
     */
    public void setToolbarBackgroundColor(int res) {
        initToolbar();
        mToolbarLayout.setBackgroundColor(res);
    }

    /**
     * 设置并显示标题内容.
     *
     * @param title 标题文字
     */
    public void showTitle(String title) {
        initToolbar();
        mTitleTxt.setText(title);
        mTitleTxt.setVisibility(View.VISIBLE);
    }

    /**
     * 设置并显示标题内容.
     *
     * @param titleId 标题文字资源id
     */
    public void showTitle(int titleId) {
        initToolbar();
        mTitleTxt.setText(getString(titleId));
        mTitleTxt.setVisibility(View.VISIBLE);
    }

    /**
     * 设置title透明度
     *
     * @param alpha 透明度 0 - 255
     */
    public void setTitleAlpha(int alpha) {
        initToolbar();
        mTitleTxt.setAlpha(alpha / 255f);
    }

    /**
     * 设置title是否显示
     *
     * @param visible 是否显示
     */
    public void setTitleVisible(int visible) {
        initToolbar();
        mTitleTxt.setVisibility(visible);
    }

    /**
     * 设置toolbar是否显示
     *
     * @param visible 是否显示
     */
    public void setToolBarVisible(int visible) {
        initToolbar();
        mToolbarLayout.setVisibility(visible);
    }

    /**
     * 设置返回键按钮的是否可见.
     */
    public void setBackArrowVisible(int visible) {
        initToolbar();
        mBackImg.setVisibility(visible);
    }

    /**
     * 设置返回键透明度
     *
     * @param alpha 透明度 0 - 255
     */
    public void setBackArrowAlpha(int alpha) {
        mBackImg.setAlpha(alpha / 255f);
    }

    /**
     * 设置返回键按钮的背景图片.
     */
    public void setBackArrowImageResource(int res) {
        initToolbar();
        mBackImg.setImageResource(res);
        mBackImg.setVisibility(View.VISIBLE);
    }

    /**
     * 设置菜单是否可见.
     */
    public void setMenuImgVisible(int visible) {
        initToolbar();
        mMenuImg.setVisibility(visible);
    }

    /**
     * 设置菜单按钮的背景图片.
     */
    public void setMenuImageResource(int res) {
        initToolbar();
        mMenuImg.setImageResource(res);
        mMenuImg.setVisibility(View.VISIBLE);
    }

    /**
     * 设置菜单键透明度
     *
     * @param alpha 透明度 0 - 255
     */
    public void setMenuImageAlpha(int alpha) {
        mMenuImg.setAlpha(alpha / 255f);
    }

    /**
     * 设置toolbar右侧txt文字
     *
     * @param menu txt文字
     */
    public void setMenuTxt(String menu) {
        initToolbar();
        mRightTxt.setVisibility(View.VISIBLE);
        mRightTxt.setText(menu);
    }

    /**
     * 设置toolbar右侧txt文字
     *
     * @param id txt文字
     */
    public void setMenuTxt(int id) {
        initToolbar();
        mRightTxt.setVisibility(View.VISIBLE);
        mRightTxt.setText(getString(id));
    }

    /**
     * 设置toolbar右侧txt文字是否可见
     */
    public void setMenuTxtVisible(boolean visible) {
        initToolbar();
        mRightTxt.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setMenuTxtColor(int id) {
        initToolbar();
        mRightTxt.setTextColor(getResources().getColor(id));
    }

    public void setMenuTxtSize(int size) {
        initToolbar();
        //这个很重要，根据TextView的setRawTextSize方法源代码获得
        mRightTxt.getPaint().setTextSize(size);
        mRightTxt.invalidate();
    }

    /**
     * toolbar返回键点击处理
     */
    public void onBackImgClick() {
        onBackPressed();
    }

    /**
     * menu菜单键点击处理
     */
    public void onMenuImgClick() {
    }

    public void onMenuTxtClick() {
    }

    /**
     * 设置屏幕适配以高度为基准
     */
    public void setHeightDensity() {
        if (needSetOrientation) {
            DensityUtil.getInstance().setOrientation(this, DensityUtil.HEIGHT);
        }
    }

    public void setNeedSetOrientation(boolean needSetOrientation) {
        this.needSetOrientation = needSetOrientation;
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        if (mToolbarLayout == null) {
            mToolbarLayout = findViewById(R.id.common_toolbar_layout);
            mBackImg = findViewById(R.id.common_toolbar_back_img);
            mMenuImg = findViewById(R.id.common_toolbar_menu_img);
            mTitleTxt = findViewById(R.id.common_toolbar_center_txt);
            mRightTxt = findViewById(R.id.common_toolbar_menu_txt);
            mBackImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackImgClick();
                }
            });
            mMenuImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMenuImgClick();
                }
            });
            mRightTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMenuTxtClick();
                }
            });
            if (DensityUtil.HEIGHT.equals(DensityUtil.getInstance().getCurrentOrientation())) {
                ViewGroup.LayoutParams layoutParams = mToolbarLayout.getLayoutParams();
                // 42dp 为 toolbar 默认高度
                layoutParams.height = (int) (42 * DensityUtil.getInstance().getWidthDensity() +
                        0.5f);
                mToolbarLayout.setLayoutParams(layoutParams);
            }

        }
    }
}
