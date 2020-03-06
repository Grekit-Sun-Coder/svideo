package cn.weli.svideo.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;

import cn.weli.svideo.R;
import cn.weli.svideo.baselib.utils.StringUtil;

/**
 * 金额带单位
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-19
 * @see [class/method]
 * @since [1.0.0]
 */
public class RichTextView extends AppCompatTextView {
    /**
     * 金额整数部分大小
     */
    private float mIntegerSize;
    /**
     * 金额小数部分大小
     */
    private float mDecimalSize;
    /**
     * 单位部分大小
     */
    private float mUnitSize;
    /**
     * 金额单位值调整间距
     */
    private int mOffset;

    public RichTextView(Context context) {
        this(context, null);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int defaultIntegerSize = context.getResources().getDimensionPixelSize(R.dimen
                .common_text_size_70px);
        int defaultDecimalSize = context.getResources().getDimensionPixelSize(R.dimen
                .common_text_size_70px);
        int defaultTexColor = ContextCompat.getColor(context, R.color.color_FFC244);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RichTextView);
        if (mTypedArray == null) {
            return;
        }
        mIntegerSize = mTypedArray.getDimension(R.styleable.RichTextView_richText_integer_textSize,
                defaultIntegerSize);
        mDecimalSize = mTypedArray.getDimension(R.styleable.RichTextView_richText_decimal_textSize,
                defaultDecimalSize);
        mUnitSize = mTypedArray.getDimension(R.styleable.RichTextView_richText_unit_textSize,
                defaultDecimalSize);
        int textColor = mTypedArray.getColor(R.styleable.RichTextView_richText_Color,
                defaultTexColor);
        String text = mTypedArray.getString(R.styleable.RichTextView_richText_text);
        //获取资源后要及时回收
        mTypedArray.recycle();

        mOffset = (int) (mDecimalSize / 10.0 + 0.5);
        setTextColor(textColor);
        if (!StringUtil.isNull(text)) {
            setRichText(text);
        }
    }

    /**
     * 处理金额值
     *
     * @param amount 金额值
     */
    public void setRichText(String amount) {
        if (StringUtil.isNull(amount)) {
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(amount);
        String priceInt;
        if (amount.contains(StringUtil.POINT_STR)) {
            priceInt = amount.substring(0, amount.indexOf(StringUtil.POINT_STR));
            builder.setSpan(new AbsoluteSizeSpan((int) mDecimalSize), priceInt.length(), amount
                    .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            priceInt = amount;
        }
        builder.setSpan(new AbsoluteSizeSpan((int) mIntegerSize), 0, priceInt.length(), Spannable
                .SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new AbsoluteSizeSpan((int) mDecimalSize - mOffset), amount.length(),
                builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(StringUtil.SPACE_STR);
        builder.append(getContext().getString(R.string.common_str_yuan));
        builder.setSpan(new AbsoluteSizeSpan((int) mUnitSize - mOffset), amount.length(),
                builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(builder);
    }

    /**
     * 处理金额值
     *
     * @param amount 金额值
     * @param unit   单位
     */
    public void setRichText(String amount, String unit) {
        if (StringUtil.isNull(amount)) {
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(amount);
        String priceInt;
        if (amount.contains(StringUtil.POINT_STR)) {
            priceInt = amount.substring(0, amount.indexOf(StringUtil.POINT_STR));
            builder.setSpan(new AbsoluteSizeSpan((int) mDecimalSize), priceInt.length(), amount
                    .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            priceInt = amount;
        }
        builder.setSpan(new AbsoluteSizeSpan((int) mIntegerSize), 0, priceInt.length(), Spannable
                .SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new AbsoluteSizeSpan((int) mDecimalSize - mOffset), amount.length(),
                builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(StringUtil.SPACE_STR);
        builder.append(unit);
        builder.setSpan(new AbsoluteSizeSpan((int) mUnitSize - mOffset), amount.length(),
                builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(builder);
    }
}