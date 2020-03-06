package cn.weli.svideo.module.task.component.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.widget.dialog.BaseDialog;
import cn.weli.svideo.common.Statistics.StatisticsAgent;
import cn.weli.svideo.common.Statistics.StatisticsUtils;
import cn.weli.svideo.common.helper.FontHelper;
import cn.weli.svideo.module.task.model.bean.CoinOpenBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-25
 * @see [class/method]
 * @since [1.0.0]
 */
public class CoinOpenDialog extends BaseDialog {

    @BindView(R.id.coin_txt)
    TextView mCoinTxt;

    private CoinOpenBean.PreviousTask mTaskBean;

    private OnCoinOpenVideoListener mOpenVideoListener;

    public interface OnCoinOpenVideoListener {

        void onCoinOpenVideoSelect(CoinOpenBean.PreviousTask bean);

        void onCoinOpenVideoCancel();
    }

    public CoinOpenDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_open_coin_select);
        ButterKnife.bind(this);
        mCoinTxt.setTypeface(FontHelper.getDinAlternateBoldFont(mContext));
    }

    public void setOpenVideoListener(OnCoinOpenVideoListener openVideoListener) {
        mOpenVideoListener = openVideoListener;
    }

    public void setCoinOpenInfo(long coin, CoinOpenBean.PreviousTask taskBean) {
        mCoinTxt.setText(String.valueOf(coin));
        mTaskBean = taskBean;
    }

    @OnClick({R.id.cancel_txt, R.id.video_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_txt:
                if (mOpenVideoListener != null && mTaskBean != null) {
                    mOpenVideoListener.onCoinOpenVideoCancel();
                }
                StatisticsAgent.click(mContext, StatisticsUtils.CID.CID_402, StatisticsUtils.MD.MD_2);
                break;
            case R.id.video_txt:
                if (mOpenVideoListener != null && mTaskBean != null) {
                    mOpenVideoListener.onCoinOpenVideoSelect(mTaskBean);
                }
                StatisticsAgent.click(mContext, StatisticsUtils.CID.CID_401, StatisticsUtils.MD.MD_2);
                break;
            default:
                break;
        }
        dismiss();
    }
}
