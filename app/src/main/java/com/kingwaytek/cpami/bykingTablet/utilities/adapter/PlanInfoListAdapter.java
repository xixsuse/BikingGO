package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/4.
 */
public class PlanInfoListAdapter extends BaseAdapter {

    private ArrayList<ItemsPlanItem> planItemList;
    private LayoutInflater inflater;

    public PlanInfoListAdapter(Context context, ArrayList<ItemsPlanItem> planItemList) {
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_plan_info, null);

            holder = new ViewHolder();
            holder.planNumberText = (TextView) convertView.findViewById(R.id.text_planNumber);
            holder.planNumberLine_down = convertView.findViewById(R.id.planNumberLine_down);
            holder.planNumberLine_up = convertView.findViewById(R.id.planNumberLine_up);
            holder.planName = (TextView) convertView.findViewById(R.id.text_planName);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.planNumberText.setText(String.valueOf(position + 1));

        if (position == (getCount() - 1))
            holder.planNumberLine_down.setVisibility(View.INVISIBLE);
        else
            holder.planNumberLine_down.setVisibility(View.VISIBLE);

        if (position == 0)
            holder.planNumberLine_up.setVisibility(View.INVISIBLE);
        else
            holder.planNumberLine_up.setVisibility(View.VISIBLE);

        holder.planName.setText(planItemList.get(position).TITLE);

        return convertView;
    }

    private class ViewHolder {
        TextView planNumberText;
        View planNumberLine_up;
        View planNumberLine_down;
        TextView planName;
    }
}
