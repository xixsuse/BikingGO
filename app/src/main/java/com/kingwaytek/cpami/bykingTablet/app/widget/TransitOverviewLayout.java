package com.kingwaytek.cpami.bykingTablet.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

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

    public void addMetroStep(int metroColor, String shortName, boolean hasNext) {
        TransportationWidget widget = new TransportationWidget(getContext());

        widget.showMetroStep(metroColor, shortName, hasNext);

        transportationLayout.addView(widget);
    }

    public void addMoveStep(int moveType, int estimateTime, boolean hasNext) {
        TransportationWidget widget = new TransportationWidget(getContext());

        widget.showMoveStep(moveType, estimateTime, hasNext);

        transportationLayout.addView(widget);
    }
}
