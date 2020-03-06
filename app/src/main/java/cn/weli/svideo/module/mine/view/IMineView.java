package cn.weli.svideo.module.mine.view;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;

/**
 * 我的V层接口
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see cn.weli.svideo.module.mine.ui.MinePageFragment
 * @since [1.0.0]
 */
public interface IMineView extends IBaseView {

    /**
     * 透明度值
     */
    float FLAG_ALPHA_SIZE = 255f;

    /**
     * 我喜欢的视频
     */
    int PAGE_PRAISE_VIDEO = 0;

    /**
     * 展示
     */
    void handleFragmentShow();

    /**
     * 隐藏
     */
    void handleFragmentHide();

    /**
     * 展示用户信息
     *
     * @param bean 用户信息
     */
    void showUserInfo(UserInfoBean bean);


    /**
     * 设置小红点
     *
     * @param show 是否展示
     */
    void setRedPointShow(boolean show);

    /**
     * 是否显示黑名单标注
     * @param show
     */
    void setBlacklistTxt(boolean show);

    /**
     * 展示黑名单tip
     * @param tip
     * @param isFromServer
     */
    void setBlacklistTip(String tip, boolean isFromServer);
}
