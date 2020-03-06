package cn.weli.svideo.module.task.component.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.etouch.logger.Logger;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.module.task.model.bean.SignInBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class SignInDialog extends Dialog {

    private static final int SIGN_WEEK_DAYS = 7;
    private static final int SIGN_FIRS_DAYS = 4;

    private Context mContext;

    private LinearLayout mFirstLayout;

    private LinearLayout mSecondLayout;

    private TextView mCoinTxt;

    private TextView mActionTxt;

    private ImageView mCloseImg;

    private SignInBean mSignInBean;

    private OnSignInActionListener mActionListener;

    public interface OnSignInActionListener {

        void onSignTaskActionClick();
    }

    public SignInDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        setDialogTheme();
        setContentView(R.layout.dialog_sign_info);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mFirstLayout = findViewById(R.id.sign_first_layout);
        mSecondLayout = findViewById(R.id.sign_second_layout);
        mCoinTxt = findViewById(R.id.today_coin_txt);
        mActionTxt = findViewById(R.id.know_action_txt);
        mActionTxt.setOnClickListener(view -> {
            if (mSignInBean != null && mSignInBean.next_task != null && mActionListener != null) {
                mActionListener.onSignTaskActionClick();
            }
            dismiss();
        });
        mCloseImg = findViewById(R.id.sign_close_img);
        mCloseImg.setOnClickListener(view -> dismiss());
        Window window = getWindow();
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim);
        }
    }

    public void setActionListener(OnSignInActionListener actionListener) {
        mActionListener = actionListener;
    }

    private String getDayStr(int position) {
        String[] list = mContext.getResources().getStringArray(R.array.days_title);
        return list[position];
    }

    /**
     * 设置签到信息
     *
     * @param bean 签到信息
     */
    public void setSignInfo(SignInBean bean) {
        if (bean == null || bean.records == null || bean.records.isEmpty() || bean.records.size() != SIGN_WEEK_DAYS) {
            return;
        }
        mSignInBean = bean;
        mFirstLayout.removeAllViews();
        mSecondLayout.removeAllViews();
        int todayReward = 0;
        try {
            // 第一行
            for (int i = 0; i < SIGN_FIRS_DAYS; i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_sign_normal, null);
                LinearLayout signLayout = view.findViewById(R.id.sign_layout);
                TextView dayTxt = view.findViewById(R.id.sign_day_txt);
                ImageView statusImg = view.findViewById(R.id.sign_status_img);
                TextView coinTxt = view.findViewById(R.id.sign_coin_txt);
                coinTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
                SignInBean.RecordsBean recordsBean = bean.records.get(i);
                signLayout.setBackgroundDrawable(ContextCompat.getDrawable(mContext,
                        recordsBean.hasCheckIn() ? R.drawable.shape_sign_selected : R.drawable.shape_sign_unselected));
                if (StringUtil.equals(recordsBean.date, bean.today)) {
                    dayTxt.setText(R.string.today);
                    todayReward = recordsBean.reward;
                } else if (recordsBean.hasCheckIn()) {
                    dayTxt.setText(R.string.task_has_sign_title);
                } else {
                    dayTxt.setText(getDayStr(i));
                }
                dayTxt.setTextColor(ContextCompat.getColor(mContext, recordsBean.hasCheckIn() ? R.color.color_white : R.color.color_FF891C));
                statusImg.setImageResource(recordsBean.hasCheckIn() ? R.drawable.home_icon_right : R.drawable.icon_jinbi);
                coinTxt.setText(String.valueOf(recordsBean.reward));
                coinTxt.setTextColor(ContextCompat.getColor(mContext, recordsBean.hasCheckIn() ? R.color.color_white : R.color.color_FF7723));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, mContext.getResources().getDimensionPixelSize(R.dimen.common_len_190px));
                lp.weight = 1;
                lp.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_10px);
                lp.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_10px);
                mFirstLayout.addView(view, lp);
            }
            // 第二行
            for (int i = SIGN_FIRS_DAYS; i < (SIGN_WEEK_DAYS - 1); i++) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_sign_normal, null);
                LinearLayout signLayout = view.findViewById(R.id.sign_layout);
                TextView dayTxt = view.findViewById(R.id.sign_day_txt);
                ImageView statusImg = view.findViewById(R.id.sign_status_img);
                TextView coinTxt = view.findViewById(R.id.sign_coin_txt);
                coinTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
                SignInBean.RecordsBean recordsBean = bean.records.get(i);
                signLayout.setBackgroundDrawable(ContextCompat.getDrawable(mContext,
                        recordsBean.hasCheckIn() ? R.drawable.shape_sign_selected : R.drawable.shape_sign_unselected));
                if (StringUtil.equals(recordsBean.date, bean.today)) {
                    dayTxt.setText(R.string.today);
                    todayReward = recordsBean.reward;
                } else if (recordsBean.hasCheckIn()) {
                    dayTxt.setText(R.string.task_has_sign_title);
                } else {
                    dayTxt.setText(getDayStr(i));
                }
                dayTxt.setTextColor(ContextCompat.getColor(mContext, recordsBean.hasCheckIn() ? R.color.color_white : R.color.color_FF891C));
                statusImg.setImageResource(recordsBean.hasCheckIn() ? R.drawable.home_icon_right : R.drawable.icon_jinbi);
                coinTxt.setText(String.valueOf(recordsBean.reward));
                coinTxt.setTextColor(ContextCompat.getColor(mContext, recordsBean.hasCheckIn() ? R.color.color_white : R.color.color_FF7723));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, mContext.getResources().getDimensionPixelSize(R.dimen.common_len_190px));
                lp.weight = 1;
                lp.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_10px);
                lp.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_10px);
                mSecondLayout.addView(view, lp);
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_sign_special, null);
            LinearLayout signLayout = view.findViewById(R.id.sign_layout);
            TextView dayTxt = view.findViewById(R.id.sign_day_txt);
            TextView coinTxt = view.findViewById(R.id.sign_coin_txt);
            coinTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
            SignInBean.RecordsBean recordsBean = bean.records.get(SIGN_WEEK_DAYS - 1);
            signLayout.setBackgroundDrawable(ContextCompat.getDrawable(mContext,
                    recordsBean.hasCheckIn() ? R.drawable.shape_sign_selected : R.drawable.shape_sign_unselected));
            if (StringUtil.equals(recordsBean.date, bean.today)) {
                dayTxt.setText(R.string.today);
                todayReward = recordsBean.reward;
            } else if (recordsBean.hasCheckIn()) {
                dayTxt.setText(R.string.task_has_sign_title);
            } else {
                dayTxt.setText(getDayStr(SIGN_WEEK_DAYS - 1));
            }
            dayTxt.setTextColor(ContextCompat.getColor(mContext, recordsBean.hasCheckIn() ? R.color.color_white : R.color.color_FF891C));
            coinTxt.setText(String.valueOf(recordsBean.reward));
            coinTxt.setTextColor(ContextCompat.getColor(mContext, recordsBean.hasCheckIn() ? R.color.color_white : R.color.color_FF7723));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, mContext.getResources().getDimensionPixelSize(R.dimen.common_len_190px));
            lp.weight = 2.1f;
            lp.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_10px);
            lp.rightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_10px);
            mSecondLayout.addView(view, lp);

            mCoinTxt.setText(mContext.getString(R.string.task_sign_coin_title, String.valueOf(todayReward)));

            if (bean.next_task != null && !StringUtil.isNull(bean.next_task.button)) {
                mActionTxt.setText(bean.next_task.button);
                mCloseImg.setVisibility(View.VISIBLE);
            } else {
                mActionTxt.setText(R.string.well);
                mCloseImg.setVisibility(View.GONE);
            }
            show();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager windowManager = dialogWindow.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = (int) (display.getWidth() * 0.83f);
            dialogWindow.setAttributes(lp);
        }
    }

    /**
     * set dialog theme(设置对话框主题)
     */
    private void setDialogTheme() {
        Window window = getWindow();
        if (window != null) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }
}
