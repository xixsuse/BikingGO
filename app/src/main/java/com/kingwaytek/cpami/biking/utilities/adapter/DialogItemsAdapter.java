package com.kingwaytek.cpami.biking.utilities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.CommonBundle;

/**
 * Created by vincent.chang on 2016/6/17.
 */
public class DialogItemsAdapter extends BaseAdapter implements CommonBundle {

    private String[] options;
    private LayoutInflater inflater;

    private int SELECT_TYPE;
    private int[] positionSelectIconArray;

    public DialogItemsAdapter(Context context, String[] options, int selectType) {
        this.options = options;
        inflater = LayoutInflater.from(context);
        this.SELECT_TYPE = selectType;

        getValueBySelectType();
    }

    private void getValueBySelectType() {
        if (SELECT_TYPE == SELECT_TYPE_POSITION)
            positionSelectIconArray = getPositionSelectIconArray();
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
            convertView = inflater.inflate(R.layout.inflate_dialog_select_options, null);
            holder = new ViewHolder();

            holder.icon = (ImageView) convertView.findViewById(R.id.options_icon);
            holder.text = (TextView) convertView.findViewById(R.id.options_text);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.text.setText(options[position]);

        if (SELECT_TYPE == SELECT_TYPE_POSITION)
            holder.icon.setImageResource(positionSelectIconArray[position]);
        else
            holder.icon.setVisibility(View.GONE);

        return convertView;
    }

    private class ViewHolder {
        ImageView icon;
        TextView text;
    }

    private int[] getPositionSelectIconArray() {
        return new int[] {
                R.drawable.ic_photo_select_current_position,
                R.drawable.ic_photo_select_from_map,
                R.drawable.ic_photo_select_from_poi_list
        };
    }

}
