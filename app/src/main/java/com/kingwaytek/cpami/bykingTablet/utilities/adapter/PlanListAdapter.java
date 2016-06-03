package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/3.
 */
public class PlanListAdapter extends BaseAdapter {

    private ArrayList<String> planNameList;
    private LayoutInflater inflater;

    public PlanListAdapter(Context context, ArrayList<String> planNameList) {
        this.planNameList = planNameList;
        inflater = LayoutInflater.from(context);
    }

    public void refreshList(ArrayList<String> planNameList) {
        this.planNameList = planNameList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return planNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return planNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_my_plan_list, null);

            holder = new ViewHolder();
            holder.planName = (TextView) convertView.findViewById(R.id.text_planName);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.planName.setText(planNameList.get(position));

        return convertView;
    }

    private class ViewHolder {
        TextView planName;
    }
}
