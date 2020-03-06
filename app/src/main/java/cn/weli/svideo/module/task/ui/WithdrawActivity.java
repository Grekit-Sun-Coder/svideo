package cn.weli.svideo.module.task.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.adapter.CommonRecyclerAdapter;
import cn.weli.svideo.baselib.component.helper.DialogHelper;
import cn.weli.svideo.baselib.component.widget.dialog.CommonToastDialog;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.constant.ConfigConstants;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.common.widget.RichTextView;
import cn.weli.svideo.module.main.ui.WebViewActivity;
import cn.weli.svideo.module.main.view.IWebView;
import cn.weli.svideo.module.task.component.adapter.WithdrawProAdapter;
import cn.weli.svideo.module.task.component.event.WithdrawSuccessEvent;
import cn.weli.svideo.module.task.model.bean.ProfitBean;
import cn.weli.svideo.module.task.model.bean.WithdrawAccountBean;
import cn.weli.svideo.module.task.model.bean.WithdrawProBean;
import cn.weli.svideo.module.task.presenter.WithdrawPresenter;
import cn.weli.svideo.module.task.view.IAccountPayBindView;
import cn.weli.svideo.module.task.view.IWithdrawView;

/**
 * 提现页面
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class WithdrawActivity extends AppBaseActivity<WithdrawPresenter, IWithdrawView> implements
        IWithdrawView, CommonRecyclerAdapter.OnItemClickListener {

    @BindView(R.id.withdraw_income_txt)
    RichTextView mIncomeTxt;
    @BindView(R.id.withdraw_progress_txt)
    TextView mProgressTxt;
    @BindView(R.id.withdraw_info_txt)
    TextView mInfoTxt;
    @BindView(R.id.withdraw_set_txt)
    TextView mSetTxt;
    @BindView(R.id.withdraw_wx_info_txt)
    TextView mWxInfoTxt;
    @BindView(R.id.withdraw_wx_set_txt)
    TextView mWxSetTxt;
    @BindView(R.id.withdraw_recycler_view)
    RecyclerView mAliRecyclerView;
    @BindView(R.id.withdraw_wx_recycler_view)
    RecyclerView mWxRecyclerView;
    @BindView(R.id.withdraw_action_txt)
    TextView mWithdrawTxt;
    @BindView(R.id.withdraw_content_layout)
    LinearLayout mContentLayout;
    @BindView(R.id.withdraw_type_ali_txt)
    TextView mAliTxt;
    @BindView(R.id.withdraw_type_wx_txt)
    TextView mWxTxt;
    @BindView(R.id.withdraw_ali_layout)
    RelativeLayout mAliTypeLayout;
    @BindView(R.id.withdraw_wx_layout)
    RelativeLayout mWxTypeLayout;

    private WithdrawProAdapter mAliAdapter;
    private WithdrawProAdapter mWxAdapter;
    private Drawable mSelectDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BIND && resultCode == RESULT_OK) {
            if (data != null) {
                WithdrawAccountBean bean = (WithdrawAccountBean) data.getSerializableExtra(IAccountPayBindView.EXTRA_BIND_INFO);
                mPresenter.handleBindResult(bean);
            }
        }
    }

    @Override
    public void setWithdrawAccount(WithdrawAccountBean bean) {
        if (bean == null || StringUtil.isNull(bean.alipay_account)) {
            mInfoTxt.setText(R.string.withdraw_empty_info_title);
            mSetTxt.setText(R.string.withdraw_set_info_title);
        } else {
            mInfoTxt.setText(bean.alipay_account);
            mSetTxt.setText(R.string.withdraw_has_set_info_title);
        }
        if (bean == null || StringUtil.isNull(bean.wx_account)) {
            mWxInfoTxt.setText(R.string.withdraw_wx_empty_info_title);
            mWxSetTxt.setText(R.string.wx_go_bind_title);
        } else {
            mWxInfoTxt.setText(StringUtil.isNull(bean.wx_nickname) ? bean.wx_account : bean.wx_nickname);
            mWxSetTxt.setText(R.string.wx_go_has_bind_title);
        }
    }

    @Override
    public void showWithdrawProList(List<WithdrawProBean> aliList, List<WithdrawProBean> wxList) {
        mContentLayout.setVisibility(View.VISIBLE);
        mAliAdapter.addItems(aliList);
        mWxAdapter.addItems(wxList);
    }

    @Override
    public void showAccountEmptyTip() {
        showToast(R.string.withdraw_empty_account_title);
    }

    @Override
    public void handleWithdrawSuccess() {
        showToast(R.string.withdraw_success_title);
        RxBus.get().post(new WithdrawSuccessEvent());
        mProgressTxt.performClick();
        finishActivity();
    }

    @Override
    public void setCurrentAliProductSelected(int position) {
        mAliAdapter.setCurrentPosition(position);
        mWithdrawTxt.setEnabled(true);
        mWithdrawTxt.setAlpha(1);
    }

    @Override
    public void setCurrentWxProductSelected(int position) {
        mWxAdapter.setCurrentPosition(position);
        mWithdrawTxt.setEnabled(true);
        mWithdrawTxt.setAlpha(1);
    }

    @Override
    public void showAliType(boolean selected) {
        mAliTypeLayout.setVisibility(View.VISIBLE);
        mWxTypeLayout.setVisibility(View.GONE);
        mAliTxt.setCompoundDrawables(null, null, null, mSelectDrawable);
        mWxTxt.setCompoundDrawables(null, null, null, null);
        mAliRecyclerView.setVisibility(View.VISIBLE);
        mWxRecyclerView.setVisibility(View.GONE);
        mWithdrawTxt.setEnabled(selected);
        mWithdrawTxt.setAlpha(selected ? 1 : FLAG_DISENABLE_ALPHA);
    }

    @Override
    public void showWxType(boolean selected) {
        mAliTypeLayout.setVisibility(View.GONE);
        mWxTypeLayout.setVisibility(View.VISIBLE);
        mAliTxt.setCompoundDrawables(null, null, null, null);
        mWxTxt.setCompoundDrawables(null, null, null, mSelectDrawable);
        mAliRecyclerView.setVisibility(View.GONE);
        mWxRecyclerView.setVisibility(View.VISIBLE);
        mWithdrawTxt.setEnabled(selected);
        mWithdrawTxt.setAlpha(selected ? 1 : FLAG_DISENABLE_ALPHA);
    }

    @Override
    public void handleAliProductSelected(int position) {
        mPresenter.handleProductInfo(mAliAdapter.getContentList().get(position), position);
    }

    @Override
    public void handleWxProductSelected(int position) {
        mPresenter.handleProductInfo(mWxAdapter.getContentList().get(position), position);
    }

    @Override
    public void onMenuTxtClick() {
        CommonToastDialog dialog = DialogHelper.commonToastDialog(this)
                .titleStr(R.string.withdraw_rule_title)
                .content(StringUtil.isNull(ConfigConstants.WITHDRAW_DESC)
                                ? getString(R.string.withdraw_rule_detail_title)
                                : ConfigConstants.WITHDRAW_DESC).build();
        dialog.showDialog(this);
        StatisticsAgent.view(this, StatisticsUtils.CID.CID_30311, StatisticsUtils.MD.MD_3);
    }

    @Override
    public void onItemClick(View view, int position) {
        mPresenter.handleProductClick(position);
    }

    @OnClick({R.id.withdraw_progress_txt, R.id.withdraw_set_txt, R.id.withdraw_action_txt,
            R.id.withdraw_type_ali_txt, R.id.withdraw_type_wx_txt, R.id.withdraw_wx_set_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.withdraw_progress_txt:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, HttpConstant.H5_URL_WITHDRAW_PROGRESS);
                startActivity(intent);
                StatisticsAgent.click(this, StatisticsUtils.CID.CID_30313, StatisticsUtils.MD.MD_3);
                break;
            case R.id.withdraw_set_txt:
                Intent aliBindIntent = new Intent(this, AccountPayBindActivity.class);
                aliBindIntent.putExtra(IAccountPayBindView.EXTRA_BIND_INFO, mPresenter.getAccountBean());
                aliBindIntent.putExtra(IAccountPayBindView.EXTRA_BIND_TYPE, mPresenter.getCurrentType());
                startActivityForResult(aliBindIntent, REQUEST_CODE_BIND);
                StatisticsAgent.click(this, StatisticsUtils.CID.CID_30312, StatisticsUtils.MD.MD_3);
                break;
            case R.id.withdraw_wx_set_txt:
                Intent bindIntent = new Intent(this, AccountPayBindActivity.class);
                bindIntent.putExtra(IAccountPayBindView.EXTRA_BIND_INFO, mPresenter.getAccountBean());
                bindIntent.putExtra(IAccountPayBindView.EXTRA_BIND_TYPE, mPresenter.getCurrentType());
                startActivityForResult(bindIntent, REQUEST_CODE_BIND);
                StatisticsAgent.click(this, StatisticsUtils.CID.CID_30315, StatisticsUtils.MD.MD_3);
                break;
            case R.id.withdraw_action_txt:
                WithdrawProBean bean = null;
                if (mPresenter.getCurrentType() == TYPE_ALI) {
                    bean = mAliAdapter.getCurrentProBean();
                } else if (mPresenter.getCurrentType() == TYPE_WX) {
                    bean = mWxAdapter.getCurrentProBean();
                }
                if (bean != null) {
                    mPresenter.applyWithdraw(bean);
                    try {
                        JSONObject object = new JSONObject();
                        object.put("key", bean.id);
                        object.put("channel", bean.withdraw_type);
                        StatisticsAgent.click(this, StatisticsUtils.CID.CID_30314, StatisticsUtils.MD.MD_3, "", object.toString(), "");
                    } catch (Exception e) {
                        Logger.e(e.getMessage());
                    }
                }
                break;
            case R.id.withdraw_type_ali_txt:
                mPresenter.handleTypeSelect(TYPE_ALI, mAliAdapter.getCurrentPosition());
                break;
            case R.id.withdraw_type_wx_txt:
                mPresenter.handleTypeSelect(TYPE_WX, mWxAdapter.getCurrentPosition());
                break;
            default:
                break;
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(R.string.withdraw_title);
        setMenuTxt(R.string.withdraw_rule_title);
        mIncomeTxt.setTypeface(FontHelper.getDinAlternateBoldFont(this));

        mAliRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAliRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mAliAdapter = new WithdrawProAdapter(this);
        mAliAdapter.setOnItemClickListener(this);
        mAliRecyclerView.setAdapter(mAliAdapter);

        mWxRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mWxRecyclerView.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        mWxAdapter = new WithdrawProAdapter(this);
        mWxAdapter.setOnItemClickListener(this);
        mWxRecyclerView.setAdapter(mWxAdapter);

        mWithdrawTxt.setEnabled(false);
        mWithdrawTxt.setAlpha(FLAG_DISENABLE_ALPHA);
        mContentLayout.setVisibility(View.GONE);
        mSelectDrawable = ContextCompat.getDrawable(this, R.drawable.shape_withdraw_tag_bg);
        if (mSelectDrawable != null) {
            mSelectDrawable.setBounds(0, 0, mSelectDrawable.getMinimumWidth(), mSelectDrawable.getMinimumHeight());
        }
        mPresenter.handleTypeSelect(TYPE_WX, -1);
        mPresenter.getWithdrawAccountInfo();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            ProfitBean bean = (ProfitBean) intent.getSerializableExtra(EXTRA_PROFIT_INFO);
            mIncomeTxt.setRichText(bean.money_balance);
        }
    }

    @Override
    protected Class<WithdrawPresenter> getPresenterClass() {
        return WithdrawPresenter.class;
    }

    @Override
    protected Class<IWithdrawView> getViewClass() {
        return IWithdrawView.class;
    }

    @Override
    public JSONObject getTrackProperties() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(StatisticsUtils.field.content_id, StatisticsUtils.CID.CID_3031);
        json.put(StatisticsUtils.field.module, StatisticsUtils.MD.MD_3);
        return json;
    }
}
