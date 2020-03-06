package cn.weli.svideo.module.task.model.bean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class CoinOpenBean {

    public AdConfigBean ad_config;

    public int coin;

    public String task_key;

    public PreviousTask previous_task;

    public static class PreviousTask {

        public String button;

        public String desc;

        public String name;

        public String target_url;

        public String task_key;

        public AdConfigBean ad_config;
    }
}
