package com.kingwaytek.cpami.biking.utilities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.items.ItemsShared;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/9/2.
 */
public class SharedListAdapter extends BaseAdapter {

    private boolean isPlanType;
    private ArrayList<ItemsShared> sharedItemList;
    private LayoutInflater inflater;

    private ArrayList<ItemsShared> originalList;
    private boolean showPopularityIcon = true;

    public SharedListAdapter(Context context, boolean isPlanType, ArrayList<ItemsShared> sharedItemList) {
        this.isPlanType = isPlanType;
        this.sharedItemList = sharedItemList;
        this.inflater = LayoutInflater.from(context);
        originalList = new ArrayList<>();
        originalList.addAll(sharedItemList);
    }

    public void refreshData(ArrayList<ItemsShared> sharedItemList) {
        this.sharedItemList = sharedItemList;
        originalList.clear();
        originalList.addAll(sharedItemList);
        notifyDataSetChanged();
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
        return sharedItemList.get(position).ID;
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

        if (showPopularityIcon && position < 5)
            holder.popularityIcon.setImageResource(R.drawable.ic_popular_item);
        else
            holder.popularityIcon.setImageResource(isPlanType ? R.drawable.ic_planning : R.drawable.ic_tracking);

        return convertView;
    }

    private class ViewHolder {
        ImageView popularityIcon;
        TextView name;
        TextView date;
    }

    public void filterData(String queryString) {
        sharedItemList.clear();

        if (queryString.isEmpty()) {
            sharedItemList.addAll(originalList);
            showPopularityIcon = true;
        }
        else {
            for (ItemsShared sharedItem : originalList) {
                if (sharedItem.NAME.contains(queryString))
                    sharedItemList.add(sharedItem);
            }
            showPopularityIcon = false;
        }
        notifyDataSetChanged();
    }
}
