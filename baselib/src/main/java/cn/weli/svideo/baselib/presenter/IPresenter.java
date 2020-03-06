package cn.weli.svideo.baselib.presenter;

/**
 * 逻辑事务层的接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @since [1.0.0]
 */
public interface IPresenter {

    /**
     * 清空相关数据，关闭网络请求等
     */
    void clear();
}
