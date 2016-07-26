package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
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
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTrackRecord;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent.chang on 2016/7/22.
 */
public class TrackListAdapter extends BaseAdapter {

    private ArrayList<ItemsTrackRecord> trackNameList;
    private LayoutInflater inflater;

    private boolean showCheckBox;
    private HashMap<Integer, Boolean> checkedMap;

    public TrackListAdapter(Context context, ArrayList<ItemsTrackRecord> trackNameList) {
        this.trackNameList = trackNameList;
        inflater = LayoutInflater.from(context);
        checkedMap = new HashMap<>();
    }

    public void refreshList(ArrayList<ItemsTrackRecord> trackNameList) {
        this.trackNameList = trackNameList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return trackNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return trackNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_track_list, null);

            holder = new ViewHolder();
            holder.trackName = (TextView) convertView.findViewById(R.id.text_trackName);
            holder.trackTime = (TextView) convertView.findViewById(R.id.text_trackTime);
            holder.trackDistance = (TextView) convertView.findViewById(R.id.text_trackDistance);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_eachTrack);
            holder.rightArrow = (ImageView) convertView.findViewById(R.id.icon_rightArrow);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.trackName.setText(trackNameList.get(position).NAME);
        holder.trackTime.setText(trackNameList.get(position).TIME);
        holder.trackDistance.setText(trackNameList.get(position).DISTANCE);

        if (!checkedMap.containsKey(position))
            checkedMap.put(position, false);

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

    private class ViewHolder {
        TextView trackName;
        TextView trackTime;
        TextView trackDistance;
        CheckBox checkbox;
        ImageView rightArrow;
    }
}
