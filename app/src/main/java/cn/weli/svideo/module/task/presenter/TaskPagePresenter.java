package cn.weli.svideo.module.task.presenter;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.etouch.logger.Logger;
import cn.etouch.retrofit.operation.HttpSubscriber;
import cn.weli.svideo.WlVideoAppInfo;
import cn.weli.svideo.baselib.presenter.IPresenter;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.baselib.utils.TimeUtil;
import cn.weli.svideo.common.constant.ProtocolConstants;
import cn.weli.svideo.common.http.HttpUtils;
import cn.weli.svideo.common.http.SimpleHttpSubscriber;
import cn.weli.svideo.module.task.component.event.TaskCoinStatusEvent;
import cn.weli.svideo.module.task.component.helper.TaskCoinOpenHelper;
import cn.weli.svideo.module.task.model.TaskModel;
import cn.weli.svideo.module.task.model.WithdrawModel;
import cn.weli.svideo.module.task.model.bean.CoinOpenBean;
import cn.weli.svideo.module.task.model.bean.ProfitBean;
import cn.weli.svideo.module.task.model.bean.SignInBean;
import cn.weli.svideo.module.task.model.bean.TaskBean;
import cn.weli.svideo.module.task.model.bean.VideoTaskBean;
import cn.weli.svideo.module.task.view.ITaskPageView;

/**
 * 视频首页 P层
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-12
 * @see cn.weli.svideo.module.task.ui.TaskPageFragment
 * @since [1.0.0]
 */
public class TaskPagePresenter implements IPresenter {

    private ITaskPageView mView;

    private WithdrawModel mWithdrawModel;

    private TaskModel mTaskModel;

    private List<TaskBean> mTaskList;

    private VideoTaskBean mVideoTaskBean;

    private ProfitBean mProfitBean;

    private SignInBean mSignInBean;

    private TaskBean mRewardVideoTaskBean;
    /**
     * 是否是开宝箱激励视频完成
     */
    public boolean isNeedRefreshCoinOpen;

    public TaskPagePresenter(ITaskPageView view) {
        mView = view;
        mWithdrawModel = new WithdrawModel();
        mTaskModel = new TaskModel();
    }

    /**
     * 初始化任务页
     *
     * @param noPerEnable 通知权限是否开启
     */
    public void initTaskPage(boolean noPerEnable) {
        if (!noPerEnable) {
            String currentDate = TimeUtil.milliseconds2String(System.currentTimeMillis(), TimeUtil.TIME_FORMAT_DAY);
            if (!StringUtil.equals(currentDate, mTaskModel.getLastCloseNoPerLayout())) {
                mView.setNotificationPerStatus(false);
            }
        }
        mProfitBean = mWithdrawModel.getUserProfitInfo();
        if (mProfitBean != null) {
            mView.showUserProfitInfo(mProfitBean);
        }
        mTaskList = mTaskModel.getLastTaskList();
        if (mTaskList != null && !mTaskList.isEmpty()) {
            mView.showTaskList(mTaskList);
        }
        requestUserProfit();
        getTaskList();
    }

    /**
     * 处理fragment页面展示
     *
     * @param hide 是否对用户可见
     */
    public void handleUserVisible(boolean hide) {
        if (hide) {
            mView.handleFragmentHide();
        } else {
            mView.handleFragmentShow();
        }
    }

