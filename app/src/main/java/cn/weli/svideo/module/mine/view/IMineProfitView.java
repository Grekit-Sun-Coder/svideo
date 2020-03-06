package cn.weli.svideo.module.mine.view;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.task.model.bean.ProfitBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public interface IMineProfitView extends IBaseView {

    String DEFAULT_COIN = "0";
    String DEFAULT_CASH = "0.00";

    String EXTRA_PROFIT_TYPE = "tab";

    /**
     * 金币
     */
    int PAGE_COIN = 0;
    /**
     * 现金
     */
    int PAGE_CASH = 1;

    /**
     * tab名称 coin金币明细，cash现金明细
     */
    String TAB_COIN = "coin";
    String TAB_CASH = "cash";

    /**
     * 展示金额金币信息
     *
     * @param bean 信息
     */
    void showUserProfitInfo(ProfitBean bean);
}
