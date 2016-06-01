package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class PlanEditListAdapter extends BaseAdapter {

    private ArrayList<ItemsPlanItem> planItemList;
    private LayoutInflater inflater;

    public PlanEditListAdapter(Context context, ArrayList<ItemsPlanItem> planItemList) {
        this.planItemList = planItemList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return planItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return planItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        return null;
    }
}