    /**
     * 获取用户收益数据
     */
    public void requestUserProfit() {
        mWithdrawModel.getUserProfit(new HttpSubscriber<ProfitBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
                mView.showErrorView(true);
            }

            @Override
            public void onResponseSuccess(ProfitBean bean) {
                if (bean != null) {
                    mProfitBean = bean;
                    mView.showUserProfitInfo(mProfitBean);
                    mWithdrawModel.saveUserProfitInfo(mProfitBean);
                }
            }

            @Override
            public void onResponseError(String desc, String code) {
                mView.showToast(desc);
                mView.showErrorView(false);
            }

            @Override
            public void onNetworkError() {
                mView.showNetworkError();
                mView.showErrorView(false);
            }

            @Override
            public void onPostExecute() {
                mView.finishLoadView();
                mView.finishRefresh();
            }
        });
    }

    /**
     * 获取任务列表
     */
    public void getTaskList() {
        mTaskModel.getTaskList(new HttpSubscriber<ArrayList<TaskBean>>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onNetworkUnavailable() {
            }

            @Override
            public void onResponseSuccess(ArrayList<TaskBean> list) {
                if (list != null) {
                    mTaskList = list;
                    mView.showTaskList(mTaskList);
                    mTaskModel.saveLastTaskList(list);
                    if (mTaskList != null && !mTaskList.isEmpty()) {
                        for (TaskBean bean : mTaskList) {
                            if (StringUtil.equals(bean.key, TaskBean.REWARD_VIDEO)) {
                                mRewardVideoTaskBean = bean;
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onResponseError(String desc, String code) {
            }

            @Override
            public void onNetworkError() {
            }

            @Override
            public void onPostExecute() {
            }
        });
    }

    /**
     * 处理金币奖励进入
     *
     * @param coin 金币
     */
    public void handleCoinData(long coin) {
        if (coin > 0) {
            mView.showLoginCoinReward(coin);
        } else {
            checkInviteCode();
        }
    }

    /**
     * 检查邀请码
     */
    public void checkInviteCode() {
        mView.checkInviteCodeFromClip();
    }

    /**
     * 处理检测粘贴板上的文字
     *
     * @param text 文字
     */
    public void handleClipboardTxt(String text, String matchStr, String leftTagStr, String rightTagStr) {
        Logger.d("handle clipboard txt is [" + text + "]");
        try {
            // 已经绑定邀请码的用户不再弹窗
            if (WlVideoAppInfo.getInstance().getUserInfoBean().hasBindInviteCode()) {
                return;
            }
            if (!StringUtil.isNull(text) && !StringUtil.isNull(matchStr)) {
                int index = text.indexOf(matchStr);
                if (index < 0) {
                    return;
                }
                text = text.substring(index);
                int leftTagIndex = text.indexOf(leftTagStr);
                int rightTagIndex = text.indexOf(rightTagStr);
                text = text.substring(leftTagIndex + 1, rightTagIndex);
                if (!StringUtil.equals(text, mTaskModel.getLastClipInviteCode()) &&
                        !StringUtil.equals(text, WlVideoAppInfo.getInstance().getUserInfoBean().invite_code)) {
                    mTaskModel.saveLastClipInviteCode(text);
                    mView.showBindInviteCodeTip(text);
                }
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    /**
     * 处理跳转绑定邀请码url
     *
     * @param url  邀请码地址
     * @param text 邀请码
     */
    public void handleBindInviteCodeUrl(String url, String text) {
        if (!StringUtil.isNull(url)) {
            Map<String, List<Object>> params = new HashMap<>();
            List<Object> txtList = new ArrayList<>();
            txtList.add(text);
            params.put("invite_code", txtList);
            url = URLDecoder.decode(HttpUtils.createUrlFromParams(url, params));
            mView.startProtocol(url);
        }
    }

    /**
     * 处理任务点击跳转
     *
     * @param bean 任务
     */
    public void handleTaskClick(TaskBean bean, int position) {
        if (bean == null) {
            return;
        }
        if (ProtocolConstants.SCHEMA_ACTION_SIGN_IN.equals(bean.target_url)) {
            handleSignIn(bean, position);
        } else {
            if (bean.ad_config != null && !StringUtil.isNull(bean.ad_config.space)) {
                handleRewardVideoTask(bean.target_url, bean.key, bean.ad_config.space);
            } else {
                mView.startProtocol(bean.target_url);
            }
        }
    }

    /**
     * 处理签到任务
     */
    private void handleSignIn(TaskBean taskBean, int position) {
        mTaskModel.signIn(new HttpSubscriber<SignInBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(SignInBean bean) {
                if (bean == null || bean.records == null) {
                    return;
                }
                mSignInBean = bean;
                mView.showSignInInfo(bean);
                if (!StringUtil.equals(taskBean.task_status, TaskBean.STATUS_FINISHED)
                        && mTaskList != null && !mTaskList.isEmpty() && position < mTaskList.size()) {
                    if (bean.hasTodayCheckIn()) {
                        // 完成状态切换
                        if (mTaskList != null && position < mTaskList.size()) {
                            mTaskList.get(position).task_status = TaskBean.STATUS_FINISHED;
                            mView.notifyCurrentItem(position);
                        }
                        // 加金币
                        int signCoin = 0;
                        for (SignInBean.RecordsBean recordsBean : bean.records) {
                            if (StringUtil.equals(recordsBean.date, bean.today)) {
                                signCoin = recordsBean.reward;
                                break;
                            }
                        }
                        if (mProfitBean != null) {
                            mProfitBean.gold_balance = mProfitBean.gold_balance + signCoin;
                            mView.showUserProfitInfo(mProfitBean);
                            mWithdrawModel.saveUserProfitInfo(mProfitBean);
                        }
                    }
                }
            }

            @Override
            public void onResponseError(String desc, String code) {
                mView.showToast(desc);
                if (mSignInBean != null) {
                    mView.showSignInInfo(mSignInBean);
                }
            }

            @Override
            public void onNetworkError() {
                mView.showNetworkError();
                if (mSignInBean != null) {
                    mView.showSignInInfo(mSignInBean);
                }
            }

            @Override
            public void onPostExecute() {
                mView.finishLoadView();
            }
        });
    }

    /**
     * 处理签到额外任务
     */
    public void handleSignTask() {
        if (mSignInBean == null || mSignInBean.next_task == null || StringUtil.isNull(mSignInBean.next_task.task_key)
            || StringUtil.isNull(mSignInBean.next_task.target_url) || mSignInBean.next_task.ad_config == null
            || StringUtil.isNull(mSignInBean.next_task.ad_config.space)) {
            return;
        }
        handleRewardVideoTask(mSignInBean.next_task.target_url, mSignInBean.next_task.task_key, mSignInBean.next_task.ad_config.space);
    }

    /**
     * 处理激励视频任务
     *
     * @param url 激励视频
     * @param taskKey 任务key
     */
    public void handleRewardVideoTask(String url, String taskKey, String space) {
        if (!StringUtil.isNull(url) && !StringUtil.isNull(taskKey)) {
            Map<String, Object> extraMap = new HashMap<>();
            extraMap.put(ITaskPageView.TASK_KEY, taskKey);
            extraMap.put(ITaskPageView.TASK_SPACE, space);
            mView.startProtocol(url, extraMap);
        }
    }

    /**
     * 激励视频开始
     *
     * @param simpleName 当前页面
     * @param from       来源页面
     */
    public void handleRewardVideoStart(String simpleName, String from, String taskKey) {
        Logger.d("Reward video start simpleName=" + simpleName + " from=" + from + " taskKey=" + taskKey);
        if (!StringUtil.equals(simpleName, from)) {
            return;
        }
        if (mRewardVideoTaskBean != null && StringUtil.equals(mRewardVideoTaskBean.key, taskKey)) {
            if (StringUtil.equals(mRewardVideoTaskBean.task_status, TaskBean.STATUS_FINISHED)) {
                Logger.d("Reward video task has finished, so not obtain");
                return;
            }
        }
        if (StringUtil.isNull(taskKey)) {
            return;
        }
        mTaskModel.getVideoTask(taskKey, new SimpleHttpSubscriber<VideoTaskBean>() {
            @Override
            public void onResponseSuccess(VideoTaskBean bean) {
                mVideoTaskBean = bean;
            }
        });
    }

    /**
     * 激励视频完成
     *
     * @param simpleName 当前页面
     * @param from       来源页面
     */
    public void handleRewardVideoComplete(String simpleName, String from, String taskKey) {
        Logger.d("Reward video complete simpleName=" + simpleName + " from=" + from + " taskKey=" + taskKey);
        if (!StringUtil.equals(simpleName, from)) {
            return;
        }
        if (mRewardVideoTaskBean != null && StringUtil.equals(mRewardVideoTaskBean.key, taskKey)) {
            if (StringUtil.equals(mRewardVideoTaskBean.task_status, TaskBean.STATUS_FINISHED)) {
                Logger.d("Reward video task has finished, so not submit");
                return;
            }
        }
        if (mVideoTaskBean == null || StringUtil.isNull(mVideoTaskBean.task_id) || StringUtil.isNull(taskKey)) {
            Logger.d("Reward video task id is null, so not submit");
            return;
        }
        mTaskModel.submitVideoTask(taskKey, mVideoTaskBean.task_id,
                mVideoTaskBean.task_timestamp, new SimpleHttpSubscriber<VideoTaskBean>() {
                    @Override
                    public void onResponseSuccess(VideoTaskBean bean) {
                        Logger.d("Reward video submit success, so refresh list");
                        if (bean != null) {
                            mView.showCoinDialog(bean.coin, taskKey, bean.ad_config);
                            // 改列表数据，这里使用重新加载任务列表，为了防止多个激励视频任务
                            mView.refreshTaskList();
                            // 加金币
                            addProfitCoin(bean.coin);
                            if (isNeedRefreshCoinOpen) {
                                TaskCoinOpenHelper.getInstance().queryTaskCoinOpenStatus();
                            }
                            isNeedRefreshCoinOpen = false;
                        }
                    }
                });
    }

    /**
     * 加金币
     *
     * @param coin 金币数量
     */
    public void addProfitCoin(int coin) {
        if (mProfitBean != null) {
            mProfitBean.gold_balance = mProfitBean.gold_balance + coin;
            mView.showUserProfitInfo(mProfitBean);
            mWithdrawModel.saveUserProfitInfo(mProfitBean);
        }
    }

    /**
     * 处理开宝箱事件
     *
     * @param event 事件
     */
    public void handleCoinOpenEvent(TaskCoinStatusEvent event) {
        if (!WlVideoAppInfo.getInstance().hasLogin()) {
            return;
        }
        if (event == null) {
            return;
        }
        if (event.status == TaskCoinStatusEvent.STATUS_ERROR) {
            mView.hideCoinOpenFloat();
        } else if (event.status == TaskCoinStatusEvent.STATUS_CAN_OPEN
            || event.status == TaskCoinStatusEvent.STATUS_TIMEING) {
            mView.setCoinOpenFloatStatus(event.status, event.msg);
        }
    }

    /**
     * 开宝箱
     */
    public void handleTaskCoinOpen() {
        mTaskModel.openCoin(new HttpSubscriber<CoinOpenBean>() {
            @Override
            public void onPreExecute() {
                mView.showLoadView();
            }

            @Override
            public void onNetworkUnavailable() {
                mView.showNetworkUnAvailable();
            }

            @Override
            public void onResponseSuccess(CoinOpenBean bean) {
                if (bean == null) {
                    return;
                }
                if (bean.previous_task == null || StringUtil.isNull(bean.previous_task.task_key)) {
                    // 没有激励视频任务，则直接弹窗
                    mView.showCoinDialog(bean.coin, bean.task_key, bean.ad_config);
                    TaskCoinOpenHelper.getInstance().queryTaskCoinOpenStatus();
                } else {
                    // 有激励视频任务，则先弹窗选择
                    mView.showCoinOpenDialog(bean.coin, bean.previous_task);
                }
            }

            @Override
            public void onResponseError(String desc, String s1) {
                mView.showToast(desc);
            }

            @Override
            public void onNetworkError() {
                mView.showNetworkError();
            }

            @Override
            public void onPostExecute() {
                mView.finishLoadView();
            }
        });
    }

    /**
     * 处理通知区域关闭
     */
    public void handleNoPerClose() {
        mTaskModel.saveLastCloseNoPerLayout(TimeUtil.milliseconds2String(System.currentTimeMillis(), TimeUtil.TIME_FORMAT_DAY));
    }

    public void setNeedRefreshCoinOpen(boolean needRefreshCoinOpen) {
        isNeedRefreshCoinOpen = needRefreshCoinOpen;
    }

    @Override
    public void clear() {
        mWithdrawModel.cancelProfitRequest();
        mTaskModel.cancelTaskListRequest();
        mTaskModel.cancelSignInRequest();
        mTaskModel.cancelOpenActRequest();
        TaskCoinOpenHelper.getInstance().onDestroy();
    }
}
