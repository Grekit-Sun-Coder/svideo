package cn.weli.svideo.module.task.model.bean;

import java.io.Serializable;

/**
 * Function description
 *
 * @author chenbixin
 * @version [v_1.0.6]
 * @date 2020-02-21
 * @see [class/method]
 * @since [v_1.0.6]
 */
public class DrawAdTaskBean implements Serializable {
    public DrawAdTaskBean(String taskKey, String token, int type, int position) {
        this.taskKey = taskKey;
        this.token = token;
        this.type = type;
        this.position = position;
    }

    public DrawAdTaskBean() {

    }

    public String taskKey;//做任务的key
    public String token;//任务唯一标识符
    public int type;//0:未知，1：跳转类，2：下载类
    public int position;//当前任务条目所在位置

    public int status;

}
