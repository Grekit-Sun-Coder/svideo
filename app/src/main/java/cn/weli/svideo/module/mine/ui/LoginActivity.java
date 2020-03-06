package cn.weli.svideo.module.mine.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.RxBus;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.helper.StatusBarHelper;
import cn.weli.svideo.baselib.helper.WeCountDownTimer;
import cn.weli.svideo.baselib.utils.ConstUtil;
import cn.weli.svideo.baselib.utils.DensityUtil;
import cn.weli.svideo.baselib.utils.ScreenUtil;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.constant.HttpConstant;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.common.ui.AppBaseActivity;
import cn.weli.svideo.common.widget.SeparatorPhoneEditView;
import cn.weli.svideo.module.main.ui.MainActivity;
import cn.weli.svideo.module.main.ui.WebViewActivity;
import cn.weli.svideo.module.main.view.IMainView;
import cn.weli.svideo.module.main.view.IWebView;
import cn.weli.svideo.module.mine.component.event.LoginSuccessEvent;
import cn.weli.svideo.module.mine.presenter.LoginPresenter;
import cn.weli.svideo.module.mine.view.ILoginView;
import cn.weli.svideo.module.task.component.event.LoginCoinEvent;
import cn.weli.svideo.module.task.view.ITaskPageView;

/**
 * 登陆页
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-14
 * @see MainActivity
 * @since [1.0.0]
 */
