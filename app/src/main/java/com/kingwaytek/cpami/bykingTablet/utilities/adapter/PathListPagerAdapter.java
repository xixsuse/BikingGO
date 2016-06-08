package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/7.
 */
public class PathListPagerAdapter extends PagerAdapter {

    private ArrayList<View> viewList;

    public PathListPagerAdapter(ArrayList<View> viewList) {
        this.viewList = viewList;
    }

    public void refeshList(ArrayList<View> viewList) {
        this.viewList = viewList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position), 0);
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
