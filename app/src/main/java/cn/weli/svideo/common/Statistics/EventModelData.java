package cn.weli.svideo.common.Statistics;

/**
 * JXH
 * 2018/11/2
 */
public interface EventModelData {
    String START_NUM = "start_num";
    String START_TYPE = "start_type";
    String PRE_VERSION = "pre_version";
    String PRE_CHANNEL = "pre_channel";
    String PRE_VERSION_CODE = "pre_version_code";

    public interface EVENT {
        String APP_START = "app-start";
        String APP_UPGRADE = "app-upgrade";
    }

    public interface START_TYPE_VALUE {
        String APP_ICON = "app_icon";
        String APP_THIRD_PARTY = "app_third_party";
        String STATUSBAR_WEATHER = "statusbar_weather";
        String STATUSBAR_WEEK = "statusbar_week";
        String STATUSBAR_MONTH = "statusbar_month";
        String REMINDER_FESTIVAL = "reminder_festival";
        String REMINDER_UGC = "reminder_ugc";
        String PUSH_WEATHER = "push_weather";
        String PUSH_POST = "push_post";
        String PLUGIN_CALENDAR = "plugin_calendar";
        String PLUGIN_WEATHER = "plugin_weather";
        String PLUGIN_UGC = "plugin_ugc";
    }
}
