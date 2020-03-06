package cn.weli.svideo.module.task.view;

import java.util.List;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.task.model.bean.WithdrawAccountBean;
import cn.weli.svideo.module.task.model.bean.WithdrawProBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public interface IWithdrawView extends IBaseView {

    String EXTRA_PROFIT_INFO = "extra_profit_info";

    float FLAG_DISENABLE_ALPHA = 0.3f;

    int REQUEST_CODE_BIND = 0x100;

    /**
     * 提现类型，0-支付宝，1-微信
     */
    int TYPE_ALI = 0;
    int TYPE_WX = 1;

    /**
     * 提现类型
     */
    String TYPE_ALI_STR = "alipay";
    String TYPE_WX_STR = "wxpay";

    /**
     * 展示提现列表
     *
     * @param aliList 提现列表
     */
    void showWithdrawProList(List<WithdrawProBean> aliList, List<WithdrawProBean> axList);

    /**
     * 设置提现账号信息是否存在
     *
     * @param bean 提现账号信息
     */
    void setWithdrawAccount(WithdrawAccountBean bean);

    /**
     * 处理产品选中
     *
     * @param position 位置
     */
    void handleAliProductSelected(int position);

    /**
     * 处理产品选中
     *
     * @param position 位置
     */
    void handleWxProductSelected(int position);

    /**
     * 提示账号为空
     */
    void showAccountEmptyTip();

    /**
     * 提现成功
     */
    void handleWithdrawSuccess();

    /**
     * 设置当前选中产品
     *
     * @param position 位置
     */
    void setCurrentAliProductSelected(int position);

    /**
     * 设置当前选中产品
     *
     * @param position 位置
     */
    void setCurrentWxProductSelected(int position);

    /**
     * 展示支付宝
     */
    void showAliType(boolean selected);

    /**
     * 展示微信
     */
    void showWxType(boolean selected);
}
