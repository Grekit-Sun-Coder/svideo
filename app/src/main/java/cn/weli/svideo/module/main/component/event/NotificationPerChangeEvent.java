package cn.weli.svideo.module.main.component.event;

/**
 * 通知权限变更事件
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2020-02-13
 * @see [class/method]
 * @since [1.0.0]
 */
public class NotificationPerChangeEvent {

    public boolean enable;

    public NotificationPerChangeEvent(boolean enable) {
        this.enable = enable;
    }
}
