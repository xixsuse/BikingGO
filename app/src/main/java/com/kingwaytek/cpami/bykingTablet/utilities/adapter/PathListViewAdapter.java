package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathStep;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/6/13.
 */
public class PathListViewAdapter extends BaseAdapter {

    private ArrayList<ItemsPathStep> pathStepList;
    private LayoutInflater inflater;

    public PathListViewAdapter(Context context, ArrayList<ItemsPathStep> pathStepList) {
        this.pathStepList = pathStepList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return pathStepList.size();
    }

    @Override
    public Object getItem(int position) {
        return pathStepList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_path_list_step, null);

            holder = new ViewHolder();
            holder.instruction = (TextView) convertView.findViewById(R.id.text_pathInstruction);
            holder.distance = (TextView) convertView.findViewById(R.id.text_distance);
            holder.goOnPath = (TextView) convertView.findViewById(R.id.text_goOnPath);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.instruction.setText(Html.fromHtml(pathStepList.get(position).INSTRUCTIONS));
        holder.distance.setText(pathStepList.get(position).DISTANCE);
        holder.goOnPath.setText(Html.fromHtml(pathStepList.get(position).GO_ON_PATH));

        return convertView;
    }

    private class ViewHolder {
        TextView instruction;
        TextView distance;
        TextView goOnPath;
    }
}
