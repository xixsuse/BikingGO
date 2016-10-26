package com.kingwaytek.cpami.biking.utilities.adapter;

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

import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.items.ItemsTrackRecord;
import com.kingwaytek.cpami.biking.app.ui.track.UiTrackListActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent.chang on 2016/7/22.
 */
public class TrackListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemsTrackRecord> trackItemList;

    private boolean showCheckBox;
    private HashMap<Integer, Boolean> checkedMap;

    private boolean isUploadMode;

    public TrackListAdapter(Context context, ArrayList<ItemsTrackRecord> trackItemList) {
        this.trackItemList = trackItemList;
        this.context = context;

        checkedMap = new HashMap<>();
    }

    public void refreshList(ArrayList<ItemsTrackRecord> trackItemList) {
        this.trackItemList = trackItemList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return trackItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return trackItemList.get(getCount() - (position + 1));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.inflate_track_list, null);

            holder = new ViewHolder();
            holder.trackName = (TextView) convertView.findViewById(R.id.edit_trackName);
            holder.trackTime = (TextView) convertView.findViewById(R.id.text_trackTime);
            holder.trackDistance = (TextView) convertView.findViewById(R.id.text_trackDistance);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_eachTrack);
            holder.rightArrow = (ImageView) convertView.findViewById(R.id.icon_rightArrow);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.trackName.setText(trackItemList.get(position).NAME);
        holder.trackTime.setText(trackItemList.get(position).DATE);
        holder.trackDistance.setText(trackItemList.get(position).DISTANCE);

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

        if (isUploadMode) {
            holder.rightArrow.setImageResource(R.drawable.selector_toolbar_upload);
            holder.rightArrow.setBackgroundColor(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_blue_grey_900));
            holder.rightArrow.setOnClickListener(getUploadClick(trackItemList.get(position).NAME, position));
        }
        else {
            holder.rightArrow.setImageResource(R.drawable.ic_right_arrow_grey);
            holder.rightArrow.setBackgroundResource(0);
            holder.rightArrow.setOnClickListener(null);
        }
        holder.rightArrow.setFocusable(false);
        holder.rightArrow.setFocusableInTouchMode(false);
        holder.rightArrow.setClickable(true);

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
                ((UiTrackListActivity) context).uploadTrack(name, position);
            }
        };
    }

    private class ViewHolder {
        TextView trackName;
        TextView trackTime;
        TextView trackDistance;
        CheckBox checkbox;
        ImageView rightArrow;
    }
}
