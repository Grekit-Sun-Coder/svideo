package cn.weli.svideo.module.task.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.etouch.logger.Logger;
import cn.weli.common.libs.WeliLib;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.helper.PackageHelper;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.constant.BusinessConstants;
import cn.weli.svideo.common.constant.ConfigConstants;
import cn.weli.svideo.common.helper.SharePasteHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.module.task.model.bean.WithdrawAccountBean;
import cn.weli.svideo.module.task.presenter.AccountPayBindPresenter;
import cn.weli.svideo.module.task.view.IAccountPayBindView;
import cn.weli.svideo.module.task.view.IWithdrawView;
import cn.weli.svideo.wxapi.WxAuthorizeEvent;

/**
 * 支付宝绑定页面
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see WithdrawActivity
 * @since [1.0.0]
 */
public class AccountPayBindActivity extends AppBaseActivity<AccountPayBindPresenter, IAccountPayBindView> implements IAccountPayBindView {

    @BindView(R.id.bind_name_txt)
    EditText mBindNameTxt;
    @BindView(R.id.bind_account_txt)
    EditText mBindAccountTxt;
    @BindView(R.id.bind_id_txt)
    EditText mBindIdTxt;
    @BindView(R.id.withdraw_action_txt)
    TextView mWithdrawActionTxt;
    @BindView(R.id.bind_desc_txt)
    TextView mBindDescTxt;
    @BindView(R.id.wx_bind_layout)
    RelativeLayout mWxBindLayout;
    @BindView(R.id.ali_bind_layout)
    LinearLayout mAliBindLayout;
    @BindView(R.id.wx_bind_txt)
    TextView mWxBindTxt;
    @BindView(R.id.withdraw_tip_txt)
    TextView mBindTipTxt;

