package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

/**
 * Created by vincent.chang on 2016/6/17.
 */
public class PhotoOptionsAdapter extends BaseAdapter {

    private String[] options;
    private LayoutInflater inflater;

    public PhotoOptionsAdapter(Context context, String[] options) {
        this.options = options;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return options.length;
    }

    @Override
    public Object getItem(int position) {
        return options[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_photo_options, null);
            holder = new ViewHolder();

            holder.icon = (ImageView) convertView.findViewById(R.id.photoOptions_icon);
            holder.text = (TextView) convertView.findViewById(R.id.photoOptions_text);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.text.setText(options[position]);

        return convertView;
    }

    private class ViewHolder {
        ImageView icon;
        TextView text;
    }
}
