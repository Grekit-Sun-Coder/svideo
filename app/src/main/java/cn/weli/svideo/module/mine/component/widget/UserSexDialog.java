package cn.weli.svideo.module.mine.component.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.widget.dialog.BaseDialog;
import cn.weli.svideo.baselib.utils.StringUtil;
import cn.weli.svideo.module.mine.model.bean.UserInfoBean;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-23
 * @see [class/method]
 * @since [1.0.0]
 */
public class UserSexDialog extends BaseDialog {

    @BindView(R.id.user_sex_man_txt)
    TextView mUserSexManTxt;
    @BindView(R.id.user_sex_woman_txt)
    TextView mUserSexWomanTxt;

    private Drawable mNormalDrawable;
    private Drawable mSelectedDrawable;

    private OnUserSexListener mSexListener;

    public interface OnUserSexListener {

        void onUserSexSelect(String sex);
    }

    public UserSexDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_user_sex);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);
        mNormalDrawable = ContextCompat.getDrawable(mContext, R.drawable.my_icon_choose_normal);
        if (mNormalDrawable != null) {
            mNormalDrawable.setBounds(0, 0, mNormalDrawable.getMinimumWidth(), mNormalDrawable.getMinimumHeight());
        }
        mSelectedDrawable = ContextCompat.getDrawable(mContext, R.drawable.my_icon_choose_selected);
        if (mSelectedDrawable != null) {
            mSelectedDrawable.setBounds(0, 0, mSelectedDrawable.getMinimumWidth(), mSelectedDrawable.getMinimumHeight());
        }
    }

    public void setSexListener(OnUserSexListener sexListener) {
        mSexListener = sexListener;
    }

    public void setCurrentSex(String sex) {
        if (StringUtil.equals(sex, UserInfoBean.SEX_MAN)) {
            mUserSexManTxt.setCompoundDrawables(null, null, mSelectedDrawable, null);
        } else {
            mUserSexManTxt.setCompoundDrawables(null, null, mNormalDrawable, null);
        }

        if (StringUtil.equals(sex, UserInfoBean.SEX_WOMAN)) {
            mUserSexWomanTxt.setCompoundDrawables(null, null, mSelectedDrawable, null);
        } else {
            mUserSexWomanTxt.setCompoundDrawables(null, null, mNormalDrawable, null);
        }
    }

    @OnClick({R.id.user_sex_man_txt, R.id.user_sex_woman_txt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_sex_man_txt:
                setCurrentSex(UserInfoBean.SEX_MAN);
                if (mSexListener != null) {
                    mSexListener.onUserSexSelect(UserInfoBean.SEX_MAN);
                }
                break;
            case R.id.user_sex_woman_txt:
                setCurrentSex(UserInfoBean.SEX_WOMAN);
                if (mSexListener != null) {
                    mSexListener.onUserSexSelect(UserInfoBean.SEX_WOMAN);
                }
                break;
            default:
                break;
        }
    }
}
