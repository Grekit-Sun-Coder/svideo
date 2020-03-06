package cn.weli.svideo.common.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-18
 * @see [class/method]
 * @since [1.0.0]
 */
public class SeparatorPhoneEditView extends AppCompatEditText implements TextWatcher {
    //允许最大输入长度为11+2,考虑两个占位符占的位数
    private final int MAX_INPUT_LENGTH = 13;
    //分隔符,一般为-或半角空格
    private final char SEPARATOR = ' ';

    private StringBuilder mBuilder = new StringBuilder();
    private int mChangeCount;

    private OnPhoneEditTxtChangedListener mTxtChangedListener;

    public SeparatorPhoneEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeparatorPhoneEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setTxtChangedListener(OnPhoneEditTxtChangedListener txtChangedListener) {
        mTxtChangedListener = txtChangedListener;
    }

    private void init() {
        requestFocus();
        //设置键盘输入模式
        setInputType(InputType.TYPE_CLASS_NUMBER);
        //设置最大输入长度
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
        addTextChangedListener(this);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mChangeCount = count - before;
        if (mTxtChangedListener != null) {
            mTxtChangedListener.onPhoneEditChanged(getPhoneCode());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //当只有输入数字时才进行调整
        if (mChangeCount > 0 && !TextUtils.equals(s, mBuilder.toString())) {
            mBuilder.delete(0, mBuilder.length());
            //因为在setText会重置为0，所以要在setText前记录光标的位置
            int cursorIndex = getSelectionStart();
            int length = s.length();
            for (int i = 0; i < length; i++) {
                char c = s.charAt(i);
                if (c != SEPARATOR) {
                    mBuilder.append(c);
                }
                if ((isSeparationPosition(mBuilder.length()))) {
                    mBuilder.append(SEPARATOR);
                }
            }
            //可能会得到大于最大长度的字符串，将最后一个删除
            if (mBuilder.length() > MAX_INPUT_LENGTH) {
                mBuilder.delete(MAX_INPUT_LENGTH, mBuilder.length());
            }
            //计算光标的偏移量
            int offset = calculateOffset(s, cursorIndex);
            setText(mBuilder.toString());
            //光标位置,做最小值判断是为防止越界,也是为了调整光标位
            setSelection(Math.min(cursorIndex + offset, mBuilder.length()));
        }
    }

    /**
     * 计算光标位之前需要的偏移量
     */
    private int calculateOffset(Editable s, int cursorIndex) {
        int length = s.length();
        //需要偏移的光标位数，负数表示向前调，正数表示向后偏移
        int offset = 0;
        //调整后长度
        int adjustedLen = mBuilder.length();
        int minLen = adjustedLen > length ? length : adjustedLen;
        for (int i = 0; i < minLen; i++) {
            if (i > cursorIndex - 1) {
                break;
            }

            char c = s.charAt(i);
            char adjustedChar = mBuilder.charAt(i);
            //只需要考虑调整后字符类型不同的情况
            if (c == SEPARATOR && adjustedChar != SEPARATOR) {
                offset--;
            } else if (c != SEPARATOR && adjustedChar == SEPARATOR) {
                offset++;
            }
        }
        return offset;
    }

    /**
     * 是否分隔位置
     */
    private boolean isSeparationPosition(int index) {
        //前面有几位数
        final int NUMBER_FRONT = 3;
        //中间有几位数
        final int NUMBER_MIDDLE = 4;
        //第二个空位的位置
        final int SECOND_SEPARATOR_POSITION = NUMBER_FRONT + NUMBER_MIDDLE + 1;
        return index == NUMBER_FRONT || index == SECOND_SEPARATOR_POSITION;
    }

    public String getPhoneCode() {
        Editable text = getText();
        return text == null ? null : text.toString().replace(String.valueOf(SEPARATOR), "");
    }

    public interface OnPhoneEditTxtChangedListener {

        void onPhoneEditChanged(String text);
    }
}