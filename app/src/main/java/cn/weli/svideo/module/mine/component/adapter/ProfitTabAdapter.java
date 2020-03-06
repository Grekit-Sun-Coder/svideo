package cn.weli.svideo.module.mine.component.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.List;

import cn.weli.svideo.R;

/**
 * Function description
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019-11-20
 * @see [class/method]
 * @since [1.0.0]
 */
public class ProfitTabAdapter extends CommonNavigatorAdapter {

    private Context mContext;
    private List<String> mDataList;
    private ViewPager mViewPager;

    public ProfitTabAdapter(Context context, List<String> dataList, ViewPager viewPager) {
        mContext = context;
        mDataList = dataList;
        mViewPager = viewPager;
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public IPagerTitleView getTitleView(Context context, int index) {
        SimplePagerTitleView textView = new ColorTransitionPagerTitleView(context);
        int hPadding = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_40px);
        int bPadding = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_10px);
        textView.setPadding(hPadding, 0, hPadding, bPadding);
        textView.setText(mDataList.get(index));
        textView.setTextSize(18);
        textView.setNormalColor(ContextCompat.getColor(mContext, R.color.color_888888));
        textView.setSelectedColor(ContextCompat.getColor(mContext, R.color.color_E7E7E7));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(index);
            }
        });
        return textView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
        int indicatorWidth = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_24px);
        int indicatorHeight = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_10px);
        int indicatorRadius = mContext.getResources().getDimensionPixelSize(R.dimen.common_len_12px);
        indicator.setLineWidth(indicatorWidth);
        indicator.setLineHeight(indicatorHeight);
        indicator.setRoundRadius(indicatorRadius);
        indicator.setColors(ContextCompat.getColor(mContext, R.color.color_50_E7E7E7));
        return indicator;
    }
}
