package cn.weli.svideo.baselib.component.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * 通用FragmentAdapter
 *
 * @author Lei Jiang
 * @version [1.0.0]
 * @date 2019/11/04
 * @see CommonFragmentAdapter
 * @since [1.0.0]
 */

public class CommonFragmentAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> mList = new ArrayList<>();

    public CommonFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        mList.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
