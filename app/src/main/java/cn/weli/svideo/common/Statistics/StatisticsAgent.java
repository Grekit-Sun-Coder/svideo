package cn.weli.svideo.common.Statistics;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cn.etouch.logger.Logger;
import cn.weli.analytics.EventName;
import cn.weli.svideo.BuildConfig;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-28
 * @see [class/method]
 * @since [1.0.0]
 */
public class StatisticsAgent {

    public static void pageView(Activity context, long cid, int md) {
        if (context != null) {
            StatisticsUtils.pageViewStart(context, EventName.PAGE_VIEW_START.getEventName(), cid, md, 0, "", "");
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + EventName.PAGE_VIEW_START.getEventName() + " cid=" + cid + " md=" + md + " args=" + "" + " pos=" + "");
            }
        }
    }

    public static void pageView(Activity context, long cid, int md, String pos, String args) {
        if (context != null) {
            StatisticsUtils.pageViewStart(context, EventName.PAGE_VIEW_START.getEventName(), cid, md, 0, pos, args);
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + EventName.PAGE_VIEW_START.getEventName() + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }
    public static void pageEnd(Activity context, long cid, int md) {
        if (context != null) {
            StatisticsUtils.pageViewEnd(context, EventName.PAGE_VIEW_END.getEventName(), cid, md, 0, "", "");
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + EventName.PAGE_VIEW_END.getEventName() + " cid=" + cid + " md=" + md + " args=" + "" + " pos=" + "");
            }
        }
    }

    public static void pageEnd(Activity context, long cid, int md, String pos, String args) {
        if (context != null) {
            StatisticsUtils.pageViewEnd(context, EventName.PAGE_VIEW_END.getEventName(), cid, md, 0, pos, args);
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + EventName.PAGE_VIEW_END.getEventName() + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void enter(Activity context, long cid, int md) {
        if (context != null) {
            StatisticsUtils.eventTongji(context, "page_view", cid, md, 0, "", "");
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "page_view" + " cid=" + cid + " md=" + md + " args=" + "" + " pos=" + "");
            }
        }
    }

    public static void enter(Activity context, long cid, int md, String pos, String args) {
        if (context != null) {
            StatisticsUtils.eventTongji(context, "page_view", cid, md, 0, pos, args);
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "page_view" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void enter(Activity context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, "page_view", cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, "page_view", cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "page_view" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void exit(Activity context, long cid, int md, String pos, String args) {
        if (context != null) {
            StatisticsUtils.eventTongji(context, "exit", cid, md, 0, pos, args);
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "exit" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void exit(Activity context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, "exit", cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, "exit", cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "exit" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void share(Activity context, long cid, int md, String pos, String args) {
        if (context != null) {
            StatisticsUtils.eventTongji(context, "share", cid, md, 0, pos, args);
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "share" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void share(Activity context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, "share", cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, "share", cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "share" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void view(Context context, long cid, int md) {
        view(context, cid, md, "", "");
    }

    public static void view(Context context, long cid, int md, String pos, String args) {
        if (context != null) {
            StatisticsUtils.eventTongji(context, EventName.VIEW.getEventName(), cid, md, 0, pos, args);
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + EventName.VIEW.getEventName() + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void view(Context context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, EventName.VIEW.getEventName(), cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, EventName.VIEW.getEventName(), cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }

            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + EventName.VIEW.getEventName() + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void click(Context context, long cid, int md) {
        click(context, cid, md, "", "", "");
    }

    public static void click(Context context, long cid, int md, String pos, String args, String cm) {
        if (context != null) {
            StatisticsUtils.eventTongji(context, EventName.CLICK.getEventName(), cid, md, 0, pos, args, cm);
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + EventName.CLICK.getEventName() + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void click(Context context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, EventName.CLICK.getEventName(), cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, EventName.CLICK.getEventName(), cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + EventName.CLICK.getEventName() + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void like(Context context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, "like", cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, "like", cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "like" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void pause(Context context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, "pause", cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, "pause", cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "pause" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void play(Context context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, "play", cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, "play", cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "play" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void watching(Context context, long cid, int md, String pos, String args, Object cmObj) {
        if (context != null) {
            if (cmObj == null) {
                StatisticsUtils.eventTongji(context, "watching", cid, md, 0, pos, args);
            } else {
                try {
                    StatisticsUtils.eventTongji(context, "watching", cid, md, 0, pos, args, new JSONObject(cmObj + "") + "");
                } catch (Exception e) {
                    Logger.e(e.getMessage());
                }
            }
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + "watching" + " cid=" + cid + " md=" + md + " args=" + args + " pos=" + pos);
            }
        }
    }

    public static void push(Context context, String eventName, long cid, int md) {
        if (context != null) {
            StatisticsUtils.eventTongji(context, eventName, cid, md, 0, "", "");
            if (BuildConfig.LOG_DEBUG) {
                Log.i("statistics", "eventName=" + eventName + " cid=" + cid + " md=" + md);
            }
        }
    }

    public static void appStart(Context context, String start_type, int startNum) {
        if (context == null) {
            return;
        }
        JSONObject args = new JSONObject();
        try {
            args.put(EventModelData.START_TYPE, start_type);
            args.put(EventModelData.START_NUM, startNum);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        StatisticsUtils.eventTongji(context, EventModelData.EVENT.APP_START, "", "", 0, "", args.toString(), "");
    }
}