    private int mCurrentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_pay_bind);
        ButterKnife.bind(this);
        RxBus.get().register(this);
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

    @Override
    public JSONObject getTrackProperties() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(StatisticsUtils.field.content_id, mCurrentType == IWithdrawView.TYPE_ALI ? StatisticsUtils.CID.CID_303121 : StatisticsUtils.CID.CID_303151);
        json.put(StatisticsUtils.field.module, StatisticsUtils.MD.MD_3);
        return json;
    }

    @Subscribe
    public void onAuthorSuccess(WxAuthorizeEvent event) {
        String appId = PackageHelper.getMetaData(this, BusinessConstants.MetaData.Wx_APP_ID);
        String appSecret = PackageHelper.getMetaData(this, BusinessConstants.MetaData.Wx_APP_SECRET);
        String code = event.getCode();
        mPresenter.handleWxAuthorToken(appId, appSecret, code);
    }

    @Override
    public void setCompleteTxtEnable(boolean enable) {
        mWithdrawActionTxt.setEnabled(enable);
        mWithdrawActionTxt.setAlpha(enable ? 1 : FLAG_DISENABLE_ALPHA);
    }

    @Override
    public void setWithdrawInfo(WithdrawAccountBean bean, int type) {
        mBindAccountTxt.setText(bean.alipay_account);
        mBindNameTxt.setText(bean.real_name);
        try {
            mBindIdTxt.setText(WeliLib.getInstance().doTheSecrypt(bean.id_card, 4));
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        mBindAccountTxt.setEnabled(StringUtil.isNull(bean.alipay_account));
        mBindNameTxt.setEnabled(StringUtil.isNull(bean.real_name));
        mBindIdTxt.setEnabled(StringUtil.isNull(bean.id_card));
        showWxBindView(bean.wx_nickname);
        setCompleteTxtEnable(false);

        setWithdrawInfoView(type);
    }

    @Override
    public void handleBindSuccess(WithdrawAccountBean bean) {
        showToast(R.string.ali_bind_input_success_title);
        Intent intent = new Intent();
        intent.putExtra(EXTRA_BIND_INFO, bean);
        setResult(RESULT_OK, intent);
        finishActivity();
    }

    @Override
    public void setWithdrawInfoView(int type) {
        mWxBindLayout.setVisibility(type == IWithdrawView.TYPE_ALI ? View.GONE : View.VISIBLE);
        mAliBindLayout.setVisibility(type == IWithdrawView.TYPE_ALI ? View.VISIBLE : View.GONE);
        mBindDescTxt.setText(type == IWithdrawView.TYPE_ALI ? (StringUtil.isNull(ConfigConstants.ACCOUNT_DESC)
                ? getString(R.string.ali_bind_info_title) : ConfigConstants.ACCOUNT_DESC) : getString(R.string.wx_bind_info_title));
        mBindTipTxt.setText(type == IWithdrawView.TYPE_ALI ? R.string.ali_bind_input_title : R.string.wx_bind_input_title);
    }

    @Override
    public void showWxBindView(String nickname) {
        if (!StringUtil.isNull(nickname)) {
            mWxBindTxt.setText(nickname);
            mWxBindTxt.setTextColor(ContextCompat.getColor(this, R.color.color_E7E7E7));
            mWxBindTxt.setCompoundDrawables(null, null, null, null);
            onBindEditChanged();
        }
    }

    @Override
    public void startWxBind() {
        doAuthorWx();
    }

    @Override
    public void setCompleteStatus(boolean hasSet) {
        mWithdrawActionTxt.setText(hasSet ? R.string.withdraw_has_set_info_title : R.string.ali_bind_input_complete_title);
    }

    @OnTextChanged({R.id.bind_name_txt, R.id.bind_account_txt, R.id.bind_id_txt})
    public void onBindEditChanged() {
        mPresenter.handleEditChanged(mBindNameTxt.getText().toString().trim(), mBindAccountTxt.getText().toString().trim(),
                mBindIdTxt.getText().toString().trim());
    }

    @OnClick(R.id.withdraw_action_txt)
    public void onCompleteClick() {
        mPresenter.handleComplete(mBindNameTxt.getText().toString().trim(),
                mBindAccountTxt.getText().toString().trim(), mBindIdTxt.getText().toString().trim());
        StatisticsAgent.click(this, mCurrentType == IWithdrawView.TYPE_ALI ? StatisticsUtils.CID.CID_3031211 : StatisticsUtils.CID.CID_3031511, StatisticsUtils.MD.MD_3);
    }

    @OnClick(R.id.wx_bind_txt)
    public void onWxBindClick() {
        if (SharePasteHelper.isWxInstalled(this)) {
            mPresenter.handleWxBind();
        } else {
            showToast(R.string.share_wx_not_installed);
        }
    }

    /**
     * 微信认证
     */
    private void doAuthorWx() {
        String appId = PackageHelper.getMetaData(this, BusinessConstants.MetaData.Wx_APP_ID);
        IWXAPI api = WXAPIFactory.createWXAPI(this, appId, true);
        // 将应用的appId注册到微信
        api.registerApp(appId);
        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该app注册到微信
                api.registerApp(appId);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "do_author";
        api.sendReq(req);
    }

    /**
     * 初始化
     */
    private void initView() {
        StatusBarHelper.setStatusBarColor(this, ContextCompat.getColor(this, R.color.color_transparent), false);
        showTitle(R.string.ali_bind_title);
        onBindEditChanged();
        Intent intent = getIntent();
        if (intent != null) {
            WithdrawAccountBean bean = (WithdrawAccountBean) intent.getSerializableExtra(EXTRA_BIND_INFO);
            mCurrentType= intent.getIntExtra(EXTRA_BIND_TYPE, IWithdrawView.TYPE_ALI);
            mPresenter.initWithdrawInfo(bean, mCurrentType);
        }
    }

    @Override
    protected Class<AccountPayBindPresenter> getPresenterClass() {
        return AccountPayBindPresenter.class;
    }

    @Override
    protected Class<IAccountPayBindView> getViewClass() {
        return IAccountPayBindView.class;
    }
}
