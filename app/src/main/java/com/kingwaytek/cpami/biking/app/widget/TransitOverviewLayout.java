package com.kingwaytek.cpami.biking.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.utilities.Utility;

/**
 * Created by vincent.chang on 2016/9/12.
 */
public class TransitOverviewLayout extends LinearLayout {

    private LinearLayout transportationLayout;
    private TextView totalTimeText;

    public TransitOverviewLayout(Context context) {
        super(context);
        init();
    }

    public TransitOverviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TransitOverviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        findViews();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.inflate_transit_overview_widget_layout, null);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        this.addView(view);
    }

    private void findViews() {
        transportationLayout = (LinearLayout) findViewById(R.id.transportationLayout);
        totalTimeText = (TextView) findViewById(R.id.text_transitTotalTime);
    }

    public void setTotalTime(String totalTime) {
        totalTimeText.setText(totalTime);
    }

    public void addVehicleStep(String vehicleType, String shortName, String iconUrl, boolean hasNext) {
        TransportationWidget widget = new TransportationWidget(getContext());

        widget.showVehicleStep(vehicleType, shortName, iconUrl, hasNext);

        transportationLayout.addView(widget);
    }

    public void addWalkStep(int estimateTime, boolean hasNext) {
        TransportationWidget widget = new TransportationWidget(getContext());

        widget.showWalkStep(Utility.getRoundMinutes(estimateTime), hasNext);

        //Log.i("TransitOverviewLayout", "EstimateWalkTime: " + estimateTime);
        transportationLayout.addView(widget);
    }

    public void removeTransportationWidgets() {
        transportationLayout.removeAllViews();
    }
}
