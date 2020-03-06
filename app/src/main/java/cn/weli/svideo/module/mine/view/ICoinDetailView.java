package cn.weli.svideo.module.mine.view;

import java.util.List;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.task.model.bean.FlowDetailBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public interface ICoinDetailView extends IBaseView {

    /**
     * 错误页面
     */
    void showErrorView();

    /**
     * 空页面
     */
    void showEmptyView();

    /**
     * 没有更多
     */
    void handleNoLoadMoreData();

    /**
     * 刷新列表
     *
     * @param list 列表数据
     */
    void handleListRefresh(List<FlowDetailBean> list);

    /**
     * 加载更多
     *
     * @param list 列表数据
     */
    void handleListAppend(List<FlowDetailBean> list);

    /**
     * 停止刷新加载更多
     */
    void finishRefreshLoadMore();
}
