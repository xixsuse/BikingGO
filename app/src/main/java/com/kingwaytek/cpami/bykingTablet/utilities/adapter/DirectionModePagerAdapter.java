package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by vincent.chang on 2016/8/12.
 */
public class DirectionModePagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 2;

    public DirectionModePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {


        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
