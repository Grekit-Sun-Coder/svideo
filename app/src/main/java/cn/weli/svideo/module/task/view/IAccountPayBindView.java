package cn.weli.svideo.module.task.view;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.task.model.bean.WithdrawAccountBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public interface IAccountPayBindView extends IBaseView {

    String EXTRA_BIND_INFO = "withdraw_account";

    String EXTRA_BIND_TYPE = "type";

    float FLAG_DISENABLE_ALPHA = 0.3f;

    /**
     * 设置完成按钮是否可点击
     *
     * @param enable 是否可点击
     */
    void setCompleteTxtEnable(boolean enable);

    /**
     * 设置提现信息以及绑定
     *
     * @param bean 提现信息
     */
    void setWithdrawInfo(WithdrawAccountBean bean, int type);

    /**
     * 处理绑定成功
     *
     * @param bean 提现信息
     */
    void handleBindSuccess(WithdrawAccountBean bean);

    /**
     * 设置当前需要输入类型
     *
     * @param currentType 类型
     */
    void setWithdrawInfoView(int currentType);

    /**
     * 展示微信绑定信息
     *
     * @param nickname 微信信息
     */
    void showWxBindView(String nickname);

    /**
     * 开始微信绑定
     */
    void startWxBind();

    /**
     * 设置完成按钮状态
     *
     * @param hasSet 是否设置
     */
    void setCompleteStatus(boolean hasSet);
}
