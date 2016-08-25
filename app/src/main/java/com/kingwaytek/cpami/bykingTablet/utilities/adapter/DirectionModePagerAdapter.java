package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kingwaytek.cpami.bykingTablet.app.ui.fragment.UiDirectionModeFragment;

/**
 * Created by vincent.chang on 2016/8/12.
 */
public class DirectionModePagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 1;

    private String[] fromTo;

    public DirectionModePagerAdapter(FragmentManager fm, String origin, String destination) {
        super(fm);
        this.fromTo = new String[]{origin, destination};
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return UiDirectionModeFragment.getInstance(UiDirectionModeFragment.MODE_WALK, fromTo);
        else
            return UiDirectionModeFragment.getInstance(UiDirectionModeFragment.MODE_TRANSIT, fromTo);
    }

    public UiDirectionModeFragment getDirectionFragmentInstance(int position) {
        if (position == 0)
            return UiDirectionModeFragment.getInstance(UiDirectionModeFragment.MODE_WALK);
        else
            return UiDirectionModeFragment.getInstance(UiDirectionModeFragment.MODE_TRANSIT);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
