package cn.weli.svideo.module.task.component.helper;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;

import cn.weli.svideo.advert.kuaima.DownloadMarketDBManager;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.task.component.helper.listener.DrawAdRewardListener;
import cn.weli.svideo.module.task.model.TaskModel;
import cn.weli.svideo.module.task.model.bean.DrawAdTaskBean;
import cn.weli.svideo.module.task.model.bean.VideoTaskBean;

/**
 * 查看领取**金币的任务帮助类
 *
 * @author chenbixin
 * @version [v_1.0.6]
 * @date 2020-02-21
 * @see [class/method]
 * @since [v_1.0.6]
 */
public class DrawAdRewardHelper {
    private static DrawAdRewardHelper mDrawAdRewardHelper;
    private static boolean sIsAdEnable = false;//任务是否开放
    private static final int TASK_OBTIN = 0x0001;
    private static final int TASK_SUBMIT = 0x0002;
    private ArrayList<DrawAdTaskBean> taskList;
    public static final int STATUS_DOWNLOAD_START = 0;
    public static final int STATUS_DOWNLOADING = 1;
    public static final int STATUS_DOWNLOAD_SUCCESS = 2;
    private TaskModel mTaskModel;
    private DrawAdRewardListener mDrawAdRewardListener;
    private Context mContext;

    private DrawAdRewardHelper() {
        taskList = new ArrayList<DrawAdTaskBean>();
    }

    public static DrawAdRewardHelper getInstance() {
        if (mDrawAdRewardHelper == null) {
            mDrawAdRewardHelper = new DrawAdRewardHelper();
        }
        return mDrawAdRewardHelper;
    }

    public void setDrawAdRewardListener(DrawAdRewardListener mDrawAdRewardListener) {
        this.mDrawAdRewardListener = mDrawAdRewardListener;
    }


    public static void setIsADEnable(boolean isADEnable) {
        sIsAdEnable = isADEnable;
    }


    /**
     * 插入一个条目
     **/
    public void addTaskItem(DrawAdTaskBean drawAdTaskBean) {
        if (drawAdTaskBean == null || !sIsAdEnable) {
            return;
        }
        boolean isFound = false;
        for (DrawAdTaskBean adTaskBean : taskList) {
            if (StringUtil.equals(adTaskBean.token, drawAdTaskBean.token) || adTaskBean.token.contains(drawAdTaskBean.token)) {
                isFound = true;
            }
        }
        if (!isFound) {
            taskList.add(drawAdTaskBean);
        }
    }

    /**
     * 更新一个条目
     **/
    public void updateTaskItem(String oldToken, String newToken) {
        if (!sIsAdEnable) {
            return;
        }
        for (DrawAdTaskBean adTaskBean : taskList) {
            if (StringUtil.equals(adTaskBean.token, oldToken)) {
                adTaskBean.token = newToken;
            }
        }
    }

    /**
     * 更新一条任务的状态
     *
     * @param status 状态0：未开始，1下载中，2已完成
     * @param token  根据此标识符来确定更新的是哪个条目
     **/
    public synchronized void upDateTaskStatus(String token, int status) {
        if (!sIsAdEnable) {
            return;
        }
        DrawAdTaskBean drawAdTaskBean = null;
        for (DrawAdTaskBean adTaskBean : taskList) {
            if (StringUtil.equals(adTaskBean.token, token)) {
                drawAdTaskBean = adTaskBean;
            }
        }
        if (drawAdTaskBean != null && drawAdTaskBean.status != 2) {
            drawAdTaskBean.status = status;
            if (mDrawAdRewardListener != null) {
                mDrawAdRewardListener.onAdDownLoadStatusChange(status, drawAdTaskBean.position);
            }
        }
    }

    /**
     * @param token 任务唯一标识符
     *              开始一个任务
     *              根据唯一标识符拿到这个任务的所有信息并开始做任务
     **/
    public synchronized void doTask(String token, Context context) {
        if (!sIsAdEnable || context == null) {
            return;
        }
        this.mContext = context;
        Runnable runnable = (() -> {
            boolean isFound = DownloadMarketDBManager.open(context).queryIfExist(token);
            if (!isFound) {
                DrawAdTaskBean drawAdTaskBean = null;
                for (DrawAdTaskBean adTaskBean : taskList) {
                    if (StringUtil.equals(adTaskBean.token, token)) {
                        drawAdTaskBean = adTaskBean;
                    }
                }
                if (mTaskModel == null) {
                    mTaskModel = new TaskModel();
                }
                if (drawAdTaskBean != null) {
                    Message msg = new Message();
                    msg.arg1 = TASK_OBTIN;
                    Bundle d = new Bundle();
                    d.putSerializable("DrawAdTaskBean", drawAdTaskBean);
                    msg.setData(d);
                    handler.sendMessage(msg);
                }
            } else {
                if (mDrawAdRewardListener != null) {
                    mDrawAdRewardListener.onAdRewardEd();
                }
            }
        });
        runnable.run();
    }

    /**
     * 当activity销毁的时候调用
     **/
    public void clear() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mTaskModel != null) {
            mTaskModel.cancelObtainRequest();
            mTaskModel.cancelSubmitRequest();
        }
        if (mDrawAdRewardHelper != null) {
            mDrawAdRewardHelper.setDrawAdRewardListener(null);
        }
        mDrawAdRewardHelper = null;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case TASK_OBTIN://开始任务
                    Bundle obtinBund = msg.getData();
                    DrawAdTaskBean obtinDrawAdTaskBean = (DrawAdTaskBean) obtinBund.getSerializable("DrawAdTaskBean");
                    if (obtinDrawAdTaskBean == null) {
                        return;
                    }
                    mTaskModel.getVideoTask(obtinDrawAdTaskBean, new SimpleHttpSubscriber<VideoTaskBean>() {
                        @Override
                        public void onResponseSuccess(VideoTaskBean bean) {
                            if (!StringUtil.isNull(bean.task_id)) {
                                Message msg = new Message();
                                msg.arg1 = TASK_SUBMIT;
                                Bundle d = new Bundle();
                                d.putSerializable("DrawAdTaskBean", obtinDrawAdTaskBean);
                                d.putString("taskID", bean.task_id);
                                d.putLong("timeL", bean.task_timestamp);
                                msg.setData(d);
                                handler.sendMessage(msg);
                            } else {
                                if (mDrawAdRewardListener != null) {
                                    mDrawAdRewardListener.onAdRewardFail();
                                }
                            }
                        }
                    });
                    break;
                case TASK_SUBMIT://提交任务
                    Bundle submitBund = msg.getData();
                    String taskId = submitBund.getString("taskID");
                    long timeL = submitBund.getLong("timeL");
                    DrawAdTaskBean submitDrawAdTaskBean = (DrawAdTaskBean) submitBund.getSerializable("DrawAdTaskBean");
                    if (submitDrawAdTaskBean == null) {
                        return;
                    }
                    mTaskModel.submitVideoTask(submitDrawAdTaskBean, taskId, timeL, new SimpleHttpSubscriber<VideoTaskBean>() {
                        @Override
                        public void onResponseSuccess(VideoTaskBean bean) {
                            if (bean != null) {//发奖励成功之后插入数据库
                                if (mContext != null) {
                                    DownloadMarketDBManager.open(mContext).inster(submitDrawAdTaskBean.type, submitDrawAdTaskBean.token);
                                }
                                if (mDrawAdRewardListener != null) {
                                    mDrawAdRewardListener.onAdReward(bean.coin);
                                }
                            }
                        }
                    });
                    break;
            }
        }
    };
}

