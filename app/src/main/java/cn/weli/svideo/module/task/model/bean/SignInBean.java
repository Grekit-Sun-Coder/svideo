package cn.weli.svideo.module.task.model.bean;

import java.util.List;

/**
 * 签到数据
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class SignInBean {

    public int days;
    public String today;
    public int today_check_in;
    public List<RecordsBean> records;
    public SignVideoTaskBean next_task;

    public boolean hasTodayCheckIn() {
        return today_check_in == 1;
    }

    public static class RecordsBean {

        public int check_in;
        public String date;
        public int reward;

        public boolean hasCheckIn() {
            return check_in == 1;
        }
    }

    public static class SignVideoTaskBean {
        public String task_key;
        public String name;
        public String desc;
        public String button;
        public String target_url;

        public AdConfigBean ad_config;
    }
}
