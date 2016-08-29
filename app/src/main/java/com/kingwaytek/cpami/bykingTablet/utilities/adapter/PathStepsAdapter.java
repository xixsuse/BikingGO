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
public class PathStepsAdapter extends BaseAdapter {

    private ArrayList<ItemsPathStep> pathStepList;
    private LayoutInflater inflater;

    private boolean lightText;

    public PathStepsAdapter(Context context, ArrayList<ItemsPathStep> pathStepList, boolean lightText) {
        this.pathStepList = pathStepList;
        this.lightText = lightText;
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
            int layoutId = lightText ? R.layout.inflate_path_steps_light_text : R.layout.inflate_path_steps_dark_text;

            convertView = inflater.inflate(layoutId, null);

            holder = new ViewHolder();
            holder.instruction = (TextView) convertView.findViewById(R.id.text_pathInstruction);
            holder.distance = (TextView) convertView.findViewById(R.id.text_distance);
            holder.duration = (TextView) convertView.findViewById(R.id.text_duration);
            holder.goOnPath = (TextView) convertView.findViewById(R.id.text_goOnPath);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.instruction.setText(Html.fromHtml(pathStepList.get(position).INSTRUCTIONS));
        holder.distance.setText(pathStepList.get(position).DISTANCE);

        if (pathStepList.get(position).DURATION != null) {
            holder.duration.setVisibility(View.VISIBLE);
            holder.duration.setText(pathStepList.get(position).DURATION);
        }

        holder.goOnPath.setText(Html.fromHtml(pathStepList.get(position).GO_ON_PATH));

        return convertView;
    }

    private class ViewHolder {
        TextView instruction;
        TextView distance;
        TextView duration;
        TextView goOnPath;
    }
}
