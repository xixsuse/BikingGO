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
 * Created by vincent.chang on 2016/7/22.
 */
public class TrackListAdapter extends BaseAdapter {

    private ArrayList<String> trackNameList;
    private LayoutInflater inflater;

    public TrackListAdapter(Context context, ArrayList<String> trackNameList) {
        this.trackNameList = trackNameList;
        inflater = LayoutInflater.from(context);
    }

    public void refreshList(ArrayList<String> trackNameList) {
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_track_list, null);

            holder = new ViewHolder();
            holder.trackName = (TextView) convertView.findViewById(R.id.text_trackName);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.trackName.setText(trackNameList.get(position));

        return convertView;
    }

    private class ViewHolder {
        TextView trackName;
    }
}
