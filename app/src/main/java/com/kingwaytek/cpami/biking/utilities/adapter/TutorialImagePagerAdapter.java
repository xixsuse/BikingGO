package com.kingwaytek.cpami.biking.utilities.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kingwaytek.cpami.biking.app.ui.fragment.UiTutorialImageFragment;

/**
 * Created by vincent.chang on 2016/9/7.
 */
public class TutorialImagePagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 5;

    public TutorialImagePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return UiTutorialImageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
