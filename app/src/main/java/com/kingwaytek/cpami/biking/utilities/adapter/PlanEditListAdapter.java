package com.kingwaytek.cpami.biking.utilities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.biking.app.ui.planning.UiMyPlanEditActivity;
import com.kingwaytek.cpami.biking.utilities.DialogHelper;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/1.
 */
public class PlanEditListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemsPlanItem> planItemList;
    private LayoutInflater inflater;

    private View.OnClickListener planSelectClick;
    private View.OnLongClickListener planLongClick;

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
        notifyDataSetChanged();
    }

    public void insertPlanItem(int toPosition, ItemsPlanItem planItem) {
        planItemList.add(toPosition, planItem);
        notifyDataSetChanged();
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
        holder.planSelectedName.setTag(position);
        holder.planSelectedName.setOnClickListener(getPlanSelectClick());
        holder.planSelectedName.setOnLongClickListener(getLongClick());

        return convertView;
    }

    private View.OnClickListener getRemoveClick(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePlanItem(position);
                ((UiMyPlanEditActivity) context).hidePlanAddButtonIfReachMax();
            }
        };
    }

    private View.OnClickListener getPlanSelectClick() {
        if (planSelectClick == null) {
            planSelectClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((UiMyPlanEditActivity) context).selectPlanItem((int)v.getTag());
                }
            };
        }
        return planSelectClick;
    }

    private View.OnLongClickListener getLongClick() {
        if (planLongClick == null) {
            planLongClick = new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final int index = (int) v.getTag();
                    DialogHelper.showSpotTitleEditDialog(context, planItemList.get(index).TITLE, true, new DialogHelper.OnSpotTitleConfirmCallback() {
                        @Override
                        public void onSpotTitleConfirm(String title) {
                            planItemList.get(index).setTitle(title);
                            notifyDataSetChanged();
                        }
                    });
                    return true;
                }
            };
        }
        return planLongClick;
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
