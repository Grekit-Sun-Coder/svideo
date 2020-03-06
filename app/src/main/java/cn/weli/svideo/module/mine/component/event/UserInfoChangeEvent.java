package cn.weli.svideo.module.mine.component.event;

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
public class UserInfoChangeEvent {

    public UserInfoBean bean;

    public UserInfoChangeEvent(UserInfoBean bean) {
        this.bean = bean;
    }
}