public class LoginActivity extends AppBaseActivity<LoginPresenter, ILoginView> implements ILoginView,
        SeparatorPhoneEditView.OnPhoneEditTxtChangedListener {

    @BindView(R.id.login_parent_layout)
    LinearLayout mLoginParentLayout;
    @BindView(R.id.login_phone_title_txt)
    TextView mLoginPhoneTitleTxt;
    @BindView(R.id.login_phone_edit)
    SeparatorPhoneEditView mLoginPhoneEdit;
    @BindView(R.id.login_code_title_txt)
    TextView mLoginCodeTitleTxt;
    @BindView(R.id.login_code_txt)
    EditText mLoginCodeTxt;
    @BindView(R.id.login_get_code_txt)
    TextView mLoginGetCodeTxt;
    @BindView(R.id.login_action_txt)
    TextView mLoginActionTxt;
    @BindView(R.id.login_argue_img)
    ImageView mLoginArgueImg;

    /**
     * 协议是否同意
     */
    private boolean isArgueAgreed = true;
    /**
     * 倒计时
     */
    private WeCountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOrientation(DensityUtil.HEIGHT);
        setContentView(R.layout.activity_login_page);
        ScreenUtil.setTranslucentStatus(this);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public JSONObject getTrackProperties() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(StatisticsUtils.field.content_id, StatisticsUtils.CID.CID_1);
        json.put(StatisticsUtils.field.module, StatisticsUtils.MD.MD_4);
        return json;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    public void setLoginCodeTitleStatus(boolean isEmpty) {
        mLoginCodeTitleTxt.setTextColor(isEmpty ? ContextCompat.getColor(this, R.color.color_white)
                : ContextCompat.getColor(this, R.color.color_20_white));
    }

    @Override
    public void setLoginPhoneTitleStatus(boolean isEmpty) {
        mLoginPhoneTitleTxt.setTextColor(isEmpty ? ContextCompat.getColor(this, R.color.color_white)
                : ContextCompat.getColor(this, R.color.color_20_white));
    }

    @Override
    public void setLoginBtnStatus(boolean enable) {
        mLoginActionTxt.setEnabled(enable);
        mLoginActionTxt.setAlpha(enable ? 1 : FLAG_DISENABLE_ALPHA);
    }

    @Override
    public void showAgreeArgueTip() {
        showToast(R.string.login_agree_tip);
    }

    @Override
    public void showPhoneNumberErrorTip() {
        showToast(R.string.login_phone_tip);
    }

    @Override
    public void handleGetPhoneCodeSuccess() {
        showToast(R.string.login_code_success_tip);
        mLoginGetCodeTxt.setEnabled(false);
        if (mCountDownTimer == null) {
            initCountTimer();
        }
        mCountDownTimer.cancel();
        mCountDownTimer.start();
    }

    @Override
    public void handleLoginSuccess(boolean jump) {
        RxBus.get().post(new LoginSuccessEvent());
        showToast(R.string.login_success_tip);
        handleEventDelay(this::finishActivity, jump ? FLAG_FINISH_DELAY : 0);
    }

    @Override
    public void handleLoginStart2TaskPage(long gold) {
        setOrientation(DensityUtil.WIDTH);
        handleEventDelay(() -> {
            RxBus.get().post(new LoginCoinEvent(gold));
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(ITaskPageView.EXTRA_LOGIN_COIN, gold);
            intent.putExtra(IMainView.EXTRA_TAB_FLAG_VALUE, IMainView.TAB_TASK);
            startActivity(intent);
        }, FLAG_FINISH_DELAY);

    }

    @OnTextChanged(R.id.login_code_txt)
    public void onLoginCodeTxtChange() {
        mPresenter.checkLoginCodeChange(mLoginPhoneEdit.getPhoneCode(), mLoginCodeTxt.getText().toString());
    }

    @Override
    public void onPhoneEditChanged(String phone) {
        mPresenter.checkLoginCodeChange(phone, mLoginCodeTxt.getText().toString());
    }

    @OnClick({R.id.login_close_img, R.id.login_action_txt, R.id.login_user_argue_txt, R.id.login_user_policy_txt,
            R.id.login_argue_img, R.id.login_get_code_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_close_img:
                finishActivity();
                break;
            case R.id.login_get_code_txt:
                mPresenter.handleGetCode(mLoginPhoneEdit.getPhoneCode());
                StatisticsAgent.click(this, StatisticsUtils.CID.CID_102, StatisticsUtils.MD.MD_4);
                break;
            case R.id.login_action_txt:
                mPresenter.handleLogin(isArgueAgreed, mLoginPhoneEdit.getPhoneCode(), mLoginCodeTxt.getText().toString());
                StatisticsAgent.click(this, StatisticsUtils.CID.CID_101, StatisticsUtils.MD.MD_4);
                break;
            case R.id.login_user_argue_txt:
                Intent agreeIntent = new Intent(this, WebViewActivity.class);
                agreeIntent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, HttpConstant.H5_URL_USER_AGREEMENT);
                startActivity(agreeIntent);
                break;
            case R.id.login_user_policy_txt:
                Intent policyIntent = new Intent(this, WebViewActivity.class);
                policyIntent.putExtra(IWebView.EXTRA_WEB_VIEW_URL, HttpConstant.H5_URL_PRIVACY_POLICY);
                startActivity(policyIntent);
                break;
            case R.id.login_argue_img:
                isArgueAgreed = !isArgueAgreed;
                mLoginArgueImg.setSelected(isArgueAgreed);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mLoginParentLayout.setPadding(0, DensityUtil.getInstance().getStatusBarHeight(), 0, 0);
        }
        mLoginPhoneEdit.setTypeface(FontHelper.getDinAlternateBoldFont(this));
        mLoginCodeTxt.setTypeface(FontHelper.getDinAlternateBoldFont(this));
        mLoginPhoneEdit.setTxtChangedListener(this);
        setLoginBtnStatus(false);
        mLoginArgueImg.setSelected(isArgueAgreed);
    }

    /**
     * 初始化倒计时
     */
    private void initCountTimer() {
        mCountDownTimer = new WeCountDownTimer(ConstUtil.MIN, ConstUtil.SEC);
        mCountDownTimer.setCountDownCallBack(new WeCountDownTimer.CountDownCallBack() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onTick(long millisUntilFinished) {
                if (isFinishing()) {
                    return;
                }
                long second = millisUntilFinished / 1000;
                mLoginGetCodeTxt.setText(getString(R.string.login_count_title, second));
                if (second == ConstUtil.MSEC) {
                    handleEventDelay(() -> {
                        onFinish();
                        mCountDownTimer.cancel();
                    }, ConstUtil.SEC);
                }
            }

            @Override
            public void onFinish() {
                if (isFinishing()) {
                    return;
                }
                mLoginGetCodeTxt.setText(R.string.login_new_code_title);
                mLoginGetCodeTxt.setEnabled(true);
            }
        });
    }

    @Override
    protected Class<LoginPresenter> getPresenterClass() {
        return LoginPresenter.class;
    }

    @Override
    protected Class<ILoginView> getViewClass() {
        return ILoginView.class;
    }
}
