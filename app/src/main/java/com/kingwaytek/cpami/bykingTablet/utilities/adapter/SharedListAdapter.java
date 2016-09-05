package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsShared;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/9/2.
 */
public class SharedListAdapter extends BaseAdapter {

    private ArrayList<ItemsShared> sharedItemList;
    private LayoutInflater inflater;

    public SharedListAdapter(Context context, ArrayList<ItemsShared> sharedItemList) {
        this.sharedItemList = sharedItemList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sharedItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return sharedItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_shared_list, null);

            holder = new ViewHolder();
            holder.popularityIcon = (ImageView) convertView.findViewById(R.id.popularityImageView);
            holder.name = (TextView) convertView.findViewById(R.id.text_sharedItemName);
            holder.date = (TextView) convertView.findViewById(R.id.text_sharedItemDate);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.name.setText(sharedItemList.get(position).NAME);
        holder.date.setText(sharedItemList.get(position).DATE);

        return convertView;
    }

    private class ViewHolder {
        ImageView popularityIcon;
        TextView name;
        TextView date;
    }
}
