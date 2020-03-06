package cn.weli.svideo.module.task.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.widget.WeEmptyErrorView;
import cn.weli.svideo.baselib.component.widget.smartrefresh.WeRefreshLayout;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.common.ui.AppBaseFragment;
import cn.weli.svideo.common.widget.RichTextView;
import cn.weli.svideo.module.main.component.event.NotificationPerChangeEvent;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.main.view.IMainView;
import cn.weli.svideo.module.mine.ui.MineProfitActivity;
import cn.weli.svideo.module.mine.view.IMineProfitView;
import cn.weli.svideo.module.task.component.adapter.TaskListAdapter;
import cn.weli.svideo.module.task.component.event.LoginCoinEvent;
import cn.weli.svideo.module.task.component.event.RefreshCoinEvent;
import cn.weli.svideo.module.task.component.event.TaskCoinStatusEvent;
import cn.weli.svideo.module.task.component.event.TaskRefreshEvent;
import cn.weli.svideo.module.task.component.event.WithdrawSuccessEvent;
import cn.weli.svideo.module.task.component.helper.RewardVideoHelper;
import cn.weli.svideo.module.task.component.helper.TaskCoinOpenHelper;
import cn.weli.svideo.module.task.component.widget.BindInviteCodeDialog;
import cn.weli.svideo.module.task.component.widget.CoinOpenDialog;
import cn.weli.svideo.module.task.component.widget.RewardCoinDialog;
import cn.weli.svideo.module.task.component.widget.SignInDialog;
import cn.weli.svideo.module.task.component.widget.TaskCoinDialog;
import cn.weli.svideo.module.task.component.widget.floatdrag.OpenCoinLayout;
import cn.weli.svideo.module.task.component.widget.floatdrag.view.FloatViewImpl;
import cn.weli.svideo.module.task.component.widget.floatdrag.widget.FloatViewHoledr;
import cn.weli.svideo.module.task.component.widget.floatdrag.widget.FloatViewLayout;
import cn.weli.svideo.module.task.model.bean.AdConfigBean;
import cn.weli.svideo.module.task.model.bean.CoinOpenBean;
import cn.weli.svideo.module.task.model.bean.ProfitBean;
import cn.weli.svideo.module.task.model.bean.SignInBean;
import cn.weli.svideo.module.task.model.bean.TaskBean;
import cn.weli.svideo.module.task.presenter.TaskPagePresenter;
import cn.weli.svideo.module.task.view.ITaskPageView;
import cn.weli.svideo.push.NotificationUtils;

/**
 * 赚钱任务页
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-06
 * @see MainActivity
 * @since [1.0.0]
 */
