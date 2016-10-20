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
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanPreview;
import com.kingwaytek.cpami.bykingTablet.app.ui.planning.UiMyPlanListActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent.chang on 2016/6/3.
 */
public class PlanListAdapter extends BaseAdapter {

    private static final String TAG = "PlanListAdapter";

    private Context context;
    private ArrayList<ItemsPlanPreview> planPreviewList;

    private boolean showCheckBox;
    private HashMap<Integer, Boolean> checkedMap;

    private boolean isUploadMode;

    /**
     * @param planPreviewList The list of Plan preview item, each item contains NAME, DATE and SPOT_COUNTS.
     */
    public PlanListAdapter(Context context, ArrayList<ItemsPlanPreview> planPreviewList) {
        this.planPreviewList = planPreviewList;
        this.context = context;
        checkedMap = new HashMap<>();
    }

    public void refreshList(ArrayList<ItemsPlanPreview> planPreviewList) {
        this.planPreviewList = planPreviewList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return planPreviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return planPreviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.inflate_my_plan_list, null);

            holder = new ViewHolder();
            holder.planName = (TextView) convertView.findViewById(R.id.text_planName);
            holder.planDate = (TextView) convertView.findViewById(R.id.text_planDate);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_eachPlan);
            holder.rightArrow = (ImageView) convertView.findViewById(R.id.icon_rightArrow);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.planName.setText(planPreviewList.get(position).NAME);
        holder.planDate.setText(planPreviewList.get(position).DATE);

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

        if (isUploadMode) {
            holder.rightArrow.setImageResource(R.drawable.selector_toolbar_upload);
            int colorRes = planPreviewList.get(position).SPOT_COUNTS > 1 ? R.color.md_grey_900 : R.color.md_grey_600;
            holder.rightArrow.setBackgroundColor(ContextCompat.getColor(AppController.getInstance().getAppContext(), colorRes));
            holder.rightArrow.setOnClickListener(getUploadClick(planPreviewList.get(position).NAME, position));
        }
        else {
            holder.rightArrow.setImageResource(R.drawable.ic_right_arrow_grey);
            holder.rightArrow.setBackgroundResource(0);
            holder.rightArrow.setOnClickListener(null);
        }
        holder.rightArrow.setFocusable(false);
        holder.rightArrow.setFocusableInTouchMode(false);
        holder.rightArrow.setClickable(true);

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

    public void setUploadMode(boolean isUploadMode) {
        this.isUploadMode = isUploadMode;
        notifyDataSetChanged();
    }

    public boolean isUploadMode() {
        return isUploadMode;
    }

    private View.OnClickListener getUploadClick(final String name, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSpotsMoreThenOne(position))
                    ((UiMyPlanListActivity) context).uploadPlan(name, position);
                else
                    Utility.toastLong(AppController.getInstance().getString(R.string.plan_require_more_then_two_for_upload));
            }
        };
    }

    private boolean isSpotsMoreThenOne(int position) {
        return planPreviewList.get(position).SPOT_COUNTS > 1;
    }

    public String getName(int position) {
        return planPreviewList.get(position).NAME;
    }

    private class ViewHolder {
        TextView planName;
        TextView planDate;
        CheckBox checkbox;
        ImageView rightArrow;
    }
}