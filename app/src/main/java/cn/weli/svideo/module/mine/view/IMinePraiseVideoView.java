package cn.weli.svideo.module.mine.view;

import java.util.List;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.video.model.bean.VideoBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public interface IMinePraiseVideoView extends IBaseView {

    /**
     * 停止加载更多动画
     */
    void finishLoadMore();

    /**
     * 空页面
     */
    void showEmptyView();

    /**
     * 没呀更多
     */
    void handleNoLoadMoreData();

    /**
     * 刷新
     *
     * @param list 列表
     */
    void handleVideoListRefresh(List<VideoBean> list);

    /**
     * 加载更多
     *
     * @param list 列表
     */
    void handleVideoListAppend(List<VideoBean> list);

    /**
     * 清除列表数据
     */
    void clearList();

    /**
     * 刷新列表
     */
    void notifyCurrentList();
}
