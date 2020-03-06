package cn.weli.svideo.module.task.component.event;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class TaskCoinStatusEvent {

    public static final int STATUS_TIMEING = 0x100;
    public static final int STATUS_CAN_OPEN = 0x101;
    public static final int STATUS_ERROR = 0x102;

    public int status;

    public String msg;

    public TaskCoinStatusEvent(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
