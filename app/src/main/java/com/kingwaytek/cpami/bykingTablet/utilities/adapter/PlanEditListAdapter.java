package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.bykingTablet.app.ui.planning.UiMyPlanEditActivity;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class PlanEditListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemsPlanItem> planItemList;
    private LayoutInflater inflater;

    public PlanEditListAdapter(Context context, ArrayList<ItemsPlanItem> planItemList) {
        this.context = context;
        this.planItemList = planItemList;
        inflater = LayoutInflater.from(context);
    }

    public void addPlanItem(ItemsPlanItem planItem) {
        planItemList.add(planItem);
        notifyDataSetChanged();
    }

    public void setPlanItem(int index, ItemsPlanItem planItem) {
        planItemList.set(index, planItem);
        notifyDataSetChanged();
    }

    public void removePlanItem(int position) {
        planItemList.remove(position);
        resetAllPlanItemOrder();
        notifyDataSetChanged();
    }

    public void insertPlanItem(int toPosition, ItemsPlanItem planItem) {
        planItemList.add(toPosition, planItem);
        resetAllPlanItemOrder();
        notifyDataSetChanged();
    }

    public void resetAllPlanItemOrder() {
        int order = 1;

        for (ItemsPlanItem planItem : planItemList) {
            planItem.setOrder(order);
            planItemList.set(order - 1, planItem);
            order++;
        }
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
            convertView = inflater.inflate(R.layout.inflate_plan_edit, null);

            holder = new ViewHolder();
            holder.planNumberText = (TextView) convertView.findViewById(R.id.text_planNumber);
            holder.planNumberLine_up = convertView.findViewById(R.id.planNumberLine_up);
            holder.planNumberLine_down = convertView.findViewById(R.id.planNumberLine_down);
            holder.dragIcon = (ImageView) convertView.findViewById(R.id.drag_handle);
            holder.planSelectedName = (TextView) convertView.findViewById(R.id.text_planSelectedName);
            holder.planDeleteBtn = (ImageButton) convertView.findViewById(R.id.drag_click_remove);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.planNumberText.setText(String.valueOf(position + 1));
        holder.planDeleteBtn.setOnClickListener(getRemoveClick(position));

        if (position == (getCount() - 1))
            holder.planNumberLine_down.setVisibility(View.INVISIBLE);
        else
            holder.planNumberLine_down.setVisibility(View.VISIBLE);

        if (position == 0)
            holder.planNumberLine_up.setVisibility(View.INVISIBLE);
        else
            holder.planNumberLine_up.setVisibility(View.VISIBLE);

        holder.planSelectedName.setText(planItemList.get(position).TITLE);
        holder.planSelectedName.setOnClickListener(getPlanSelectClick(position));

        Log.i("PlanEditListAdapter", "position: " + position + " Order: " + planItemList.get(position).ORDER);

        return convertView;
    }

    private View.OnClickListener getRemoveClick(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlanItem(position);
                ((UiMyPlanEditActivity) context).hidePlanAddButtonIfHasFive();
            }
        };
    }

    private View.OnClickListener getPlanSelectClick(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UiMyPlanEditActivity) context).selectPlanItem(position);
            }
        };
    }

    private class ViewHolder {
        TextView planNumberText;
        View planNumberLine_up;
        View planNumberLine_down;
        ImageView dragIcon;
        TextView planSelectedName;
        ImageButton planDeleteBtn;
    }
}
