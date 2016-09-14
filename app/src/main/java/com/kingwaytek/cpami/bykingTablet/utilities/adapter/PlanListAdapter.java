package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent.chang on 2016/6/3.
 */
public class PlanListAdapter extends BaseAdapter {

    private static final String TAG = "PlanListAdapter";

    private ArrayList<String[]> planPairList;
    private LayoutInflater inflater;

    private boolean showCheckBox;
    private HashMap<Integer, Boolean> checkedMap;

    private boolean isUploadMode;

    /**
     * @param planPairList The list contains name and updated time of each plans.
     */
    public PlanListAdapter(Context context, ArrayList<String[]> planPairList) {
        this.planPairList = planPairList;
        inflater = LayoutInflater.from(context);
        checkedMap = new HashMap<>();
    }

    public void refreshList(ArrayList<String[]> planNameList) {
        this.planPairList = planNameList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return planPairList.size();
    }

    @Override
    public Object getItem(int position) {
        return planPairList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_my_plan_list, null);

            holder = new ViewHolder();
            holder.planName = (TextView) convertView.findViewById(R.id.text_planName);
            holder.planDate = (TextView) convertView.findViewById(R.id.text_planDate);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_eachPlan);
            holder.rightArrow = (ImageView) convertView.findViewById(R.id.icon_rightArrow);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.planName.setText(planPairList.get(position)[0]);
        holder.planDate.setText(planPairList.get(position)[1]);

        if (!checkedMap.containsKey(position)) {
            checkedMap.put(position, false);
            Log.i(TAG, "checkedMap key: " + position);
        }

        if (showCheckBox) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.rightArrow.setVisibility(View.GONE);

            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkedMap.put(position, isChecked);
                    notifyDataSetChanged();
                }
            });
        }
        else {
            holder.checkbox.setVisibility(View.GONE);
            holder.rightArrow.setVisibility(View.VISIBLE);
            holder.checkbox.setOnCheckedChangeListener(null);
        }

        if (checkedMap.containsKey(position) && checkedMap.get(position)) {
            convertView.setBackgroundColor(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_grey_300));
            holder.checkbox.setChecked(true);
        }
        else {
            convertView.setBackgroundColor(0);
            holder.checkbox.setChecked(false);
        }

        convertView.setBackgroundResource(isUploadMode ? R.drawable.background_upload_item : 0);

        //boolean isThisPositionChecked = checkedMap.containsKey(position) && checkedMap.get(position);
        //Log.i(TAG, "isThisPositionChecked: " + position + " " + isThisPositionChecked);

        return convertView;
    }

    public void showCheckBox(boolean isShow) {
        showCheckBox = isShow;
    }

    public void setBoxChecked(int position) {
        if (checkedMap.containsKey(position))
            checkedMap.put(position, true);

        notifyDataSetChanged();
    }

    public void unCheckAllBox() {
        for (int i = 0; i < checkedMap.size(); i++) {
            if (checkedMap.containsKey(i))
                checkedMap.put(i, false);
        }
        showCheckBox(false);

        notifyDataSetChanged();
    }

    public boolean isCheckBoxShowing() {
        return showCheckBox;
    }

    public ArrayList<Integer> getCheckedList() {
        ArrayList<Integer> checkedList = new ArrayList<>();

        for (int i = 0; i < checkedMap.size(); i++) {
            if (checkedMap.containsKey(i) && checkedMap.get(i))
                checkedList.add(i);
        }

        return checkedList;
    }

    public void setUploadRowBackground(boolean isUploadMode) {
        this.isUploadMode = isUploadMode;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView planName;
        TextView planDate;
        CheckBox checkbox;
        ImageView rightArrow;
    }
}