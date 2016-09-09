package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kingwaytek.cpami.bykingTablet.app.ui.fragment.UiPoiPhotoFragment;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/9/7.
 */
public class PoiPhotoPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<String> photoPathList;

    public PoiPhotoPagerAdapter(FragmentManager fm, ArrayList<String> photoPathList) {
        super(fm);
        this.photoPathList = photoPathList;
    }

    @Override
    public Fragment getItem(int position) {
        return UiPoiPhotoFragment.getInstance(photoPathList.get(position), position + 1);
    }

    @Override
    public int getCount() {
        return photoPathList.size();
    }
}