public class TaskPageFragment extends AppBaseFragment<TaskPagePresenter, ITaskPageView> implements
        ITaskPageView, CommonRecyclerAdapter.OnItemClickListener, OnRefreshListener,
        SignInDialog.OnSignInActionListener, OpenCoinLayout.OnTaskOpenCoinListener,
        RewardVideoHelper.OnRewardVideoListener {

    @BindView(R.id.task_parent_layout)
    RelativeLayout mTaskParentLayout;
    @BindView(R.id.task_refresh_layout)
    WeRefreshLayout mWeRefreshLayout;
    @BindView(R.id.coin_txt)
    RichTextView mCoinTxt;
    @BindView(R.id.cash_txt)
    RichTextView mCashTxt;
    @BindView(R.id.task_recycler_view)
    RecyclerView mTaskRecyclerView;
    @BindView(R.id.task_content_layout)
    NestedScrollView mTaskContentLayout;
    @BindView(R.id.task_empty_error_view)
    WeEmptyErrorView mEmptyErrorView;
    @BindView(R.id.task_open_coin_layout)
    FloatViewLayout mTaskOpenCoinLayout;
    @BindView(R.id.task_no_per_layout)
    LinearLayout mTaskNoPerLayout;
    private OpenCoinLayout mOpenCoinLayout;
    private TaskListAdapter mAdapter;
    private SignInDialog mSignInDialog;
    private TaskCoinDialog mTaskCoinDialog;
    private BindInviteCodeDialog mInviteCodeDialog;

    public boolean isResume;
    public boolean isResumeNeedShowCoin;

    private View mFragmentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        if (mFragmentView == null) {
            DensityUtil.getInstance().setOrientation(getActivity(), DensityUtil.WIDTH);
            mFragmentView = inflater.inflate(R.layout.fragment_task_page, container, false);
            ButterKnife.bind(this, mFragmentView);
            RxBus.get().register(this);
            initView();
            initData();
        } else {
            if (mFragmentView.getParent() != null) {
                ((ViewGroup) mFragmentView.getParent()).removeView(mFragmentView);
            }
        }
        return mFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded() && getActivity() != null) {
            isResume = true;
            if (isResumeNeedShowCoin) {
                isResumeNeedShowCoin = false;
                if (mTaskCoinDialog != null) {
                    handleEventDelay(() -> {
                        if (isAdded() && getActivity() != null) {
                            mTaskCoinDialog.show();
                        }
                    }, FLAG_SHOW_COIN_DELAY);
                    onTaskCoinRecord(mTaskCoinDialog.getTaskKey());
                }
            }
            StatisticsAgent.enter(getActivity(), StatisticsUtils.CID.CID_1,
                    StatisticsUtils.MD.MD_2);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isAdded()) {
            mPresenter.handleUserVisible(hidden);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mOpenCoinLayout != null) {
            mOpenCoinLayout.onDestory();
        }
        RxBus.get().unregister(this);
        RewardVideoHelper.getInstance().removeRewardVideoListener(this);
    }

    @Override
    public void handleFragmentShow() {
        if (isAdded()) {
            StatisticsAgent.enter(getActivity(), StatisticsUtils.CID.CID_1,
                    StatisticsUtils.MD.MD_2);
        }
    }

    @Override
    public void handleFragmentHide() {
    }

    @Subscribe
    public void onNotificationPerChange(NotificationPerChangeEvent event) {
        if (getActivity() != null && isAdded() && event != null) {
            setNotificationPerStatus(event.enable);
        }
    }

    @Override
    public void onRewardVideoStart(String from, String taskKey) {
        if (getActivity() != null && isAdded()) {
            mPresenter.handleRewardVideoStart(getActivity().getClass().getSimpleName(), from,
                    taskKey);
        }
    }

    @Override
    public void onRewardVideoComplete(String from, String taskKey) {
        if (getActivity() != null && isAdded()) {
            mPresenter.handleRewardVideoComplete(getActivity().getClass().getSimpleName(), from,
                    taskKey);
        }
    }

    @Subscribe
    public void onWithdrawSuccess(WithdrawSuccessEvent event) {
        if (getActivity() != null && isAdded()) {
            mPresenter.requestUserProfit();
        }
    }

    @Subscribe
    public void onTaskRefresh(TaskRefreshEvent event) {
        if (getActivity() != null && isAdded()) {
            handleEventDelay(() -> {
                if (getActivity() != null && isAdded()) {
                    onRefresh(mWeRefreshLayout);
                }
            }, FLAG_SHOW_COIN_DELAY);
        }
    }

    @Subscribe
    public void onLoginCoinSuccess(LoginCoinEvent event) {
        if (getActivity() != null && isAdded()) {
            if (event.coin > 0) {
                showLoginCoinReward(event.coin);
            }
        }
    }

    @Subscribe
    public void onRefreshCoinEvent(RefreshCoinEvent event) {
        if (getActivity() != null && isAdded()) {
            if (event.coin > 0) {
                mPresenter.addProfitCoin(event.coin);
            } else {
                mPresenter.requestUserProfit();
            }
        }
    }

    @Subscribe
    public void onTaskOpenCoinStatus(TaskCoinStatusEvent event) {
        mPresenter.handleCoinOpenEvent(event);
    }

    @Override
    public void showUserProfitInfo(ProfitBean bean) {
        if (getActivity() != null && isAdded()) {
            mEmptyErrorView.setVisibility(View.GONE);
            mTaskContentLayout.setVisibility(View.VISIBLE);
            mCoinTxt.setRichText(String.valueOf(bean.gold_balance),
                    getString(R.string.common_str_coin));
            mCashTxt.setRichText(bean.money_balance, getString(R.string.common_str_yuan));
        }
    }

    @Override
    public void showTaskList(List<TaskBean> list) {
        if (getActivity() != null && isAdded()) {
            mAdapter.addItems(list);
        }
    }

    @Override
    public void showLoginCoinReward(long coin) {
        if (getActivity() != null && isAdded()) {
            handleEventDelay(() -> {
                if (getActivity() != null && isAdded() && !getActivity().isFinishing()) {
                    RewardCoinDialog dialog = new RewardCoinDialog(getActivity());
                    dialog.setMoreListener(() -> mPresenter.checkInviteCode());
                    dialog.setCoinTitleTxt(getString(R.string.login_success_tip));
                    dialog.setCoinTxt(coin);
                    dialog.show();
                    StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_101,
                            StatisticsUtils.MD.MD_2);
                }
            }, FLAG_SHOW_COIN_DELAY);
        }
    }

    @Override
    public void checkInviteCodeFromClip() {
        if (getActivity() != null && isAdded()) {
            ClipboardManager manager = (ClipboardManager) getActivity().getSystemService(
                    Context.CLIPBOARD_SERVICE);
            if (manager != null) {
                ClipData data = manager.getPrimaryClip();
                if (data != null) {
                    ClipData.Item item = data.getItemAt(0);
                    if (item != null && item.getText() != null) {
                        mPresenter.handleClipboardTxt(item.getText().toString(),
                                getString(R.string.task_find_title),
                                getString(R.string.task_left_tag_title),
                                getString(R.string.task_right_tag_title));
                    }
                }
            }
        }
    }

    @Override
    public void showBindInviteCodeTip(String text) {
        if (getActivity() != null && isAdded()) {
            if (mInviteCodeDialog == null) {
                mInviteCodeDialog = new BindInviteCodeDialog(getActivity());
                mInviteCodeDialog.setActionListener(
                        new BindInviteCodeDialog.OnBindInviteActionListener() {
                            @Override
                            public void onBindInviteCodeNow() {
                                mPresenter.handleBindInviteCodeUrl(HttpConstant.H5_URL_BIND_INVITE,
                                        text);
                                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_7,
                                        StatisticsUtils.MD.MD_2);
                            }
                        });
            }
            mInviteCodeDialog.setContentTxt(text);
            mInviteCodeDialog.show();
            StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_7, StatisticsUtils.MD.MD_2);
        }
    }

    @Override
    public void hideCoinOpenFloat() {
        if (isAdded() && getActivity() != null) {
            mTaskOpenCoinLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setCoinOpenFloatStatus(int status, String msg) {
        if (isAdded() && getActivity() != null) {
            if (mTaskOpenCoinLayout.getVisibility() == View.GONE) {
                mTaskOpenCoinLayout.setVisibility(View.VISIBLE);
                StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_9,
                        StatisticsUtils.MD.MD_2);
            }
            mOpenCoinLayout.setCoinStatus(status, msg);
        }
    }

    @Override
    public void showCoinOpenDialog(int coin, CoinOpenBean.PreviousTask taskBean) {
        if (isAdded() && getActivity() != null) {
            CoinOpenDialog dialog = new CoinOpenDialog(getActivity());
            dialog.setCoinOpenInfo(coin, taskBean);
            dialog.setOpenVideoListener(new CoinOpenDialog.OnCoinOpenVideoListener() {
                @Override
                public void onCoinOpenVideoSelect(CoinOpenBean.PreviousTask bean) {
                    if (bean != null && bean.ad_config != null) {
                        mPresenter.setNeedRefreshCoinOpen(true);
                        mPresenter.handleRewardVideoTask(bean.target_url, bean.task_key,
                                bean.ad_config.space);
                    }
                }

                @Override
                public void onCoinOpenVideoCancel() {
                    mOpenCoinLayout.continueCoinOpenAnim();
                }
            });
            dialog.show();
            StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_4, StatisticsUtils.MD.MD_2);
        }
    }

    @Override
    public void showTaskHasFinished() {
        if (getActivity() != null && isAdded()) {
            showToast(R.string.task_has_done_title);
        }
    }

    @Override
    public void showSignInInfo(SignInBean bean) {
        if (getActivity() != null && isAdded()) {
            if (mSignInDialog == null) {
                mSignInDialog = new SignInDialog(getActivity());
                mSignInDialog.setActionListener(this);
            }
            mSignInDialog.setSignInfo(bean);
            StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_201,
                    StatisticsUtils.MD.MD_2);
        }
    }

    @Override
    public void onSignTaskActionClick() {
        if (isAdded() && getActivity() != null) {
            mPresenter.handleSignTask();
            StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_2001,
                    StatisticsUtils.MD.MD_2);
        }
    }

    @Override
    public void showErrorView(boolean netUnAvailable) {
        if (getActivity() != null && isAdded()) {
            mEmptyErrorView.setErrorView(netUnAvailable);
            mEmptyErrorView.setVisibility(View.VISIBLE);
            mTaskContentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyCurrentItem(int position) {
        if (getActivity() != null && isAdded()) {
            mAdapter.notifyItemChanged(position);
        }
    }

    @Override
    public void showCoinDialog(int coin, String taskKey, AdConfigBean bean) {
        if (getActivity() != null && isAdded()) {
            if (mTaskCoinDialog == null) {
                mTaskCoinDialog = new TaskCoinDialog(getActivity());
            }
            mTaskCoinDialog.setCoinTxt(String.valueOf(coin));
            mTaskCoinDialog.setTaskKey(taskKey);
            mTaskCoinDialog.setTaskAdConfig(bean);
            if (isResume) {
                mTaskCoinDialog.show();
                onTaskCoinRecord(mTaskCoinDialog.getTaskKey());
            } else {
                isResumeNeedShowCoin = true;
            }
        }
    }

    @Override
    public void refreshTaskList() {
        if (getActivity() != null && isAdded()) {
            handleEventDelay(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && isAdded()) {
                        mPresenter.getTaskList();
                    }
                }
            }, FLAG_SHOW_COIN_DELAY);
        }
    }

    @Override
    public void finishRefresh() {
        if (getActivity() != null && isAdded()) {
            mWeRefreshLayout.finishRefresh();
        }
    }

    @Override
    public void setNotificationPerStatus(boolean enable) {
        if (getActivity() != null && isAdded()) {
            mTaskNoPerLayout.setVisibility(enable ? View.GONE : View.VISIBLE);
            if (!enable) {
                StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_5, StatisticsUtils.MD.MD_2);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (getActivity() != null && isAdded()) {
            mPresenter.handleTaskClick(mAdapter.getContentList().get(position), position);

            try {
                JSONObject object = new JSONObject();
                object.put("task", mAdapter.getContentList().get(position).key);
                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_3,
                        StatisticsUtils.MD.MD_2, "", object.toString(), "");
            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (getActivity() != null && isAdded()) {
            mPresenter.requestUserProfit();
            mPresenter.getTaskList();
        }
    }

    @Override
    public void onTaskCoinOpen() {
        mPresenter.handleTaskCoinOpen();
    }

    @OnClick({R.id.coin_layout, R.id.cash_layout, R.id.task_profit_txt, R.id.task_no_per_close_img, R.id.task_no_per_set_img})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.coin_layout:
                intent = new Intent(getActivity(), MineProfitActivity.class);
                intent.putExtra(IMineProfitView.EXTRA_PROFIT_TYPE, IMineProfitView.TAB_COIN);
                startActivity(intent);
                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_1,
                        StatisticsUtils.MD.MD_3);
                break;
            case R.id.cash_layout:
                intent = new Intent(getActivity(), MineProfitActivity.class);
                intent.putExtra(IMineProfitView.EXTRA_PROFIT_TYPE, IMineProfitView.TAB_CASH);
                startActivity(intent);
                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_2,
                        StatisticsUtils.MD.MD_3);
                break;
            case R.id.task_profit_txt:
                intent = new Intent(getActivity(), MineProfitActivity.class);
                startActivity(intent);
                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_4,
                        StatisticsUtils.MD.MD_3);
                break;
            case R.id.task_no_per_close_img:
                mTaskNoPerLayout.setVisibility(View.GONE);
                mPresenter.handleNoPerClose();
                break;
            case R.id.task_no_per_set_img:
                NotificationUtils.setNotification(getActivity(), IMainView.REQUEST_CODE_NOTIFICATION);
                StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_501, StatisticsUtils.MD.MD_2);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        if (getActivity() == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTaskParentLayout.setPadding(0, DensityUtil.getInstance().getStatusBarHeight(), 0, 0);
        }
        RewardVideoHelper.getInstance().addRewardVideoListener(this);
        mCoinTxt.setRichText(DEFAULT_COIN, getString(R.string.common_str_coin));
        mCashTxt.setRichText(DEFAULT_CASH, getString(R.string.common_str_yuan));
        mCoinTxt.setTypeface(FontHelper.getDinAlternateBoldFont(getActivity()));
        mCashTxt.setTypeface(FontHelper.getDinAlternateBoldFont(getActivity()));
        mWeRefreshLayout.setEnableRefresh(true);
        mWeRefreshLayout.setEnableLoadMore(false);
        mWeRefreshLayout.setOnRefreshListener(this);
        mTaskRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mAdapter = new TaskListAdapter(getActivity());
        mAdapter.setOnItemClickListener(this);
        mTaskRecyclerView.setAdapter(mAdapter);
        initCoinFloat();
        mPresenter.initTaskPage(NotificationUtils.isNotificationEnable(getActivity()));
        TaskCoinOpenHelper.getInstance().checkTaskCoinStatus();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (getActivity() == null) {
            return;
        }
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            long coin = intent.getLongExtra(EXTRA_LOGIN_COIN, 0);
            mPresenter.handleCoinData(coin);
            intent.putExtra(EXTRA_LOGIN_COIN, 0);
        }
    }

    /**
     * 初始化悬浮开宝箱
     */
    private void initCoinFloat() {
        if (mOpenCoinLayout == null) {
            mOpenCoinLayout = new OpenCoinLayout(getActivity());
            mOpenCoinLayout.setOpenCoinListener(this);
        }
        mTaskOpenCoinLayout.setmFloatView(new FloatViewImpl() {
            @Override
            public View createFloatView() {
                return mOpenCoinLayout;
            }

            @Override
            public boolean setEnableBackground() {
                return false;
            }
        });
        mTaskOpenCoinLayout.setFloatViewClickListener(
                new FloatViewHoledr.OnFloatViewClickListener() {
                    @Override
                    public void onFloatViewClick() {
                        mOpenCoinLayout.handleOpenClick();
                        try {
                            JSONObject object = new JSONObject();
                            object.put("task", "box");
                            StatisticsAgent.click(getActivity(), StatisticsUtils.CID.CID_3,
                                    StatisticsUtils.MD.MD_2, "", object.toString(), "");
                        } catch (Exception e) {
                            Logger.e(e.getMessage());
                        }
                    }
                });
    }

    /**
     * 金币弹窗埋点
     *
     * @param taskKey 任务key
     */
    private void onTaskCoinRecord(String taskKey) {
        try {
            JSONObject object = new JSONObject();
            object.put("task", taskKey);
            StatisticsAgent.view(getActivity(), StatisticsUtils.CID.CID_601,
                    StatisticsUtils.MD.MD_2, "", object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Class<TaskPagePresenter> getPresenterClass() {
        return TaskPagePresenter.class;
    }

    @Override
    protected Class<ITaskPageView> getViewClass() {
        return ITaskPageView.class;
    }
}
