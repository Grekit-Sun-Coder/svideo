package cn.weli.svideo.common.Statistics;

import android.app.Activity;
import android.content.Context;

import org.json.JSONObject;

import cn.weli.analytics.AnalyticsDataAPI;
import cn.weli.svideo.BuildConfig;

/**
 * 埋点
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-28
 * @see [class/method]
 * @since [1.0.0]
 */
public class StatisticsUtils {

    public interface CID {

        int CID_1 = -1;
        int CID_2 = -2;
        int CID_3 = -3;
        int CID_4 = -4;
        int CID_5 = -5;
        int CID_9 = -9;
        int CID_101 = -101;
        int CID_102 = -102;
        int CID_103 = -103;
        int CID_201 = -201;
        int CID_2001 = -2001;
        int CID_301 = -301;
        int CID_302 = -302;
        int CID_303 = -303;
        int CID_3031 = -3031;
        int CID_30311 = -30311;
        int CID_30312 = -30312;
        int CID_30315 = -30315;
        int CID_303121 = -303121;
        int CID_303151 = -303151;
        int CID_3031211 = -3031211;
        int CID_3031511 = -3031511;
        int CID_30313 = -30313;
        int CID_30314 = -30314;
        int CID_401 = -401;
        int CID_402 = -402;
        int CID_403 = -403;
        int CID_404 = -404;
        int CID_501 = -501;
        int CID_5012 = -5012;
        int CID_601 = -601;
        int CID_6011 = -6011;
        int CID_602 = -602;
        int CID_603 = -603;
        int CID_7 = -7;
    }

    public interface MD {
        int MD_1 = 1;
        int MD_2 = 2;
        int MD_3 = 3;
        int MD_4 = 4;
        int MD_5 = 5;
        int MD_9 = 9;
        int MD_10 = 10;
    }

    public interface field {
        String content_id = "content_id";//cid
        String content_model = "content_model";//cm
        String module = "module";//md
        String position = "position";//pos
        String args = "args";//args
    }

    public interface EventName {
        String PUSH_REGISTER = "push_register";
        String PUSH_MSG_RECEIVE = "push_msg_receive";
        String PUSH_MSG_VIEW = "push_msg_view";
        String PUSH_MSG_CLICK = "push_msg_click";
    }

    /**
     * 某个位置的事件的统计，所有的所需字段都传入
     *
     * @param event_type
     * @param c_id
     * @param md
     * @param is_anchor
     * @param pos
     * @param args
     */
    public static void eventTongji(Context ctx, String event_type, long c_id, int md, int is_anchor, String pos, String args) {
        try {
            EventDataBean bean = new EventDataBean();
            bean.event_type = event_type;
            bean.content_id = c_id + "";
            bean.position = pos;
            bean.module = md + "";
            bean.args = args;
            if (BuildConfig.STATISTICS_DEBUG) {
                FloatViewManager.getInstance(ctx).addEvent(bean);
            }
            JSONObject p = new JSONObject();
            p.put(field.content_id, c_id);
            p.put(field.content_model, "");
            p.put(field.module, md + "");
            p.put(field.position, pos);
            p.put(field.args, args);
            AnalyticsDataAPI.sharedInstance(ctx).track(event_type, p);
            if (is_anchor == 1) {
                AnalyticsDataAPI.sharedInstance(ctx).flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 某个位置的事件的统计，所有的所需字段都传入
     *
     * @param event_type
     * @param c_id
     * @param md
     * @param is_anchor
     * @param pos
     * @param args
     * @param c_m        xujun add ，之前的方法漏传了一个 c_m
     */
    public static void eventTongji(Context ctx, String event_type, long c_id, int md, int is_anchor, String pos, String args, String c_m) {
        eventTongji(ctx, event_type, c_id + "", md + "", is_anchor, pos, args, c_m);
    }

    public static void eventTongji(Context ctx, String event_type, String c_id, String md, int is_anchor, String pos, String args, String c_m) {
        try {
            EventDataBean bean = new EventDataBean();
            bean.event_type = event_type;
            bean.content_id = c_id + "";
            bean.position = pos;
            bean.args = args;
            bean.module = md;
            bean.content_model = c_m;
            if (BuildConfig.STATISTICS_DEBUG) {
                FloatViewManager.getInstance(ctx).addEvent(bean);
            }
            JSONObject p = new JSONObject();
            p.put(field.content_id, c_id);
            p.put(field.content_model, c_m);
            p.put(field.module, md + "");
            p.put(field.position, pos);
            p.put(field.args, args);
            AnalyticsDataAPI.sharedInstance(ctx).track(event_type, p);
            if (is_anchor == 1) {
                AnalyticsDataAPI.sharedInstance(ctx).flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pageViewStart(Activity ctx, String event_type, long c_id, int md, int is_anchor, String pos, String args) {
        pageView(ctx, event_type, c_id, md, is_anchor, pos, args, true);
    }

    public static void pageViewEnd(Activity ctx, String event_type, long c_id, int md, int is_anchor, String pos, String args) {
        pageView(ctx, event_type, c_id, md, is_anchor, pos, args, false);
    }

    private static void pageView(Activity ctx, String event_type, long c_id, int md, int is_anchor, String pos, String args, boolean isStart) {
        try {
            EventDataBean bean = new EventDataBean();
            bean.event_type = event_type;
            bean.content_id = c_id + "";
            bean.position = pos;
            bean.module = md + "";
            bean.args = args;
            if (BuildConfig.STATISTICS_DEBUG) {
                FloatViewManager.getInstance(ctx).addEvent(bean);
            }
            JSONObject p = new JSONObject();
            p.put(field.content_id, c_id);
            p.put(field.content_model, "");
            p.put(field.module, md + "");
            p.put(field.position, pos);
            p.put(field.args, args);
            if (isStart) {
                // page_view_start
                AnalyticsDataAPI.sharedInstance(ctx).trackViewScreen(ctx, p);
            } else {
                // page_view_end
                AnalyticsDataAPI.sharedInstance(ctx).trackViewScreenEnd(ctx, p);
            }

            if (is_anchor == 1) {
                AnalyticsDataAPI.sharedInstance(ctx).flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
