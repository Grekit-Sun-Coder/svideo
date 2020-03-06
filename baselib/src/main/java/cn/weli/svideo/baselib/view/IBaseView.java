package cn.weli.svideo.baselib.view;

import android.content.Intent;

import java.util.Map;

/**
 * 基类View接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/12
 * @since [1.0.0]
 */
public interface IBaseView {

    /**
     * 弹出toast.
     *
     * @param toastInfo toast内容
     */
    void showToast(String toastInfo);

    /**
     * 弹出toast.
     *
     * @param resId toast资源内容
     */
    void showToast(int resId);

    /**
     * 延迟显示加载视图.
     */
    void showLoadView();

    /**
     * 延迟显示加载视图.
     */
    void showLoadView(long delay);
    /**
     * 取消加载视图.
     */
    void finishLoadView();

    /**
     * 关闭当前页面
     */
    void finishActivity();

    /**
     * 跳转
     */
    void startActivity(Intent intent);

    /**
     * 跳转并有返回
     */
    void startActivityForResult(Intent intent, int requestCode);

    /**
     * 提示无网络
     */
    void showNetworkUnAvailable();

    /**
     * 提示网络错误
     */
    void showNetworkError();

    /**
     * 处理私有协议
     *
     * @param url 协议
     */
    void startProtocol(String url);

    /**
     * 处理私有协议
     *
     * @param url 协议
     * @param extraMap 额外参数
     */
    void startProtocol(String url, Map<String, Object> extraMap);

    /**
     * 跳转登录
     */
    void startToLogin();
}
