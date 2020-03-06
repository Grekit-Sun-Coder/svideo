package cn.weli.svideo.common.ui;

import android.content.Intent;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import java.util.Map;

import cn.etouch.statusbar.StatusBarCompat;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.ui.fragment.BaseFragment;
import cn.weli.svideo.common.helper.ProtocolHelper;
import cn.weli.svideo.module.mine.ui.LoginActivity;

/**
 * App Fragment基类
 *
 * @author jianglei
 * @version [1.0.0]
 */

public abstract class AppBaseFragment<T extends IPresenter, K> extends BaseFragment<T, K> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 处理私有协议
     *
     * @param url 协议
     */
    @Override
    public void startProtocol(String url) {
        if (getActivity() != null && isAdded()) {
            ProtocolHelper.handleProtocolEvent(getActivity(), url);
        }
    }

    @Override
    public void startProtocol(String url, Map<String, Object> extraMap) {
        if (getActivity() != null && isAdded()) {
            ProtocolHelper.handleProtocolEvent(getActivity(), url, extraMap);
        }
    }

    /**
     * 设置当前页面StatusBar颜色
     *
     * @param color 颜色
     */
    public void setStatusBarColor(int color) {
        if (isAdded() && getActivity() != null) {
            StatusBarCompat.setStatusBarColor(getActivity(), color);
        }
    }

    /**
     * 设置当前页面StatusBar颜色
     *
     * @param color 颜色
     */
    public void setStatusBarColorLight(int color) {
        if (isAdded() && getActivity() != null) {
            StatusBarCompat.setStatusBarColor(getActivity(), color, true);
        }
    }

    /**
     * 跳转登录
     */
    public void startToLogin() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    public void showErrorToast(String code, String des) {
    }

    public void showErrorToast(int code, String des) {
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
}
