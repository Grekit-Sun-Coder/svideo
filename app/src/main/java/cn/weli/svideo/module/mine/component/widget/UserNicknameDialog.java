package cn.weli.svideo.module.mine.component.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.weli.svideo.R;
import cn.weli.svideo.baselib.component.widget.dialog.BaseDialog;
import cn.weli.svideo.baselib.component.widget.toast.WeToast;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-12-23
 * @see [class/method]
 * @since [1.0.0]
 */
public class UserNicknameDialog extends BaseDialog {

    @BindView(R.id.nickname_edit)
    EditText mNicknameEdit;

    private OnUserNickChangeListener mChangeListener;

    public interface OnUserNickChangeListener {

        void onUserNickChanged(String nickname);
    }

    public UserNicknameDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_user_nickname);
        ButterKnife.bind(this);
    }

    public void setCurrentNickname(String nickname) {
        if (!StringUtil.isNull(nickname)) {
            mNicknameEdit.setText(nickname);
            mNicknameEdit.setSelection(mNicknameEdit.getText().toString().length());
        }
    }

    public void setChangeListener(OnUserNickChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    @OnClick({R.id.normal_button_pos, R.id.normal_button_neg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.normal_button_pos:
                if (StringUtil.isNull(mNicknameEdit.getText().toString())) {
                    WeToast.getInstance().showToast(mContext, R.string.user_info_nickname_input);
                    return;
                }
                if (mChangeListener != null) {
                    mChangeListener.onUserNickChanged(mNicknameEdit.getText().toString().trim());
                }
                break;
            case R.id.normal_button_neg:
                dismiss();
                break;
            default:
                break;
        }
    }
}
