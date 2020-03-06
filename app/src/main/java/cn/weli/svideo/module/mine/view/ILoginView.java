package cn.weli.svideo.module.mine.view;

import cn.weli.svideo.baselib.view.IBaseView;

/**
 * 登陆页 V层接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see cn.weli.svideo.module.mine.ui.LoginActivity
 * @since [1.0.0]
 */
public interface ILoginView extends IBaseView {

    float FLAG_DISENABLE_ALPHA = 0.3f;

    int FLAG_FINISH_DELAY = 500;

    /**
     * 设置验证码标题是否高亮
     *
     * @param isEmpty 验证码是否为空
     */
    void setLoginCodeTitleStatus(boolean isEmpty);

    /**
     * 设置手机号标题是否高亮
     *
     * @param isEmpty 手机号是否为空
     */
    void setLoginPhoneTitleStatus(boolean isEmpty);

    /**
     * 设置登录按钮状态
     *
     * @param enable 是否可点击
     */
    void setLoginBtnStatus(boolean enable);

    /**
     * 提示先同意协议
     */
    void showAgreeArgueTip();

    /**
     * 提示输入正确的手机号
     */
    void showPhoneNumberErrorTip();

    /**
     * 获取验证码成功
     */
    void handleGetPhoneCodeSuccess();

    /**
     * 处理登录成功
     */
    void handleLoginSuccess(boolean jump);

    /**
     * 首次登录如果有金币奖励，则跳转任务
     *
     * @param gold 金币数量
     */
    void handleLoginStart2TaskPage(long gold);
}
