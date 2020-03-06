package cn.weli.svideo.module.mine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonFragmentAdapter;
import cn.weli.svideo.baselib.component.helper.DialogHelper;
import cn.weli.svideo.baselib.component.widget.dialog.CommonToastDialog;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.constant.ConfigConstants;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.common.widget.CommonWeakDialog;
import cn.weli.svideo.common.widget.RichTextView;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.main.view.IMainView;
import cn.weli.svideo.module.mine.component.adapter.ProfitTabAdapter;
import cn.weli.svideo.module.mine.presenter.MineProfitPresenter;
import cn.weli.svideo.module.mine.view.IMineProfitView;
import cn.weli.svideo.module.task.component.event.WithdrawSuccessEvent;
import cn.weli.svideo.module.task.model.bean.ProfitBean;
import cn.weli.svideo.module.task.ui.WithdrawActivity;
import cn.weli.svideo.module.task.view.IWithdrawView;

/**
 * 我的收益页面
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see cn.weli.svideo.module.task.ui.TaskPageFragment
 * @since [1.0.0]
 */
public class MineProfitActivity extends AppBaseActivity<MineProfitPresenter, IMineProfitView> implements IMineProfitView {

    @BindView(R.id.coin_txt)
    RichTextView mCoinTxt;
    @BindView(R.id.cash_txt)
    RichTextView mCashTxt;
    @BindView(R.id.rate_txt)
    TextView mRateTxt;
    @BindView(R.id.total_income_txt)
    TextView mTotalIncomeTxt;
    @BindView(R.id.profit_tab_view)
    MagicIndicator mProfitTabView;
    @BindView(R.id.profit_view_pager)
    ViewPager mProfitViewPager;

    private CoinDetailFragment mCoinFragment;
    private CashDetailFragment mCashFragment;
    /**
     * 当前页
     */
    private int mCurrentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_profit);
        ButterKnife.bind(this);
        RxBus.get().register(this);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);
    }

    @Subscribe
    public void onWithdrawSuccess(WithdrawSuccessEvent event) {
        mPresenter.requestUserProfit();
    }

    @Override
    public void showUserProfitInfo(ProfitBean bean) {
        mCoinTxt.setRichText(String.valueOf(bean.gold_balance), getString(R.string.common_str_coin));
        mCashTxt.setRichText(bean.money_balance, getString(R.string.common_str_yuan));
        mRateTxt.setText(bean.rate_desc);
        mRateTxt.setTag(bean.rate_toast);
        mTotalIncomeTxt.setText(getString(R.string.profit_income_title, bean.total_money_income));
    }

    @OnClick(R.id.withdraw_txt)
    public void onViewClicked() {
        if (mPresenter.getProfitBean() != null) {
            Intent intent = new Intent(this, WithdrawActivity.class);
            intent.putExtra(IWithdrawView.EXTRA_PROFIT_INFO, mPresenter.getProfitBean());
            startActivity(intent);
            StatisticsAgent.click(this, StatisticsUtils.CID.CID_303, StatisticsUtils.MD.MD_3);
        }
    }

    @OnClick(R.id.watch_action_txt)
    public void onWatchVideoClick() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(IMainView.EXTRA_TAB_FLAG_VALUE, IMainView.TAB_VIDEO);
        startActivity(intent);
        StatisticsAgent.click(this, StatisticsUtils.CID.CID_302, StatisticsUtils.MD.MD_3);
    }

    @OnClick({R.id.rate_title_txt, R.id.rate_txt})
    public void onRateDescClick() {
        try {
            StatisticsAgent.click(this, StatisticsUtils.CID.CID_5, StatisticsUtils.MD.MD_3);
            String toast = (String) mRateTxt.getTag();
            CommonWeakDialog dialog = new CommonWeakDialog(this);
            dialog.showWeakTxt(toast);
            StatisticsAgent.view(this, StatisticsUtils.CID.CID_501, StatisticsUtils.MD.MD_3);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void onMenuTxtClick() {
        CommonToastDialog dialog = DialogHelper.commonToastDialog(this)
                .titleStr(R.string.profit_info_title)
                .content(StringUtil.isNull(ConfigConstants.PROFIT_DESC)
                        ? getString(R.string.profit_detail_title)
                        : ConfigConstants.PROFIT_DESC).build();
        dialog.showDialog(this);
        StatisticsAgent.view(this, StatisticsUtils.CID.CID_301, StatisticsUtils.MD.MD_3);
    }

    /**
     * 初始化
     */
    private void initView() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(R.string.profit_title);
        setMenuTxt(R.string.profit_info_title);
        mCoinTxt.setRichText(DEFAULT_COIN, getString(R.string.common_str_coin));
        mCashTxt.setRichText(DEFAULT_CASH, getString(R.string.common_str_yuan));
        mCoinTxt.setTypeface(FontHelper.getDinAlternateBoldFont(this));
        mCashTxt.setTypeface(FontHelper.getDinAlternateBoldFont(this));
        ProfitTabAdapter tabAdapter = new ProfitTabAdapter(this, Arrays.asList(getResources
                ().getStringArray(R.array.profit_title)), mProfitViewPager);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(tabAdapter);
        mProfitTabView.setNavigator(commonNavigator);

        CommonFragmentAdapter adapter = new CommonFragmentAdapter(getSupportFragmentManager());
        mCoinFragment = (CoinDetailFragment) getSupportFragmentManager()
                .findFragmentByTag(makeFragmentTag(mProfitViewPager.getId(), PAGE_COIN));
        if (mCoinFragment == null) {
            mCoinFragment = new CoinDetailFragment();
        }
        mCashFragment = (CashDetailFragment) getSupportFragmentManager()
                .findFragmentByTag(makeFragmentTag(mProfitViewPager.getId(), PAGE_CASH));
        if (mCashFragment == null) {
            mCashFragment = new CashDetailFragment();
        }
        adapter.addFragment(mCoinFragment);
        adapter.addFragment(mCashFragment);
        mProfitViewPager.setAdapter(adapter);
        ViewPagerHelper.bind(mProfitTabView, mProfitViewPager);
        mProfitViewPager.setCurrentItem(mCurrentPage);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            String tab = intent.getStringExtra(EXTRA_PROFIT_TYPE);
            if (StringUtil.equals(tab, TAB_CASH)) {
                mCurrentPage = PAGE_CASH;
            } else {
                mCurrentPage = PAGE_COIN;
            }
            mPresenter.initMineProfit();
        }
    }

    @Override
    protected Class<MineProfitPresenter> getPresenterClass() {
        return MineProfitPresenter.class;
    }

    @Override
    protected Class<IMineProfitView> getViewClass() {
        return IMineProfitView.class;
    }

    @Override
    public JSONObject getTrackProperties() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(StatisticsUtils.field.content_id, StatisticsUtils.CID.CID_3);
        json.put(StatisticsUtils.field.module, StatisticsUtils.MD.MD_3);
        JSONObject object = new JSONObject();
        if (mCurrentPage == PAGE_COIN) {
            object.put("tab", "jinbi");
        } else if (mCurrentPage == PAGE_CASH) {
            object.put("tab", "xianjin");
        }
        json.put(StatisticsUtils.field.args, object.toString());
        return json;
    }
}
