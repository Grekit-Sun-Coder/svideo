package cn.weli.svideo.module.main.model.bean;

/**
 * 推送
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-26
 * @see cn.weli.svideo.push.WlVideoIntentService
 * @since [1.0.0]
 */
public class PushBean {
    /**
     * 标题
     */
    public String a;
    /**
     * 推送描述
     */
    public String b;
    /**
     * 一些自定义事件及字段、跳转等
     */
    public C c;

    public static class C {
        /**
         * 跳转的链接url或scheme或参数值
         */
        public String b;
        /**
         * 消息的唯一标示字段(类似msgid)
         */
        public String e;
        /**
         * 展示图片icon的url
         */
        public String img;
        /**
         * 推送content_moudle字段，统计时将该字段取出原样放在统计报文中
         */
        public String c_m = "";
    }
}
