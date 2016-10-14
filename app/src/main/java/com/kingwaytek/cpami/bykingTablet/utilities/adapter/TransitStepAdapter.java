package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTransitStep;
import com.kingwaytek.cpami.bykingTablet.app.widget.TransportationWidget;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/9/21.
 */
public class TransitStepAdapter extends BaseAdapter {

    private ArrayList<ItemsTransitStep> transitSteps;
    private String fare;
    private Context context;

    private static final String TRAVEL_MODE_WALK = "WALKING";
    private static final String TRAVEL_MODE_TRANSIT = "TRANSIT";

    public TransitStepAdapter(Context context, ArrayList<ItemsTransitStep> transitStepItems, String fare) {
        this.transitSteps = transitStepItems;
        this.fare = fare;
        this.context = context;
    }

    public void refreshData(ArrayList<ItemsTransitStep> transitSteps, String fare) {
        this.transitSteps = transitSteps;
        this.fare = fare;
        notifyDataSetChanged();
        Log.i("UiTransitStepAdapter", "DataRefreshed!!!");
    }

    public void clearAllData() {
        transitSteps.clear();
        notifyDataSetChanged();
    }

    private Context appContext() {
        return AppController.getInstance().getAppContext();
    }

    @Override
    public int getCount() {
        if (transitSteps.isEmpty())
            return 0;
        else
            return transitSteps.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position != getCount() - 1)
            return transitSteps.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.inflate_transit_step, null);

            holder = new ViewHolder();
            holder.spotIcon = (ImageView) convertView.findViewById(R.id.transitIcon);
            holder.headWay = (TextView) convertView.findViewById(R.id.text_transitHeadWay);
            holder.lineLayout = (LinearLayout) convertView.findViewById(R.id.transitLineLayout);
            holder.lineIcon = (ImageView) convertView.findViewById(R.id.transitSpotIcon);
            holder.line = convertView.findViewById(R.id.transitLine);
            holder.spotName = (TextView) convertView.findViewById(R.id.text_transitSpotName);
            holder.headSign = (TextView) convertView.findViewById(R.id.text_transitHeadSign);
            holder.snippetLayout = (LinearLayout) convertView.findViewById(R.id.transitDescriptionLayout);
            holder.snippetIcon = (ImageView) convertView.findViewById(R.id.transitDescriptionIcon);
            holder.snippet = (TextView) convertView.findViewById(R.id.text_transitSpotDescription);
            holder.fare = (TextView) convertView.findViewById(R.id.text_transitFare);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        if (position != getCount() - 1)
        {
            ItemsTransitStep transitItem = transitSteps.get(position);

            if (isWalkingMode(transitItem.TRAVEL_MODE)) {
                holder.spotIcon.setVisibility(View.GONE);
                holder.headWay.setText(transitItem.TRANSIT_DEPARTURE_TIME);

                holder.headSign.setBackgroundResource(0);
                holder.headSign.setTextColor(ContextCompat.getColor(appContext(), R.color.md_grey_700));
                holder.headSign.setText(transitItem.INSTRUCTIONS);

                holder.snippetLayout.setVisibility(View.VISIBLE);
                holder.snippetIcon.setVisibility(View.VISIBLE);
                holder.snippet.setText(appContext().getString(R.string.transit_walking_duration,
                        transitItem.DISTANCE, Utility.getRoundMinutes(transitItem.DURATION)));

                if (position == 0)
                    holder.spotName.setText(appContext().getString(R.string.transit_my_location));
                else
                    holder.spotName.setText(transitSteps.get(position - 1).TRANSIT_ARRIVAL_STOP);

                setLineStatus(holder.lineLayout, holder.lineIcon, holder.line, position, false);
            }
            else if (isMetroLine(transitItem.TRAVEL_MODE, transitItem.TRANSIT_VEHICLE_TYPE)) {
                holder.spotIcon.setVisibility(View.VISIBLE);
                holder.spotIcon.setImageResource(R.drawable.ic_taipei_metro);

                if (transitItem.TRANSIT_HEAD_WAY == 0)
                    holder.headWay.setText(transitItem.TRANSIT_DEPARTURE_TIME);
                else
                    holder.headWay.setText(appContext().getString(R.string.transit_headway, Utility.getRoundMinutes(transitItem.TRANSIT_HEAD_WAY)));

                holder.spotName.setText(transitItem.TRANSIT_DEPARTURE_STOP);

                int backgroundRes = 0;

                switch (transitItem.TRANSIT_SHORT_NAME) {
                    case TransportationWidget.METRO_LINE_NAME_BLUE:
                        backgroundRes = R.drawable.background_metro_label_blue;
                        break;

                    case TransportationWidget.METRO_LINE_NAME_BROWN:
                        backgroundRes = R.drawable.background_metro_label_brown;
                        break;

                    case TransportationWidget.METRO_LINE_NAME_GREEN:
                    case TransportationWidget.KRT_LINE_NAME_LIGHT_RAIL:
                        backgroundRes = R.drawable.background_metro_label_green;
                        break;

                    case TransportationWidget.METRO_LINE_NAME_ORANGE:
                    case TransportationWidget.KRT_LINE_NAME_ORANGE:
                        backgroundRes = R.drawable.background_metro_label_orange;
                        break;

                    case TransportationWidget.METRO_LINE_NAME_RED:
                    case TransportationWidget.KRT_LINE_NAME_RED:
                        backgroundRes = R.drawable.background_metro_label_red;
                        break;
                }

                holder.headSign.setBackgroundResource(backgroundRes);
                holder.headSign.setTextColor(ContextCompat.getColor(appContext(), R.color.md_grey_50));
                holder.headSign.setText(transitItem.TRANSIT_SHORT_NAME);

                holder.snippetIcon.setVisibility(View.GONE);
                holder.snippet.setText(appContext().getString(R.string.transit_stops,
                        transitItem.TRANSIT_HEAD_SIGN, transitItem.TRANSIT_NUM_STOPS, Utility.getRoundMinutes(transitItem.DURATION)));

                setLineStatus(holder.lineLayout, holder.lineIcon, holder.line, position, true);
            }
            else {
                holder.spotIcon.setVisibility(View.VISIBLE);

                if (transitItem.TRANSIT_VEHICLE_ICON_URL.isEmpty())
                    holder.spotIcon.setImageResource(R.drawable.ic_directions_transit);
                else
                    Glide.with(context).load(transitItem.TRANSIT_VEHICLE_ICON_URL).into(holder.spotIcon);

                if (transitItem.TRANSIT_HEAD_WAY == 0)
                    holder.headWay.setText(transitItem.TRANSIT_DEPARTURE_TIME);
                else
                    holder.headWay.setText(appContext().getString(R.string.transit_headway, Utility.getRoundMinutes(transitItem.TRANSIT_HEAD_WAY)));

                holder.spotName.setText(transitItem.TRANSIT_DEPARTURE_STOP);

                holder.headSign.setBackgroundResource(R.drawable.background_common_vehicle_short_name);
                holder.headSign.setTextColor(ContextCompat.getColor(appContext(), R.color.md_grey_800));
                holder.headSign.setText(transitItem.TRANSIT_SHORT_NAME);

                holder.snippetIcon.setVisibility(View.GONE);
                holder.snippet.setText(appContext().getString(R.string.transit_stops,
                        transitItem.TRANSIT_HEAD_SIGN, transitItem.TRANSIT_NUM_STOPS, Utility.getRoundMinutes(transitItem.DURATION)));

                setLineStatus(holder.lineLayout, holder.lineIcon, holder.line, position, true);
            }
            holder.headSign.setVisibility(View.VISIBLE);
            holder.snippetLayout.setVisibility(View.VISIBLE);
            holder.fare.setVisibility(View.GONE);
        }
        else {
            holder.spotIcon.setVisibility(View.GONE);

            holder.headWay.setText(transitSteps.get(transitSteps.size() - 1).TRANSIT_DEPARTURE_TIME);

            String destination = transitSteps.get(transitSteps.size() - 1).TRANSIT_ARRIVAL_STOP;
            if (destination.isEmpty())
                destination = appContext().getString(R.string.transit_destination);

            holder.spotName.setText(destination);

            holder.headSign.setVisibility(View.GONE);
            holder.snippetLayout.setVisibility(View.GONE);

            holder.lineIcon.setImageResource(R.drawable.ic_page_dot_off);
            holder.line.setVisibility(View.GONE);
            holder.lineLayout.setBackgroundResource(0);

            if (!fare.isEmpty()) {
                holder.fare.setVisibility(View.VISIBLE);
                holder.fare.setText(appContext().getString(R.string.transit_fare, fare));
            }
        }

        return convertView;
    }

    private boolean isWalkingMode(String travelMode) {
        return travelMode.equals(TRAVEL_MODE_WALK);
    }

    private boolean isMetroLine(String travelMode, String vehicleType) {
        return travelMode.equals(TRAVEL_MODE_TRANSIT) && vehicleType.equals(TransportationWidget.VEHICLE_TYPE_METRO);
    }

    private void setLineStatus(LinearLayout lineLayout, ImageView lineIcon, View line, int position, boolean isNotWalking) {
        /*
        int height = isNotWalking ?
                appContext().getResources().getDimensionPixelSize(R.dimen.font_text_size_s) :
                appContext().getResources().getDimensionPixelSize(R.dimen.font_text_size_m);
        */
        int height = appContext().getResources().getDimensionPixelSize(R.dimen.font_text_size_s);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);

        if (isNotWalking) {
            lineIcon.setImageResource(R.drawable.ic_page_dot_white_on);

            switch (transitSteps.get(position).TRANSIT_SHORT_NAME) {
                case TransportationWidget.METRO_LINE_NAME_BLUE:
                    lineLayout.setBackgroundResource(R.drawable.background_metro_line_blue);
                    break;

                case TransportationWidget.METRO_LINE_NAME_BROWN:
                    lineLayout.setBackgroundResource(R.drawable.background_metro_line_brown);
                    break;

                case TransportationWidget.METRO_LINE_NAME_GREEN:
                case TransportationWidget.KRT_LINE_NAME_LIGHT_RAIL:
                    lineLayout.setBackgroundResource(R.drawable.background_metro_line_green);
                    break;

                case TransportationWidget.METRO_LINE_NAME_ORANGE:
                case TransportationWidget.KRT_LINE_NAME_ORANGE:
                    lineLayout.setBackgroundResource(R.drawable.background_metro_line_orange);
                    break;

                case TransportationWidget.METRO_LINE_NAME_RED:
                case TransportationWidget.KRT_LINE_NAME_RED:
                    lineLayout.setBackgroundResource(R.drawable.background_metro_line_red);
                    break;

                default:
                    lineLayout.setBackgroundResource(R.drawable.background_common_vehicle_line);
                    break;
            }
        }
        else {
            lineIcon.setImageResource(R.drawable.ic_page_dot_off);
            lineLayout.setBackgroundResource(0);
        }

        lineIcon.setLayoutParams(params);
        line.setVisibility(isNotWalking ? View.GONE : View.VISIBLE);
    }

    public boolean isContextDestroyed() {
        boolean isDestroyed = context instanceof Activity && ((Activity) context).isFinishing();
        Log.i("UiTransitStepAdapter", "isContextDestroyed: " + isDestroyed);
        return isDestroyed;
    }

    private class ViewHolder {
        ImageView spotIcon;
        TextView headWay;
        LinearLayout lineLayout;
        ImageView lineIcon;
        View line;
        TextView spotName;
        TextView headSign;
        LinearLayout snippetLayout;
        ImageView snippetIcon;
        TextView snippet;
        TextView fare;
    }
}
