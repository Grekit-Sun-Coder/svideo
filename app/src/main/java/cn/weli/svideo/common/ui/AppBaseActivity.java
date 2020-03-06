package cn.weli.svideo.common.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.inputmethod.InputMethodManager;

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.weli.analytics.ScreenAutoTracker;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.ui.activity.BaseActivity;
import cn.weli.svideo.baselib.utils.CleanLeakUtils;
import cn.weli.svideo.common.helper.FragmentFactory;
import cn.weli.svideo.common.helper.ProtocolHelper;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.main.ui.SplashActivity;
import cn.weli.svideo.module.mine.ui.LoginActivity;

/**
 * App Activity基类
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-04
 * @see AppBaseActivity
 * @since [1.0.0]
 */
public abstract class AppBaseActivity<T extends IPresenter, K> extends BaseActivity<T, K> implements ScreenAutoTracker {

    protected String mFragmentOpenState = FragmentFactory.FLAG_FRAGMENT_NONE;
    private int mContentLayoutID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        CleanLeakUtils.fixInputMethodManagerLeak(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置该activity中需要替换成fragment的ID.
     */
    public void setContentLayoutID(int mContentLayoutID) {
        this.mContentLayoutID = mContentLayoutID;
    }

    /**
     * 打开fragment.
     *
     * @param tag 对应的fragment在FragmentFactory里的标签字符串值
     */
    public void openFragment(String tag) {
        if (mFragmentOpenState.equals(tag)) {
            return;
        }
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            fragment = FragmentFactory.getFragment(tag);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(mContentLayoutID, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
        mFragmentOpenState = tag;
    }

    /**
     * 切换fragment.
     *
     * @param from 当前fragment的标识
     * @param to   需要切换的fragment的标识
     */
    public void switchFragment(String from, String to) {
        if (mFragmentOpenState.equals(to)) {
            return;
        }
        Fragment fromFg = getSupportFragmentManager().findFragmentByTag(from);
        if (fromFg == null) {
            fromFg = FragmentFactory.getFragment(from);
        }
        Fragment toFg = getSupportFragmentManager().findFragmentByTag(to);
        if (toFg == null) {
            toFg = FragmentFactory.getFragment(to);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!toFg.isAdded()) {
            fragmentTransaction.hide(fromFg).add(mContentLayoutID, toFg, to);
        } else {
            fragmentTransaction.hide(fromFg).show(toFg);
        }
        fragmentTransaction.commitAllowingStateLoss();
        mFragmentOpenState = to;
    }

    /**
     * 隐藏fragment.
     *
     * @param hideFragment 需要隐藏的fragment
     */
    public void hideFragment(String hideFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment hideFg = getSupportFragmentManager().findFragmentByTag(hideFragment);
        if (hideFg != null) {
            fragmentTransaction.hide(hideFg).commitAllowingStateLoss();
        }
    }

    /**
     * 设置当前页面StatusBar颜色
     *
     * @param color 颜色
     */
    public void setStatusBarColor(int color) {
        StatusBarHelper.setStatusBarColor(this, color, false);
    }

    /**
     * 设置当前页面StatusBar颜色
     *
     * @param color 颜色
     */
    public void setStatusBarColorLight(int color) {
        StatusBarHelper.setStatusBarColor(this, color, true);
    }

    /**
     * 提示业务接口错误
     */
    public void showErrorToast(String code, String desc) {
    }

    /**
     * 提示业务接口错误
     */
    public void showErrorToast(int code, String desc) {
    }

    /**
     * 使用广播+singleTask的模式，退出应用的所有activity
     */
    public void exitApp() {
        if (!isFinishing()) {
            Intent mainIntent = new Intent();
            mainIntent.setClass(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (!(this instanceof MainActivity)) {
                startActivity(mainIntent);
            } else {
                finishActivity();
            }
            //退出登录，因此需要清除所有的消息
            NotificationManager nm = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.cancelAll();
            }
        }
    }

    @Override
    public void startProtocol(String url) {
        ProtocolHelper.handleProtocolEvent(this, url);
    }

    @Override
    public void startProtocol(String url, Map<String, Object> extraMap) {
        ProtocolHelper.handleProtocolEvent(this, url, extraMap);
    }

    /**
     * 跳转登录
     */
    public void startToLogin() {
        if (!isFinishing()) {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSystemKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 通过Fragment当前的position来获取它的标识
     *
     * @param viewId viewpager的id
     * @param id     position位置
     * @return 该Fragment的标识
     */
    protected String makeFragmentTag(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    @Override
    public String getScreenUrl() {
        return getComponentName().getClassName();
    }

    /**
     * 需注意，若activity实现了ScreenAutoTracker接口，
     * 则getTrackProperties必须重写不为null，否则PAGE_VIEW_START事件不会统计。
     * @return
     * @throws JSONException
     */
    @Override
    public JSONObject getTrackProperties() throws JSONException {
        return null;
    }
}
