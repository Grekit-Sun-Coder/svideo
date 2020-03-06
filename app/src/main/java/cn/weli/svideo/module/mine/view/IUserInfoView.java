package cn.weli.svideo.module.mine.view;

import cn.weli.svideo.baselib.view.IBaseView;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-23
 * @see [class/method]
 * @since [1.0.0]
 */
public interface IUserInfoView extends IBaseView {
    /**
     * 展示用户信息
     *
     * @param bean 用户信息
     */
    void showUserInfoBean(UserInfoBean bean);

    /**
     * 弹出性别选择
     *
     * @param sex 当前性别
     */
    void showUserSexSelectDialog(String sex);

    /**
     * 弹出昵称编辑
     *
     * @param nickname 昵称
     */
    void showUserNickDialog(String nickname);

    /**
     * 修改昵称成功
     *
     * @param nickname 昵称
     */
    void handleUserNickChangeSuccess(String nickname);

    /**
     * 修改性别成功
     *
     * @param sex 性别
     */
    void handleUserSexChangeSuccess(String sex);

    /**
     * 上传头像成功
     *
     * @param url 地址
     */
    void handleUserHeadImgUploadSuccess(String url);
}
