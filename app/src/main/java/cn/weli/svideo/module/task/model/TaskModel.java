package cn.weli.svideo.module.task.model;

import java.util.ArrayList;
import java.util.HashMap;

import cn.etouch.cache.CacheManager;
import cn.etouch.retrofit.RetrofitManager;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.etouch.retrofit.transfer.HttpRxFun;
import cn.etouch.retrofit.transfer.RxComposer;
import cn.weli.svideo.baselib.utils.SharePrefUtil;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.constant.CacheConstant;
import cn.weli.svideo.common.constant.SharePrefConstant;
import cn.weli.svideo.common.helper.ApiHelper;
import cn.weli.svideo.module.task.model.api.TaskService;
import cn.weli.svideo.module.task.model.bean.CoinOpenBean;
import cn.weli.svideo.module.task.model.bean.CoinOpenStatusBean;
import cn.weli.svideo.module.task.model.bean.DrawAdTaskBean;
import cn.weli.svideo.module.task.model.bean.SignInBean;
import cn.weli.svideo.module.task.model.bean.TaskBean;
import cn.weli.svideo.module.task.model.bean.VideoTaskBean;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

/**
 * 任务相关model
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see cn.weli.svideo.module.task.presenter.TaskPagePresenter
 * @since [1.0.0]
 */
public class TaskModel {

    /**
     * 观看视频
     */
    public static final String KEY_WATCH_VIDEO = "watch_video";

    private DisposableObserver mObtainObserver;

    private DisposableObserver mSubmitObserver;

    private DisposableObserver mTaskListObserver;

    private DisposableObserver mSignInObserver;

    private DisposableObserver mOpenCoinObserver;

    private DisposableObserver mOpenActObserver;

    private TaskService mTaskService;

    public TaskModel() {
        mTaskService = RetrofitManager.getInstance().getDefaultRetrofit().create(TaskService.class);
    }

    /**
     * 获取任务
     *
     * @param subscriber 回调
     */
    public void getVideoTask(String key, HttpSubscriber<VideoTaskBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mObtainObserver = mTaskService
                .getVideoTask(key, map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 获取任务
     *
     * @param subscriber 回调
     */
    public void getVideoTask(DrawAdTaskBean drawAdTaskBean, HttpSubscriber<VideoTaskBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mObtainObserver = mTaskService
                .getVideoTask(drawAdTaskBean.taskKey, map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 签到
     *
     * @param subscriber 回调
     */
    public void signIn(HttpSubscriber<SignInBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mSignInObserver = mTaskService
                .signIn(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 获取提现列表
     *
     * @param subscriber 产品回调
     */
    public void getTaskList(HttpSubscriber<ArrayList<TaskBean>> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mTaskListObserver = mTaskService
                .getTaskList(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 提交任务
     *
     * @param subscriber 回调
     */
    public void submitVideoTask(DrawAdTaskBean drawAdTaskBean, String taskId, long time, HttpSubscriber<VideoTaskBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("task_id", taskId);
        map.put("task_timestamp", String.valueOf(time));
        ApiHelper.addCommonParams(map);
        mSubmitObserver = mTaskService
                .submitVideoTask(drawAdTaskBean.taskKey, map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 提交任务
     *
     * @param subscriber 回调
     */
    public void submitVideoTask(String key, String taskId, long time, HttpSubscriber<VideoTaskBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("task_id", taskId);
        map.put("task_timestamp", String.valueOf(time));
        ApiHelper.addCommonParams(map);
        mSubmitObserver = mTaskService
                .submitVideoTask(key, map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 查询宝箱状态
     *
     * @param subscriber 回调
     */
    public void getCoinOpenStatus(HttpSubscriber<CoinOpenStatusBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mOpenCoinObserver = mTaskService
                .queryCoinOpenStatus(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 开宝箱
     *
     * @param subscriber 回调
     */
    public void openCoin(HttpSubscriber<CoinOpenBean> subscriber) {
        HashMap<String, String> map = new HashMap<>();
        ApiHelper.addCommonParams(map);
        mOpenActObserver = mTaskService
                .openCoin(map)
                .flatMap(new HttpRxFun<>())
                .compose(RxComposer.applyIoSchedulers())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    /**
     * 保存任务列表
     *
     * @param list 列表
     */
    public void saveLastTaskList(ArrayList<TaskBean> list) {
        CacheManager.getInstance().saveSerializable(CacheConstant.CACHE_TASK_LIST, list);
    }

    public ArrayList<TaskBean> getLastTaskList() {
        return CacheManager.getInstance().loadSerializable(CacheConstant.CACHE_TASK_LIST);
    }

    /**
     * 保存上次关闭通知区域的日期
     *
     * @param date 日期
     */
    public void saveLastCloseNoPerLayout(String date) {
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_CLOSE_NO_PERMISSION, date);
    }

    /**
     * 获取上次关闭通知区域的日期
     *
     * @return date日期
     */
    public String getLastCloseNoPerLayout() {
        return SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_CLOSE_NO_PERMISSION, StringUtil.EMPTY_STR);
    }

    public void cancelObtainRequest() {
        if (mObtainObserver != null && !mObtainObserver.isDisposed()) {
            mObtainObserver.dispose();
        }
    }

    public void cancelSubmitRequest() {
        if (mSubmitObserver != null && !mSubmitObserver.isDisposed()) {
            mSubmitObserver.dispose();
        }
    }

    public void cancelTaskListRequest() {
        if (mTaskListObserver != null && !mTaskListObserver.isDisposed()) {
            mTaskListObserver.dispose();
        }
    }

    public void cancelSignInRequest() {
        if (mSignInObserver != null && !mSignInObserver.isDisposed()) {
            mSignInObserver.dispose();
        }
    }

    public void cancelOpenCoinRequest() {
        if (mOpenCoinObserver != null && !mOpenCoinObserver.isDisposed()) {
            mOpenCoinObserver.dispose();
        }
    }

    public void cancelOpenActRequest() {
        if (mOpenActObserver != null && !mOpenActObserver.isDisposed()) {
            mOpenActObserver.dispose();
        }
    }


    public void saveLastClipInviteCode(String text) {
        SharePrefUtil.saveInfoToPref(SharePrefConstant.PREF_INVITE_CODE, text);
    }

    public String getLastClipInviteCode() {
        return SharePrefUtil.getInfoFromPref(SharePrefConstant.PREF_INVITE_CODE, StringUtil.EMPTY_STR);
    }
}
