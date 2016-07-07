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
 * Created by vincent.chang on 2016/7/7.
 */
public class EventListAdapter extends BaseAdapter {

    private ArrayList<String> nameList;
    private LayoutInflater inflater;

    public EventListAdapter(Context context,ArrayList<String> nameList) {
        this.nameList = nameList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return nameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_event_list, null);

            holder = new ViewHolder();
            holder.eventName = (TextView) convertView.findViewById(R.id.text_eventName);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.eventName.setText(nameList.get(position));

        return convertView;
    }

    private class ViewHolder {
        TextView eventName;
    }
}
